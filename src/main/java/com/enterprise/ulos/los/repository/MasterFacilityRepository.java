package com.enterprise.ulos.los.repository;

import com.enterprise.ulos.los.entity.MasterFacilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MasterFacilityRepository extends JpaRepository<MasterFacilityEntity, Long> {

    List<MasterFacilityEntity> findAllByOrderByProductCodeAscFacilityCodeAsc();
}
