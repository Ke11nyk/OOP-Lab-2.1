package com.lowcost.service;

import com.lowcost.dao.FlightDAO;
import com.lowcost.model.Flight;
import java.util.List;

public class FlightService {
    private final FlightDAO flightDAO = new FlightDAO();

    public List<Flight> findAvailableFlights(String departure, String arrival) {
        return flightDAO.findByAirports(departure, arrival)
                .stream()
                .filter(Flight::isActive)
                .filter(f -> f.getAvailableSeats() > 0)
                .toList();
    }

    public Flight createFlight(Flight flight) {
        if (flight.getDepartureAirport() == null || flight.getArrivalAirport() == null) {
            return null;
        }
        return flightDAO.save(flight);
    }
}