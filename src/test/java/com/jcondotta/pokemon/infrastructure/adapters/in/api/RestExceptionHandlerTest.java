package com.jcondotta.pokemon.infrastructure.adapters.in.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RestExceptionHandlerTest.TestController.class)
@Import(RestExceptionHandler.class)
class RestExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    static class TestController {
        @GetMapping("/test-exception")
        public void throwException() {
            throw new RuntimeException("Simulated exception");
        }
    }

    @Test
    void shouldReturn500_whenUnhandledExceptionOccurs() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test-exception")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}

