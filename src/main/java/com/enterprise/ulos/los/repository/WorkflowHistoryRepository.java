package com.enterprise.ulos.los.repository;

import com.enterprise.ulos.los.entity.ApplicationWorkspaceEntity;
import com.enterprise.ulos.los.entity.WorkflowHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowHistoryRepository extends JpaRepository<WorkflowHistoryEntity, Long> {

    List<WorkflowHistoryEntity> findByApplicationOrderByDecidedAtAsc(ApplicationWorkspaceEntity application);
}
