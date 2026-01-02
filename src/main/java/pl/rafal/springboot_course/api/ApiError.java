package pl.rafal.springboot_course.api;

public record ApiError(
        int status,
        String message
) {}
