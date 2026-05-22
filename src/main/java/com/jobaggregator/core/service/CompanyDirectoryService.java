package com.jobaggregator.core.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.Map;

@Service
public class CompanyDirectoryService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    // Your exact working RAW Gist URL
    private static final String GIST_URL = "https://gist.githubusercontent.com/harshshukla07/20deb0e1b482f5083719840be2971631/raw/fadb4148a85946571d12e5fc6a1b83e3aeea4295/companies.json";

    public CompanyDirectoryService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, String> fetchDirectory() {
        try {
            // 1. Fetch the payload simply as a raw String to bypass strict Content-Type headers
            String rawJson = webClient.get()
                    .uri(GIST_URL)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // 2. Manually parse the text string into our Java Map using Jackson
            if (rawJson != null && !rawJson.isBlank()) {
                return objectMapper.readValue(rawJson, new TypeReference<Map<String, String>>() {});
            }

            return Collections.emptyMap();
        } catch (Exception e) {
            System.err.println("Failed to fetch dynamic company directory: " + e.getMessage());
            return Collections.emptyMap(); // Fallback so the app doesn't crash
        }
    }
}