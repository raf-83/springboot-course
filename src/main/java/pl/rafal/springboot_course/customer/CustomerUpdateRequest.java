package pl.rafal.springboot_course.customer;

import jakarta.validation.constraints.Email;

public record CustomerUpdateRequest(
        String name,

        @Email(message = "email must be valid")
        String email
) {}
