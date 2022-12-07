package ru.practicum.ewmservice.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.comment.CommentDto;
import ru.practicum.ewmservice.service.comment.CommentAdminService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RequestMapping("/admin")
@RestController
@RequiredArgsConstructor
public class CommentAdminController {
    private final CommentAdminService commentService;

    @GetMapping("/comments")
    public List<CommentDto> getAll(
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Receive get-request for getting all comments from admin.");
        return commentService.getAllComments(from, size);
    }

    @GetMapping("/comments/filter")
    public List<CommentDto> getAllCommentsByVisibility(
            @RequestParam(defaultValue = "false") boolean visible,
            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Receive get-request for getting all comments with visibility={} from admin.", visible);
        return commentService.getAllCommentsByVisibility(visible, from, size);
    }

    @PatchMapping("/events/{eventId}/comments/{commId}/hide")
    public CommentDto showComment(@PathVariable @PositiveOrZero Long eventId,
                             @PathVariable(name = "commId") @PositiveOrZero Long commentId) {
        log.info("Receive request for showing comment with id={} to event with id={}.",
                commentId, eventId);
        return commentService.showComment(eventId, commentId);
    }

    @DeleteMapping("/events/{eventId}/comments/{commId}")
    public void deleteCommentByAdmin(
            @PathVariable @PositiveOrZero Long eventId,
            @PathVariable(name = "commId") @PositiveOrZero Long commentId) {
        log.info("Receive request for deleting comment with id={} to event with id={}.",
                commentId, eventId);
        commentService.deleteCommentByAdmin(eventId, commentId);
    }

    @DeleteMapping("/events/{eventId}/comments/{commId}/hide")
    public CommentDto hideComment(
            @PathVariable @PositiveOrZero Long eventId,
            @PathVariable(name = "commId") @PositiveOrZero Long commentId) {
        log.info("Receive request for hiding comment with id={} to event with id={}.",
                commentId, eventId);
        return commentService.hideComment(eventId, commentId);
    }
}
