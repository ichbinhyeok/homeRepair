package com.livingcostcheck.home_repair.repository;

import com.livingcostcheck.home_repair.domain.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventLogRepository extends JpaRepository<EventLog, Long> {
    List<EventLog> findByCreatedAtAfter(LocalDateTime createdAt);
    List<EventLog> findByVerdictIdOrderByCreatedAtAsc(java.util.UUID verdictId);
}
