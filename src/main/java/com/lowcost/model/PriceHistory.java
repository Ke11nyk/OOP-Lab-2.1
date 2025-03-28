package com.lowcost.model;

import lombok.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PriceHistory {
    private int id;
    private int flightId;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private LocalDateTime changeTime;
    private PriceChangeReason reason; // Enum

    public enum PriceChangeReason {
        DEMAND_INCREASE,
        LAST_MINUTE,
        SEATS_LEFT,
        MANUAL_ADJUSTMENT
    }
}