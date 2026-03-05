package com.carrefour.delivery.infrastructure.persistence.mapper;

import com.carrefour.delivery.domain.model.TimeSlot;
import com.carrefour.delivery.infrastructure.persistence.entity.TimeSlotEntity;
import org.springframework.stereotype.Component;

@Component
public class TimeSlotEntityMapper {

    public TimeSlot toDomain(TimeSlotEntity entity) {
        return new TimeSlot(
                entity.getId(),
                entity.getDeliveryMode(),
                entity.getDate(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getCapacity(),
                entity.getReservedCount()
        );
    }

    public TimeSlotEntity toEntity(TimeSlot domain) {
        TimeSlotEntity entity = new TimeSlotEntity(
                domain.getDeliveryMode(),
                domain.getDate(),
                domain.getStartTime(),
                domain.getEndTime(),
                domain.getCapacity(),
                domain.getReservedCount()
        );
        entity.setId(domain.getId());
        return entity;
    }
}
