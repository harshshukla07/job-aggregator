package com.jobaggregator.core.controller;

import com.jobaggregator.core.service.JobOrchestratorService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aggregator")
public class AggregatorController {

    private final JobOrchestratorService orchestratorService;

    @Value("${app.api.trigger-secret}")
    private String expectedSecret;

    public AggregatorController(JobOrchestratorService orchestratorService) {
        this.orchestratorService = orchestratorService;
    }

    @PostMapping("/sync")
    public ResponseEntity<String> triggerSync(@RequestHeader(value = "X-API-SECRET", required = false) String providedSecret) {

        if (providedSecret == null || !expectedSecret.equals(providedSecret)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: Invalid Security Token");
        }

        Thread.startVirtualThread(orchestratorService::runPipeline);

        return ResponseEntity.ok("Pipeline triggered successfully. Execution running in background.");
    }
}