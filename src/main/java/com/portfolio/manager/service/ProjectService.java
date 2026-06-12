package com.portfolio.manager.service;

import com.portfolio.manager.dto.ProjectRequest;
import com.portfolio.manager.dto.ProjectResponse;
import com.portfolio.manager.entity.*;
import com.portfolio.manager.exception.BusinessException;
import com.portfolio.manager.mapper.ProjectMapper;
import com.portfolio.manager.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private static final BigDecimal LOW_BUDGET_LIMIT = new BigDecimal("100000");
    private static final BigDecimal HIGH_BUDGET_LIMIT = new BigDecimal("500000");
    private static final long LOW_MONTHS_LIMIT = 3;
    private static final long HIGH_MONTHS_LIMIT = 6;
    private static final int MAX_MEMBERS = 10;
    private static final long MAX_ACTIVE_PROJECTS_PER_MEMBER = 3;

    private final ProjectRepository projectRepository;
    private final MemberService memberService;
    private final ProjectMapper projectMapper;

    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        Member manager = memberService.getOrThrow(request.managerId());
        Project project = projectMapper.toEntity(request);
        project.setManager(manager);
        project.setStatus(ProjectStatus.EM_ANALISE);
        return toResponse(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = getOrThrow(id);
        Member manager = memberService.getOrThrow(request.managerId());
        projectMapper.updateFromRequest(request, project);
        project.setManager(manager);
        return toResponse(projectRepository.save(project));
    }

    public Page<ProjectResponse> findAll(ProjectStatus status, Long managerId, String name, Pageable pageable) {
        return projectRepository.findWithFilters(status, managerId, name, pageable)
                .map(this::toResponse);
    }

    public ProjectResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional
    public ProjectResponse updateStatus(Long id, ProjectStatus newStatus) {
        Project project = getOrThrow(id);
        if (!project.getStatus().canTransitionTo(newStatus)) {
            throw new BusinessException(
                    "Transição inválida: " + project.getStatus() + " → " + newStatus
            );
        }
        project.setStatus(newStatus);
        return toResponse(projectRepository.save(project));
    }

    @Transactional
    public void delete(Long id) {
        Project project = getOrThrow(id);
        Set<ProjectStatus> blocked = Set.of(ProjectStatus.INICIADO, ProjectStatus.EM_ANDAMENTO, ProjectStatus.ENCERRADO);
        if (blocked.contains(project.getStatus())) {
            throw new BusinessException("Projeto com status '" + project.getStatus() + "' não pode ser excluído.");
        }
        projectRepository.delete(project);
    }

    @Transactional
    public ProjectResponse addMember(Long projectId, Long memberId) {
        Project project = getOrThrow(projectId);
        Member member = memberService.getOrThrow(memberId);

        if (member.getRole() != MemberRole.FUNCIONARIO) {
            throw new BusinessException("Apenas funcionários podem ser associados a projetos.");
        }
        if (project.getMembers().size() >= MAX_MEMBERS) {
            throw new BusinessException("O projeto já atingiu o limite de " + MAX_MEMBERS + " membros.");
        }
        if (projectRepository.countActiveProjectsByMember(memberId) >= MAX_ACTIVE_PROJECTS_PER_MEMBER) {
            throw new BusinessException("O membro já está alocado em " + MAX_ACTIVE_PROJECTS_PER_MEMBER + " projetos ativos.");
        }

        project.getMembers().add(member);
        return toResponse(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse removeMember(Long projectId, Long memberId) {
        Project project = getOrThrow(projectId);
        Member member = memberService.getOrThrow(memberId);
        project.getMembers().remove(member);
        return toResponse(projectRepository.save(project));
    }

    public RiskLevel calculateRisk(Project project) {
        long months = ChronoUnit.MONTHS.between(project.getStartDate(), project.getExpectedEndDate());
        BigDecimal budget = project.getTotalBudget();

        if (budget.compareTo(HIGH_BUDGET_LIMIT) > 0 || months > HIGH_MONTHS_LIMIT) {
            return RiskLevel.ALTO;
        }
        if (budget.compareTo(LOW_BUDGET_LIMIT) > 0 || months > LOW_MONTHS_LIMIT) {
            return RiskLevel.MEDIO;
        }
        return RiskLevel.BAIXO;
    }

    private ProjectResponse toResponse(Project project) {
        ProjectResponse response = projectMapper.toResponse(project);
        return new ProjectResponse(
                response.id(),
                response.name(),
                response.description(),
                response.startDate(),
                response.expectedEndDate(),
                response.actualEndDate(),
                response.totalBudget(),
                response.status(),
                calculateRisk(project),
                response.manager(),
                response.members()
        );
    }

    public Project getOrThrow(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new com.portfolio.manager.exception.ResourceNotFoundException("Projeto não encontrado: " + id));
    }
}
