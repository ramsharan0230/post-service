package com.video.processing.exceptions;

import java.io.IOException;
import java.util.logging.Logger;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.video.processing.utilities.ResponseFromApi;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = Logger.getLogger(GlobalExceptionHandler.class.getName());

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ResponseFromApi<Object>> handleResourceNotFoundException(ResourceNotFoundException exception){
        logger.info("hello from ResourceNotfound");
        
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseFromApi.error(exception.getMessage(), HttpStatus.NOT_FOUND.value()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseFromApi<Object>> handleRuntimeException(RuntimeException exception){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseFromApi.error(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value())); 
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ResponseFromApi<Object>> handleIOException(IOException exception){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseFromApi.error(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value())); 
    }
}
