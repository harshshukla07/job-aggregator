package com.jobaggregator.core.service;

import com.jobaggregator.core.dto.LeverJobDto;
import com.jobaggregator.core.dto.StandardJob;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeverApiService implements AtsProvider {

    private final WebClient webClient;

    public LeverApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public String getProviderName() {
        return "lever";
    }

    @Override
    public List<StandardJob> fetchJobs(String companySlug) {
        String url = "https://api.lever.co/v0/postings/" + companySlug + "?mode=json";

        try {
            List<LeverJobDto> leverJobs = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<LeverJobDto>>() {})
                    .block();

            if (leverJobs == null) return Collections.emptyList();

            return leverJobs.stream().map(job -> {
                boolean isRemote = (job.workplaceType() != null && job.workplaceType().toLowerCase().contains("remote"));
                String location = (job.categories() != null && job.categories().location() != null)
                        ? job.categories().location() : "Unspecified";

                return new StandardJob(
                        job.id(),
                        job.text(),
                        companySlug,
                        getProviderName(),
                        job.hostedUrl(),
                        job.descriptionPlain(),
                        location,
                        isRemote
                );
            }).collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Failed to fetch Lever jobs for " + companySlug + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }
}