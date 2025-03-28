package com.lowcost.mapper;

import com.lowcost.dto.BookingRequestDTO;
import com.lowcost.dto.BookingResponseDTO;
import com.lowcost.model.Booking;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = FlightMapper.class)
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    Booking toBooking(BookingRequestDTO bookingRequestDTO);

    @Mapping(target = "flightNumber", source = "flight.flightNumber")
    @Mapping(target = "departureAirport", source = "flight.departureAirport")
    @Mapping(target = "arrivalAirport", source = "flight.arrivalAirport")
    @Mapping(target = "departureTime", source = "flight.departureTime")
    BookingResponseDTO toBookingResponseDTO(Booking booking);
}