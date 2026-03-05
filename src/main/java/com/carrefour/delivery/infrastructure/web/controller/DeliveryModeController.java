package com.carrefour.delivery.infrastructure.web.controller;

import com.carrefour.delivery.domain.model.DeliveryMode;
import com.carrefour.delivery.domain.port.in.GetDeliveryModesUseCase;
import com.carrefour.delivery.infrastructure.web.dto.DeliveryModeResponse;
import com.carrefour.delivery.infrastructure.web.mapper.DeliveryModeWebMapper;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/delivery-modes")
public class DeliveryModeController {

    private final GetDeliveryModesUseCase useCase;
    private final DeliveryModeWebMapper mapper;

    public DeliveryModeController(GetDeliveryModesUseCase useCase,
                                  DeliveryModeWebMapper mapper) {
        this.useCase = useCase;
        this.mapper = mapper;
    }

    @GetMapping
    public CollectionModel<EntityModel<DeliveryModeResponse>> getDeliveryModes() {
        List<DeliveryMode> modes = useCase.getDeliveryModes();

        List<EntityModel<DeliveryModeResponse>> modeModels = modes.stream()
                .map(mode -> EntityModel.of(
                        mapper.toResponse(mode),
                        linkTo(methodOn(DeliveryModeController.class).getDeliveryModes())
                                .slash(mode.name()).withSelfRel()
                ))
                .toList();

        return CollectionModel.of(
                modeModels,
                linkTo(methodOn(DeliveryModeController.class).getDeliveryModes()).withSelfRel()
        );
    }
}
