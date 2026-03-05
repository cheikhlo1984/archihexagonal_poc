package com.carrefour.delivery.domain.port.in;

import com.carrefour.delivery.domain.model.DeliveryMode;

import java.util.List;

/** Driving port: list all available delivery modes. */
public interface GetDeliveryModesUseCase {
    List<DeliveryMode> getDeliveryModes();
}
