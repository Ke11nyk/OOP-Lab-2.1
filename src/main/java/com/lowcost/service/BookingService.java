package com.lowcost.service;

import com.lowcost.dao.BookingDAO;
import com.lowcost.dao.FlightDAO;
import com.lowcost.model.Booking;
import com.lowcost.model.Booking.BookingStatus;
import com.lowcost.model.Flight;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Log4j2
public class BookingService {
    private final BookingDAO bookingDAO = new BookingDAO();
    private final FlightDAO flightDAO = new FlightDAO();
    private final PriceService priceService = new PriceService();

    private String lastErrorMessage = null;

    public Booking createBooking(Booking booking) {
        try {
            lastErrorMessage = null;

            // Log input parameters
            log.info("Creating booking: userId={}, flightId={}, priorityBoarding={}, baggageCount={}",
                    booking.getUserId(), booking.getFlightId(), booking.isPriorityBoarding(), booking.getBaggageCount());

            // Validate input
            if (booking == null) {
                setErrorAndLog("Booking object is null");
                return null;
            }

            if (booking.getUserId() <= 0) {
                setErrorAndLog("Invalid user ID: " + booking.getUserId());
                return null;
            }

            if (booking.getFlightId() <= 0) {
                setErrorAndLog("Invalid flight ID: " + booking.getFlightId());
                return null;
            }

            // 1. Find the flight
            log.info("Finding flight with ID: {}", booking.getFlightId());
            Flight flight = flightDAO.findById(booking.getFlightId());
            if (flight == null) {
                setErrorAndLog("Flight not found for id: " + booking.getFlightId());
                return null;
            }
            log.info("Found flight: {}", flight);

            // 2. Check seat availability
            if (flight.getAvailableSeats() <= 0) {
                setErrorAndLog("No available seats for flight id: " + flight.getId());
                return null;
            }
            log.info("Available seats: {}", flight.getAvailableSeats());

            // 3. Apply dynamic pricing
            try {
                log.info("Applying dynamic pricing for flight: {}", flight.getId());
                priceService.applyDynamicPricing(flight.getId());
                flight = flightDAO.findById(flight.getId()); // Refresh flight data
                log.info("Updated flight price: base={}, current={}, increased={}",
                        flight.getBasePrice(), flight.getCurrentPrice(), flight.isPriceIncreased());
            } catch (Exception e) {
                log.warn("Error applying dynamic pricing, using base price", e);
                // Continue with base price if dynamic pricing fails
            }

            // 4. Calculate final price
            BigDecimal basePrice = flight.isPriceIncreased() ? flight.getCurrentPrice() : flight.getBasePrice();
            if (basePrice == null) {
                log.warn("Base price is null, using default price");
                basePrice = new BigDecimal("100.00");
            }

            BigDecimal finalPrice = calculateFinalPrice(
                    basePrice,
                    booking.isPriorityBoarding(),
                    booking.getBaggageCount()
            );
            log.info("Calculated price: base={}, final={}", basePrice, finalPrice);

            // 5. Setup booking details
            booking.setBookingReference(generateBookingNumber());
            booking.setBookingDate(LocalDateTime.now());
            booking.setTotalPrice(finalPrice);
            booking.setStatus(BookingStatus.PENDING);

            // Set checked baggage flag based on baggage count
            booking.setCheckedBaggage(booking.getBaggageCount() > 0);

            log.info("Prepared booking: {}", booking);

            // 6. Save booking to database
            Booking createdBooking = bookingDAO.save(booking);
            if (createdBooking == null) {
                setErrorAndLog("Failed to save booking to database");
                return null;
            }
            log.info("Booking saved with ID: {}", createdBooking.getId());

            // 7. Decrease available seats
            log.info("Decreasing available seats for flight: {}", flight.getId());
            if (!flightDAO.decreaseAvailableSeats(flight.getId())) {
                setErrorAndLog("Failed to decrease available seats for flight id: " + flight.getId());
                // Rollback booking if seat update fails
                log.info("Rolling back booking: {}", createdBooking.getId());
                bookingDAO.cancelBooking(createdBooking.getId());
                return null;
            }

            log.info("Booking successfully created: {}", createdBooking);
            return createdBooking;
        } catch (Exception e) {
            setErrorAndLog("Unexpected error creating booking", e);
            return null;
        }
    }

    public boolean confirmBooking(String bookingNumber) {
        return updateBookingStatus(bookingNumber, BookingStatus.CONFIRMED);
    }

    public boolean cancelBooking(String bookingNumber) {
        Booking booking = bookingDAO.findByReference(bookingNumber);
        if (booking == null) {
            return false;
        }

        // Return seat to flight
        if (!flightDAO.increaseAvailableSeats(booking.getFlightId())) {
            log.error("Failed to increase available seats for flight id: {}", booking.getFlightId());
        }

        return updateBookingStatus(bookingNumber, BookingStatus.CANCELLED);
    }

    public boolean payForBooking(String bookingNumber) {
        return updateBookingStatus(bookingNumber, BookingStatus.PAID);
    }

    public Booking getBooking(String bookingNumber) {
        return bookingDAO.findByReference(bookingNumber);
    }

    public List<Booking> getUserBookings(int userId) {
        return bookingDAO.findByUserId(userId);
    }

    public boolean updateBookingStatus(String bookingNumber, BookingStatus status) {
        try {
            if (bookingNumber == null || bookingNumber.isEmpty()) {
                log.error("Booking number is null or empty");
                return false;
            }

            Booking booking = bookingDAO.findByReference(bookingNumber);
            if (booking == null) {
                log.error("Booking not found: {}", bookingNumber);
                return false;
            }

            return bookingDAO.updateStatus(bookingNumber, status);
        } catch (Exception e) {
            log.error("Error updating booking status", e);
            return false;
        }
    }

    private BigDecimal calculateFinalPrice(BigDecimal basePrice, boolean priority, int baggage) {
        BigDecimal finalPrice = basePrice;

        if (priority) {
            finalPrice = finalPrice.add(new BigDecimal("15.00"));
        }

        if (baggage > 0) {
            finalPrice = finalPrice.add(new BigDecimal("25.00").multiply(new BigDecimal(baggage)));
        }

        return finalPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private String generateBookingNumber() {
        return "LC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private void setErrorAndLog(String message) {
        lastErrorMessage = message;
        log.error(message);
    }

    private void setErrorAndLog(String message, Exception e) {
        lastErrorMessage = message + ": " + e.getMessage();
        log.error(message, e);
    }

    public String getLastErrorMessage() {
        return lastErrorMessage != null ? lastErrorMessage : "Unknown error";
    }
}