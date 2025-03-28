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
                    "INSERT INTO bookings (user_id, flight_id, booking_number, created_at, " +
                            "total_price, status, priority_boarding, baggage_count) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, booking.getUserId());
            statement.setInt(2, booking.getFlightId());
            statement.setString(3, booking.getBookingNumber());
            statement.setTimestamp(4, Timestamp.valueOf(booking.getCreatedAt()));
            statement.setBigDecimal(5, booking.getTotalPrice());
            statement.setString(6, booking.getStatus().name());
            statement.setBoolean(7, booking.isPriorityBoarding());
            statement.setInt(8, booking.getBaggageCount());

            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                booking.setId(rs.getInt(1));
            }
            return booking;
        } catch (SQLException e) {
            log.error("Error creating booking: " + booking.toString(), e);
            return null;
        }
    }

    public boolean updateStatus(String bookingId, BookingStatus status) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE bookings SET status = ? WHERE booking_number = ?");
            statement.setString(1, status.name());
            statement.setString(2, bookingId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Error updating booking status: {}", bookingId, e);
            return false;
        }
    }

    public Booking findByNumber(String bookingNumber) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM bookings WHERE booking_number = ?");
            statement.setString(1, bookingNumber);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return mapBooking(rs);
            }
        } catch (SQLException e) {
            log.error("Error finding booking by number: {}", bookingNumber, e);
        }
        return null;
    }

    public List<Booking> findByUserId(String userId) {
        List<Booking> bookings = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM bookings WHERE user_id = ? ORDER BY created_at DESC");
            statement.setString(1, userId);

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
        return updateStatus(String.valueOf(bookingId), BookingStatus.CANCELLED);
    }

    private Booking mapBooking(ResultSet rs) throws SQLException {
        return Booking.builder()
                .id(rs.getInt("id"))
                .userId(rs.getString("user_id"))
                .flightId(rs.getInt("flight_id"))
                .bookingNumber(rs.getString("booking_number"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .totalPrice(rs.getBigDecimal("total_price"))
                .status(BookingStatus.valueOf(rs.getString("status")))
                .priorityBoarding(rs.getBoolean("priority_boarding"))
                .baggageCount(rs.getInt("baggage_count"))
                .build();
    }
}