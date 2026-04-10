package com.enterprise.ulos.los.repository;

import com.enterprise.ulos.los.entity.CustomerPortfolioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerPortfolioRepository extends JpaRepository<CustomerPortfolioEntity, String> {
}
