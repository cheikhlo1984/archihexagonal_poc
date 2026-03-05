package com.carrefour.delivery.infrastructure.web.mapper;

import com.carrefour.delivery.domain.model.DeliveryMode;
import com.carrefour.delivery.infrastructure.web.dto.DeliveryModeResponse;
import org.springframework.stereotype.Component;

@Component
public class DeliveryModeWebMapper {

    public DeliveryModeResponse toResponse(DeliveryMode mode) {
        long hours = mode.getSlotDuration().toHours();
        long minutes = mode.getSlotDuration().toMinutesPart();
        String duration = hours > 0
                ? hours + "h" + (minutes > 0 ? minutes + "min" : "")
                : minutes + "min";

        return new DeliveryModeResponse(
                mode.name(),
                duration,
                mode.getStartTime().toString(),
                mode.getEndTime().toString(),
                mode.getMaxDaysAhead(),
                mode.getCapacity()
        );
    }
}
