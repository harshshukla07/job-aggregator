package com.jobaggregator.core.service;

import com.jobaggregator.core.dto.LeverJobDto;
import com.jobaggregator.core.entity.JobEntity;
import com.jobaggregator.core.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobOrchestratorService {

    private final LeverApiService leverApiService;
    private final JobFilterService jobFilterService;
    private final JobRepository jobRepository;
    private final TelegramNotificationService telegramService;

    private static final List<String> TARGET_COMPANIES = List.of(
            "netflix", "canva", "figma", "palantir", "openai", "lyft", "twitch"
    );

    public JobOrchestratorService(LeverApiService leverApiService, JobFilterService jobFilterService,
                                  JobRepository jobRepository, TelegramNotificationService telegramService) {
        this.leverApiService = leverApiService;
        this.jobFilterService = jobFilterService;
        this.jobRepository = jobRepository;
        this.telegramService = telegramService;
    }

    public void runPipeline() {
        System.out.println("🚀 Starting Job Aggregation Pipeline...");

        for (String company : TARGET_COMPANIES) {
            List<LeverJobDto> jobs = leverApiService.fetchJobs(company);

            for (LeverJobDto job : jobs) {
                if (jobFilterService.isPerfectMatch(job)) {

                    if (!jobRepository.existsByAtsJobId(job.id())) {
                        jobRepository.save(new JobEntity(job.id(), company, job.text()));
                        telegramService.sendJobAlert(job, company);
                    }
                }
            }
        }
        System.out.println("✅ Pipeline Execution Completed.");
    }
}