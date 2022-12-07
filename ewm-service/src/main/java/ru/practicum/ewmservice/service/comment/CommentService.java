package ru.practicum.ewmservice.service.comment;

import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.dto.comment.CommentDto;
import ru.practicum.ewmservice.dto.comment.NewCommentDto;
import ru.practicum.ewmservice.dto.comment.UpdateCommentRequest;

import java.util.List;

@Repository
public interface CommentService {
    CommentDto addComment(Long eventId, NewCommentDto dto);

    CommentDto getComment(Long eventId, Long commentId);

    List<CommentDto> getAllCommentsByEvent(Long eventId, int from, int size);

    List<CommentDto> getAllCommentsByUser(Long userId, int from, int size);

    CommentDto updateComment(Long eventId, UpdateCommentRequest request);

    void deleteComment(Long eventId, Long userId, Long commentId);
}
