package com.carrefour.delivery.domain.port.in;

/** Driving port: authenticate a user and return a JWT token. */
public interface AuthenticateUserUseCase {
    String authenticate(String username, String rawPassword);
}
