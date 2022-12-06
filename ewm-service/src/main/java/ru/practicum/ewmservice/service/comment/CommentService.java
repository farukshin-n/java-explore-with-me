package ru.practicum.ewmservice.service.comment;

import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.dto.comment.CommentDto;
import ru.practicum.ewmservice.dto.comment.NewCommentDto;
import ru.practicum.ewmservice.dto.comment.UpdateCommentRequest;
import ru.practicum.ewmservice.model.CommentSort;

import java.util.List;

@Repository
public interface CommentService {
    CommentDto addComment(Long eventId, NewCommentDto dto);

    CommentDto getComment(Long eventId, Long commentId);

    List<CommentDto> getAllCommentsByEvent(Long eventId, CommentSort sort, int from, int size);

    List<CommentDto> getAllCommentsByUser(Long userId, CommentSort sort, int from, int size);

    List<CommentDto> getAllCommentsSortedByUpdated(CommentSort sort, int from, int size);

    CommentDto updateComment(Long eventId, UpdateCommentRequest request);

    CommentDto hide(Long eventId, Long commentId);

    CommentDto unhide(Long eventId, Long commentId);

    void deleteComment(Long eventId, Long userId, Long commentId);

    void deleteCommentByAdmin(Long eventId, Long commentId);
}
