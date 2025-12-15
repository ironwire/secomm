package org.yiqixue.secomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yiqixue.secomm.entity.Address;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    // 基础的CRUD操作已由JpaRepository提供
    // 如果需要特殊查询，可以在这里添加

    /**
     * 根据用户ID查找所有地址，按ID倒序排列
     */
    List<Address> findByUserIdOrderByIdDesc(Long userId);

    /**
     * 根据用户ID和地址类型查找地址
     */
    Optional<Address> findByUserIdAndAddressType(Long userId, Address.AddressType addressType);
}
