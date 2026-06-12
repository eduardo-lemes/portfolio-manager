package com.portfolio.manager.service;

import com.portfolio.manager.dto.ProjectRequest;
import com.portfolio.manager.dto.ProjectResponse;
import com.portfolio.manager.entity.*;
import com.portfolio.manager.exception.BusinessException;
import com.portfolio.manager.exception.ResourceNotFoundException;
import com.portfolio.manager.mapper.ProjectMapper;
import com.portfolio.manager.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MemberService memberService;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    private Member manager;
    private Member funcionario;
    private Project project;

    @BeforeEach
    void setUp() {
        manager = new Member();
        manager.setId(1L);
        manager.setName("Ana");
        manager.setRole(MemberRole.GERENTE);

        funcionario = new Member();
        funcionario.setId(2L);
        funcionario.setName("João");
        funcionario.setRole(MemberRole.FUNCIONARIO);

        project = new Project();
        project.setId(1L);
        project.setName("Projeto Teste");
        project.setStartDate(LocalDate.now());
        project.setExpectedEndDate(LocalDate.now().plusMonths(2));
        project.setTotalBudget(new BigDecimal("50000"));
        project.setStatus(ProjectStatus.EM_ANALISE);
        project.setManager(manager);
        project.setMembers(new HashSet<>());
    }

    @Test
    void calculateRisk_lowBudgetShortDeadline_returnsLow() {
        project.setTotalBudget(new BigDecimal("50000"));
        project.setExpectedEndDate(project.getStartDate().plusMonths(2));

        RiskLevel risk = projectService.calculateRisk(project);

        assertThat(risk).isEqualTo(RiskLevel.BAIXO);
    }

    @Test
    void calculateRisk_highBudget_returnsHigh() {
        project.setTotalBudget(new BigDecimal("600000"));
        project.setExpectedEndDate(project.getStartDate().plusMonths(2));

        RiskLevel risk = projectService.calculateRisk(project);

        assertThat(risk).isEqualTo(RiskLevel.ALTO);
    }

    @Test
    void calculateRisk_longDeadline_returnsHigh() {
        project.setTotalBudget(new BigDecimal("50000"));
        project.setExpectedEndDate(project.getStartDate().plusMonths(8));

        RiskLevel risk = projectService.calculateRisk(project);

        assertThat(risk).isEqualTo(RiskLevel.ALTO);
    }

    @Test
    void calculateRisk_mediumBudget_returnsMedium() {
        project.setTotalBudget(new BigDecimal("200000"));
        project.setExpectedEndDate(project.getStartDate().plusMonths(2));

        RiskLevel risk = projectService.calculateRisk(project);

        assertThat(risk).isEqualTo(RiskLevel.MEDIO);
    }

    @Test
    void calculateRisk_mediumDeadline_returnsMedium() {
        project.setTotalBudget(new BigDecimal("50000"));
        project.setExpectedEndDate(project.getStartDate().plusMonths(4));

        RiskLevel risk = projectService.calculateRisk(project);

        assertThat(risk).isEqualTo(RiskLevel.MEDIO);
    }

    @Test
    void updateStatus_validTransition_updatesStatus() {
        project.setStatus(ProjectStatus.EM_ANALISE);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any())).thenReturn(project);
        when(projectMapper.toResponse(any())).thenReturn(mock(ProjectResponse.class));

        projectService.updateStatus(1L, ProjectStatus.ANALISE_REALIZADA);

        assertThat(project.getStatus()).isEqualTo(ProjectStatus.ANALISE_REALIZADA);
    }

    @Test
    void updateStatus_invalidTransition_throwsBusinessException() {
        project.setStatus(ProjectStatus.EM_ANALISE);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.updateStatus(1L, ProjectStatus.ENCERRADO))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void updateStatus_cancelledFromAnyStatus_succeeds() {
        project.setStatus(ProjectStatus.EM_ANDAMENTO);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any())).thenReturn(project);
        when(projectMapper.toResponse(any())).thenReturn(mock(ProjectResponse.class));

        projectService.updateStatus(1L, ProjectStatus.CANCELADO);

        assertThat(project.getStatus()).isEqualTo(ProjectStatus.CANCELADO);
    }

    @ParameterizedTest
    @EnumSource(value = ProjectStatus.class, names = {"INICIADO", "EM_ANDAMENTO", "ENCERRADO"})
    void delete_blockedStatus_throwsBusinessException(ProjectStatus status) {
        project.setStatus(status);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThatThrownBy(() -> projectService.delete(1L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void delete_statusEmAnalise_succeeds() {
        project.setStatus(ProjectStatus.EM_ANALISE);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        assertThatCode(() -> projectService.delete(1L)).doesNotThrowAnyException();
        verify(projectRepository).delete(project);
    }

    @Test
    void addMember_roleNotFuncionario_throwsBusinessException() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberService.getOrThrow(1L)).thenReturn(manager);

        assertThatThrownBy(() -> projectService.addMember(1L, 1L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void addMember_projectFull_throwsBusinessException() {
        Set<Member> members = new HashSet<>();
        for (long i = 0; i < 10; i++) {
            Member m = new Member();
            m.setId(i);
            m.setRole(MemberRole.FUNCIONARIO);
            members.add(m);
        }
        project.setMembers(members);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberService.getOrThrow(2L)).thenReturn(funcionario);

        assertThatThrownBy(() -> projectService.addMember(1L, 2L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void addMember_memberInTooManyActiveProjects_throwsBusinessException() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberService.getOrThrow(2L)).thenReturn(funcionario);
        when(projectRepository.countActiveProjectsByMember(2L)).thenReturn(3L);

        assertThatThrownBy(() -> projectService.addMember(1L, 2L))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void addMember_validFuncionario_addsMember() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(memberService.getOrThrow(2L)).thenReturn(funcionario);
        when(projectRepository.countActiveProjectsByMember(2L)).thenReturn(1L);
        when(projectRepository.save(any())).thenReturn(project);
        when(projectMapper.toResponse(any())).thenReturn(mock(ProjectResponse.class));

        projectService.addMember(1L, 2L);

        assertThat(project.getMembers()).contains(funcionario);
    }
}
