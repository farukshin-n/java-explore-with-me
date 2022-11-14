package ru.practicum.ewmservice.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {
    private Long id;
    @NotBlank(message = "Name cannot be blank.")
    private String name;
    @NotEmpty
    @Email(message = "Incorrect email.")
    private String email;
}
