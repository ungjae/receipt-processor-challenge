package com.fetch.receiptprocessor.service;

import com.fetch.receiptprocessor.dao.ReceiptDao;
import com.fetch.receiptprocessor.dto.GetPointsResponse;
import com.fetch.receiptprocessor.dto.ProcessReceiptRequest;
import com.fetch.receiptprocessor.model.Item;
import com.fetch.receiptprocessor.model.Receipt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ReceiptServiceTest {
    @Mock
    private ReceiptDao receiptDao;
    private ReceiptService receiptService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        receiptService = new ReceiptService(receiptDao);
    }

    @Test
    void calculatePointsExampleTest() {
        ProcessReceiptRequest request = ProcessReceiptRequest.builder()
                .withRetailer("Target")
                .withPurchaseDate("2022-01-01")
                .withPurchaseTime("13:01")
                .withTotal("35.35")
                .withItems(Arrays.asList(
                        Item.builder()
                                .withShortDescription("Mountain Dew 12PK")
                                .withPrice("6.49")
                                .build(),
                        Item.builder()
                                .withShortDescription("Emils Cheese Pizza")
                                .withPrice("12.25")
                                .build(),
                        Item.builder()
                                .withShortDescription("Knorr Creamy Chicken")
                                .withPrice("1.26")
                                .build(),
                        Item.builder()
                                .withShortDescription("Doritos Nacho Cheese")
                                .withPrice("3.35")
                                .build(),
                        Item.builder()
                                .withShortDescription("   Klarbrunn 12-PK 12 FL OZ  ")
                                .withPrice("12.00")
                                .build()
                ))
                .build();

        Receipt receipt = request.toReceipt();
        receiptService.calculatePoints(receipt);
        assertEquals(28, receipt.getPoints());
    }

    @Test
    void calculatePointsRoundDollarAmountTest() {
        ProcessReceiptRequest request = ProcessReceiptRequest.builder()
                .withRetailer("M&M Corner Market")
                .withPurchaseDate("2022-03-20")
                .withPurchaseTime("14:33")
                .withTotal("100.00")
                .withItems(Collections.singletonList(
                        Item.builder()
                                .withShortDescription("Gatorade")
                                .withPrice("100.00")
                                .build()
                ))
                .build();

        Receipt receipt = request.toReceipt();
        receiptService.calculatePoints(receipt);
        assertEquals(99, receipt.getPoints());
    }

    @Test
    void findByIdFoundTest() {
        UUID id = UUID.randomUUID();
        when(receiptDao.findById(id)).thenReturn(Optional.of(
                Receipt.builder()
                        .withId(id)
                        .withPoints(100)
                        .build()
        ));

        Optional<Receipt> receipt = receiptService.findById(id);

        assertNotNull(receipt);
        assertFalse(receipt.isEmpty());
        assertEquals(100, receipt.get().getPoints());
    }

    @Test
    void findByIdNotFoundTest() {
        UUID id = UUID.randomUUID();
        when(receiptDao.findById(id)).thenReturn(Optional.empty());

        Optional<Receipt> receipt = receiptService.findById(id);

        assertTrue(receipt.isEmpty());
    }

    @Test
    void getPointsByIdFoundTest() {
        UUID id = UUID.randomUUID();
        when(receiptDao.getPointsById(id)).thenReturn(Optional.of(
                Receipt.builder()
                        .withId(id)
                        .withPoints(100)
                        .build()
        ));

        Optional<GetPointsResponse> response = receiptService.getPointsById(id);

        assertNotNull(response);
        assertFalse(response.isEmpty());
        assertEquals(100, response.get().getPoints());
    }

    @Test
    void getPointsByIdNotFoundTest() {
        UUID id = UUID.randomUUID();
        when(receiptDao.getPointsById(id)).thenReturn(Optional.empty());

        Optional<GetPointsResponse> response = receiptService.getPointsById(id);

        assertTrue(response.isEmpty());
    }
}
