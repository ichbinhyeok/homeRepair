package com.livingcostcheck.home_repair.repository;

import com.livingcostcheck.home_repair.domain.VerdictHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface HomeRepairRepository extends JpaRepository<VerdictHistory, UUID> {
}
