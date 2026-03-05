package com.carrefour.delivery.domain.service;

import com.carrefour.delivery.domain.exception.InvalidDateRangeException;
import com.carrefour.delivery.domain.exception.TimeSlotNotFoundException;
import com.carrefour.delivery.domain.model.DeliveryMode;
import com.carrefour.delivery.domain.model.TimeSlot;
import com.carrefour.delivery.domain.port.in.GetDeliveryModesUseCase;
import com.carrefour.delivery.domain.port.in.GetTimeSlotsUseCase;
import com.carrefour.delivery.domain.port.out.TimeSlotRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Domain service responsible for:
 * - Listing delivery modes
 * - Retrieving (and lazily generating) time slots per mode/date
 */
@Service
public class TimeSlotDomainService implements GetTimeSlotsUseCase, GetDeliveryModesUseCase {

    private final TimeSlotRepositoryPort timeSlotRepo;

    public TimeSlotDomainService(TimeSlotRepositoryPort timeSlotRepo) {
        this.timeSlotRepo = timeSlotRepo;
    }

    // ---- GetDeliveryModesUseCase ----

    @Override
    public List<DeliveryMode> getDeliveryModes() {
        return Arrays.asList(DeliveryMode.values());
    }

    // ---- GetTimeSlotsUseCase ----

    @Override
    @Transactional
    public List<TimeSlot> getSlots(DeliveryMode mode, LocalDate date) {
        LocalDate effectiveDate = (mode == DeliveryMode.DELIVERY_ASAP) ? LocalDate.now() : date;
        validateDateForMode(mode, effectiveDate);

        List<TimeSlot> existing = timeSlotRepo.findByModeAndDate(mode, effectiveDate);
        List<TimeSlot> slots = existing.isEmpty()
                ? timeSlotRepo.saveAll(generateSlotsForDate(mode, effectiveDate))
                : existing;

        return (mode == DeliveryMode.DELIVERY_ASAP) ? filterAsapSlots(slots) : slots;
    }

    @Override
    @Transactional(readOnly = true)
    public TimeSlot getSlotById(Long id) {
        return timeSlotRepo.findById(id)
                .orElseThrow(() -> new TimeSlotNotFoundException(id));
    }

    // ---- Private helpers ----

    private void validateDateForMode(DeliveryMode mode, LocalDate date) {
        LocalDate today = LocalDate.now();
        LocalDate maxDate = today.plusDays(mode.getMaxDaysAhead());
        if (date.isBefore(today) || date.isAfter(maxDate)) {
            throw new InvalidDateRangeException(
                    "Date " + date + " is not valid for mode " + mode +
                    ". Allowed range: " + today + " – " + maxDate
            );
        }
    }

    /**
     * Generates all possible slots for the given mode and date.
     * Uses seconds-of-day comparison to avoid LocalTime midnight wrap-around.
     */
    private List<TimeSlot> generateSlotsForDate(DeliveryMode mode, LocalDate date) {
        List<TimeSlot> slots = new ArrayList<>();
        long durationSeconds = mode.getSlotDuration().toSeconds();
        long endSeconds = mode.getEndTime().toSecondOfDay();
        LocalTime current = mode.getStartTime();

        while (current.toSecondOfDay() + durationSeconds <= endSeconds) {
            LocalTime slotEnd = current.plusSeconds(durationSeconds);
            slots.add(new TimeSlot(null, mode, date, current, slotEnd, mode.getCapacity(), 0));
            current = slotEnd;
        }
        return slots;
    }

    /** For DELIVERY_ASAP: return only the next 3 upcoming slots from now. */
    private List<TimeSlot> filterAsapSlots(List<TimeSlot> slots) {
        LocalTime now = LocalTime.now();
        return slots.stream()
                .filter(s -> s.getStartTime().isAfter(now))
                .limit(3)
                .toList();
    }
}
