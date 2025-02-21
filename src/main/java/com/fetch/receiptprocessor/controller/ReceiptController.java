package com.fetch.receiptprocessor.controller;

import com.fetch.receiptprocessor.dto.GetPointsResponse;
import com.fetch.receiptprocessor.dto.ProcessReceiptRequest;
import com.fetch.receiptprocessor.dto.ProcessReceiptResponse;
import com.fetch.receiptprocessor.service.ReceiptService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {
    public final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) { this.receiptService = receiptService; }

    @PostMapping(value = "/process", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProcessReceiptResponse> processReceipt(@Valid @RequestBody ProcessReceiptRequest request) throws BadRequestException {
        Optional<ProcessReceiptResponse> response = receiptService.saveReceipt(request);
        if(response.isPresent()) {
            return ResponseEntity.ok(response.get());
        } else {
            throw new BadRequestException();
        }
    }

    @GetMapping(value = "/{id}/points", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetPointsResponse> getPoints(@PathVariable("id") String id) {
        Optional<GetPointsResponse> response = receiptService.getPointsById(UUID.fromString(id));
        if(response.isPresent()) {
            return ResponseEntity.ok(response.get());
        } else {
            throw new NoSuchElementException();
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        ErrorResponse response = ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, "The receipt is invalid.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        ErrorResponse response = ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, "The receipt is invalid.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public ResponseEntity<ErrorResponse> handleInternalErrorException(HttpServerErrorException.InternalServerError ex) {
        ErrorResponse response = ErrorResponse.create(ex, HttpStatus.BAD_REQUEST, "The receipt is invalid.");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException ex) {
        ErrorResponse response = ErrorResponse.create(ex, HttpStatus.NOT_FOUND, "No receipt found for that ID.");
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
