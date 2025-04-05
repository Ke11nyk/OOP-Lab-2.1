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

        // Початковий список рейсів з DAO
        List<Flight> allFlights = flightDAO.findByAirports(departure, arrival);
        log.info("Total flights found in DAO: {}", allFlights.size());

        // Фільтруємо по активності
        List<Flight> activeFlights = allFlights.stream()
                .filter(Flight::isActive)
                .toList();
        log.info("Active flights: {}/{}", activeFlights.size(), allFlights.size());

        // Фільтруємо по наявності місць
        List<Flight> availableFlights = activeFlights.stream()
                .filter(f -> f.getAvailableSeats() > 0)
                .toList();
        log.info("Flights with available seats: {}/{}", availableFlights.size(), activeFlights.size());

        // Виведемо детальну інформацію про кожен рейс, що залишився
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