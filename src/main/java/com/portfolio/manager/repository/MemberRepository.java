package com.portfolio.manager.repository;

import com.portfolio.manager.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
