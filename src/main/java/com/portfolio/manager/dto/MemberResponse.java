package com.portfolio.manager.dto;

import com.portfolio.manager.entity.MemberRole;

public record MemberResponse(
        Long id,
        String name,
        MemberRole role
) {}
