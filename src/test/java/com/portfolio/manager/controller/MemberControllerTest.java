package com.portfolio.manager.controller;

import com.portfolio.manager.dto.MemberRequest;
import com.portfolio.manager.dto.MemberResponse;
import com.portfolio.manager.entity.MemberRole;
import com.portfolio.manager.service.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock
    private MemberService service;

    @InjectMocks
    private MemberController controller;

    @Test
    void create_validRequest_returnsCreatedMember() {
        MemberRequest request = new MemberRequest("Ana", MemberRole.FUNCIONARIO);
        MemberResponse response = new MemberResponse(1L, "Ana", MemberRole.FUNCIONARIO);
        when(service.create(request)).thenReturn(response);

        MemberResponse result = controller.create(request);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Ana");
        verify(service).create(request);
    }

    @Test
    void findAll_returnsAllMembers() {
        MemberResponse response = new MemberResponse(1L, "Ana", MemberRole.FUNCIONARIO);
        when(service.findAll()).thenReturn(List.of(response));

        List<MemberResponse> result = controller.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }

    @Test
    void findById_existingId_returnsMember() {
        MemberResponse response = new MemberResponse(1L, "Ana", MemberRole.FUNCIONARIO);
        when(service.findById(1L)).thenReturn(response);

        assertThat(controller.findById(1L).name()).isEqualTo("Ana");
    }
}
