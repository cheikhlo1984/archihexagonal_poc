package com.carrefour.delivery.domain.service;

import com.carrefour.delivery.domain.exception.ReservationNotFoundException;
import com.carrefour.delivery.domain.exception.SlotNotAvailableException;
import com.carrefour.delivery.domain.exception.TimeSlotNotFoundException;
import com.carrefour.delivery.domain.exception.UnauthorizedException;
import com.carrefour.delivery.domain.model.Reservation;
import com.carrefour.delivery.domain.model.ReservationStatus;
import com.carrefour.delivery.domain.model.TimeSlot;
import com.carrefour.delivery.domain.port.in.CancelReservationUseCase;
import com.carrefour.delivery.domain.port.in.CreateReservationUseCase;
import com.carrefour.delivery.domain.port.in.GetReservationsUseCase;
import com.carrefour.delivery.domain.port.out.ReservationRepositoryPort;
import com.carrefour.delivery.domain.port.out.TimeSlotRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Domain service responsible for reservation lifecycle.
 * Uses pessimistic locking (PESSIMISTIC_WRITE) to prevent over-booking.
 */
@Service
public class ReservationDomainService
        implements CreateReservationUseCase, GetReservationsUseCase, CancelReservationUseCase {

    private final TimeSlotRepositoryPort timeSlotRepo;
    private final ReservationRepositoryPort reservationRepo;

    public ReservationDomainService(TimeSlotRepositoryPort timeSlotRepo,
                                    ReservationRepositoryPort reservationRepo) {
        this.timeSlotRepo = timeSlotRepo;
        this.reservationRepo = reservationRepo;
    }

    // ---- CreateReservationUseCase ----

    @Override
    @Transactional
    public Reservation createReservation(Long slotId, Long userId, String username) {
        // Acquire pessimistic write lock to prevent concurrent over-booking
        TimeSlot slot = timeSlotRepo.findByIdWithLock(slotId)
                .orElseThrow(() -> new TimeSlotNotFoundException(slotId));

        if (!slot.isAvailable()) {
            throw new SlotNotAvailableException(slotId);
        }

        slot.incrementReservedCount();
        timeSlotRepo.save(slot);

        Reservation reservation = new Reservation(
                null, slotId, userId, username,
                LocalDateTime.now(), ReservationStatus.CONFIRMED
        );
        return reservationRepo.save(reservation);
    }

    // ---- GetReservationsUseCase ----

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByUser(Long userId) {
        return reservationRepo.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Reservation getReservationById(Long id) {
        return reservationRepo.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }

    // ---- CancelReservationUseCase ----

    @Override
    @Transactional
    public void cancelReservation(Long reservationId, Long userId) {
        Reservation reservation = reservationRepo.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        if (!reservation.getUserId().equals(userId)) {
            throw new UnauthorizedException("You do not own this reservation");
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new IllegalStateException("Reservation " + reservationId + " is already cancelled");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepo.save(reservation);

        // Release the slot capacity back (with lock to prevent races)
        timeSlotRepo.findByIdWithLock(reservation.getTimeSlotId())
                .ifPresent(slot -> {
                    slot.decrementReservedCount();
                    timeSlotRepo.save(slot);
                });
    }
}
