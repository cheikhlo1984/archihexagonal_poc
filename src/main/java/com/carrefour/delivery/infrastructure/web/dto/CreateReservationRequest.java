package com.carrefour.delivery.infrastructure.web.dto;

import jakarta.validation.constraints.NotNull;

public record CreateReservationRequest(
        @NotNull Long slotId
) {}
