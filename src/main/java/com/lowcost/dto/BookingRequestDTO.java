package com.lowcost.dto;

import lombok.Data;

@Data
public class BookingRequestDTO {
    private String userId;
    private int flightId;
    private boolean priorityBoarding;
    private int baggageCount;
}