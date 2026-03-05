package com.carrefour.delivery.infrastructure.web.controller;

import com.carrefour.delivery.infrastructure.web.dto.CreateReservationRequest;
import com.carrefour.delivery.infrastructure.web.dto.LoginRequest;
import com.carrefour.delivery.infrastructure.web.dto.RegisterRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String jwtToken;
    private static Long reservationId;

    // ---- Auth ----

    @Test
    @Order(1)
    void register_returnsCreatedWithToken() throws Exception {
        RegisterRequest req = new RegisterRequest("testuser", "test@example.com", "password123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andReturn();

        jwtToken = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();
    }

    @Test
    @Order(2)
    void register_duplicateUsername_returnsConflict() throws Exception {
        RegisterRequest req = new RegisterRequest("testuser", "other@example.com", "password123");

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    void login_returnsToken() throws Exception {
        LoginRequest req = new LoginRequest("testuser", "password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @Order(4)
    void login_wrongPassword_returnsUnauthorized() throws Exception {
        LoginRequest req = new LoginRequest("testuser", "wrongpassword");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    // ---- Delivery modes (public) ----

    @Test
    @Order(5)
    void getDeliveryModes_isPublicAndReturns4Modes() throws Exception {
        mockMvc.perform(get("/api/v1/delivery-modes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.deliveryModeResponseList", hasSize(4)));
    }

    // ---- Time slots (requires JWT) ----

    @Test
    @Order(6)
    void getSlots_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/v1/slots")
                        .param("mode", "DRIVE")
                        .param("date", LocalDate.now().toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(7)
    void getDriveSlots_withToken_returns24Slots() throws Exception {
        mockMvc.perform(get("/api/v1/slots")
                        .param("mode", "DRIVE")
                        .param("date", LocalDate.now().toString())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.timeSlotResponseList", hasSize(24)));
    }

    @Test
    @Order(8)
    void getDeliverySlots_returns7Slots() throws Exception {
        mockMvc.perform(get("/api/v1/slots")
                        .param("mode", "DELIVERY")
                        .param("date", LocalDate.now().toString())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.timeSlotResponseList", hasSize(7)));
    }

    @Test
    @Order(9)
    void getDriveSlots_dateOutOfRange_returns400() throws Exception {
        mockMvc.perform(get("/api/v1/slots")
                        .param("mode", "DRIVE")
                        .param("date", LocalDate.now().plusDays(8).toString())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isBadRequest());
    }

    // ---- Reservations ----

    @Test
    @Order(10)
    void createReservation_succeeds() throws Exception {
        // Get slot id from first DRIVE slot
        MvcResult slotsResult = mockMvc.perform(get("/api/v1/slots")
                        .param("mode", "DRIVE")
                        .param("date", LocalDate.now().toString())
                        .header("Authorization", "Bearer " + jwtToken))
                .andReturn();

        JsonNode slotsJson = objectMapper.readTree(slotsResult.getResponse().getContentAsString());
        long slotId = slotsJson.at("/_embedded/timeSlotResponseList/0/id").asLong();

        CreateReservationRequest req = new CreateReservationRequest(slotId);

        MvcResult resResult = mockMvc.perform(post("/api/v1/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andReturn();

        reservationId = objectMapper.readTree(resResult.getResponse().getContentAsString())
                .get("id").asLong();
    }

    @Test
    @Order(11)
    void getMyReservations_returnsCreatedReservation() throws Exception {
        mockMvc.perform(get("/api/v1/reservations")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.reservationResponseList", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @Order(12)
    void cancelReservation_succeeds() throws Exception {
        mockMvc.perform(delete("/api/v1/reservations/" + reservationId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @Order(13)
    void cancelReservation_alreadyCancelled_returnsConflict() throws Exception {
        mockMvc.perform(delete("/api/v1/reservations/" + reservationId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isConflict());
    }
}
