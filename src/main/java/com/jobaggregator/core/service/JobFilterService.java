package com.jobaggregator.core.service;

import com.jobaggregator.core.dto.LeverJobDto;
import com.jobaggregator.core.dto.StandardJob;
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

    public boolean isPerfectMatch(StandardJob job) {
        if (job == null || job.title() == null || job.description() == null) {
            return false;
        }

        String title = job.title().toLowerCase();
        String description = job.description().toLowerCase();

        if (BLOCKED_TITLES.stream().anyMatch(title::contains)) return false;
        if (ALLOWED_TITLES.stream().noneMatch(title::contains)) return false;
        if (SENIORITY_REGEX.matcher(description).find()) return false;
        if (REQUIRED_SKILLS.stream().noneMatch(description::contains)) return false;

        return isLocationMatch(job);
    }

    private boolean isLocationMatch(StandardJob job) {
        if (job.isRemote()) {
            return true;
        }

        if (job.location() != null) {
            String location = job.location().toLowerCase();
            return ALLOWED_LOCATIONS.stream().anyMatch(location::contains);
        }

        return false;
    }
}