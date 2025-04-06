package com.lowcost.service;

import com.lowcost.dao.FlightDAO;
import com.lowcost.model.Flight;
import lombok.extern.log4j.Log4j2;

import java.util.List;

@Log4j2
public class FlightService {
    private final FlightDAO flightDAO = new FlightDAO();

    public List<Flight> findAvailableFlights(String departure, String arrival) {
        log.info("Finding available flights from '{}' to '{}'", departure, arrival);

        // Initial list of flights from DAO
        List<Flight> allFlights = flightDAO.findByAirports(departure, arrival);
        log.info("Total flights found in DAO: {}", allFlights.size());

        // Filter by activity
        List<Flight> activeFlights = allFlights.stream()
                .filter(Flight::isActive)
                .toList();
        log.info("Active flights: {}/{}", activeFlights.size(), allFlights.size());

        // Filter by availability
        List<Flight> availableFlights = activeFlights.stream()
                .filter(f -> f.getAvailableSeats() > 0)
                .toList();
        log.info("Flights with available seats: {}/{}", availableFlights.size(), activeFlights.size());

        // We will display detailed information about each remaining flight
        availableFlights.forEach(flight ->
                log.debug("Available flight: id={}, number={}, active={}, seats={}",
                        flight.getId(), flight.getFlightNumber(),
                        flight.isActive(), flight.getAvailableSeats()));

        return availableFlights;
    }

    public Flight createFlight(Flight flight) {
        if (flight.getDepartureAirport() == null || flight.getArrivalAirport() == null) {
            return null;
        }
        return flightDAO.save(flight);
    }
}