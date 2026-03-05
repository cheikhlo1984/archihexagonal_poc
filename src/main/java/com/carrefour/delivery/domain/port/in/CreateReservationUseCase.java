package com.carrefour.delivery.domain.port.in;

import com.carrefour.delivery.domain.model.Reservation;

/** Driving port: create a reservation on a time slot. */
public interface CreateReservationUseCase {
    Reservation createReservation(Long slotId, Long userId, String username);
}
