package com.carrefour.delivery.infrastructure.web.mapper;

import com.carrefour.delivery.domain.model.TimeSlot;
import com.carrefour.delivery.infrastructure.web.dto.TimeSlotResponse;
import org.springframework.stereotype.Component;

@Component
public class TimeSlotWebMapper {

    public TimeSlotResponse toResponse(TimeSlot slot) {
        return new TimeSlotResponse(
                slot.getId(),
                slot.getDeliveryMode().name(),
                slot.getDate(),
                slot.getStartTime(),
                slot.getEndTime(),
                slot.getCapacity(),
                slot.getReservedCount(),
                slot.getAvailableCapacity(),
                slot.isAvailable()
        );
    }
}
