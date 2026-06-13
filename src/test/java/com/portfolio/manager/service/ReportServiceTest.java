package com.portfolio.manager.service;

import com.portfolio.manager.dto.PortfolioReportResponse;
import com.portfolio.manager.entity.Member;
import com.portfolio.manager.entity.MemberRole;
import com.portfolio.manager.entity.Project;
import com.portfolio.manager.entity.ProjectStatus;
import com.portfolio.manager.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ReportService reportService;

    private Member membro1;
    private Member membro2;

    @BeforeEach
    void setUp() {
        membro1 = new Member();
        membro1.setId(1L);
        membro1.setName("Ana");
        membro1.setRole(MemberRole.FUNCIONARIO);

        membro2 = new Member();
        membro2.setId(2L);
        membro2.setName("Carlos");
        membro2.setRole(MemberRole.FUNCIONARIO);
    }

    private Project buildProject(ProjectStatus status, BigDecimal budget, LocalDate start, LocalDate end, LocalDate actualEnd, Set<Member> members) {
        Project p = new Project();
        p.setStatus(status);
        p.setTotalBudget(budget);
        p.setStartDate(start);
        p.setExpectedEndDate(end);
        p.setActualEndDate(actualEnd);
        p.setMembers(members);
        return p;
    }

    @Test
    void generatePortfolioReport_countsProjectsByStatus() {
        LocalDate today = LocalDate.now();
        Project p1 = buildProject(ProjectStatus.EM_ANALISE, new BigDecimal("50000"), today, today.plusMonths(2), null, new HashSet<>());
        Project p2 = buildProject(ProjectStatus.EM_ANALISE, new BigDecimal("30000"), today, today.plusMonths(1), null, new HashSet<>());
        Project p3 = buildProject(ProjectStatus.ENCERRADO, new BigDecimal("200000"), today, today.plusMonths(4), today.plusMonths(3), new HashSet<>());
        when(projectRepository.findAll()).thenReturn(List.of(p1, p2, p3));

        PortfolioReportResponse report = reportService.generatePortfolioReport();

        assertThat(report.projectsByStatus().get("EM_ANALISE")).isEqualTo(2L);
        assertThat(report.projectsByStatus().get("ENCERRADO")).isEqualTo(1L);
    }

    @Test
    void generatePortfolioReport_sumsBudgetByStatus() {
        LocalDate today = LocalDate.now();
        Project p1 = buildProject(ProjectStatus.EM_ANALISE, new BigDecimal("50000"), today, today.plusMonths(2), null, new HashSet<>());
        Project p2 = buildProject(ProjectStatus.EM_ANALISE, new BigDecimal("30000"), today, today.plusMonths(2), null, new HashSet<>());
        when(projectRepository.findAll()).thenReturn(List.of(p1, p2));

        PortfolioReportResponse report = reportService.generatePortfolioReport();

        assertThat(report.budgetByStatus().get("EM_ANALISE")).isEqualByComparingTo(new BigDecimal("80000"));
    }

    @Test
    void generatePortfolioReport_averageDurationOfClosedProjects() {
        LocalDate start = LocalDate.of(2024, 1, 1);
        LocalDate actualEnd = LocalDate.of(2024, 1, 31);
        Project p = buildProject(ProjectStatus.ENCERRADO, new BigDecimal("10000"), start, actualEnd, actualEnd, new HashSet<>());
        when(projectRepository.findAll()).thenReturn(List.of(p));

        PortfolioReportResponse report = reportService.generatePortfolioReport();

        assertThat(report.averageClosedDurationDays()).isEqualTo(30.0);
    }

    @Test
    void generatePortfolioReport_ignoresClosedWithoutActualEndDate() {
        LocalDate today = LocalDate.now();
        Project p = buildProject(ProjectStatus.ENCERRADO, new BigDecimal("10000"), today, today.plusMonths(1), null, new HashSet<>());
        when(projectRepository.findAll()).thenReturn(List.of(p));

        PortfolioReportResponse report = reportService.generatePortfolioReport();

        assertThat(report.averageClosedDurationDays()).isEqualTo(0.0);
    }

    @Test
    void generatePortfolioReport_countsUniqueMembersAcrossProjects() {
        LocalDate today = LocalDate.now();
        Set<Member> membros1 = new HashSet<>(Set.of(membro1, membro2));
        Set<Member> membros2 = new HashSet<>(Set.of(membro1));
        Project p1 = buildProject(ProjectStatus.EM_ANDAMENTO, new BigDecimal("10000"), today, today.plusMonths(1), null, membros1);
        Project p2 = buildProject(ProjectStatus.EM_ANDAMENTO, new BigDecimal("10000"), today, today.plusMonths(1), null, membros2);
        when(projectRepository.findAll()).thenReturn(List.of(p1, p2));

        PortfolioReportResponse report = reportService.generatePortfolioReport();

        assertThat(report.totalUniqueMembers()).isEqualTo(2L);
    }

    @Test
    void generatePortfolioReport_emptyProjectList_returnsZeroes() {
        when(projectRepository.findAll()).thenReturn(List.of());

        PortfolioReportResponse report = reportService.generatePortfolioReport();

        assertThat(report.projectsByStatus()).isEmpty();
        assertThat(report.budgetByStatus()).isEmpty();
        assertThat(report.averageClosedDurationDays()).isEqualTo(0.0);
        assertThat(report.totalUniqueMembers()).isEqualTo(0L);
    }
}
