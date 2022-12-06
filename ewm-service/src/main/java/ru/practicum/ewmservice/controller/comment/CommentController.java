package ru.practicum.ewmservice.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.comment.CommentDto;
import ru.practicum.ewmservice.dto.comment.NewCommentDto;
import ru.practicum.ewmservice.dto.comment.UpdateCommentRequest;
import ru.practicum.ewmservice.model.CommentSort;
import ru.practicum.ewmservice.service.comment.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/events/{eventId}/comments")
    public CommentDto addComment(@PathVariable @PositiveOrZero Long eventId,
                                 @RequestBody @Valid NewCommentDto dto) {
        log.info("Received post-request for adding comment from user with id={}.", dto.getAuthorId());
        return commentService.addComment(eventId, dto);
    }

    @GetMapping("/events/{eventId}/comments/{commId}")
    public CommentDto getComment(
            @PathVariable @PositiveOrZero Long eventId,
            @PathVariable(name = "commId") Long commentId) {
        log.info("Received get-request for getting comment with id={} to event with id={}", commentId, eventId);
        return commentService.getComment(eventId, commentId);
    }

    @GetMapping("/events/{eventId}/comments/")
    public List<CommentDto> getAllCommentsByEvent(
            @PathVariable @PositiveOrZero Long eventId,
            @RequestParam(value = "sort", defaultValue = "asc") CommentSort sort,
            @RequestParam(defaultValue = "0") @Positive int from,
            @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        log.info("Receive get-request for getting all comments to event with id={}.", eventId);
        return commentService.getAllCommentsByEvent(eventId, sort, from, size);
    }

    @GetMapping("/users/{userId}/comments/")
    public List<CommentDto> getAllCommentsByUser(
            @PathVariable @PositiveOrZero Long userId,
            @RequestParam(value = "sort", defaultValue = "asc") CommentSort sort,
            @RequestParam(defaultValue = "0") @Positive int from,
            @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        log.info("Receive get-request for getting all comments from user with id={}.", userId);
        return commentService.getAllCommentsByUser(userId, sort, from, size);
    }

    @PatchMapping("/events/{eventId}/comments")
    public CommentDto updateComment(
            @PathVariable @PositiveOrZero Long eventId,
            @RequestBody @Valid UpdateCommentRequest request) {
        log.info("Get request for updating comment with id={} to event with id={}.", request.getId(), eventId);
        return commentService.updateComment(eventId, request);
    }

    @DeleteMapping("/users/{userId}/events/{eventId}/comments/{commId}")
    public void deleteComment(
            @PathVariable @PositiveOrZero Long eventId,
            @PathVariable @PositiveOrZero Long userId,
            @PathVariable(name = "commId") @PositiveOrZero Long commentId) {
        log.info("Receive request for deleting comment with id={} from user with id={} to event with id={}.",
                commentId, userId, eventId);
        commentService.deleteComment(eventId, userId, commentId);
    }
}
