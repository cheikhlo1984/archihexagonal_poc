package com.carrefour.delivery.infrastructure.web.controller;

import com.carrefour.delivery.domain.model.Reservation;
import com.carrefour.delivery.domain.port.in.CancelReservationUseCase;
import com.carrefour.delivery.domain.port.in.CreateReservationUseCase;
import com.carrefour.delivery.domain.port.in.GetReservationsUseCase;
import com.carrefour.delivery.domain.port.out.UserRepositoryPort;
import com.carrefour.delivery.infrastructure.web.dto.CreateReservationRequest;
import com.carrefour.delivery.infrastructure.web.dto.ReservationResponse;
import com.carrefour.delivery.infrastructure.web.mapper.ReservationWebMapper;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {

    private final CreateReservationUseCase createUseCase;
    private final GetReservationsUseCase getUseCase;
    private final CancelReservationUseCase cancelUseCase;
    private final UserRepositoryPort userRepo;
    private final ReservationWebMapper mapper;

    public ReservationController(CreateReservationUseCase createUseCase,
                                  GetReservationsUseCase getUseCase,
                                  CancelReservationUseCase cancelUseCase,
                                  UserRepositoryPort userRepo,
                                  ReservationWebMapper mapper) {
        this.createUseCase = createUseCase;
        this.getUseCase = getUseCase;
        this.cancelUseCase = cancelUseCase;
        this.userRepo = userRepo;
        this.mapper = mapper;
    }

    /** POST /api/v1/reservations */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<ReservationResponse> createReservation(
            @Valid @RequestBody CreateReservationRequest request,
            @AuthenticationPrincipal UserDetails principal) {

        Long userId = resolveUserId(principal);
        Reservation reservation = createUseCase.createReservation(
                request.slotId(), userId, principal.getUsername());

        return toEntityModel(reservation);
    }

    /** GET /api/v1/reservations (current user's reservations) */
    @GetMapping
    public CollectionModel<EntityModel<ReservationResponse>> getMyReservations(
            @AuthenticationPrincipal UserDetails principal) {

        Long userId = resolveUserId(principal);
        List<Reservation> reservations = getUseCase.getReservationsByUser(userId);

        List<EntityModel<ReservationResponse>> models = reservations.stream()
                .map(this::toEntityModel)
                .toList();

        return CollectionModel.of(
                models,
                linkTo(methodOn(ReservationController.class).getMyReservations(null)).withSelfRel()
        );
    }

    /** GET /api/v1/reservations/{id} */
    @GetMapping("/{id}")
    public EntityModel<ReservationResponse> getReservationById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {

        Reservation reservation = getUseCase.getReservationById(id);
        return toEntityModel(reservation);
    }

    /** DELETE /api/v1/reservations/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails principal) {

        Long userId = resolveUserId(principal);
        cancelUseCase.cancelReservation(id, userId);
        return ResponseEntity.noContent().build();
    }

    // ---- Helpers ----

    private Long resolveUserId(UserDetails principal) {
        return userRepo.findByUsername(principal.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"))
                .getId();
    }

    private EntityModel<ReservationResponse> toEntityModel(Reservation reservation) {
        return EntityModel.of(
                mapper.toResponse(reservation),
                linkTo(methodOn(ReservationController.class)
                        .getReservationById(reservation.getId(), null)).withSelfRel(),
                linkTo(methodOn(ReservationController.class)
                        .cancelReservation(reservation.getId(), null)).withRel("cancel"),
                linkTo(methodOn(TimeSlotController.class)
                        .getSlotById(reservation.getTimeSlotId())).withRel("slot")
        );
    }
}
