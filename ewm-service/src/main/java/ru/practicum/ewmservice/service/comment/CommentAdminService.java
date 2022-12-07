package ru.practicum.ewmservice.service.comment;

import ru.practicum.ewmservice.dto.comment.CommentDto;

import java.util.List;

public interface CommentAdminService {
    List<CommentDto> getAllComments(int from, int size);

    List<CommentDto> getAllCommentsByVisibility(boolean visible, int from, int size);

    CommentDto hideComment(Long eventId, Long commentId);

    CommentDto showComment(Long eventId, Long commentId);

    void deleteCommentByAdmin(Long eventId, Long commentId);
}
