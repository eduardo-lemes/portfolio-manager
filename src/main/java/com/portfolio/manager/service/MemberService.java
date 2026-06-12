package com.portfolio.manager.service;

import com.portfolio.manager.dto.MemberRequest;
import com.portfolio.manager.dto.MemberResponse;
import com.portfolio.manager.entity.Member;
import com.portfolio.manager.exception.ResourceNotFoundException;
import com.portfolio.manager.mapper.MemberMapper;
import com.portfolio.manager.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository repository;
    private final MemberMapper mapper;

    public MemberResponse create(MemberRequest request) {
        return mapper.toResponse(repository.save(mapper.toEntity(request)));
    }

    public List<MemberResponse> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public MemberResponse findById(Long id) {
        return mapper.toResponse(getOrThrow(id));
    }

    public Member getOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado: " + id));
    }
}
