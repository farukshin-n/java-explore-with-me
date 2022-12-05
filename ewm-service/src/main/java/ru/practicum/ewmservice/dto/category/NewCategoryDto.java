package ru.practicum.ewmservice.dto.category;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class NewCategoryDto {
    @NotBlank
    private String name;
}
