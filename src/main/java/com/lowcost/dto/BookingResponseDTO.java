package com.lowcost.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingResponseDTO {
    private String bookingReference;
    private int flightId;
    private String departureAirport;
    private String arrivalAirport;
    private LocalDateTime departureTime;
    private BigDecimal totalPrice;
    private String status;
    private boolean priorityBoarding;
    private int baggageCount;
}