package ru.practicum.ewmservice.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.dto.comment.CommentDto;
import ru.practicum.ewmservice.exception.BadRequestException;
import ru.practicum.ewmservice.model.Comment;
import ru.practicum.ewmservice.repository.CommentRepository;
import ru.practicum.ewmservice.service.mapper.CommentMapper;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentAdminServiceImpl implements CommentAdminService {
    private final CommentRepository commentRepository;

    @Override
    public List<CommentDto> getAllComments(int from, int size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);

        return commentRepository.findAllComments(pageRequest).stream()
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
    public CommentDto showComment(Long eventId, Long commentId) {
        final Comment comment = changeCommentVisibility(eventId, commentId, true);
        log.info("Admin show comment with id={} to event with id={}.",
                comment.getId(), comment.getEvent().getId());

        return CommentMapper.toDto(comment);
    }

    @Override
    @Transactional
    public CommentDto hideComment(Long eventId, Long commentId) {
        final Comment comment = changeCommentVisibility(eventId, commentId, false);
        log.info("Admin hide comment with id={} to event with id={}.",
                comment.getId(), comment.getEvent().getId());

        return CommentMapper.toDto(comment);
    }

    @Override
    public void deleteCommentByAdmin(Long eventId, Long commentId) {
        findCommentByIdAndEventId(commentId, eventId);
        commentRepository.deleteById(commentId);
        log.info("Comment with id={} from event with id={} successfully deleted by admin.",
                commentId, eventId);
    }

    private Comment findCommentByIdAndEventId(Long commentId, Long eventId) {
        return commentRepository.findCommentByIdAndEvent_Id(commentId, eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "There isn't comment with id=%d from event with id=%d in this database.", commentId, eventId
                )));
    }

    private Comment changeCommentVisibility(Long eventId, Long commentId, boolean isVisible) {
        String wordForLogging;
        if (isVisible) {
            wordForLogging = "visible";
        } else {
            wordForLogging = "invisible";
        }
        final Comment comment = findCommentByIdAndEventId(commentId, eventId);
        if (comment.getVisible().equals(isVisible)) {
            throw new BadRequestException(String.format(
                    "Comment with id=%d already %s.", comment.getId(), wordForLogging
            ));
        }
        comment.setVisible(isVisible);

        return commentRepository.save(comment);
    }
}
