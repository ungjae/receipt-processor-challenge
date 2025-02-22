package com.fetch.receiptprocessor.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fetch.receiptprocessor.dto.GetPointsResponse;
import com.fetch.receiptprocessor.dto.ProcessReceiptRequest;
import com.fetch.receiptprocessor.dto.ProcessReceiptResponse;
import com.fetch.receiptprocessor.model.Item;
import com.fetch.receiptprocessor.service.ReceiptService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.ErrorResponse;
import org.springframework.web.servlet.View;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Configuration
class TestConfig {
    @Bean
    public ReceiptService receiptService() {
        return Mockito.mock(ReceiptService.class);
    }
}

@WebMvcTest(ReceiptController.class)
@Import(TestConfig.class)
class ReceiptControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private View error;

    @Test
    void processReceipt_Success() throws Exception {
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
                                .build()
                ))
                .build();

        UUID id = UUID.randomUUID();
        when(receiptService.saveReceipt(any())).thenReturn(
                Optional.of(ProcessReceiptResponse.builder().withId(String.valueOf(id)).build())
        );

        mockMvc.perform(post("/receipts/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void processReceipt_InvalidRequest() throws Exception {
        ProcessReceiptRequest request = ProcessReceiptRequest.builder()
                                            .withRetailer("")
                                            .withPurchaseDate("2022-01-01")
                                            .withPurchaseTime("13:01")
                                            .withTotal("35.35")
                                            .withItems(List.of())
                                            .build();

        mockMvc.perform(post("/receipts/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPoints_Success() throws Exception {
        String id = UUID.randomUUID().toString();
        when(receiptService.getPointsById(UUID.fromString(id))).thenReturn(
                Optional.of(GetPointsResponse.builder()
                        .withPoints(100)
                        .build())
        );

        mockMvc.perform(get("/receipts/{id}/points", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points").value("100"));
    }

    @Test
    void getPoints_NotFound() throws Exception {
        String id = UUID.randomUUID().toString();
        when(receiptService.getPointsById(UUID.fromString(id))).thenReturn(
                Optional.empty()
        );

        mockMvc.perform(get("/receipts/{id}/points", id)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPoints_InvalidUUID() throws Exception {
        String invalidId = "not-a-uuid";

        mockMvc.perform(get("/receipts/{id}/points", invalidId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }
}
