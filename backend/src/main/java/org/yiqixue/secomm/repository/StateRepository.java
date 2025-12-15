package org.yiqixue.secomm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yiqixue.secomm.entity.State;
import java.util.List;

@Repository
public interface StateRepository extends JpaRepository<State, Short> {

    /**
     * 根据国家ID查找所有州/省
     */
    List<State> findByCountryId(Short countryId);

    /**
     * 根据国家代码查找所有州/省
     */
    List<State> findByCountryCode(String countryCode);
}