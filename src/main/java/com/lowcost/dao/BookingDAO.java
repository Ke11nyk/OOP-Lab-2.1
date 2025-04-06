package com.lowcost.dao;

import com.lowcost.db.LowCostDBConnection;
import com.lowcost.model.Booking;
import com.lowcost.model.Booking.BookingStatus;
import lombok.extern.log4j.Log4j2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class BookingDAO {
    private final Connection connection;

    public BookingDAO() {
        this.connection = LowCostDBConnection.getConnection();
    }

    public Booking save(Booking booking) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO bookings (user_id, flight_id, booking_reference, booking_date, " +
                            "total_price, status, has_priority_boarding, has_checked_baggage, baggage_count) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, booking.getUserId());  // Changed from String.valueOf to direct setInt
            statement.setInt(2, booking.getFlightId());
            statement.setString(3, booking.getBookingReference());
            statement.setTimestamp(4, Timestamp.valueOf(booking.getBookingDate()));
            statement.setBigDecimal(5, booking.getTotalPrice());
            statement.setString(6, booking.getStatus().name());
            statement.setBoolean(7, booking.isPriorityBoarding());
            statement.setBoolean(8, booking.isCheckedBaggage());
            statement.setInt(9, booking.getBaggageCount());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                log.error("Creating booking failed, no rows affected.");
                return null;
            }

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                booking.setId(rs.getInt(1));
                return booking;
            } else {
                log.error("Creating booking failed, no ID obtained.");
                return null;
            }
        } catch (SQLException e) {
            log.error("Error creating booking: " + booking.toString(), e);
            return null;
        }
    }

    // Method to update status by ID
    public boolean updateStatusById(int bookingId, BookingStatus status) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE bookings SET status = ? WHERE id = ?");
            statement.setString(1, status.name());
            statement.setInt(2, bookingId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Error updating booking status by ID: {}", bookingId, e);
            return false;
        }
    }

    // Method to update status by reference
    public boolean updateStatus(String bookingReference, BookingStatus status) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE bookings SET status = ? WHERE booking_reference = ?");
            statement.setString(1, status.name());
            statement.setString(2, bookingReference);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Error updating booking status by reference: {}", bookingReference, e);
            return false;
        }
    }

    public Booking findByReference(String bookingReference) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM bookings WHERE booking_reference = ?");
            statement.setString(1, bookingReference);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return mapBooking(rs);
            }
        } catch (SQLException e) {
            log.error("Error finding booking by reference: {}", bookingReference, e);
        }
        return null;
    }

    public List<Booking> findByUserId(int userId) {
        List<Booking> bookings = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM bookings WHERE user_id = ? ORDER BY booking_date DESC");
            statement.setInt(1, userId);

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                bookings.add(mapBooking(rs));
            }
        } catch (SQLException e) {
            log.error("Error finding bookings for user: {}", userId, e);
        }
        return bookings;
    }

    public boolean cancelBooking(int bookingId) {
        return updateStatusById(bookingId, BookingStatus.CANCELLED);
    }

    private Booking mapBooking(ResultSet rs) throws SQLException {
        return Booking.builder()
                .id(rs.getInt("id"))
                .userId(rs.getInt("user_id"))
                .flightId(rs.getInt("flight_id"))
                .bookingReference(rs.getString("booking_reference"))
                .bookingDate(rs.getTimestamp("booking_date").toLocalDateTime())
                .totalPrice(rs.getBigDecimal("total_price"))
                .status(BookingStatus.valueOf(rs.getString("status")))
                .priorityBoarding(rs.getBoolean("has_priority_boarding"))
                .checkedBaggage(rs.getBoolean("has_checked_baggage"))
                .baggageCount(rs.getInt("baggage_count"))
                .build();
    }
}