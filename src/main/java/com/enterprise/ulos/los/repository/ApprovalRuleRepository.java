package com.enterprise.ulos.los.repository;

import com.enterprise.ulos.los.entity.ApprovalRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalRuleRepository extends JpaRepository<ApprovalRuleEntity, String> {

    List<ApprovalRuleEntity> findByActiveTrueOrderByRuleIdAsc();
}
