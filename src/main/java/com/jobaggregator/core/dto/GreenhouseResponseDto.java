package com.jobaggregator.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GreenhouseResponseDto(List<GreenhouseJob> jobs) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GreenhouseJob(
            Long id,
            String title,
            String absolute_url,      // The apply link
            String content,           // Greenhouse sends the description as an HTML string
            Location location
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Location(String name) {}
}