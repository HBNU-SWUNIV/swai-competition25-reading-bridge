package com.dyslexia.dyslexia.exception;

import com.dyslexia.dyslexia.exception.notfound.StudentNotFoundException;
import com.dyslexia.dyslexia.exception.notfound.TeacherNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  private ResponseEntity<ApiErrorResponse> buildErrorResponse(HttpStatus status, String message) {
    ApiErrorResponse error = ApiErrorResponse.builder()
        .status(status.value())
        .error(status.getReasonPhrase())
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
    return ResponseEntity.status(status).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
    StringBuilder sb = new StringBuilder();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      sb.append(error.getField()).append(": ").append(error.getDefaultMessage()).append("; ");
    }
    log.warn("Validation failed: {}", sb);
    return buildErrorResponse(HttpStatus.BAD_REQUEST, sb.toString());
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ApiErrorResponse> handleStatus(ResponseStatusException ex) {
    log.warn("StatusException: {}", ex.getMessage());
    return buildErrorResponse((HttpStatus) ex.getStatusCode(), ex.getReason());
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ApiErrorResponse> handleRuntime(RuntimeException ex) {
    log.error("Unhandled RuntimeException", ex);
    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
  }

  @ExceptionHandler(IOException.class)
  public ResponseEntity<ApiErrorResponse> handleIO(IOException ex) {
    log.error("IO Exception", ex);
    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex) {
    log.error("Unexpected Exception", ex);
    return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다.");
  }

  @ExceptionHandler(TeacherNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleTeacherNotFoundException(
      TeacherNotFoundException ex) {
    log.warn("Teacher not found: {}", ex.getMessage());
    return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(StudentNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleStudentNotFoundException(
      StudentNotFoundException ex) {
    log.warn("Student not found: {}", ex.getMessage());
    return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<ApiErrorResponse> handleUserAlreadyExistsException(
      UserAlreadyExistsException ex) {
    log.warn("Teacher already exists: {}", ex.getMessage());
    return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage());
  }
}