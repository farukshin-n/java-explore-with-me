package ru.practicum.ewmservice.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.comment.CommentDto;
import ru.practicum.ewmservice.model.CommentSort;
import ru.practicum.ewmservice.service.comment.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RequestMapping("/admin")
@RestController
@RequiredArgsConstructor
public class CommentAdminController {
    private final CommentService commentService;

    @GetMapping("/comments")
    public List<CommentDto> getAllCommentsSortedByUpdated(
            @RequestParam(value = "sort", defaultValue = "desc") CommentSort sort,
            @RequestParam(defaultValue = "0") @Positive int from,
            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("Received get-request from admin for setting all comments sorted by update time.");
        return commentService.getAllCommentsSortedByUpdated(sort, from, size);
    }

    @PatchMapping("/events/{eventId}/comments/{commId}/hide")
    public CommentDto unhide(@PathVariable @PositiveOrZero Long eventId,
                             @PathVariable(name = "commId") @PositiveOrZero Long commentId) {
        log.info("Receive request for unhide comment with id={} to event with id={}.",
                commentId, eventId);
        return commentService.unhide(eventId, commentId);
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
    public CommentDto hide(
            @PathVariable @PositiveOrZero Long eventId,
            @PathVariable(name = "commId") @PositiveOrZero Long commentId) {
        log.info("Receive request for hiding comment with id={} to event with id={}.",
                commentId, eventId);
        return commentService.hide(eventId, commentId);
    }
}
