package com.enterprise.ulos.los.repository;

import com.enterprise.ulos.los.entity.ApplicationWorkspaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicationWorkspaceRepository extends JpaRepository<ApplicationWorkspaceEntity, Long> {

    Optional<ApplicationWorkspaceEntity> findByApplicationId(String applicationId);

    Optional<ApplicationWorkspaceEntity> findByProcessInstanceId(String processInstanceId);

    long countByWorkflowStatus(String workflowStatus);
}
