package ru.practicum.ewmservice.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewmservice.dto.comment.CommentDto;
import ru.practicum.ewmservice.dto.comment.NewCommentDto;
import ru.practicum.ewmservice.dto.comment.UpdateCommentRequest;
import ru.practicum.ewmservice.exception.BadRequestException;
import ru.practicum.ewmservice.model.Comment;
import ru.practicum.ewmservice.model.Event;
import ru.practicum.ewmservice.model.User;
import ru.practicum.ewmservice.repository.CommentRepository;
import ru.practicum.ewmservice.repository.EventRepository;
import ru.practicum.ewmservice.repository.UserRepository;
import ru.practicum.ewmservice.service.mapper.CommentMapper;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
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
        final Comment comment = findCommentByIdAndEventId(commentId, eventId);
        log.info("Comment with id={} to event with id={} found successfully", commentId, eventId);

        return CommentMapper.toDto(comment);
    }

    @Override
    public List<CommentDto> getAllComments(int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        return commentRepository.findAllComments(pageRequest).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllCommentsByEvent(Long eventId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        return commentRepository.findCommentsByEvent_IdOrderByCreatedDesc(eventId, pageRequest).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllCommentsByUser(Long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        return commentRepository.findCommentsByAuthor_IdOrderByCreatedDesc(userId, pageRequest).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllCommentsByVisibility(boolean visible, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        return commentRepository.findCommentsByVisibleIsOrderByCreatedDesc(visible, pageRequest).stream()
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
        comment.setComment(request.getComment());
        final Comment updatedComment = commentRepository.save(comment);
        log.info("Comment with id={} to event with id ={} successfully updated by text={}",
                updatedComment.getId(), updatedComment.getEvent().getId(), updatedComment.getComment());

        return CommentMapper.updateToDto(updatedComment);
    }

    @Override
    @Transactional
    public CommentDto hideComment(Long eventId, Long commentId) {
        final Comment comment = findCommentByIdAndEventId(commentId, eventId);
        if (comment.getVisible().equals(false)) {
            throw new BadRequestException(String.format(
                    "Comment with id=%d already hidden.", comment.getId()
            ));
        }
        comment.setVisible(false);
        final Comment updatedComment = commentRepository.save(comment);
        log.info("Admin hide comment with id={} to event with id={}.",
                updatedComment.getId(), updatedComment.getEvent().getId());

        return CommentMapper.toDto(updatedComment);
    }

    @Override
    @Transactional
    public CommentDto showComment(Long eventId, Long commentId) {
        final Comment comment = findCommentByIdAndEventId(commentId, eventId);
        if (comment.getVisible().equals(true)) {
            throw new BadRequestException(String.format(
                    "Comment with id=%d already visible.", comment.getId()
            ));
        }
        comment.setVisible(true);
        final Comment updatedComment = commentRepository.save(comment);
        log.info("Admin show comment with id={} to event with id={}.",
                updatedComment.getId(), updatedComment.getEvent().getId());

        return CommentMapper.toDto(updatedComment);
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

    @Override
    public void deleteCommentByAdmin(Long eventId, Long commentId) {
        findCommentByIdAndEventId(commentId, eventId);
        commentRepository.deleteById(commentId);
        log.info("Comment with id={} from event with id={} successfully deleted by admin.",
                commentId, eventId);
    }

    private User getUserFromRepo(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new BadRequestException(
                String.format("There isn't user with id %d in this database.", id)
        ));
    }

    private Event getEventFromRepo(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        String.format("There isn't event with id=%d in repository.", id)
                ));
    }

    private Comment findCommentByIdAndEventId(Long commentId, Long eventId) {
        return commentRepository.findCommentByIdAndEvent_Id(commentId, eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "There isn't comment with id=%d from event with id=%d in this database.", commentId, eventId
                )));
    }
}
