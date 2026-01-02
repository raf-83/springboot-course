// OrderResponse.java
package pl.rafal.springboot_course.customer;

import java.time.LocalDateTime;

public record OrdersResponse(
        Long id,
        String description,
        Double amount,
        LocalDateTime createdAt
) {}
