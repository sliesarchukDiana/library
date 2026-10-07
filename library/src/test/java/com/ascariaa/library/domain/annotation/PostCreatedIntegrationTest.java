package com.ascariaa.library.domain.annotation;

import com.ascariaa.library.domain.annotation.PostCreated;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PostCreatedIntegrationTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController()).build();
    }

    @Test
    void postCreated_routesPostRequestAndReturns201Created() throws Exception {
        mockMvc.perform(post("/api/test-annotation"))
                .andExpect(status().isCreated())
                .andExpect(content().string("created"));
    }

    @Test
    void postCreated_rejectsGetRequest() throws Exception {
        mockMvc.perform(get("/api/test-annotation"))
                .andExpect(status().isMethodNotAllowed());
    }

    @RestController
    public static class TestController {
        @PostCreated("/api/test-annotation")
        public String createResource() {
            return "created";
        }
    }
}