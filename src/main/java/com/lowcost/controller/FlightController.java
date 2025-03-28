package com.lowcost.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowcost.model.Flight;
import com.lowcost.service.FlightService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "FlightServlet", value = "/flights")
public class FlightController extends HttpServlet {
    private final FlightService flightService = new FlightService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String departure = req.getParameter("departure");
        String arrival = req.getParameter("arrival");

        List<Flight> flights = flightService.findAvailableFlights(departure, arrival);
        resp.setContentType("application/json");
        resp.getWriter().write(objectMapper.writeValueAsString(flights));
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