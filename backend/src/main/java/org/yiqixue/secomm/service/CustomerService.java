package org.yiqixue.secomm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yiqixue.secomm.entity.Customer;
import org.yiqixue.secomm.exception.ResourceNotFoundException;
import org.yiqixue.secomm.repository.CustomerRepository;

/**
 * 客户服务层
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * 根据用户ID获取客户信息
     */
    public Customer getCustomerByUserId(Long userId) {
        log.info("根据用户ID获取客户信息 - 用户ID: {}", userId);
        
        return customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "userId", userId));
    }
}