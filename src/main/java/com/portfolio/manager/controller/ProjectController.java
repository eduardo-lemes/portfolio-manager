package com.portfolio.manager.controller;

import com.portfolio.manager.dto.ProjectRequest;
import com.portfolio.manager.dto.ProjectResponse;
import com.portfolio.manager.dto.StatusUpdateRequest;
import com.portfolio.manager.entity.ProjectStatus;
import com.portfolio.manager.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projetos")
@RequiredArgsConstructor
@Tag(name = "Projetos", description = "Gerenciamento de projetos do portfólio")
public class ProjectController {

    private final ProjectService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria um novo projeto")
    public ProjectResponse create(@Valid @RequestBody ProjectRequest request) {
        return service.create(request);
    }

    @GetMapping
    @Operation(summary = "Lista projetos com paginação e filtros")
    public Page<ProjectResponse> findAll(
            @RequestParam(required = false) ProjectStatus status,
            @RequestParam(required = false) Long managerId,
            @RequestParam(required = false) String name,
            @PageableDefault(size = 10, sort = "name") Pageable pageable
    ) {
        return service.findAll(status, managerId, name, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca projeto por ID")
    public ProjectResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza um projeto")
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody ProjectRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove um projeto")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualiza o status do projeto")
    public ProjectResponse updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        return service.updateStatus(id, request.status());
    }

    @PostMapping("/{id}/membros/{membroId}")
    @Operation(summary = "Associa um membro ao projeto")
    public ProjectResponse addMember(@PathVariable Long id, @PathVariable Long membroId) {
        return service.addMember(id, membroId);
    }

    @DeleteMapping("/{id}/membros/{membroId}")
    @Operation(summary = "Remove um membro do projeto")
    public ProjectResponse removeMember(@PathVariable Long id, @PathVariable Long membroId) {
        return service.removeMember(id, membroId);
    }
}
