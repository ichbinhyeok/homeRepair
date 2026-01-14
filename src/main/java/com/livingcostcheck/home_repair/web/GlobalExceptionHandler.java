package com.livingcostcheck.home_repair.web;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        // Log the exception (in a real app, use a Logger)
        e.printStackTrace();

        // Add error details to the model
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again.");

        // Return a generic error page (create error.jte if needed, or use a default)
        // For MVP, we can redirect to home or show a simple message
        return "error";
    }
}
