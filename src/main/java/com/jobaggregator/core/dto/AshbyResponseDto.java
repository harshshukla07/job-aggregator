package com.jobaggregator.core.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AshbyResponseDto(List<AshbyJob> jobs) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record AshbyJob(
            String id,
            String title,
            String jobUrl,
            String descriptionHtml,
            String location,
            Boolean isRemote
    ) {}
}