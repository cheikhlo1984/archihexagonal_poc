package com.carrefour.delivery.domain.service;

import com.carrefour.delivery.domain.exception.ReservationNotFoundException;
import com.carrefour.delivery.domain.exception.SlotNotAvailableException;
import com.carrefour.delivery.domain.exception.TimeSlotNotFoundException;
import com.carrefour.delivery.domain.exception.UnauthorizedException;
import com.carrefour.delivery.domain.model.*;
import com.carrefour.delivery.domain.port.out.ReservationRepositoryPort;
import com.carrefour.delivery.domain.port.out.TimeSlotRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationDomainServiceTest {

    @Mock
    private TimeSlotRepositoryPort timeSlotRepo;

    @Mock
    private ReservationRepositoryPort reservationRepo;

    @InjectMocks
    private ReservationDomainService service;

    // ---- createReservation ----

    @Test
    void createReservation_succeeds_whenSlotHasCapacity() {
        TimeSlot slot = availableSlot(1L, 10, 0);
        when(timeSlotRepo.findByIdWithLock(1L)).thenReturn(Optional.of(slot));
        when(timeSlotRepo.save(slot)).thenReturn(slot);

        Reservation expected = new Reservation(1L, 1L, 2L, "alice",
                LocalDateTime.now(), ReservationStatus.CONFIRMED);
        when(reservationRepo.save(any())).thenReturn(expected);

        Reservation result = service.createReservation(1L, 2L, "alice");

        assertThat(result.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(slot.getReservedCount()).isEqualTo(1); // incremented
        verify(timeSlotRepo).save(slot);
    }

    @Test
    void createReservation_throwsSlotNotAvailable_whenSlotFull() {
        TimeSlot slot = availableSlot(1L, 2, 2); // full
        when(timeSlotRepo.findByIdWithLock(1L)).thenReturn(Optional.of(slot));

        assertThatThrownBy(() -> service.createReservation(1L, 2L, "alice"))
                .isInstanceOf(SlotNotAvailableException.class);
        verify(reservationRepo, never()).save(any());
    }

    @Test
    void createReservation_throwsTimeSlotNotFound_whenSlotMissing() {
        when(timeSlotRepo.findByIdWithLock(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.createReservation(99L, 1L, "alice"))
                .isInstanceOf(TimeSlotNotFoundException.class);
    }

    // ---- cancelReservation ----

    @Test
    void cancelReservation_succeeds_andFreesSlotCapacity() {
        Reservation res = confirmedReservation(1L, 1L, 2L);
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(res));
        when(reservationRepo.save(res)).thenReturn(res);

        TimeSlot slot = availableSlot(1L, 3, 1);
        when(timeSlotRepo.findByIdWithLock(1L)).thenReturn(Optional.of(slot));
        when(timeSlotRepo.save(slot)).thenReturn(slot);

        service.cancelReservation(1L, 2L);

        assertThat(res.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
        assertThat(slot.getReservedCount()).isZero();
    }

    @Test
    void cancelReservation_throwsUnauthorized_whenNotOwner() {
        Reservation res = confirmedReservation(1L, 1L, 2L); // owned by userId=2
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(res));

        assertThatThrownBy(() -> service.cancelReservation(1L, 99L)) // different user
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    void cancelReservation_throwsIllegalState_whenAlreadyCancelled() {
        Reservation res = confirmedReservation(1L, 1L, 2L);
        res.setStatus(ReservationStatus.CANCELLED);
        when(reservationRepo.findById(1L)).thenReturn(Optional.of(res));

        assertThatThrownBy(() -> service.cancelReservation(1L, 2L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void cancelReservation_throwsReservationNotFound_whenMissing() {
        when(reservationRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancelReservation(99L, 1L))
                .isInstanceOf(ReservationNotFoundException.class);
    }

    // ---- getReservationsByUser ----

    @Test
    void getReservationsByUser_returnsUserReservations() {
        List<Reservation> reservations = List.of(
                confirmedReservation(1L, 10L, 5L),
                confirmedReservation(2L, 11L, 5L)
        );
        when(reservationRepo.findByUserId(5L)).thenReturn(reservations);

        List<Reservation> result = service.getReservationsByUser(5L);

        assertThat(result).hasSize(2);
    }

    // ---- Helpers ----

    private TimeSlot availableSlot(Long id, int capacity, int reserved) {
        return new TimeSlot(id, DeliveryMode.DRIVE, LocalDate.now(),
                LocalTime.of(9, 0), LocalTime.of(9, 30), capacity, reserved);
    }

    private Reservation confirmedReservation(Long id, Long slotId, Long userId) {
        return new Reservation(id, slotId, userId, "alice",
                LocalDateTime.now(), ReservationStatus.CONFIRMED);
    }
}
