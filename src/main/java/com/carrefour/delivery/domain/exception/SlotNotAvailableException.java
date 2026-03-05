package com.carrefour.delivery.domain.exception;

public class SlotNotAvailableException extends RuntimeException {
    public SlotNotAvailableException(Long slotId) {
        super("Time slot " + slotId + " has no available capacity");
    }
}
