package com.jobaggregator.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LeverJobDto(
        String id,
        String text,
        String hostedUrl,
        String descriptionPlain,
        String workplaceType,
        Categories categories
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Categories(
            String commitment,
            String team,
            String location
    ) {}
}