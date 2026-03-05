package com.carrefour.delivery.domain.port.out;

import com.carrefour.delivery.domain.model.DeliveryMode;
import com.carrefour.delivery.domain.model.TimeSlot;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Driven port: time slot persistence contract. */
public interface TimeSlotRepositoryPort {
    List<TimeSlot> findByModeAndDate(DeliveryMode mode, LocalDate date);
    Optional<TimeSlot> findById(Long id);
    /** Retrieves a slot with a PESSIMISTIC_WRITE lock for concurrent booking safety. */
    Optional<TimeSlot> findByIdWithLock(Long id);
    List<TimeSlot> saveAll(List<TimeSlot> slots);
    TimeSlot save(TimeSlot slot);
}
