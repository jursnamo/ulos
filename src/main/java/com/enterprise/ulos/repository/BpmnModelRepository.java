package com.enterprise.ulos.repository;

import com.enterprise.ulos.domain.bpmn.BpmnModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BpmnModelRepository extends JpaRepository<BpmnModelEntity, Long> {

    Optional<BpmnModelEntity> findTopByProcessKeyOrderByVersionDesc(String processKey);

    List<BpmnModelEntity> findByProcessKeyOrderByVersionDesc(String processKey);

    List<BpmnModelEntity> findAllByOrderByProcessKeyAscVersionDesc();
}
