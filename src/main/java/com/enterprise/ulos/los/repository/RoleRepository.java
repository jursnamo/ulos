package com.enterprise.ulos.los.repository;

import com.enterprise.ulos.los.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByRoleCode(String roleCode);

    List<RoleEntity> findByRoleCodeInAndActiveFlagTrue(Collection<String> roleCodes);
}
