package com.calendarugr.schedule_consumer_service.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.jsoup.HttpStatusException;

import com.calendarugr.schedule_consumer_service.dtos.ErrorResponseDTO;

import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponseDTO> handleIOException(IOException ex, WebRequest request) {
        return new ResponseEntity<>(
            new ErrorResponseDTO("IOException", "IO error occurred: " + ex.getMessage()),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(HttpStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleHttpStatusException(HttpStatusException ex, WebRequest request) {
        return new ResponseEntity<>(
            new ErrorResponseDTO("HttpStatusException", "HTTP error occurred: " + ex.getStatusCode()),
            HttpStatus.valueOf(ex.getStatusCode())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception ex, WebRequest request) {
        return new ResponseEntity<>(
            new ErrorResponseDTO("Exception", "An error occurred: " + ex.getMessage()),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}