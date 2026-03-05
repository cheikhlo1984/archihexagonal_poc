package com.carrefour.delivery.infrastructure.persistence.mapper;

import com.carrefour.delivery.domain.model.Reservation;
import com.carrefour.delivery.infrastructure.persistence.entity.ReservationEntity;
import org.springframework.stereotype.Component;

@Component
public class ReservationEntityMapper {

    public Reservation toDomain(ReservationEntity entity) {
        return new Reservation(
                entity.getId(),
                entity.getTimeSlotId(),
                entity.getUserId(),
                entity.getUsername(),
                entity.getCreatedAt(),
                entity.getStatus()
        );
    }

    public ReservationEntity toEntity(Reservation domain) {
        ReservationEntity entity = new ReservationEntity(
                domain.getTimeSlotId(),
                domain.getUserId(),
                domain.getUsername(),
                domain.getCreatedAt(),
                domain.getStatus()
        );
        entity.setId(domain.getId());
        return entity;
    }
}
