package com.carrefour.delivery.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;

/** Domain entity representing a bookable time slot. Pure Java – no framework dependency. */
public class TimeSlot {

    private Long id;
    private DeliveryMode deliveryMode;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int capacity;
    private int reservedCount;

    public TimeSlot() {}

    public TimeSlot(Long id, DeliveryMode deliveryMode, LocalDate date,
                    LocalTime startTime, LocalTime endTime,
                    int capacity, int reservedCount) {
        this.id = id;
        this.deliveryMode = deliveryMode;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.reservedCount = reservedCount;
    }

    // ---- Business logic ----

    public boolean isAvailable() {
        return reservedCount < capacity;
    }

    public int getAvailableCapacity() {
        return capacity - reservedCount;
    }

    public void incrementReservedCount() {
        if (reservedCount >= capacity) {
            throw new IllegalStateException("Slot is already at full capacity");
        }
        this.reservedCount++;
    }

    public void decrementReservedCount() {
        if (this.reservedCount > 0) {
            this.reservedCount--;
        }
    }

    // ---- Getters / Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DeliveryMode getDeliveryMode() { return deliveryMode; }
    public void setDeliveryMode(DeliveryMode deliveryMode) { this.deliveryMode = deliveryMode; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getReservedCount() { return reservedCount; }
    public void setReservedCount(int reservedCount) { this.reservedCount = reservedCount; }
}
