package org.yiqixue.secomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yiqixue.secomm.entity.Customer;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * 根据邮箱查找顾客
     */
    Optional<Customer> findByEmail(String email);

    /**
     * 检查邮箱是否已存在
     */
    boolean existsByEmail(String email);

    /**
     * 根据姓氏查找顾客
     */
    Optional<Customer> findByLastName(String lastName);

    Optional<Customer> findByUserId(Long userId);

    /**
     * 检查用户是否已有Customer记录
     */
    boolean existsByUserId(Long userId);
}
