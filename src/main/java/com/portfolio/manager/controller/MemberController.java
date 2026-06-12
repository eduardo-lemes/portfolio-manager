package com.portfolio.manager.controller;

import com.portfolio.manager.dto.MemberRequest;
import com.portfolio.manager.dto.MemberResponse;
import com.portfolio.manager.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membros")
@RequiredArgsConstructor
@Tag(name = "Membros", description = "API externa mockada para cadastro e consulta de membros")
public class MemberController {

    private final MemberService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria um novo membro")
    public MemberResponse create(@Valid @RequestBody MemberRequest request) {
        return service.create(request);
    }

    @GetMapping
    @Operation(summary = "Lista todos os membros")
    public List<MemberResponse> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca membro por ID")
    public MemberResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }
}
