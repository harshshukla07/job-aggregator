package com.jobaggregator.core.service;

import com.jobaggregator.core.dto.StandardJob;
import java.util.List;

public interface AtsProvider {

    String getProviderName();
    List<StandardJob> fetchJobs(String companySlug);
}