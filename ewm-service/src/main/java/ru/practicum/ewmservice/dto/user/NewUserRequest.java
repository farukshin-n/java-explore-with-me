package ru.practicum.ewmservice.dto.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewUserRequest {
    @NotBlank(message = "Name cannot be blank.")
    private String name;
    @NotEmpty
    @Email(message = "Email should be correct.")
    private String email;
}
