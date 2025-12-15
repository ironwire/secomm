package org.yiqixue.secomm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

/**
 * 用户角色关联实体类
 * 使用复合主键适应现有表结构（只有user_id和role_id两列）
 */
@Entity
@Table(name = "user_roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserRoleId.class)
public class UserRole {

    /**
     * 用户ID（复合主键的一部分）
     */
    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 角色ID（复合主键的一部分）
     */
    @Id
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    /**
     * 关联用户
     */
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", insertable = false, updatable = false)
//    private User user;

    /**
     * 关联角色
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", insertable = false, updatable = false)
    private Role role;
}