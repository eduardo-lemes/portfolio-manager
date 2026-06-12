package com.portfolio.manager.entity;

import java.util.List;

public enum ProjectStatus {
    EM_ANALISE(List.of()),
    ANALISE_REALIZADA(List.of()),
    ANALISE_APROVADA(List.of()),
    INICIADO(List.of()),
    PLANEJADO(List.of()),
    EM_ANDAMENTO(List.of()),
    ENCERRADO(List.of()),
    CANCELADO(List.of());

    private final List<ProjectStatus> allowed;

    static {
        EM_ANALISE.allowed.addAll(List.of(ANALISE_REALIZADA));
        ANALISE_REALIZADA.allowed.addAll(List.of(ANALISE_APROVADA));
        ANALISE_APROVADA.allowed.addAll(List.of(INICIADO));
        INICIADO.allowed.addAll(List.of(PLANEJADO));
        PLANEJADO.allowed.addAll(List.of(EM_ANDAMENTO));
        EM_ANDAMENTO.allowed.addAll(List.of(ENCERRADO));
    }

    ProjectStatus(List<ProjectStatus> allowed) {
        this.allowed = new java.util.ArrayList<>(allowed);
    }

    public boolean canTransitionTo(ProjectStatus next) {
        return next == CANCELADO || allowed.contains(next);
    }
}
