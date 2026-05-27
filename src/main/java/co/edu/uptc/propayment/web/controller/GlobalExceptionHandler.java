package co.edu.uptc.propayment.web.controller;

import co.edu.uptc.propayment.exceptions.CompanyNotFoundException;
import co.edu.uptc.propayment.exceptions.InvalidCardException;
import co.edu.uptc.propayment.exceptions.InvalidTransactionException;
import co.edu.uptc.propayment.web.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCompanyNotFound(
            CompanyNotFoundException ex, WebRequest request) {
        log.error("Error de autenticación: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "COMPANY_NOT_FOUND",
                "La empresa con la API key proporcionada no existe",
                HttpStatus.UNAUTHORIZED.value(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(errorResponse);
    }

    @ExceptionHandler(InvalidTransactionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransaction(
            InvalidTransactionException ex, WebRequest request) {
        log.error("Transacción inválida: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_TRANSACTION",
                String.format("Campo inválido: %s", ex.getMessage()),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(InvalidCardException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCard(
            InvalidCardException ex, WebRequest request) {
        log.error("Tarjeta inválida: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                "INVALID_CARD",
                String.format("Validación de tarjeta fallida: %s", ex.getMessage()),
                HttpStatus.BAD_REQUEST.value(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Error no controlado: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Ocurrió un error interno. Por favor intenta más tarde.",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}