package com.portfolio.manager.service;

import com.portfolio.manager.dto.MemberRequest;
import com.portfolio.manager.dto.MemberResponse;
import com.portfolio.manager.entity.Member;
import com.portfolio.manager.entity.MemberRole;
import com.portfolio.manager.exception.ResourceNotFoundException;
import com.portfolio.manager.mapper.MemberMapper;
import com.portfolio.manager.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository repository;

    @Mock
    private MemberMapper mapper;

    @InjectMocks
    private MemberService memberService;

    private Member member;
    private MemberResponse memberResponse;

    @BeforeEach
    void setUp() {
        member = new Member();
        member.setId(1L);
        member.setName("João");
        member.setRole(MemberRole.FUNCIONARIO);

        memberResponse = new MemberResponse(1L, "João", MemberRole.FUNCIONARIO);
    }

    @Test
    void create_validRequest_returnsMemberResponse() {
        MemberRequest request = new MemberRequest("João", MemberRole.FUNCIONARIO);
        when(mapper.toEntity(request)).thenReturn(member);
        when(repository.save(member)).thenReturn(member);
        when(mapper.toResponse(member)).thenReturn(memberResponse);

        MemberResponse result = memberService.create(request);

        assertThat(result.name()).isEqualTo("João");
        assertThat(result.role()).isEqualTo(MemberRole.FUNCIONARIO);
        verify(repository).save(member);
    }

    @Test
    void findAll_returnsMappedList() {
        when(repository.findAll()).thenReturn(List.of(member));
        when(mapper.toResponse(member)).thenReturn(memberResponse);

        List<MemberResponse> result = memberService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }

    @Test
    void findById_existingId_returnsMemberResponse() {
        when(repository.findById(1L)).thenReturn(Optional.of(member));
        when(mapper.toResponse(member)).thenReturn(memberResponse);

        MemberResponse result = memberService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void findById_nonExistingId_throwsResourceNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getOrThrow_nonExistingId_throwsResourceNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getOrThrow(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
