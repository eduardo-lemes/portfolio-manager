package com.portfolio.manager.dto;

import com.portfolio.manager.entity.ProjectStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ProjectRequest(
        @NotBlank String name,
        String description,
        @NotNull LocalDate startDate,
        @NotNull LocalDate expectedEndDate,
        LocalDate actualEndDate,
        @NotNull @DecimalMin("0.01") BigDecimal totalBudget,
        @NotNull Long managerId
) {}
