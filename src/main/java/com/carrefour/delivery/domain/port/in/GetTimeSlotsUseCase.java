package com.carrefour.delivery.domain.port.in;

import com.carrefour.delivery.domain.model.DeliveryMode;
import com.carrefour.delivery.domain.model.TimeSlot;

import java.time.LocalDate;
import java.util.List;

/** Driving port: retrieve available time slots. */
public interface GetTimeSlotsUseCase {

    /**
     * Returns slots for the given mode and date.
     * Slots are generated lazily on first access.
     * For DELIVERY_ASAP the date is ignored and the next 3 slots from now are returned.
     */
    List<TimeSlot> getSlots(DeliveryMode mode, LocalDate date);

    TimeSlot getSlotById(Long id);
}
