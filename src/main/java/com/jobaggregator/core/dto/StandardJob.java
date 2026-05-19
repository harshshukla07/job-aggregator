package com.jobaggregator.core.dto;
import java.time.LocalDateTime;

public record StandardJob(
        String atsJobId,
        String title,
        String companyName,
        String atsProvider,
        String applyUrl,
        String description,
        String location,
        boolean isRemote
) {}