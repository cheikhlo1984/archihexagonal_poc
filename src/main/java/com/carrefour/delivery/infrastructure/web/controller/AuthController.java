package com.carrefour.delivery.infrastructure.web.controller;

import com.carrefour.delivery.domain.model.User;
import com.carrefour.delivery.domain.port.in.AuthenticateUserUseCase;
import com.carrefour.delivery.domain.port.in.RegisterUserUseCase;
import com.carrefour.delivery.infrastructure.web.dto.AuthResponse;
import com.carrefour.delivery.infrastructure.web.dto.LoginRequest;
import com.carrefour.delivery.infrastructure.web.dto.RegisterRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserUseCase registerUseCase;
    private final AuthenticateUserUseCase authenticateUseCase;

    public AuthController(RegisterUserUseCase registerUseCase,
                          AuthenticateUserUseCase authenticateUseCase) {
        this.registerUseCase = registerUseCase;
        this.authenticateUseCase = authenticateUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = registerUseCase.register(
                request.username(), request.email(), request.password());
        String token = authenticateUseCase.authenticate(request.username(), request.password());
        return new AuthResponse(token, user.getUsername());
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authenticateUseCase.authenticate(request.username(), request.password());
        return ResponseEntity.ok(new AuthResponse(token, request.username()));
    }
}
