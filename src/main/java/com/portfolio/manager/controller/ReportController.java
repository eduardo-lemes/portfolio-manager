package com.portfolio.manager.controller;

import com.portfolio.manager.dto.PortfolioReportResponse;
import com.portfolio.manager.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/relatorio")
@RequiredArgsConstructor
@Tag(name = "Relatório", description = "Relatório resumido do portfólio")
public class ReportController {

    private final ReportService service;

    @GetMapping("/portfolio")
    @Operation(summary = "Gera relatório resumido do portfólio")
    public PortfolioReportResponse portfolio() {
        return service.generatePortfolioReport();
    }
}
