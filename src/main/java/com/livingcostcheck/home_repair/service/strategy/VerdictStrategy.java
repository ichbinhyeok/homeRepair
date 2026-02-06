package com.livingcostcheck.home_repair.service.strategy;

import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.RiskAdjustedItem;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.UserContext;
import com.livingcostcheck.home_repair.service.dto.verdict.VerdictDTOs.StrategyType;
import java.util.List;

public interface VerdictStrategy {

    StrategyType getStrategyType();

    /**
     * Filters and prioritizes items based on the specific strategy.
     * 
     * @param items   Initial list of risk-adjusted items
     * @param context User context (budget, goals, etc.)
     * @return Filtered and sorted list of items
     */
    List<RiskAdjustedItem> execute(List<RiskAdjustedItem> items, UserContext context);

    /**
     * Determines if this strategy is applicable for the current context.
     */
    boolean isEligible(UserContext context);
}
