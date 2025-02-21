package com.fetch.receiptprocessor.controller;

import com.fetch.receiptprocessor.dto.ProcessReceiptRequest;
import com.fetch.receiptprocessor.service.ReceiptService;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/receipts")
public class ReceiptController {
    public final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService) { this.receiptService = receiptService; }

    @PostMapping(value = "/process", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UUID> processReceipt(@Valid @RequestBody ProcessReceiptRequest request) throws BadRequestException {
        Optional<UUID> response = receiptService.saveReceipt(request);
        if(response.isPresent()) {
            return ResponseEntity.ok(response.get());
        } else {
            throw new BadRequestException();
        }
    }

    @GetMapping(value = "/{id}/points", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getPoints(@PathVariable("id") String id) {
        Optional<Integer> response = receiptService.getPointsById(UUID.fromString(id));
        if(response.isPresent()) {
            return ResponseEntity.ok(response.get());
        } else {
            throw new NoSuchElementException();
        }
    }
}
