package com.portfolio.manager.mapper;

import com.portfolio.manager.dto.MemberRequest;
import com.portfolio.manager.dto.MemberResponse;
import com.portfolio.manager.entity.Member;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    Member toEntity(MemberRequest request);
    MemberResponse toResponse(Member member);
}
