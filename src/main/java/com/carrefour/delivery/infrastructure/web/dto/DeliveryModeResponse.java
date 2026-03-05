package com.carrefour.delivery.infrastructure.web.dto;

public record DeliveryModeResponse(
        String name,
        String slotDuration,
        String startTime,
        String endTime,
        int maxDaysAhead,
        int capacity
) {}
