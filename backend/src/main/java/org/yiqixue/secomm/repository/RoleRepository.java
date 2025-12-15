package org.yiqixue.secomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yiqixue.secomm.entity.Role;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    Optional<Role> findByRoleCode(String roleCode);
    
    Optional<Role> findByRoleName(String roleName);
    
    Boolean existsByRoleCode(String roleCode);
}