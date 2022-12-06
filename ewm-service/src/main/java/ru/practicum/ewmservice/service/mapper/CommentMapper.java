package ru.practicum.ewmservice.service.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewmservice.dto.comment.CommentDto;
import ru.practicum.ewmservice.dto.comment.NewCommentDto;
import ru.practicum.ewmservice.model.Comment;
import ru.practicum.ewmservice.model.Event;
import ru.practicum.ewmservice.model.User;

import java.time.LocalDateTime;

@Component
public class CommentMapper {
    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getComment(),
                comment.getAuthor().getId(),
                comment.getEvent().getId(),
                comment.getCreated(),
                comment.getUpdated(),
                comment.getVisible()
        );
    }

    public static CommentDto updateToDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getComment(),
                comment.getAuthor().getId(),
                comment.getEvent().getId(),
                comment.getCreated(),
                LocalDateTime.now(),
                comment.getVisible()
        );
    }

    public static Comment fromNewDtoToComment(NewCommentDto dto, User author, Event event) {
        return new Comment(
                null,
                dto.getComment(),
                author,
                event,
                LocalDateTime.now(),
                LocalDateTime.now(),
                true
        );
    }

    public static Comment fromDtoToComment(CommentDto dto, User author, Event event) {
        return new Comment(
                dto.getId(),
                dto.getComment(),
                author,
                event,
                dto.getCreated(),
                dto.getUpdated(),
                dto.getVisible()
        );
    }
}
