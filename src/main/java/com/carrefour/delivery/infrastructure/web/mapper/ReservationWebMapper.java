package com.carrefour.delivery.infrastructure.web.mapper;

import com.carrefour.delivery.domain.model.Reservation;
import com.carrefour.delivery.infrastructure.web.dto.ReservationResponse;
import org.springframework.stereotype.Component;

@Component
public class ReservationWebMapper {

    public ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getTimeSlotId(),
                reservation.getUsername(),
                reservation.getCreatedAt(),
                reservation.getStatus().name()
        );
    }
}
