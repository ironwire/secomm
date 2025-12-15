package org.yiqixue.secomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yiqixue.secomm.entity.Country;
import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Short> {

    /**
     * 根据国家代码查找国家
     */
    Optional<Country> findByCode(String code);

    /**
     * 根据国家名称查找国家
     */
    Optional<Country> findByName(String name);
}