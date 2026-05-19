package com.codeit.findex.global.exception;

import com.codeit.findex.global.common.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;
import java.time.Instant;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    log.error("IllegalArgumentException: {}", e.getMessage());
    ErrorResponse errorResponse = new ErrorResponse(
        Instant.now().toString(),
        HttpStatus.BAD_REQUEST.value(),
        "IllegalArgumentException",
        e.getMessage()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
    log.error("AccessDeniedException: {}", e.getMessage());
    ErrorResponse errorResponse = new ErrorResponse(
        Instant.now().toString(),
        HttpStatus.FORBIDDEN.value(),
        HttpStatus.FORBIDDEN.getReasonPhrase(),
        e.getMessage()
    );
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException e) {
    log.error("NoSuchElementException: {}", e.getMessage());
    ErrorResponse errorResponse = new ErrorResponse(
        Instant.now().toString(),
        HttpStatus.NOT_FOUND.value(),
        HttpStatus.NOT_FOUND.getReasonPhrase(),
        e.getMessage()
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
    log.error("EntityNotFoundException: {}", e.getMessage());
    ErrorResponse errorResponse = new ErrorResponse(
        Instant.now().toString(),
        HttpStatus.NOT_FOUND.value(),
        "EntityNotFoundException",
        e.getMessage()
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(errorResponse);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
    log.error("IllegalStateException: {}", e.getMessage());
    ErrorResponse errorResponse = new ErrorResponse(
        Instant.now().toString(),
        HttpStatus.BAD_REQUEST.value(),
        "IllegalStateException",
        e.getMessage()
    );
    return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("Exception: {}", e.getMessage());
    ErrorResponse errorResponse = new ErrorResponse(
        Instant.now().toString(),
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
        e.getMessage()
    );
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
