package ru.practicum.ewmservice.dto.comment;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@ToString
public class UpdateCommentRequest {
    private Long id;
    @NotBlank @Size(message = "Comment's length must be less than 2000 characters and more than 3",
            max = 2000, min = 3)
    private String comment;
    private Long authorId;
}
