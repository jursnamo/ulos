package com.enterprise.ulos.repository;

import com.enterprise.ulos.domain.loan.LoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoanRepository extends JpaRepository<LoanEntity, String> {

    Optional<LoanEntity> findByBusinessKey(String businessKey);

    Optional<LoanEntity> findByProcessInstanceId(String processInstanceId);
}
