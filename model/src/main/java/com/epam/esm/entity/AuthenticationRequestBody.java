package com.epam.esm.entity;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public record AuthenticationRequestBody(
        @Email
        @NotBlank
        @Size(max = 255)
        String email,

        @NotBlank
        @Size(max = 30)
        String password) {
}
