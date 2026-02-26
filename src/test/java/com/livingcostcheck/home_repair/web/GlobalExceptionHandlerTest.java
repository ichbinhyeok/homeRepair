package com.livingcostcheck.home_repair.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ThrowingController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .setSingleView(new MappingJackson2JsonView())
                .build();
    }

    @Test
    void shouldPreserveNotFoundStatusForResponseStatusException() throws Exception {
        mockMvc.perform(get("/_test/not-found"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnInternalServerErrorForGenericException() throws Exception {
        mockMvc.perform(get("/_test/error"))
                .andExpect(status().isInternalServerError());
    }

    @Controller
    static class ThrowingController {

        @GetMapping("/_test/not-found")
        public void notFound() {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        @GetMapping("/_test/error")
        public void error() {
            throw new IllegalStateException("boom");
        }
    }
}
