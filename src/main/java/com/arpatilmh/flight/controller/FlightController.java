package com.arpatilmh.flight.controller;

import com.arpatilmh.flight.service.IFlightService;
import com.arpatilmh.flight.service.ILlmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;



import java.util.*;

@RestController
@RequestMapping("/api/flights")
public class FlightController {
    @Autowired
    private IFlightService flightService;

    @Autowired
    private ILlmService llmService;

    @GetMapping("/fastest-routes")
    public ResponseEntity<List<Map<String, Map<String, Integer>>>> getFastestRoutes(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "5") int noOfFlights) {

        return ResponseEntity.status(200).body(flightService.getFastestRoutes(from, to, noOfFlights));
    }


    @GetMapping("/fastest-routes/nlp")
    public ResponseEntity<List<Map<String, Map<String, Integer>>>> getFastestRoutes(@RequestParam String nlpQuery) {
        try {
            JsonNode contentJson = llmService.getLlmResponse(nlpQuery, null);
            String from = contentJson.path("from").asText();
            String to = contentJson.path("to").asText();
            int noOfFlights = contentJson.path("noOfFlight").asInt();

            return ResponseEntity.ok(flightService.getFastestRoutes(from, to, noOfFlights));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }
}
