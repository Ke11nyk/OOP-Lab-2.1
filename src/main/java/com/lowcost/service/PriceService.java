package com.lowcost.service;

import com.lowcost.dao.FlightDAO;
import com.lowcost.dao.PriceHistoryDAO;
import com.lowcost.model.Flight;
import com.lowcost.model.PriceHistory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class PriceService {
    private final PriceHistoryDAO priceHistoryDAO = new PriceHistoryDAO();
    private final FlightDAO flightDAO = new FlightDAO();

    public List<PriceHistory> getPriceHistoryForFlight(String flightId) {
        return priceHistoryDAO.findByFlightId(flightId);
    }

    public PriceHistory recordPriceChange(PriceHistory priceChange) {
        Flight flight = flightDAO.findById(priceChange.getFlightId());
        if (flight == null) {
            return null;
        }

        priceChange.setChangeTime(LocalDateTime.now());
        PriceHistory savedRecord = priceHistoryDAO.save(priceChange);

        // Update flight price if needed
        if (shouldUpdateCurrentPrice(priceChange)) {
            flight.setBasePrice(priceChange.getNewPrice());
            flightDAO.update(flight);
        }

        return savedRecord;
    }

    private boolean shouldUpdateCurrentPrice(PriceHistory priceChange) {
        return priceChange.getReason() == PriceHistory.PriceChangeReason.MANUAL_ADJUSTMENT;
    }

    public void applyDynamicPricing(int flightId) {
        Flight flight = flightDAO.findById(flightId);
        if (flight == null) return;

        BigDecimal newPrice = calculateDynamicPrice(flight);

        if (newPrice.compareTo(flight.getCurrentPrice()) != 0) {
            // Record the price change in history
            priceHistoryDAO.save(PriceHistory.builder()
                    .flightId(flightId)
                    .oldPrice(flight.getCurrentPrice())
                    .newPrice(newPrice)
                    .reason(PriceHistory.PriceChangeReason.DEMAND_INCREASE)
                    .build());

            // Updating the current price
            flightDAO.updateCurrentPrice(flightId, newPrice);
        }
    }

    private BigDecimal calculateDynamicPrice(Flight flight) {
        BigDecimal price = flight.getBasePrice();

        // The logic of dynamic pricing
        double fillRate = 1 - (flight.getAvailableSeats() / (double) flight.getTotalSeats());
        long daysToDeparture = ChronoUnit.DAYS.between(LocalDateTime.now(), flight.getDepartureTime());

        // Increase factors
        if (fillRate > 0.7) price = price.multiply(BigDecimal.valueOf(1.2));
        else if (fillRate > 0.5) price = price.multiply(BigDecimal.valueOf(1.1));

        if (daysToDeparture < 3) price = price.multiply(BigDecimal.valueOf(1.3));
        else if (daysToDeparture < 7) price = price.multiply(BigDecimal.valueOf(1.15));

        return price.setScale(2, RoundingMode.HALF_UP);
    }
}