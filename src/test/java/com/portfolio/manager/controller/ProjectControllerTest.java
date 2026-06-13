package com.portfolio.manager.controller;

import com.portfolio.manager.dto.ProjectRequest;
import com.portfolio.manager.dto.ProjectResponse;
import com.portfolio.manager.dto.StatusUpdateRequest;
import com.portfolio.manager.dto.MemberResponse;
import com.portfolio.manager.entity.ProjectStatus;
import com.portfolio.manager.entity.RiskLevel;
import com.portfolio.manager.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService service;

    @InjectMocks
    private ProjectController controller;

    private ProjectResponse projectResponse;
    private ProjectRequest projectRequest;

    @BeforeEach
    void setUp() {
        projectResponse = new ProjectResponse(
                1L, "Projeto A", "Descrição", LocalDate.now(),
                LocalDate.now().plusMonths(2), null,
                new BigDecimal("50000"), ProjectStatus.EM_ANALISE,
                RiskLevel.BAIXO, null, Set.of()
        );

        projectRequest = new ProjectRequest(
                "Projeto A", "Descrição", LocalDate.now(), LocalDate.now().plusMonths(2),
                null, new BigDecimal("50000"), 1L
        );
    }

    @Test
    void create_validRequest_returnsResponse() {
        when(service.create(projectRequest)).thenReturn(projectResponse);

        ProjectResponse result = controller.create(projectRequest);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Projeto A");
        verify(service).create(projectRequest);
    }

    @Test
    void findAll_withFilters_delegatesToService() {
        var page = new PageImpl<>(List.of(projectResponse));
        when(service.findAll(any(), any(), any(), any(Pageable.class))).thenReturn(page);

        var result = controller.findAll(null, null, null, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void findById_existingId_returnsResponse() {
        when(service.findById(1L)).thenReturn(projectResponse);

        assertThat(controller.findById(1L).id()).isEqualTo(1L);
    }

    @Test
    void update_validRequest_returnsUpdatedResponse() {
        when(service.update(1L, projectRequest)).thenReturn(projectResponse);

        assertThat(controller.update(1L, projectRequest).name()).isEqualTo("Projeto A");
    }

    @Test
    void delete_callsService() {
        controller.delete(1L);

        verify(service).delete(1L);
    }

    @Test
    void updateStatus_delegatesToService() {
        StatusUpdateRequest req = new StatusUpdateRequest(ProjectStatus.ANALISE_REALIZADA);
        when(service.updateStatus(1L, ProjectStatus.ANALISE_REALIZADA)).thenReturn(projectResponse);

        assertThat(controller.updateStatus(1L, req)).isEqualTo(projectResponse);
    }

    @Test
    void addMember_delegatesToService() {
        when(service.addMember(1L, 2L)).thenReturn(projectResponse);

        assertThat(controller.addMember(1L, 2L)).isEqualTo(projectResponse);
    }

    @Test
    void removeMember_delegatesToService() {
        when(service.removeMember(1L, 2L)).thenReturn(projectResponse);

        assertThat(controller.removeMember(1L, 2L)).isEqualTo(projectResponse);
    }
}
