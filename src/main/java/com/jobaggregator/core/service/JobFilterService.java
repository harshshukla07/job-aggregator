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

    private static final Pattern SENIORITY_REGEX = Pattern.compile("(?i)\\b([3-9]|1[0-9])\\+?\\s*years?\\b");

    public boolean isPerfectMatch(LeverJobDto job) {
        if (job == null || job.text() == null || job.descriptionPlain() == null) {
            return false;
        }

        String title = job.text().toLowerCase();
        String description = job.descriptionPlain().toLowerCase();

        boolean hasBlockedTitle = BLOCKED_TITLES.stream().anyMatch(title::contains);
        if (hasBlockedTitle) return false;

        boolean hasAllowedTitle = ALLOWED_TITLES.stream().anyMatch(title::contains);
        if (!hasAllowedTitle) return false;

        if (SENIORITY_REGEX.matcher(description).find()) return false;

        return REQUIRED_SKILLS.stream().anyMatch(description::contains);
    }
}