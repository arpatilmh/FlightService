package com.arpatilmh.flight.controller;

import com.arpatilmh.flight.service.IFlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


import java.util.*;

@RestController
@RequestMapping("/api/flights")
public class FlightController {
    @Autowired
    private IFlightService flightService;

    @GetMapping("/fastest-routes")
    public ResponseEntity<List<Map<String, Map<String, Integer>>>> getFastestRoutes(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam(defaultValue = "5") int noOfFlights) {

        return ResponseEntity.status(200).body(flightService.getFastestRoutes(from, to, noOfFlights));
    }


    @GetMapping("/fastest-routes/nlp")
    public ResponseEntity<List<Map<String, Map<String, Integer>>>> getFastestRoutes(@RequestParam String nlpQuery) {
        String apiKey = "";
        String endpoint = "https://api.openai.com/v1/chat/completions";
        nlpQuery += "Give me response in json format : { \"from\": \"<from>\", \"to\": \"<to>\", \"noOfFlight\": <number> }";
        String requestBody = "{"
                + "\"model\": \"gpt-3.5-turbo\","
                + "\"messages\": [{\"role\": \"user\", \"content\": \"" + nlpQuery.replace("\"", "\\\"") + "\"}]"
                + "}";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Parse OpenAI response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.body());
            String content = root.path("choices").get(0).path("message").path("content").asText();

            // Parse the content as JSON
            JsonNode contentJson = mapper.readTree(content);
            String from = contentJson.path("from").asText();
            String to = contentJson.path("to").asText();
            int noOfFlights = contentJson.path("noOfFlight").asInt();

            return ResponseEntity.ok(flightService.getFastestRoutes(from, to, noOfFlights));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }
}
