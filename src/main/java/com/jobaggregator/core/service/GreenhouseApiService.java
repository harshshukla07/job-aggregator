package com.jobaggregator.core.service;

import com.jobaggregator.core.dto.GreenhouseResponseDto;
import com.jobaggregator.core.dto.StandardJob;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GreenhouseApiService implements AtsProvider {

    private final WebClient webClient;

    public GreenhouseApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public String getProviderName() {
        return "greenhouse";
    }

    @Override
    public List<StandardJob> fetchJobs(String companySlug) {
        // Greenhouse endpoint format. The '?content=true' is critical to get the job description.
        String url = "https://boards-api.greenhouse.io/v1/boards/" + companySlug + "/jobs?content=true";

        try {
            GreenhouseResponseDto response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(GreenhouseResponseDto.class)
                    .block();

            if (response == null || response.jobs() == null) {
                return Collections.emptyList();
            }

            // Map the Greenhouse-specific data into our Universal StandardJob
            return response.jobs().stream().map(job -> {

                String locName = (job.location() != null && job.location().name() != null)
                        ? job.location().name() : "Unspecified";

                boolean isRemote = locName.toLowerCase().contains("remote");

                return new StandardJob(
                        String.valueOf(job.id()),
                        job.title(),
                        companySlug,
                        getProviderName(),
                        job.absolute_url(),
                        job.content(),
                        locName,
                        isRemote
                );
            }).collect(Collectors.toList());

        } catch (Exception e) {
            System.err.println("Failed to fetch Greenhouse jobs for " + companySlug + ": " + e.getMessage());
            return Collections.emptyList();
        }
    }
}