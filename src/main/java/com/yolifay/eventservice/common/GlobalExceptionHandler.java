package com.yolifay.eventservice.common;

import com.yolifay.eventservice.exception.BadRequestException;
import com.yolifay.eventservice.exception.ConflictException;
import com.yolifay.eventservice.exception.DataNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ConstantsProperties constantsProperties;

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ResponseApiService<Void>> handleDataNotFound(DataNotFoundException e) {
        log.warn("DataNotFound: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseApiUtil.setResponse(
                        HttpStatus.NOT_FOUND.value(),
                        constantsProperties.getServiceId(),
                        Constants.RESPONSE.ACCOUNT_NOT_FOUND,
                        null
                )
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseApiService<String>> handleBadRequest(BadRequestException e) {
        log.warn("BadRequest: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseApiUtil.setResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        constantsProperties.getServiceId(),
                        Constants.RESPONSE.BAD_REQUEST,
                        e.getMessage()
                )
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ResponseApiService<Void>> handleConflict(ConflictException e) {
        log.warn("Conflict: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ResponseApiUtil.setResponse(
                        HttpStatus.CONFLICT.value(),
                        constantsProperties.getServiceId(),
                        Constants.RESPONSE.DATA_EXISTS,
                        null
                )
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ResponseApiService<Void>> handleNoResource(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ResponseApiUtil.setResponse(
                        HttpStatus.NOT_FOUND.value(),
                        constantsProperties.getServiceId(),
                        Constants.RESPONSE.HTTP_NOT_FOUND,
                        null
                )
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseApiService<Void>> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                ResponseApiUtil.setResponse(
                        HttpStatus.METHOD_NOT_ALLOWED.value(),
                        constantsProperties.getServiceId(),
                        Constants.RESPONSE.METHOD_NOT_ALLOWED,
                        null
                )
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseApiService<String>> handleConstraintViolation(ConstraintViolationException e) {
        // ambil pesan pertama
        String msg = e.getConstraintViolations().stream()
                .findFirst().map(ConstraintViolation::getMessage).orElse("Validation error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ResponseApiUtil.setResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        constantsProperties.getServiceId(),
                        Constants.RESPONSE.BAD_REQUEST,
                        msg
                )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseApiService<Void>> handleOthers(Exception e) {
        log.error("Unhandled error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ResponseApiUtil.setResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        constantsProperties.getServiceId(),
                        Constants.RESPONSE.HTTP_INTERNAL_ERROR,
                        null
                )
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseApiService<String>> handleUpstream5xx(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
                ResponseApiUtil.setResponse(
                        HttpStatus.SERVICE_UNAVAILABLE.value(),
                        constantsProperties.getServiceId(),
                        Constants.RESPONSE.SERVICE_UNAVAILABLE,
                        "Upstream error/timeout"
                )
        );
    }


}