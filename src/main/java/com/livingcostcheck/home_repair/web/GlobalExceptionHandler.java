package com.livingcostcheck.home_repair.web;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public String handleResponseStatusException(ResponseStatusException e, Model model, HttpServletResponse response) {
        response.setStatus(e.getStatusCode().value());
        model.addAttribute("errorMessage", e.getReason() != null ? e.getReason() : "Requested resource not found.");
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model, HttpServletResponse response) {
        // Log the exception (in a real app, use a Logger)
        e.printStackTrace();

        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

        // Add error details to the model
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");

        // Return a generic error page (create error.jte if needed, or use a default)
        // For MVP, we can redirect to home or show a simple message
        return "error";
    }
}
