package com.enterprise.ulos.los.repository;

import com.enterprise.ulos.los.entity.PrecheckSessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PrecheckSessionRepository extends JpaRepository<PrecheckSessionEntity, Long> {

    Optional<PrecheckSessionEntity> findByCheckRef(String checkRef);

    @Query("""
            select p
            from PrecheckSessionEntity p
            where (
                :type is null
                or :type = ''
                or upper(p.checkType) = upper(:type)
            )
            and (
                :customerCif is null
                or :customerCif = ''
                or lower(p.customer.cifNumber) = lower(:customerCif)
            )
            and (
                :keyword is null
                or :keyword = ''
                or lower(p.checkRef) like lower(concat('%', :keyword, '%'))
                or lower(p.customer.cifNumber) like lower(concat('%', :keyword, '%'))
                or lower(p.customer.companyName) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(p.status, '')) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(p.resultSummary, '')) like lower(concat('%', :keyword, '%'))
            )
            """)
    Page<PrecheckSessionEntity> search(
            @Param("type") String type,
            @Param("customerCif") String customerCif,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
