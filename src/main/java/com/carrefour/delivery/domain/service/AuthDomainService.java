package com.carrefour.delivery.domain.service;

import com.carrefour.delivery.domain.exception.InvalidCredentialsException;
import com.carrefour.delivery.domain.exception.UserAlreadyExistsException;
import com.carrefour.delivery.domain.exception.UserNotFoundException;
import com.carrefour.delivery.domain.model.Role;
import com.carrefour.delivery.domain.model.User;
import com.carrefour.delivery.domain.port.in.AuthenticateUserUseCase;
import com.carrefour.delivery.domain.port.in.RegisterUserUseCase;
import com.carrefour.delivery.domain.port.out.TokenPort;
import com.carrefour.delivery.domain.port.out.UserRepositoryPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Domain service for registration and authentication.
 * Uses {@link TokenPort} (driven port) to keep JWT infrastructure out of the domain.
 * Uses Spring's PasswordEncoder (a pragmatic dependency on a well-known interface).
 */
@Service
public class AuthDomainService implements RegisterUserUseCase, AuthenticateUserUseCase {

    private final UserRepositoryPort userRepo;
    private final PasswordEncoder passwordEncoder;
    private final TokenPort tokenPort;

    public AuthDomainService(UserRepositoryPort userRepo,
                             PasswordEncoder passwordEncoder,
                             TokenPort tokenPort) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.tokenPort = tokenPort;
    }

    // ---- RegisterUserUseCase ----

    @Override
    @Transactional
    public User register(String username, String email, String rawPassword) {
        if (userRepo.existsByUsername(username)) {
            throw new UserAlreadyExistsException("Username already taken: " + username);
        }
        if (userRepo.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already in use: " + email);
        }
        User user = new User(null, username, email,
                passwordEncoder.encode(rawPassword), Role.USER);
        return userRepo.save(user);
    }

    // ---- AuthenticateUserUseCase ----

    @Override
    @Transactional(readOnly = true)
    public String authenticate(String username, String rawPassword) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
        return tokenPort.generateToken(username);
    }
}
