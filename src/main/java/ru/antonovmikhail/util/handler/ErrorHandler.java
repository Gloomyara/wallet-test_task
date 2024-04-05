package ru.antonovmikhail.util.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.antonovmikhail.util.InsufficientAmountException;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    private final String incorrectParamsMsg = "Incorrect request params.";

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleModelFieldsConstraint(HttpServletRequest request,
                                                                     final MethodArgumentNotValidException e) {
        StringBuilder errors = new StringBuilder();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.append("Field: ")
                    .append(error.getField())
                    .append(": ")
                    .append("Error: ")
                    .append(error.getDefaultMessage())
                    .append(", ");
        }
        errors.deleteCharAt(errors.length() - 2);
        return buildErrorResponse(request,
                errors.toString(),
                incorrectParamsMsg,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(HttpServletRequest request,
                                                          final Exception e) {
        return buildErrorResponse(request,
                e.getMessage(),
                incorrectParamsMsg,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(HttpServletRequest request,
                                                        final RuntimeException e) {
        return buildErrorResponse(request,
                e.getMessage(),
                "Entity not found.",
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            DataIntegrityViolationException.class,
            ConstraintViolationException.class,
            IllegalStateException.class,
            MissingPathVariableException.class,
            InsufficientAmountException.class,
            RuntimeException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(HttpServletRequest request,
                                                          final RuntimeException e) {
        return buildErrorResponse(request, e.getMessage(), incorrectParamsMsg,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleThrowable(HttpServletRequest request,
                                                         final Throwable e) {
        return buildErrorResponse(request,
                e.getMessage(),
                "Internal Server Error occurred during request processing.",
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpServletRequest request,
                                                             String message,
                                                             String reason,
                                                             HttpStatus httpStatus) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(httpStatus)
                .reason(reason)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
        log.error("{} {} {}", request.getMethod(), request.getRequestURI(), errorResponse);
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
}
