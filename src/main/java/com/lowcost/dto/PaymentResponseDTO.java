package com.lowcost.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentResponseDTO {
    private String transactionId;
    private String status;
    private LocalDateTime paymentDate;
}