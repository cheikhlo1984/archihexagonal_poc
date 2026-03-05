package com.carrefour.delivery.domain.model;

import java.time.Duration;
import java.time.LocalTime;

/**
 * Delivery modes with their business rules embedded.
 *
 * | Mode           | Duration | Hours        | Booking window | Capacity |
 * |----------------|----------|--------------|----------------|----------|
 * | DRIVE          | 30 min   | 08:00–20:00  | Today + 7 days | 10       |
 * | DELIVERY       | 2h       | 08:00–22:00  | Today + 7 days | 5        |
 * | DELIVERY_TODAY | 2h       | 10:00–22:00  | Today only     | 3        |
 * | DELIVERY_ASAP  | 1h       | 08:00–22:00  | Next 3 slots   | 2        |
 */
public enum DeliveryMode {

    DRIVE(
            Duration.ofMinutes(30),
            LocalTime.of(8, 0),
            LocalTime.of(20, 0),
            7,
            10
    ),
    DELIVERY(
            Duration.ofHours(2),
            LocalTime.of(8, 0),
            LocalTime.of(22, 0),
            7,
            5
    ),
    DELIVERY_TODAY(
            Duration.ofHours(2),
            LocalTime.of(10, 0),
            LocalTime.of(22, 0),
            0,
            3
    ),
    DELIVERY_ASAP(
            Duration.ofHours(1),
            LocalTime.of(8, 0),
            LocalTime.of(22, 0),
            0,
            2
    );

    private final Duration slotDuration;
    private final LocalTime startTime;
    private final LocalTime endTime;
    /** Maximum number of days ahead from today that can be booked (0 = today only). */
    private final int maxDaysAhead;
    private final int capacity;

    DeliveryMode(Duration slotDuration, LocalTime startTime, LocalTime endTime,
                 int maxDaysAhead, int capacity) {
        this.slotDuration = slotDuration;
        this.startTime = startTime;
        this.endTime = endTime;
        this.maxDaysAhead = maxDaysAhead;
        this.capacity = capacity;
    }

    public Duration getSlotDuration() { return slotDuration; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public int getMaxDaysAhead() { return maxDaysAhead; }
    public int getCapacity() { return capacity; }
}
