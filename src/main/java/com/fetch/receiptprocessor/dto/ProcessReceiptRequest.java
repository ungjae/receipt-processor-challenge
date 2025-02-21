package com.fetch.receiptprocessor.dto;

import com.fetch.receiptprocessor.model.Item;
import com.fetch.receiptprocessor.model.Receipt;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Value
@Builder(toBuilder = true, setterPrefix = "with")
public class ProcessReceiptRequest {
    @NotEmpty
    @Pattern(regexp = "^[\\w\\s\\-&]+$")
    String retailer;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$")
    String purchaseDate;

    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$")
    String purchaseTime;

    @Pattern(regexp = "^\\d+\\.\\d{2}$")
    String total;

    @NotEmpty
    @Size(min = 1, message = "At least one item is required")
    List<Item> items;

    public Receipt toReceipt() {
        return Receipt.builder()
                .withRetailer(retailer)
                .withPurchaseDateTime(LocalDateTime.of(
                        LocalDate.parse(purchaseDate),
                        LocalTime.parse(purchaseTime)))
                .withTotal(new BigDecimal(total))
                .withItems(items)
                .build();
    }
}
