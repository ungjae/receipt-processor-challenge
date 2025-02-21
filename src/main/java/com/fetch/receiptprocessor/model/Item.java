package com.fetch.receiptprocessor.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true, setterPrefix = "with")
public class Item {
    @NotEmpty
    String shortDescription;
    @Pattern(regexp = "^\\d+\\.\\d{2}$")
    String price;
}
