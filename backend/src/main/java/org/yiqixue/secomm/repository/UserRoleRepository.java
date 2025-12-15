package org.yiqixue.secomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yiqixue.secomm.entity.Role;
import org.yiqixue.secomm.entity.UserRole;
import org.yiqixue.secomm.entity.UserRoleId;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    
    /**
     * 根据用户ID查找用户角色关联
     */
    List<UserRole> findByUserId(Long userId);
    
    /**
     * 根据角色ID查找用户角色关联
     */
    List<UserRole> findByRoleId(Long roleId);
    
    /**
     * 根据用户ID和角色ID查找关联
     */
    UserRole findByUserIdAndRoleId(Long userId, Long roleId);
    
    /**
     * 根据用户ID获取角色信息
     */
    @Query("SELECT ur.role FROM UserRole ur WHERE ur.userId = :userId")
    Set<Role> findRolesByUserId(@Param("userId") Long userId);
    
    /**
     * 删除用户的所有角色关联
     */
    void deleteByUserId(Long userId);
    
    /**
     * 删除角色的所有用户关联
     */
    void deleteByRoleId(Long roleId);
}