package com.jobaggregator.core.repository;

import com.jobaggregator.core.entity.JobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<JobEntity, Long> {

    boolean existsByAtsJobId(String atsJobId);
}