package com.lowcost.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PriceAdjustmentDTO {
    private int flightId;
    private BigDecimal newPrice;
    private String reason; // "DEMAND", "LAST_MINUTE" etc.
}