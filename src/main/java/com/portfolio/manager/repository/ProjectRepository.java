package com.portfolio.manager.repository;

import com.portfolio.manager.entity.Project;
import com.portfolio.manager.entity.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Query("SELECT p FROM Project p WHERE " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:managerId IS NULL OR p.manager.id = :managerId) AND " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Project> findWithFilters(
            @Param("status") ProjectStatus status,
            @Param("managerId") Long managerId,
            @Param("name") String name,
            Pageable pageable
    );

    @Query("SELECT COUNT(p) FROM Project p JOIN p.members m WHERE m.id = :memberId " +
           "AND p.status NOT IN (com.portfolio.manager.entity.ProjectStatus.ENCERRADO, " +
           "com.portfolio.manager.entity.ProjectStatus.CANCELADO)")
    long countActiveProjectsByMember(@Param("memberId") Long memberId);

    List<Project> findByStatus(ProjectStatus status);
}
