package com.carrefour.delivery.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deliveryModes_isPublic() throws Exception {
        mockMvc.perform(get("/api/v1/delivery-modes"))
                .andExpect(status().isOk());
    }

    @Test
    void slots_requiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/slots")
                        .param("mode", "DRIVE")
                        .param("date", LocalDate.now().toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void reservations_requiresAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/reservations"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void invalidToken_returnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/v1/slots")
                        .param("mode", "DRIVE")
                        .param("date", LocalDate.now().toString())
                        .header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }
}
