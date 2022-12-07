package ru.practicum.ewmservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCommentDto {
    @NotBlank @Size(message = "Comment's length must be less than 2000 characters and more than 3",
            max = 2000, min = 3)
    private String comment;
    @NotNull
    private Long authorId;
}
