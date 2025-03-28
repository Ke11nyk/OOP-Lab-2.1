package com.lowcost.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowcost.model.Booking;
import com.lowcost.service.BookingService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet(name = "BookingServlet", value = "/bookings")
public class BookingController extends HttpServlet {
    private final BookingService bookingService = new BookingService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Booking booking = objectMapper.readValue(
                req.getReader().lines().collect(Collectors.joining()),
                Booking.class
        );

        Booking createdBooking = bookingService.createBooking(booking);
        if (createdBooking == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(createdBooking));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String bookingId = req.getParameter("id");
        String status = req.getParameter("status");

        boolean updated = bookingService.updateBookingStatus(bookingId, Booking.BookingStatus.valueOf(status));
        if (updated) {
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}