package com.portfolio.manager.dto;

import com.portfolio.manager.entity.MemberRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MemberRequest(
        @NotBlank String name,
        @NotNull MemberRole role
) {}
