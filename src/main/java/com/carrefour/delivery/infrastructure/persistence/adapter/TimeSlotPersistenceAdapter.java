package com.carrefour.delivery.infrastructure.persistence.adapter;

import com.carrefour.delivery.domain.model.DeliveryMode;
import com.carrefour.delivery.domain.model.TimeSlot;
import com.carrefour.delivery.domain.port.out.TimeSlotRepositoryPort;
import com.carrefour.delivery.infrastructure.persistence.entity.TimeSlotEntity;
import com.carrefour.delivery.infrastructure.persistence.mapper.TimeSlotEntityMapper;
import com.carrefour.delivery.infrastructure.persistence.repository.TimeSlotJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Driven adapter: bridges the domain TimeSlotRepositoryPort to JPA. */
@Component
public class TimeSlotPersistenceAdapter implements TimeSlotRepositoryPort {

    private final TimeSlotJpaRepository jpaRepository;
    private final TimeSlotEntityMapper mapper;

    public TimeSlotPersistenceAdapter(TimeSlotJpaRepository jpaRepository,
                                      TimeSlotEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<TimeSlot> findByModeAndDate(DeliveryMode mode, LocalDate date) {
        return jpaRepository.findByDeliveryModeAndDate(mode, date)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<TimeSlot> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<TimeSlot> findByIdWithLock(Long id) {
        return jpaRepository.findByIdWithLock(id).map(mapper::toDomain);
    }

    @Override
    public List<TimeSlot> saveAll(List<TimeSlot> slots) {
        List<TimeSlotEntity> entities = slots.stream().map(mapper::toEntity).toList();
        return jpaRepository.saveAll(entities).stream().map(mapper::toDomain).toList();
    }

    @Override
    public TimeSlot save(TimeSlot slot) {
        TimeSlotEntity entity = mapper.toEntity(slot);
        return mapper.toDomain(jpaRepository.save(entity));
    }
}
