package com.carrefour.delivery.infrastructure.web.dto;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        Long timeSlotId,
        String username,
        LocalDateTime createdAt,
        String status
) {}
