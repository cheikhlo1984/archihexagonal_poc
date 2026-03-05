package com.carrefour.delivery.domain.model;

import java.time.LocalDateTime;

/** Domain entity representing a slot reservation. Pure Java – no framework dependency. */
public class Reservation {

    private Long id;
    private Long timeSlotId;
    private Long userId;
    private String username;
    private LocalDateTime createdAt;
    private ReservationStatus status;

    public Reservation() {}

    public Reservation(Long id, Long timeSlotId, Long userId, String username,
                       LocalDateTime createdAt, ReservationStatus status) {
        this.id = id;
        this.timeSlotId = timeSlotId;
        this.userId = userId;
        this.username = username;
        this.createdAt = createdAt;
        this.status = status;
    }

    // ---- Getters / Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTimeSlotId() { return timeSlotId; }
    public void setTimeSlotId(Long timeSlotId) { this.timeSlotId = timeSlotId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
}
