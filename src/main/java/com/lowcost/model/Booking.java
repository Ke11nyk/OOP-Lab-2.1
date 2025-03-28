package com.lowcost.model;

import lombok.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    private int id;
    private String userId;
    private int flightId;
    private String bookingNumber;
    private LocalDateTime createdAt;
    private BigDecimal totalPrice;
    private BookingStatus status; // Enum
    private boolean priorityBoarding;
    private int baggageCount;

    public enum BookingStatus {
        PENDING, CONFIRMED, PAID, CANCELLED, REFUNDED
    }
}