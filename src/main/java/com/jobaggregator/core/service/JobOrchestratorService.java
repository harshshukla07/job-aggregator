package com.jobaggregator.core.service;

import com.jobaggregator.core.dto.StandardJob;
import com.jobaggregator.core.entity.JobEntity;
import com.jobaggregator.core.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class JobOrchestratorService {

    private final List<AtsProvider> atsProviders;
    private final JobFilterService jobFilterService;
    private final JobRepository jobRepository;
    private final TelegramNotificationService telegramService;
    private final CompanyDirectoryService directoryService;

    public JobOrchestratorService(List<AtsProvider> atsProviders, JobFilterService jobFilterService,
                                  JobRepository jobRepository, TelegramNotificationService telegramService,
                                  CompanyDirectoryService directoryService) {
        this.atsProviders = atsProviders;
        this.jobFilterService = jobFilterService;
        this.jobRepository = jobRepository;
        this.telegramService = telegramService;
        this.directoryService = directoryService;
    }

    public void runPipeline() {
        System.out.println("🚀 Starting Multi-ATS Job Aggregation Pipeline...");

        // ADDED: Fetch the directory dynamically from the cloud!
        Map<String, String> dynamicDirectory = directoryService.fetchDirectory();

        if (dynamicDirectory.isEmpty()) {
            System.out.println("⚠️ Company directory is empty or failed to load. Aborting pipeline.");
            return;
        }

        int totalScanned = 0;
        int totalPassed = 0;

        for (Map.Entry<String, String> entry : dynamicDirectory.entrySet()) {
            String companySlug = entry.getKey();
            String targetAts = entry.getValue();

            AtsProvider provider = atsProviders.stream()
                    .filter(p -> p.getProviderName().equalsIgnoreCase(targetAts))
                    .findFirst()
                    .orElse(null);

            if (provider == null) {
                System.out.println("⚠️ Unknown ATS provider: " + targetAts);
                continue;
            }

            List<StandardJob> jobs = provider.fetchJobs(companySlug);

            if (jobs.isEmpty()) continue;

            System.out.println("📥 Fetched " + jobs.size() + " jobs from " + companySlug.toUpperCase() + " (" + targetAts + ")");
            totalScanned += jobs.size();

            for (StandardJob job : jobs) {
                if (jobFilterService.isPerfectMatch(job)) {
                    totalPassed++;

                    if (!jobRepository.existsByAtsJobId(job.atsJobId())) {
                        jobRepository.save(new JobEntity(job.atsJobId(), job.companyName(), job.title()));
                        telegramService.sendJobAlert(job);
                    } else {
                        System.out.println("⏩ Skipped duplicate job already in DB: " + job.title());
                    }
                }
            }
        }

        System.out.println("========================================");
        System.out.println("📊 PIPELINE SUMMARY:");
        System.out.println("Total Jobs Scanned: " + totalScanned);
        System.out.println("Jobs Passed Filter: " + totalPassed);
        System.out.println("✅ Pipeline Execution Completed.");
        System.out.println("========================================");
    }
}