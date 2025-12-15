package org.yiqixue.secomm.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "zip_code", nullable = false)
    private String zipCode;

    /**
     * 关联的用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 关联用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @ToString.Exclude
    private User user;

    /**
     * 地址类型：SHIPPING-配送地址，BILLING-账单地址
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", nullable = false)
    private AddressType addressType = AddressType.SHIPPING;

    /**
     * 地址类型枚举
     */
    public enum AddressType {
        SHIPPING("配送地址"),
        BILLING("账单地址");

        private final String description;

        AddressType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
