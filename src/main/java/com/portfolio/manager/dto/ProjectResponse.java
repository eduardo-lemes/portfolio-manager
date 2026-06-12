package com.portfolio.manager.dto;

import com.portfolio.manager.entity.ProjectStatus;
import com.portfolio.manager.entity.RiskLevel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        LocalDate startDate,
        LocalDate expectedEndDate,
        LocalDate actualEndDate,
        BigDecimal totalBudget,
        ProjectStatus status,
        RiskLevel riskLevel,
        MemberResponse manager,
        Set<MemberResponse> members
) {}
