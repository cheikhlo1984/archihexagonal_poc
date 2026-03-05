package com.carrefour.delivery.domain.exception;

public class TimeSlotNotFoundException extends RuntimeException {
    public TimeSlotNotFoundException(Long id) {
        super("Time slot not found: " + id);
    }
}
