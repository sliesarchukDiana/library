package com.ascariaa.library.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8080/realms/test"})
@AutoConfigureMockMvc
@Testcontainers
class AsyncIntegrationTest {

    @Container
    @ServiceConnection
    protected static final org.testcontainers.postgresql.PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17");

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private AsyncLibraryService asyncLibraryService;

    @Test
    @WithMockUser
    void whenFetchingCard_ReturnsCombinedResult() throws Exception {
        long startTime = System.currentTimeMillis();

        MvcResult result = mockMvc.perform(get("/api/books/async/1/card/1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value("Rating was received"));

        long duration = System.currentTimeMillis() - startTime;

        assertTrue(duration < 1800, duration + "ms");
    }

    @Test
    @WithMockUser
    void whenExternalApiFails_FallbackIsUsed() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/books/async/999/card/1"))
                .andExpect(request().asyncStarted())
                .andReturn();

        mockMvc.perform(asyncDispatch(result))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rating").value("Rating is currently unavailable."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void whenOverloaded_ReturnsTooManyRequests() throws Exception {
        boolean isRejected = false;
        for (int i = 0; i < 10; i++) {
            int status = mockMvc.perform(post("/api/books/async/notify/1")
                            .param("email", "test@example.com"))
                    .andReturn()
                    .getResponse()
                    .getStatus();

            if (status == 429) {
                isRejected = true;
                break;
            }
        }

        assertTrue(isRejected);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void sendsEmail_ExpectsStatusAccepted() throws Exception {
        mockMvc.perform(post("/api/books/async/notify/1")
                        .param("email", ""))
                .andExpect(status().isAccepted());
    }
    }