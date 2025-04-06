package com.lowcost.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {
    private String bookingReference;
    private String paymentMethod; // "CREDIT_CARD", "PAYPAL" etc.
    private BigDecimal amount;
}