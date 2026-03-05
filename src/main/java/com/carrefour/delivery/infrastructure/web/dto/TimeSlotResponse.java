package com.carrefour.delivery.infrastructure.web.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record TimeSlotResponse(
        Long id,
        String deliveryMode,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        int capacity,
        int reservedCount,
        int availableCapacity,
        boolean available
) {}
