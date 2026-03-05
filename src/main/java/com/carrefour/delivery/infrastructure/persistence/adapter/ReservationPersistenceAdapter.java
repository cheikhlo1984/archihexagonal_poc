package com.carrefour.delivery.infrastructure.persistence.adapter;

import com.carrefour.delivery.domain.model.Reservation;
import com.carrefour.delivery.domain.port.out.ReservationRepositoryPort;
import com.carrefour.delivery.infrastructure.persistence.entity.ReservationEntity;
import com.carrefour.delivery.infrastructure.persistence.mapper.ReservationEntityMapper;
import com.carrefour.delivery.infrastructure.persistence.repository.ReservationJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/** Driven adapter: bridges the domain ReservationRepositoryPort to JPA. */
@Component
public class ReservationPersistenceAdapter implements ReservationRepositoryPort {

    private final ReservationJpaRepository jpaRepository;
    private final ReservationEntityMapper mapper;

    public ReservationPersistenceAdapter(ReservationJpaRepository jpaRepository,
                                         ReservationEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Reservation save(Reservation reservation) {
        ReservationEntity entity = mapper.toEntity(reservation);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Reservation> findByUserId(Long userId) {
        return jpaRepository.findByUserId(userId)
                .stream().map(mapper::toDomain).toList();
    }
}
