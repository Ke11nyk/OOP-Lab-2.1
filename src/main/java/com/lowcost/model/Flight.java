package com.lowcost.model;

import lombok.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Flight {
    private int id;
    private String flightNumber;
    private String departureAirport;
    private String arrivalAirport;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal basePrice;
    private int totalSeats;
    private int availableSeats;
    private BigDecimal currentPrice;
    private boolean isActive;

    public boolean isPriceIncreased() {
        if (currentPrice == null) {
            return false;
        }
        return currentPrice.compareTo(basePrice) > 0;
    }
}