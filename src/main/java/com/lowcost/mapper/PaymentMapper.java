package com.lowcost.mapper;

import com.lowcost.dto.PaymentRequestDTO;
import com.lowcost.dto.PaymentResponseDTO;
import com.lowcost.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PaymentMapper {
    PaymentMapper INSTANCE = Mappers.getMapper(PaymentMapper.class);

    @Mapping(target = "bookingReference", source = "booking.bookingReference")
    @Mapping(target = "amount", source = "booking.totalPrice")
    PaymentRequestDTO toPaymentRequestDTO(Booking booking);

    PaymentResponseDTO toPaymentResponseDTO(Booking booking);
}