package org.yiqixue.secomm.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "customer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联的用户ID（只有审批通过的Customer角色用户才会创建Customer记录）
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    /**
     * 客户状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CustomerStatus status = CustomerStatus.ACTIVE;

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

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Order> orders;

    /**
     * 客户状态枚举
     */
    public enum CustomerStatus {
        ACTIVE("激活"),
        INACTIVE("未激活"),
        SUSPENDED("暂停");

        private final String description;

        CustomerStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 从User实体获取客户信息（用于审批通过后创建Customer）
     */
    public static Customer fromApprovedUser(User user) {
        // 移除角色检查，因为调用此方法前已经在service层检查过了
        if (!User.ApprovalStatus.APPROVED.equals(user.getApprovalStatus())) {
            throw new IllegalArgumentException("User must be approved to create Customer entity");
        }
        
        return Customer.builder()
                .userId(user.getId())
                .firstName(user.getRealName().split(" ")[0]) // 简单分割姓名
                .lastName(user.getRealName().contains(" ") ? 
                         user.getRealName().substring(user.getRealName().indexOf(" ") + 1) : "")
                .email(user.getUsername())
                .status(CustomerStatus.ACTIVE)
                .build();
    }
}
