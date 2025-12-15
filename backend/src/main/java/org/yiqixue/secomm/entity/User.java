package org.yiqixue.secomm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户实体类
 * @author System
 * @date 2024
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "phone")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名（使用邮箱地址）
     */
    @NotBlank(message = "用户名不能为空")
    @Email(message = "用户名必须是有效的邮箱地址")
    @Size(max = 100)
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    /**
     * 密码（加密存储）
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少6位")
    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * 真实姓名
     */
    @NotBlank(message = "真实姓名不能为空")
    @Size(min = 2, max = 50, message = "真实姓名长度必须在2-50个字符之间")
    @Column(name = "real_name", nullable = false)
    private String realName;

    /**
     * 手机号码（中国大陆）
     */
    @NotBlank(message = "手机号码不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入有效的手机号码")
    @Column(name = "phone", nullable = false, unique = true)
    private String phone;

    /**
     * 性别：M-男，F-女，U-未知
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 1)
    private Gender gender = Gender.M;

    /**
     * 头像URL
     */
    @Column(name = "avatar_url")
    private String avatarUrl;

    /**
     * 用户状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * 用户角色关联（一对多关系）
     */
//    @OneToMany(mappedBy = "userId", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//    private Set<UserRole> userRoles = new HashSet<>();

    /**
     * 用户角色关联（一对多关系）
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")  // 改为使用 @JoinColumn
    private Set<UserRole> userRoles = new HashSet<>();
    /**
     * 获取用户的所有角色（便利方法）
     */
    @Transient
    public Set<Role> getRoles() {
        if (userRoles == null || userRoles.isEmpty()) {
            return new HashSet<>();
        }
        return userRoles.stream()
                .map(UserRole::getRole)
                .filter(role -> role != null)
                .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * 设置用户角色（便利方法）
     */
    @Transient
    public void setRoles(Set<Role> roles) {
        // 清空现有的用户角色关联
        if (this.userRoles == null) {
            this.userRoles = new HashSet<>();
        }
        this.userRoles.clear();
        
        // 添加新的角色关联
        if (roles != null) {
            for (Role role : roles) {
                UserRole userRole = UserRole.builder()
                        .userId(this.id)
                        .roleId(role.getId())
                        .build();
                this.userRoles.add(userRole);
            }
        }
    }

    /**
     * 用户地址关联（一对多关系）
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Set<Address> addresses = new HashSet<>();

    /**
     * 添加地址
     */
    public void addAddress(Address address) {
        if (this.addresses == null) {
            this.addresses = new HashSet<>();
        }
        address.setUserId(this.id);
        this.addresses.add(address);
    }

    /**
     * 移除地址
     */
    public void removeAddress(Address address) {
        if (this.addresses != null) {
            this.addresses.remove(address);
        }
    }

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
     * 审批状态（仅对Customer角色有效）
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status")
    private ApprovalStatus approvalStatus;

    /**
     * 审批时间
     */
    @Column(name = "approval_time")
    private LocalDateTime approvalTime;

    /**
     * 审批人ID
     */
    @Column(name = "approved_by")
    private Long approvedBy;

    /**
     * 审批备注
     */
    @Column(name = "approval_notes")
    private String approvalNotes;

    /**
     * 性别枚举
     */
    public enum Gender {
        M("男"),
        F("女");

        private final String description;

        Gender(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 用户状态枚举
     */
    public enum UserStatus {
        ACTIVE("激活"),
        INACTIVE("未激活"),
        LOCKED("锁定"),
        FROZEN("冻结");

        private final String description;

        UserStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 审批状态枚举
     */
    public enum ApprovalStatus {
        PENDING("待审批"),
        APPROVED("已审批"),
        REJECTED("已拒绝");

        private final String description;

        ApprovalStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 添加角色
     */
//    public void addRole(Role role) {
//        this.roles.add(role);
//    }

    /**
     * 移除角色
     */
//    public void removeRole(Role role) {
//        this.roles.remove(role);
//    }

    /**
     * 是否账号未锁定
     */
    public boolean isAccountNonLocked() {
        return !UserStatus.LOCKED.equals(this.status) && !UserStatus.FROZEN.equals(this.status);
    }

    /**
     * 是否已启用
     */
    public boolean isEnabled() {
        return UserStatus.ACTIVE.equals(this.status);
    }

    /**
     * 检查用户是否有Customer角色
     */
//    public boolean hasCustomerRole() {
//        if (roles == null || roles.isEmpty()) {
//            return false;
//        }
//        return roles.stream()
//                .anyMatch(role -> "ROLE_USER".equals(role.getRoleCode()));
//    }
//
//    /**
//     * 检查Customer角色是否已审批
//     */
//    public boolean isCustomerApproved() {
//        return hasCustomerRole() && ApprovalStatus.APPROVED.equals(this.approvalStatus);
//    }

    /**
     * 检查是否需要审批（只有Customer角色需要）
     */
//    public boolean needsApproval() {
//        return hasCustomerRole() && (approvalStatus == null || ApprovalStatus.PENDING.equals(approvalStatus));
//    }
}
