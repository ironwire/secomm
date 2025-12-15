package org.yiqixue.secomm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 角色实体类
 * @author System
 * @date 2024
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "roles",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "role_code"),
                @UniqueConstraint(columnNames = "role_name")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 角色编码（唯一标识，如：ROLE_ADMIN）
     */
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 50)
    @Column(name = "role_code", nullable = false, unique = true)
    private String roleCode;

    /**
     * 角色名称（中文名，如：系统管理员）
     */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50)
    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;

    /**
     * 角色描述
     */
    @Size(max = 200)
    @Column(name = "description")
    private String description;

    /**
     * 角色状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RoleStatus status = RoleStatus.ACTIVE;

    /**
     * 角色用户关联（一对多关系）
     */
    @OneToMany(mappedBy = "roleId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<UserRole> userRoles = new HashSet<>();


    /**
     * 创建时间
     */
    @CreatedDate
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @LastModifiedDate
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 角色状态枚举
     */
    public enum RoleStatus {
        ACTIVE("启用"),
        INACTIVE("禁用");

        private final String description;

        RoleStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 判断角色是否可用
     */
    public boolean isEnabled() {
        return RoleStatus.ACTIVE.equals(this.status);
    }
}

