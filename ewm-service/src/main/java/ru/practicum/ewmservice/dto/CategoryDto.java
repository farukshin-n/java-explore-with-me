package ru.practicum.ewmservice.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CategoryDto {
    private Long id;
    @NotBlank(message = "Category name cannot be blank.")
    private String name;
}
