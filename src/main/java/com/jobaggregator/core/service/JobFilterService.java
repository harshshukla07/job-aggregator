package com.jobaggregator.core.service;

import com.jobaggregator.core.dto.LeverJobDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class JobFilterService {

    private static final List<String> ALLOWED_TITLES = List.of(
            "backend", "java", "node", "sde", "software engineer", "developer"
    );

    private static final List<String> BLOCKED_TITLES = List.of(
            "senior", "sr", "lead", "staff", "principal", "manager", "frontend", "ios", "android"
    );

    private static final List<String> REQUIRED_SKILLS = List.of(
            "java", "spring boot", "node.js", "express"
    );

    private static final List<String> ALLOWED_LOCATIONS = List.of(
            "india", "remote", "bengaluru", "bangalore", "chennai", "hyderabad", "pune", "gurugram", "noida", "mumbai"
    );

    private static final Pattern SENIORITY_REGEX = Pattern.compile("(?i)\\b([3-9]|1[0-9])\\+?\\s*years?\\b");

    public boolean isPerfectMatch(LeverJobDto job) {
        if (job == null || job.text() == null || job.descriptionPlain() == null) {
            return false;
        }

        String title = job.text().toLowerCase();
        String description = job.descriptionPlain().toLowerCase();

        if (BLOCKED_TITLES.stream().anyMatch(title::contains)) return false;

        if (ALLOWED_TITLES.stream().noneMatch(title::contains)) return false;

        if (SENIORITY_REGEX.matcher(description).find()) return false;

        if (REQUIRED_SKILLS.stream().noneMatch(description::contains)) return false;

        return isLocationMatch(job);
    }

    private boolean isLocationMatch(LeverJobDto job) {
        if (job.workplaceType() != null && job.workplaceType().toLowerCase().contains("remote")) {
            return true;
        }

        if (job.categories() != null && job.categories().location() != null) {
            String location = job.categories().location().toLowerCase();
            return ALLOWED_LOCATIONS.stream().anyMatch(location::contains);
        }

        return false;
    }
}