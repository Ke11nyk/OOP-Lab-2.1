package com.lowcost.dto;

import lombok.Data;

@Data
public class BookingRequestDTO {
    private int userId;
    private int flightId;
    private boolean priorityBoarding;
    private boolean checkedBaggage;
    private int baggageCount;
}