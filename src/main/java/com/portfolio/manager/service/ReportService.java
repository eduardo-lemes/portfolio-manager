package com.portfolio.manager.service;

import com.portfolio.manager.dto.PortfolioReportResponse;
import com.portfolio.manager.entity.Project;
import com.portfolio.manager.entity.ProjectStatus;
import com.portfolio.manager.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ProjectRepository projectRepository;

    public PortfolioReportResponse generatePortfolioReport() {
        List<Project> all = projectRepository.findAll();

        Map<String, Long> byStatus = all.stream()
                .collect(Collectors.groupingBy(p -> p.getStatus().name(), Collectors.counting()));

        Map<String, BigDecimal> budgetByStatus = all.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getStatus().name(),
                        Collectors.reducing(BigDecimal.ZERO, Project::getTotalBudget, BigDecimal::add)
                ));

        double avgDuration = all.stream()
                .filter(p -> p.getStatus() == ProjectStatus.ENCERRADO && p.getActualEndDate() != null)
                .mapToLong(p -> ChronoUnit.DAYS.between(p.getStartDate(), p.getActualEndDate()))
                .average()
                .orElse(0.0);

        long uniqueMembers = all.stream()
                .flatMap(p -> p.getMembers().stream())
                .map(m -> m.getId())
                .distinct()
                .count();

        return new PortfolioReportResponse(byStatus, budgetByStatus, avgDuration, uniqueMembers);
    }
}
