package com.portfolio.manager.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class ProjectStatusTest {

    @Test
    void canTransitionTo_validSequence_returnsTrue() {
        assertThat(ProjectStatus.EM_ANALISE.canTransitionTo(ProjectStatus.ANALISE_REALIZADA)).isTrue();
        assertThat(ProjectStatus.ANALISE_REALIZADA.canTransitionTo(ProjectStatus.ANALISE_APROVADA)).isTrue();
        assertThat(ProjectStatus.ANALISE_APROVADA.canTransitionTo(ProjectStatus.INICIADO)).isTrue();
        assertThat(ProjectStatus.INICIADO.canTransitionTo(ProjectStatus.PLANEJADO)).isTrue();
        assertThat(ProjectStatus.PLANEJADO.canTransitionTo(ProjectStatus.EM_ANDAMENTO)).isTrue();
        assertThat(ProjectStatus.EM_ANDAMENTO.canTransitionTo(ProjectStatus.ENCERRADO)).isTrue();
    }

    @Test
    void canTransitionTo_skipStep_returnsFalse() {
        assertThat(ProjectStatus.EM_ANALISE.canTransitionTo(ProjectStatus.INICIADO)).isFalse();
        assertThat(ProjectStatus.EM_ANALISE.canTransitionTo(ProjectStatus.ENCERRADO)).isFalse();
    }

    @ParameterizedTest
    @EnumSource(ProjectStatus.class)
    void canTransitionTo_cancelado_alwaysTrue(ProjectStatus status) {
        assertThat(status.canTransitionTo(ProjectStatus.CANCELADO)).isTrue();
    }
}
