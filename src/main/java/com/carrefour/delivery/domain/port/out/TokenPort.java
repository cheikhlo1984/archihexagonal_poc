package com.carrefour.delivery.domain.port.out;

/** Driven port: token generation / validation contract.
 *  Implemented in infrastructure by JwtService. */
public interface TokenPort {
    String generateToken(String username);
    String extractUsername(String token);
    boolean isTokenValid(String token, String username);
}
