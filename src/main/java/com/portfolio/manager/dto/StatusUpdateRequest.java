package com.portfolio.manager.dto;

import com.portfolio.manager.entity.ProjectStatus;
import jakarta.validation.constraints.NotNull;

public record StatusUpdateRequest(
        @NotNull ProjectStatus status
) {}
