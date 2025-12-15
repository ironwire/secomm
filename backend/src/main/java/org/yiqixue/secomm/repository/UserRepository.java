package org.yiqixue.secomm.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.yiqixue.secomm.entity.User;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByPhone(String phone);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByPhone(String phone);

    List<User> findByApprovalStatus(User.ApprovalStatus approvalStatus);

    /**
     * 查找用户并加载角色信息
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role WHERE u.id = :userId")
    Optional<User> findByIdWithRoles(@Param("userId") Long userId);
    
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role WHERE u.username = :username")
    Optional<User> findByUsernameWithRoles(@Param("username") String username);

    // ✅ 使用 EntityGraph 加载用户和角色信息
    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})
    Optional<User> findByUsername(String username);

    // ✅ 分开查询 - 只查用户
    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findUserOnly(@Param("username") String username);

//    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role")
//    List<User> findAllWithRoles();

//    @Query("SELECT DISTINCT u FROM User u " +
//            "LEFT JOIN FETCH UserRole ur ON ur.userId = u.id " +
//            "LEFT JOIN FETCH ur.role")
//    List<User> findAllWithRoles();

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role")
    List<User> findAllWithRoles();

    /**
     * 根据日期范围统计活跃用户数
     */
    @Query("SELECT COUNT(DISTINCT u) FROM User u WHERE u.updateTime BETWEEN :startDate AND :endDate")
    Long countActiveUsersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 根据日期范围统计新用户数
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createTime BETWEEN :startDate AND :endDate")
    Long countUsersByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * 检查用户是否存在
     */
    boolean existsById(Long userId);
}
