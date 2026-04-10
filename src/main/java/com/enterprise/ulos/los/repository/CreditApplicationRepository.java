package com.enterprise.ulos.los.repository;

import com.enterprise.ulos.los.entity.CreditApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CreditApplicationRepository extends JpaRepository<CreditApplicationEntity, String> {

    Optional<CreditApplicationEntity> findByProcessInstanceId(String processInstanceId);

    long countByWorkflowStatus(String workflowStatus);
}
