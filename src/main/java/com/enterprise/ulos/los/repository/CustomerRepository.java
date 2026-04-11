package com.enterprise.ulos.los.repository;

import com.enterprise.ulos.los.entity.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {

    Optional<CustomerEntity> findByCifNumber(String cifNumber);

    @Query("""
            select c
            from CustomerEntity c
            where (
                :keyword is null
                or :keyword = ''
                or lower(c.cifNumber) like lower(concat('%', :keyword, '%'))
                or lower(c.companyName) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(c.sector, '')) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(c.location, '')) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(c.status, '')) like lower(concat('%', :keyword, '%'))
            )
            """)
    Page<CustomerEntity> search(@Param("keyword") String keyword, Pageable pageable);
}
