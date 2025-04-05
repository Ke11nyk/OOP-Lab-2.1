package com.lowcost.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lowcost.model.Flight;
import com.lowcost.service.FlightService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@WebServlet(name = "FlightServlet", value = "/flights")
public class FlightController extends HttpServlet {
    private final FlightService flightService = new FlightService();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String departure = req.getParameter("departure");
        String arrival = req.getParameter("arrival");

        log.info("GET request received for flights: departure='{}', arrival='{}'", departure, arrival);

        List<Flight> flights = flightService.findAvailableFlights(departure, arrival);
        resp.setContentType("application/json");

        if(flights == null) {
            log.warn("Flight service returned null result");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            log.info("Returning {} flights in response", flights.size());
            resp.setStatus(HttpServletResponse.SC_OK);
            String json = objectMapper.writeValueAsString(flights);
            log.debug("JSON response: {}", json);
            resp.getWriter().write(json);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Flight flight = objectMapper.readValue(
                req.getReader().lines().collect(Collectors.joining()),
                Flight.class
        );

        Flight createdFlight = flightService.createFlight(flight);
        if (createdFlight == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(createdFlight));
        }
    }
}