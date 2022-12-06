package ru.practicum.ewmservice.dto.comment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@ToString
public class UpdateCommentRequest {
    private Long id;
    @NotBlank
    private String comment;
    private Long authorId;
}
