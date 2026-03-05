package com.carrefour.delivery.domain.port.in;

/** Driving port: cancel a reservation. */
public interface CancelReservationUseCase {
    void cancelReservation(Long reservationId, Long userId);
}
