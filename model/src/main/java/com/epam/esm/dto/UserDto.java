package com.epam.esm.dto;

import lombok.ToString;
import javax.validation.constraints.*;

public record UserDto(
        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @ToString.Exclude
        @Pattern(regexp = "(?=.*\\d)(?=.*\\p{Lower})(?=.*\\p{Upper})[\\p{Alnum}]{8,30}")
        @NotNull
        @Size(max = 100)
        String password,

        @NotBlank
        @Size(max = 50)
        String firstName,

        @NotBlank
        @Size(max = 50)
        String lastName) {
}
