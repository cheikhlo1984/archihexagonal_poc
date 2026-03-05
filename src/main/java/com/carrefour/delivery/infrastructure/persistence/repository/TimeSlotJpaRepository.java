package com.carrefour.delivery.infrastructure.persistence.repository;

import com.carrefour.delivery.domain.model.DeliveryMode;
import com.carrefour.delivery.infrastructure.persistence.entity.TimeSlotEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TimeSlotJpaRepository extends JpaRepository<TimeSlotEntity, Long> {

    List<TimeSlotEntity> findByDeliveryModeAndDate(DeliveryMode deliveryMode, LocalDate date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TimeSlotEntity t WHERE t.id = :id")
    Optional<TimeSlotEntity> findByIdWithLock(@Param("id") Long id);
}
