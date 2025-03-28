package com.lowcost.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowcost.model.PriceHistory;
import com.lowcost.service.PriceService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "PriceServlet", value = "/prices")
public class PriceController extends HttpServlet {
    private final PriceService priceService = new PriceService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String flightId = req.getParameter("flightId");
        List<PriceHistory> priceHistory = priceService.getPriceHistoryForFlight(flightId);

        resp.setContentType("application/json");
        resp.getWriter().write(objectMapper.writeValueAsString(priceHistory));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PriceHistory priceChange = objectMapper.readValue(
                req.getReader().lines().collect(Collectors.joining()),
                PriceHistory.class
        );

        PriceHistory createdRecord = priceService.recordPriceChange(priceChange);
        if (createdRecord == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } else {
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(createdRecord));
        }
    }
}