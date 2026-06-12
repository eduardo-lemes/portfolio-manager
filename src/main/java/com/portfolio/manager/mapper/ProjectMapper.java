package com.portfolio.manager.mapper;

import com.portfolio.manager.dto.ProjectRequest;
import com.portfolio.manager.dto.ProjectResponse;
import com.portfolio.manager.entity.Member;
import com.portfolio.manager.entity.Project;
import com.portfolio.manager.entity.RiskLevel;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = MemberMapper.class)
public interface ProjectMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "manager", ignore = true)
    Project toEntity(ProjectRequest request);

    @Mapping(target = "riskLevel", ignore = true)
    ProjectResponse toResponse(Project project);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "manager", ignore = true)
    void updateFromRequest(ProjectRequest request, @MappingTarget Project project);
}
