package com.arpatilmh.flight.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class OpenAILlmService implements ILlmService {
    private static final String apiKey = "<API-KEY>";
    private static final String endpoint = "https://api.openai.com/v1/chat/completions";

    @Override
    public JsonNode getLlmResponse(String nlpQuery, String extraPrompt) {
        if (extraPrompt != null) {
            nlpQuery += " " + extraPrompt;
        } else {
            nlpQuery += " Give me response in json format : { \"from\": \"<from>\", \"to\": \"<to>\", \"noOfFlight\": <number> }";
        }

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
            return mapper.readTree(content);
        } catch (Exception e) {
            System.out.println("Error while calling OpenAI API: " + e.getMessage());
            return null;
        }
    }
}
