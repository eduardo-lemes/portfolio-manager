package com.portfolio.manager.controller;

import com.portfolio.manager.dto.PortfolioReportResponse;
import com.portfolio.manager.service.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportService service;

    @InjectMocks
    private ReportController controller;

    @Test
    void portfolio_delegatesToServiceAndReturnsReport() {
        PortfolioReportResponse report = new PortfolioReportResponse(
                Map.of("EM_ANALISE", 2L),
                Map.of("EM_ANALISE", new BigDecimal("80000")),
                30.0,
                3L
        );
        when(service.generatePortfolioReport()).thenReturn(report);

        PortfolioReportResponse result = controller.portfolio();

        assertThat(result.totalUniqueMembers()).isEqualTo(3L);
        assertThat(result.averageClosedDurationDays()).isEqualTo(30.0);
    }
}
