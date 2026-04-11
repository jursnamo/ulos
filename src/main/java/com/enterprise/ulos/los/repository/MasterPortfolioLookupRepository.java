package com.enterprise.ulos.los.repository;

import com.enterprise.ulos.los.entity.MasterPortfolioLookupEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MasterPortfolioLookupRepository extends JpaRepository<MasterPortfolioLookupEntity, Long> {

    List<MasterPortfolioLookupEntity> findAllByOrderByMasterTypeAscSortOrderAscItemNameAsc();

    List<MasterPortfolioLookupEntity> findAllByActiveTrueOrderByMasterTypeAscSortOrderAscItemNameAsc();

    List<MasterPortfolioLookupEntity> findAllByMasterTypeIgnoreCaseOrderBySortOrderAscItemNameAsc(String masterType);

    List<MasterPortfolioLookupEntity> findAllByMasterTypeIgnoreCaseAndActiveTrueOrderBySortOrderAscItemNameAsc(String masterType);

    Optional<MasterPortfolioLookupEntity> findByMasterTypeIgnoreCaseAndItemCodeIgnoreCase(String masterType, String itemCode);

    @Query("""
            select m from MasterPortfolioLookupEntity m
            where (:masterType is null or :masterType = '' or lower(m.masterType) = lower(:masterType))
              and (:includeInactive = true or m.active = true)
              and (
                :keyword is null
                or :keyword = ''
                or lower(m.itemCode) like lower(concat('%', :keyword, '%'))
                or lower(m.itemName) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(m.legacyCode, '')) like lower(concat('%', :keyword, '%'))
                or lower(coalesce(m.description, '')) like lower(concat('%', :keyword, '%'))
              )
            """)
    Page<MasterPortfolioLookupEntity> search(
            @Param("masterType") String masterType,
            @Param("includeInactive") boolean includeInactive,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
