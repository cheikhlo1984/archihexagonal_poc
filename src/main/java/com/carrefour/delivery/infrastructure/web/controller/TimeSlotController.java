package com.carrefour.delivery.infrastructure.web.controller;

import com.carrefour.delivery.domain.model.DeliveryMode;
import com.carrefour.delivery.domain.model.TimeSlot;
import com.carrefour.delivery.domain.port.in.GetTimeSlotsUseCase;
import com.carrefour.delivery.infrastructure.web.dto.TimeSlotResponse;
import com.carrefour.delivery.infrastructure.web.mapper.TimeSlotWebMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/slots")
public class TimeSlotController {

    private final GetTimeSlotsUseCase useCase;
    private final TimeSlotWebMapper mapper;

    public TimeSlotController(GetTimeSlotsUseCase useCase, TimeSlotWebMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    /**
     * GET /api/v1/slots?mode=DRIVE&date=2026-02-25
     * For DELIVERY_ASAP the date parameter is ignored.
     */
    @GetMapping
    public CollectionModel<EntityModel<TimeSlotResponse>> getSlots(
            @RequestParam DeliveryMode mode,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate effectiveDate = (mode == DeliveryMode.DELIVERY_ASAP || date == null)
                ? LocalDate.now()
                : date;

        List<TimeSlot> slots = useCase.getSlots(mode, effectiveDate);

        List<EntityModel<TimeSlotResponse>> slotModels = slots.stream()
                .map(slot -> EntityModel.of(
                        mapper.toResponse(slot),
                        linkTo(methodOn(TimeSlotController.class).getSlotById(slot.getId())).withSelfRel(),
                        linkTo(methodOn(ReservationController.class).createReservation(null, null))
                                .withRel("reserve")
                ))
                .toList();

        return CollectionModel.of(
                slotModels,
                linkTo(methodOn(TimeSlotController.class).getSlots(mode, effectiveDate)).withSelfRel()
        );
    }

    /** GET /api/v1/slots/{id} */
    @GetMapping("/{id}")
    public EntityModel<TimeSlotResponse> getSlotById(@PathVariable Long id) {
        TimeSlot slot = useCase.getSlotById(id);
        return EntityModel.of(
                mapper.toResponse(slot),
                linkTo(methodOn(TimeSlotController.class).getSlotById(id)).withSelfRel(),
                linkTo(methodOn(ReservationController.class).createReservation(null, null))
                        .withRel("reserve")
        );
    }
}
