package com.carrefour.delivery.domain.port.out;

import com.carrefour.delivery.domain.model.Reservation;

import java.util.List;
import java.util.Optional;

/** Driven port: reservation persistence contract. */
public interface ReservationRepositoryPort {
    Reservation save(Reservation reservation);
    Optional<Reservation> findById(Long id);
    List<Reservation> findByUserId(Long userId);
}
