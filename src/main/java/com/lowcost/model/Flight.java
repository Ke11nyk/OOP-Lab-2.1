package com.lowcost.model;

import lombok.*;

import java.math.RoundingMode;
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

    /**
     * Перевіряє, чи була підвищена ціна порівняно з базовою
     */
    public boolean isPriceIncreased() {
        if (currentPrice == null) {
            return false;
        }
        return currentPrice.compareTo(basePrice) > 0;
    }

    /**
     * Розраховує коефіцієнт підвищення ціни
     */
    public BigDecimal getPriceIncreaseFactor() {
        if (!isPriceIncreased() || basePrice.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ONE;
        }
        return currentPrice.divide(basePrice, 2, RoundingMode.HALF_UP);
    }
}