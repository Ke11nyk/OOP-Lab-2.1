package com.lowcost.dao;

import com.lowcost.db.LowCostDBConnection;
import com.lowcost.model.Flight;
import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class FlightDAO {
    private final Connection connection;

    public FlightDAO() {
        this.connection = LowCostDBConnection.getConnection();
    }

    public Flight save(Flight flight) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO flights (flight_number, departure_airport, arrival_airport, " +
                            "departure_time, arrival_time, base_price, total_seats, available_seats, current_price, is_active) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            statement.setString(1, flight.getFlightNumber());
            statement.setString(2, flight.getDepartureAirport());
            statement.setString(3, flight.getArrivalAirport());
            statement.setTimestamp(4, Timestamp.valueOf(flight.getDepartureTime()));
            statement.setTimestamp(5, Timestamp.valueOf(flight.getArrivalTime()));
            statement.setBigDecimal(6, flight.getBasePrice());
            statement.setInt(7, flight.getTotalSeats());
            statement.setInt(8, flight.getAvailableSeats());
            statement.setBigDecimal(9, flight.getCurrentPrice());
            statement.setBoolean(10, flight.isActive());

            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                flight.setId(rs.getInt(1));
            }
            return flight;
        } catch (SQLException e) {
            log.error("Error creating flight: " + flight.toString(), e);
            return null;
        }
    }

    public boolean update(Flight flight) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE flights SET " +
                            "base_price = ?, " +
                            "available_seats = ?, " +
                            "is_active = ? " +
                            "WHERE id = ?");

            statement.setBigDecimal(1, flight.getBasePrice());
            statement.setInt(2, flight.getAvailableSeats());
            statement.setBoolean(3, flight.isActive());
            statement.setInt(4, flight.getId());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            log.error("Error updating flight with id: {}", flight.getId(), e);
            return false;
        }
    }

    public List<Flight> findByAirports(String departure, String arrival) {
        List<Flight> flights = new ArrayList<>();
        try {
            log.info("Searching for flights from '{}' to '{}'", departure, arrival);
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM flights WHERE departure_airport = ? AND arrival_airport = ?");
            statement.setString(1, departure);
            statement.setString(2, arrival);

            ResultSet rs = statement.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
                flights.add(mapFlight(rs));
            }
            log.info("Found {} flights in database before filtering", count);

            // Додатково виведемо деталі кожного рейсу для діагностики
            flights.forEach(flight -> log.debug("Found flight: {}", flight));
        } catch (SQLException e) {
            log.error("Error finding flights from {} to {}", departure, arrival, e);
        }
        return flights;
    }

    public Flight findById(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM flights WHERE id = ?");
            statement.setInt(1, id);

            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return mapFlight(rs);
            }
        } catch (SQLException e) {
            log.error("Error finding flight by id: {}", id, e);
        }
        return null;
    }

    private Flight mapFlight(ResultSet rs) throws SQLException {
        return Flight.builder()
                .id(rs.getInt("id"))
                .flightNumber(rs.getString("flight_number"))
                .departureAirport(rs.getString("departure_airport"))
                .arrivalAirport(rs.getString("arrival_airport"))
                .departureTime(rs.getTimestamp("departure_time").toLocalDateTime())
                .arrivalTime(rs.getTimestamp("arrival_time").toLocalDateTime())
                .basePrice(rs.getBigDecimal("base_price"))
                .totalSeats(rs.getInt("total_seats"))
                .availableSeats(rs.getInt("available_seats"))
                .currentPrice(rs.getBigDecimal("current_price"))
                .isActive(rs.getBoolean("is_active"))
                .build();
    }

    public boolean updateCurrentPrice(int flightId, BigDecimal newPrice) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE flights SET current_price = ? WHERE id = ?");

            statement.setBigDecimal(1, newPrice);
            statement.setInt(2, flightId);

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            log.error("Error updating current price for flight id: {}", flightId, e);
            return false;
        }
    }

    public boolean decreaseAvailableSeats(int flightId) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE flights SET available_seats = available_seats - 1 " +
                            "WHERE id = ? AND available_seats > 0");

            statement.setInt(1, flightId);
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            log.error("Error decreasing available seats for flight id: {}", flightId, e);
            return false;
        }
    }

    public boolean increaseAvailableSeats(int flightId) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE flights SET available_seats = available_seats + 1 " +
                            "WHERE id = ? AND available_seats < total_seats");

            statement.setInt(1, flightId);
            int rowsUpdated = statement.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            log.error("Error increasing available seats for flight id: {}", flightId, e);
            return false;
        }
    }
}