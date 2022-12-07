package ru.practicum.ewmservice.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.dto.comment.CommentDto;
import ru.practicum.ewmservice.dto.comment.NewCommentDto;
import ru.practicum.ewmservice.dto.comment.UpdateCommentRequest;
import ru.practicum.ewmservice.exception.BadRequestException;
import ru.practicum.ewmservice.model.Comment;
import ru.practicum.ewmservice.model.Event;
import ru.practicum.ewmservice.model.EventState;
import ru.practicum.ewmservice.model.User;
import ru.practicum.ewmservice.repository.CommentRepository;
import ru.practicum.ewmservice.repository.EventRepository;
import ru.practicum.ewmservice.repository.UserRepository;
import ru.practicum.ewmservice.service.mapper.CommentMapper;

import javax.persistence.EntityNotFoundException;
import javax.validation.ValidationException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentDto addComment(Long eventId, NewCommentDto dto) {
        final User author = getUserFromRepo(dto.getAuthorId());
        final Event event = getEventFromRepo(eventId);
        final Comment comment = commentRepository.save(CommentMapper.fromNewDtoToComment(dto, author, event));
        log.info("Comment with id={} from user with id={} to event with id={} saved successfully.",
                comment.getId(), event.getId(), author.getId());

        return CommentMapper.toDto(comment);
    }

    @Override
    public CommentDto getComment(Long eventId, Long commentId) {
        final Comment comment = commentRepository.findCommentByIdAndEvent_Id(commentId, eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "There isn't comment with id=%d from event with id=%d in this database.", commentId, eventId
                )));
        checkEventPublicity(comment.getEvent());
        log.info("Comment with id={} to event with id={} found successfully",
                comment.getId(), comment.getEvent().getId());

        return CommentMapper.toDto(comment);
    }

    @Override
    public List<CommentDto> getAllCommentsByEvent(Long eventId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        Event event = getEventFromRepo(eventId);

        return commentRepository.findCommentsByEvent_IdOrderByCreatedDesc(event.getId(), pageRequest).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllCommentsByUser(Long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        return commentRepository.findCommentsByAuthor_IdAndAndEvent_StateOrderByCreatedDesc(
                userId, EventState.PUBLISHED, pageRequest).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long eventId, UpdateCommentRequest request) {
        final Comment comment = commentRepository.findCommentByIdAndEvent_IdAndAuthor_Id(
                        request.getId(), eventId, request.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Comment with id=%d by user with id=%d to event with id=%d don't exist.",
                        request.getId(), request.getAuthorId(), eventId)
                ));
        checkEventPublicity(comment.getEvent());
        comment.setComment(request.getComment());
        final Comment updatedComment = commentRepository.save(comment);
        log.info("Comment with id={} to event with id ={} successfully updated by text={}",
                updatedComment.getId(), updatedComment.getEvent().getId(), updatedComment.getComment());

        return CommentMapper.updateToDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long eventId, Long userId, Long commentId) {
        commentRepository.findCommentByIdAndEvent_IdAndAuthor_Id(commentId, eventId, userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "There isn't comment with id=%d from user with id=%d to event with id=%d.",
                        commentId, userId, eventId)));
        commentRepository.deleteById(commentId);
        log.info("Comment with id={} from event with id={} successfully deleted.",
                commentId, eventId);
    }

    private User getUserFromRepo(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new BadRequestException(
                String.format("There isn't user with id %d in this database.", id)
        ));
    }

    private Event getEventFromRepo(Long id) {
        final Event event = eventRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        String.format("There isn't event with id=%d in repository.", id)
                ));

        return checkEventPublicity(event);
    }

    private Event checkEventPublicity(Event event) {
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException(String.format("You cannot add comment to event with status %s.",
                    event.getState()));
        }

        return event;
    }

}
