package com.lowcost.mapper;

import com.lowcost.dto.BookingRequestDTO;
import com.lowcost.dto.BookingResponseDTO;
import com.lowcost.model.Booking;
import com.lowcost.model.Flight;
import com.lowcost.dao.FlightDAO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class BookingMapper {
    public static final BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    // You might need to inject this or make it static
    private FlightDAO flightDAO = new FlightDAO();

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bookingNumber", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "status", ignore = true)
    public abstract Booking toBooking(BookingRequestDTO dto);

    @Mapping(target = "flightNumber", ignore = true)
    @Mapping(target = "departureAirport", ignore = true)
    @Mapping(target = "arrivalAirport", ignore = true)
    @Mapping(target = "departureTime", ignore = true)
    @Mapping(target = "status", expression = "java(booking.getStatus().toString())")
    public abstract BookingResponseDTO toBookingResponseDTO(Booking booking);

    @AfterMapping
    protected void afterToBookingResponseDTO(Booking booking, @MappingTarget BookingResponseDTO dto) {
        Flight flight = flightDAO.findById(booking.getFlightId());
        if (flight != null) {
            dto.setFlightNumber(flight.getFlightNumber());
            dto.setDepartureAirport(flight.getDepartureAirport());
            dto.setArrivalAirport(flight.getArrivalAirport());
            dto.setDepartureTime(flight.getDepartureTime());
        }
    }
}