package com.lowcost.dao;

import com.lowcost.db.LowCostDBConnection;
import com.lowcost.model.PriceHistory;
import lombok.extern.log4j.Log4j2;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class PriceHistoryDAO {
    private final Connection connection;

    public PriceHistoryDAO() {
        this.connection = LowCostDBConnection.getConnection();
    }

    public PriceHistory save(PriceHistory priceHistory) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO price_history (flight_id, old_price, new_price, change_time, reason) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, priceHistory.getFlightId());
            statement.setBigDecimal(2, priceHistory.getOldPrice());
            statement.setBigDecimal(3, priceHistory.getNewPrice());
            statement.setTimestamp(4, Timestamp.valueOf(priceHistory.getChangeTime()));
            statement.setString(5, priceHistory.getReason().name());

            statement.executeUpdate();

            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                priceHistory.setId(rs.getInt(1));
            }
            return priceHistory;
        } catch (SQLException e) {
            log.error("Error saving price history: " + priceHistory.toString(), e);
            return null;
        }
    }

    public List<PriceHistory> findByFlightId(String flightId) {
        List<PriceHistory> history = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM price_history WHERE flight_id = ? ORDER BY change_time DESC");
            statement.setInt(1, Integer.parseInt(flightId));

            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                history.add(PriceHistory.builder()
                        .id(rs.getInt("id"))
                        .flightId(rs.getInt("flight_id"))
                        .oldPrice(rs.getBigDecimal("old_price"))
                        .newPrice(rs.getBigDecimal("new_price"))
                        .changeTime(rs.getTimestamp("change_time").toLocalDateTime())
                        .reason(PriceHistory.PriceChangeReason.valueOf(rs.getString("reason")))
                        .build());
            }
        } catch (SQLException e) {
            log.error("Error finding price history for flight: {}", flightId, e);
        }
        return history;
    }
}