package com.jobaggregator.core.service;

import com.jobaggregator.core.dto.LeverJobDto;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;

@Service
public class LeverApiService {

    private final WebClient webClient;

    public LeverApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public List<LeverJobDto> fetchJobs(String companySlug) {
        String url = "https://api.lever.co/v0/postings/" + companySlug + "?mode=json";

        try {
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<LeverJobDto>>() {})
                    .block();
        } catch (Exception e) {
            System.err.println("Failed to fetch jobs for " + companySlug + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }
}