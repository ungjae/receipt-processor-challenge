package com.fetch.receiptprocessor.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true, setterPrefix = "with")
public class Receipt {
    UUID id;
    String retailer;
    LocalDateTime purchaseDateTime;
    BigDecimal total;
    @Singular
    List<Item> items;
    Integer points;
}