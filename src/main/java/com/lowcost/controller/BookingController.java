package com.lowcost.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lowcost.dto.BookingRequestDTO;
import com.lowcost.mapper.BookingMapper;
import com.lowcost.model.Booking;
import com.lowcost.service.BookingService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@WebServlet(name = "BookingServlet", value = "/bookings")
public class BookingController extends HttpServlet {
    private final BookingService bookingService = new BookingService();
    private final ObjectMapper objectMapper;

    public BookingController() {
        objectMapper = new ObjectMapper();
        // Configure ObjectMapper to handle Java 8 date/time types
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            String requestBody = req.getReader().lines().collect(Collectors.joining());
            log.info("Received booking request: {}", requestBody);

            BookingRequestDTO dto = objectMapper.readValue(requestBody, BookingRequestDTO.class);
            log.info("Parsed request DTO: {}", dto);

            // Validate DTO
            if (dto.getFlightId() <= 0) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid flight ID");
                return;
            }

            // Convert to Booking domain object
            Booking booking = BookingMapper.INSTANCE.toBooking(dto);
            log.info("Mapped to booking entity: {}", booking);

            // Create booking
            Booking createdBooking = bookingService.createBooking(booking);

            if (createdBooking == null) {
                String errorMessage = bookingService.getLastErrorMessage();
                log.error("Booking creation failed: {}", errorMessage);
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, errorMessage);
            } else {
                log.info("Successfully created booking: {}", createdBooking);
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(objectMapper.writeValueAsString(
                        BookingMapper.INSTANCE.toBookingResponseDTO(createdBooking)));
            }
        } catch (Exception e) {
            log.error("Booking error", e);
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal error: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            String bookingId = req.getParameter("id");
            String status = req.getParameter("status");

            if (bookingId == null || bookingId.isEmpty()) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Booking ID is required");
                return;
            }

            if (status == null || status.isEmpty()) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Status is required");
                return;
            }

            try {
                Booking.BookingStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                sendErrorResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid status value");
                return;
            }

            boolean updated = bookingService.updateBookingStatus(bookingId, Booking.BookingStatus.valueOf(status));
            if (updated) {
                Map<String, String> response = new HashMap<>();
                response.put("status", "success");
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(response));
            } else {
                sendErrorResponse(resp, HttpServletResponse.SC_NOT_FOUND, "Booking not found or cannot be updated");
            }
        } catch (Exception e) {
            log.error("Error updating booking status", e);
            sendErrorResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void sendErrorResponse(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        resp.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}