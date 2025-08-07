package com.arpatilmh.flight.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface ILlmService {
    JsonNode getLlmResponse(String nlpQuery, String extraPrompt);
}
