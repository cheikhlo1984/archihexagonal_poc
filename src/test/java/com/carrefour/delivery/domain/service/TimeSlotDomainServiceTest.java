package com.carrefour.delivery.domain.service;

import com.carrefour.delivery.domain.exception.InvalidDateRangeException;
import com.carrefour.delivery.domain.model.DeliveryMode;
import com.carrefour.delivery.domain.model.TimeSlot;
import com.carrefour.delivery.domain.port.out.TimeSlotRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TimeSlotDomainServiceTest {

    @Mock
    private TimeSlotRepositoryPort timeSlotRepo;

    @InjectMocks
    private TimeSlotDomainService service;

    private final LocalDate today = LocalDate.now();

    @BeforeEach
    void setUp() {
        // Ensure saveAll returns the provided slots with IDs assigned
        when(timeSlotRepo.saveAll(anyList()))
                .thenAnswer(invocation -> {
                    List<TimeSlot> slots = invocation.getArgument(0);
                    long id = 1L;
                    for (TimeSlot slot : slots) {
                        slot.setId(id++);
                    }
                    return slots;
                });
    }

    // ---- DRIVE slot generation ----

    @Test
    void drive_generatesCorrectNumberOfSlots() {
        when(timeSlotRepo.findByModeAndDate(eq(DeliveryMode.DRIVE), eq(today)))
                .thenReturn(Collections.emptyList());

        List<TimeSlot> slots = service.getSlots(DeliveryMode.DRIVE, today);

        // 08:00–20:00 / 30 min = 24 slots
        assertThat(slots).hasSize(24);
        assertThat(slots.get(0).getStartTime().getHour()).isEqualTo(8);
        assertThat(slots.get(0).getEndTime().getHour()).isEqualTo(8);
        assertThat(slots.get(0).getEndTime().getMinute()).isEqualTo(30);
        assertThat(slots.get(23).getStartTime().getHour()).isEqualTo(19);
        assertThat(slots.get(23).getStartTime().getMinute()).isEqualTo(30);
    }

    @Test
    void drive_slotsHaveCorrectCapacity() {
        when(timeSlotRepo.findByModeAndDate(eq(DeliveryMode.DRIVE), eq(today)))
                .thenReturn(Collections.emptyList());

        List<TimeSlot> slots = service.getSlots(DeliveryMode.DRIVE, today);

        assertThat(slots).allMatch(s -> s.getCapacity() == DeliveryMode.DRIVE.getCapacity());
    }

    // ---- DELIVERY slot generation ----

    @Test
    void delivery_generates7SlotsPerDay() {
        when(timeSlotRepo.findByModeAndDate(eq(DeliveryMode.DELIVERY), eq(today)))
                .thenReturn(Collections.emptyList());

        List<TimeSlot> slots = service.getSlots(DeliveryMode.DELIVERY, today);

        // 08:00–22:00 / 2h = 7 slots
        assertThat(slots).hasSize(7);
    }

    // ---- DELIVERY_TODAY slot generation ----

    @Test
    void deliveryToday_generates6SlotsForToday() {
        when(timeSlotRepo.findByModeAndDate(eq(DeliveryMode.DELIVERY_TODAY), eq(today)))
                .thenReturn(Collections.emptyList());

        List<TimeSlot> slots = service.getSlots(DeliveryMode.DELIVERY_TODAY, today);

        // 10:00–22:00 / 2h = 6 slots
        assertThat(slots).hasSize(6);
        assertThat(slots.get(0).getStartTime().getHour()).isEqualTo(10);
    }

    // ---- Lazy generation ----

    @Test
    void slotsAreNotRegeneratedWhenAlreadyExist() {
        List<TimeSlot> existingSlots = List.of(
                new TimeSlot(1L, DeliveryMode.DRIVE, today, null, null, 10, 0));
        when(timeSlotRepo.findByModeAndDate(eq(DeliveryMode.DRIVE), eq(today)))
                .thenReturn(existingSlots);

        List<TimeSlot> result = service.getSlots(DeliveryMode.DRIVE, today);

        assertThat(result).isSameAs(existingSlots);
        verify(timeSlotRepo, never()).saveAll(anyList());
    }

    // ---- Date validation ----

    @Test
    void drive_rejectsDateBeyondBookingWindow() {
        LocalDate tooFar = today.plusDays(8);
        assertThatThrownBy(() -> service.getSlots(DeliveryMode.DRIVE, tooFar))
                .isInstanceOf(InvalidDateRangeException.class);
    }

    @Test
    void deliveryToday_rejectsTomorrow() {
        LocalDate tomorrow = today.plusDays(1);
        assertThatThrownBy(() -> service.getSlots(DeliveryMode.DELIVERY_TODAY, tomorrow))
                .isInstanceOf(InvalidDateRangeException.class);
    }

    @Test
    void drive_allowsTodayPlusSeven() {
        LocalDate validDate = today.plusDays(7);
        when(timeSlotRepo.findByModeAndDate(eq(DeliveryMode.DRIVE), eq(validDate)))
                .thenReturn(Collections.emptyList());

        assertThatCode(() -> service.getSlots(DeliveryMode.DRIVE, validDate))
                .doesNotThrowAnyException();
    }

    // ---- getSlotById ----

    @Test
    void getSlotById_returnsSlot() {
        TimeSlot slot = new TimeSlot(1L, DeliveryMode.DRIVE, today, null, null, 10, 0);
        when(timeSlotRepo.findById(1L)).thenReturn(Optional.of(slot));

        TimeSlot result = service.getSlotById(1L);

        assertThat(result).isEqualTo(slot);
    }

    // ---- Delivery modes list ----

    @Test
    void getDeliveryModes_returnsAllFourModes() {
        assertThat(service.getDeliveryModes()).hasSize(4);
    }
}
