package com.carrefour.delivery.domain.port.in;

import com.carrefour.delivery.domain.model.Reservation;

import java.util.List;

/** Driving port: query reservations. */
public interface GetReservationsUseCase {
    List<Reservation> getReservationsByUser(Long userId);
    Reservation getReservationById(Long id);
}
