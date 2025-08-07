package com.arpatilmh.flight.service;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface IFlightService {
    /**
     * Find fastest route direct or one stop
     */
    List<Map<String, Map<String, Integer>>> getFastestRoutes(
            String from,
            String to,
            int noOfFlights
    );
}
