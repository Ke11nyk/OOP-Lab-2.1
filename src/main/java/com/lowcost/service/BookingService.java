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

    public Booking createBooking(Booking booking) {
        // 1. Перевіряємо наявність рейсу
        Flight flight = flightDAO.findById(booking.getFlightId());
        if (flight == null) {
            log.error("Flight not found for id: {}", booking.getFlightId());
            return null;
        }

        // 2. Перевіряємо наявність вільних місць
        if (flight.getAvailableSeats() <= 0) {
            log.error("No available seats for flight id: {}", flight.getId());
            return null;
        }

        // 3. Застосовуємо динамічні ціни
        priceService.applyDynamicPricing(flight.getId());
        flight = flightDAO.findById(flight.getId()); // Оновлюємо дані рейсу

        // 4. Розраховуємо фінальну ціну
        BigDecimal finalPrice = calculateFinalPrice(
                flight.isPriceIncreased() ? flight.getCurrentPrice() : flight.getBasePrice(),
                booking.isPriorityBoarding(),
                booking.getBaggageCount()
        );

        // 5. Створюємо бронювання
        booking.setBookingNumber(generateBookingNumber());
        booking.setCreatedAt(LocalDateTime.now());
        booking.setTotalPrice(finalPrice);
        booking.setStatus(BookingStatus.PENDING);

        // 6. Зберігаємо бронювання
        Booking createdBooking = bookingDAO.save(booking);
        if (createdBooking == null) {
            log.error("Failed to create booking for flight id: {}", flight.getId());
            return null;
        }

        // 7. Зменшуємо кількість вільних місць
        if (!flightDAO.decreaseAvailableSeats(flight.getId())) {
            log.error("Failed to decrease available seats for flight id: {}", flight.getId());
            // Відкочуємо бронювання, якщо не вдалося оновити місця
            bookingDAO.cancelBooking(createdBooking.getId());
            return null;
        }

        return createdBooking;
    }

    public boolean confirmBooking(String bookingNumber) {
        return updateBookingStatus(bookingNumber, BookingStatus.CONFIRMED);
    }

    public boolean cancelBooking(String bookingNumber) {
        Booking booking = bookingDAO.findByNumber(bookingNumber);
        if (booking == null) {
            return false;
        }

        // Повертаємо місце у рейс
        if (!flightDAO.increaseAvailableSeats(booking.getFlightId())) {
            log.error("Failed to increase available seats for flight id: {}", booking.getFlightId());
        }

        return updateBookingStatus(bookingNumber, BookingStatus.CANCELLED);
    }

    public boolean payForBooking(String bookingNumber) {
        return updateBookingStatus(bookingNumber, BookingStatus.PAID);
    }

    public Booking getBooking(String bookingNumber) {
        return bookingDAO.findByNumber(bookingNumber);
    }

    public List<Booking> getUserBookings(String userId) {
        return bookingDAO.findByUserId(userId);
    }

    public boolean updateBookingStatus(String bookingNumber, BookingStatus status) {
        Booking booking = bookingDAO.findByNumber(bookingNumber);
        if (booking == null) {
            log.error("Booking not found: {}", bookingNumber);
            return false;
        }

        if (!bookingDAO.updateStatus(String.valueOf(booking.getId()), status)) {
            log.error("Failed to update status for booking: {}", bookingNumber);
            return false;
        }
        return true;
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
}