package com.jobaggregator.core.service;

import com.jobaggregator.core.dto.AshbyResponseDto;
import com.jobaggregator.core.dto.StandardJob;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AshbyApiService implements AtsProvider {

    private final WebClient webClient;

    public AshbyApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public String getProviderName() {
        return "ashby";
    }

    @Override
    public List<StandardJob> fetchJobs(String companySlug) {
        // Ashby's public job board API endpoint
        String url = "https://api.ashbyhq.com/posting-api/job-board/" + companySlug;

        try {
            AshbyResponseDto response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(AshbyResponseDto.class)
                    .block();

            if (response == null || response.jobs() == null) {
                return Collections.emptyList();
            }

            // Map Ashby data to our Universal StandardJob
            return response.jobs().stream().map(job -> {

                String locName = (job.location() != null) ? job.location() : "Unspecified";
                boolean isRemote = (job.isRemote() != null && job.isRemote()) || locName.toLowerCase().contains("remote");

                return new StandardJob(
                        job.id(),
                        job.title(),
                        companySlug,
                        getProviderName(),
                        job.jobUrl(),
                        job.descriptionHtml(),
                        locName,
                        isRemote
                );
            }).collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Failed to fetch Ashby jobs for " + companySlug + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }
}