package com.livingcostcheck.home_repair.repository;

import com.livingcostcheck.home_repair.domain.EventLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventLogRepository extends JpaRepository<EventLog, Long> {
}
