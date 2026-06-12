package com.portfolio.manager.dto;

import java.math.BigDecimal;
import java.util.Map;

public record PortfolioReportResponse(
        Map<String, Long> projectsByStatus,
        Map<String, BigDecimal> budgetByStatus,
        Double averageClosedDurationDays,
        long totalUniqueMembers
) {}
