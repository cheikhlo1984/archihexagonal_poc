package com.carrefour.delivery.domain.port.in;

import com.carrefour.delivery.domain.model.User;

/** Driving port: register a new user. */
public interface RegisterUserUseCase {
    User register(String username, String email, String rawPassword);
}
