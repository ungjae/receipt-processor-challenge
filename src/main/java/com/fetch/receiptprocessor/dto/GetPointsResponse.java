package com.fetch.receiptprocessor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true, setterPrefix = "with")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetPointsResponse {
    Integer points;
}
