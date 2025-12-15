package org.yiqixue.secomm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yiqixue.secomm.dto.user.UserApprovalRequest;
import org.yiqixue.secomm.dto.user.UserUpdateRequest;
import org.yiqixue.secomm.entity.Customer;
import org.yiqixue.secomm.entity.Role;
import org.yiqixue.secomm.entity.User;
import org.yiqixue.secomm.exception.ResourceNotFoundException;
import org.yiqixue.secomm.repository.CustomerRepository;
import org.yiqixue.secomm.repository.UserRepository;
import org.yiqixue.secomm.repository.UserRoleRepository;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserManagementService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final UserRoleRepository userRoleRepository;

    /**
     * 获取待审批的用户列表
     */
    public List<User> getPendingUsers() {
        return userRepository.findByApprovalStatus(User.ApprovalStatus.PENDING);
    }

    /**
     * 获取所有用户列表
     */
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAllWithRoles();
        
        // 如果某些用户的角色没有正确加载，手动加载
        users.forEach(user -> {
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                Set<Role> roles = userRoleRepository.findRolesByUserId(user.getId());
                user.setRoles(roles);
                log.info("手动加载用户角色: 用户ID={}, 角色数量={}", user.getId(), roles.size());
            }
        });
        
        return users;
    }

    /**
     * 审批用户注册
     */
    @Transactional
    public void approveUser(Long userId, UserApprovalRequest request, Long adminId) {
        log.info("审批用户: 用户ID={}, 审批状态={}", userId, request.getApprovalStatus());

        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // 获取用户角色（不设置到user对象上）
        Set<Role> userRoles = user.getRoles();
        if (userRoles == null || userRoles.isEmpty()) {
            userRoles = userRoleRepository.findRolesByUserId(userId);
            log.info("手动加载用户角色: 用户ID={}, 角色数量={}", userId, userRoles.size());
        }

        // 调试日志：打印用户角色信息
        log.info("用户角色信息: 用户ID={}, 角色数量={}", userId, userRoles.size());
        userRoles.forEach(role -> 
            log.info("角色详情: ID={}, Code={}, Name={}", role.getId(), role.getRoleCode(), role.getRoleName()));

        // 更新审批信息
        user.setApprovalStatus(request.getApprovalStatus());
        user.setApprovalTime(LocalDateTime.now());
        user.setApprovedBy(adminId);
        user.setApprovalNotes(request.getApprovalNotes());

        userRepository.save(user);

        // 检查是否有Customer角色
        boolean hasCustomerRole = userRoles.stream()
                .anyMatch(role -> "ROLE_USER".equals(role.getRoleCode()));
        log.info("用户是否有ROLE_USER角色: {}", hasCustomerRole);

        // 如果审批通过且是客户角色，创建Customer记录
        if (User.ApprovalStatus.APPROVED.equals(request.getApprovalStatus()) && hasCustomerRole) {
            
            // 检查是否已存在Customer记录
            if (!customerRepository.existsByUserId(userId)) {
                Customer customer = Customer.fromApprovedUser(user);
                customerRepository.save(customer);
                log.info("已为用户创建Customer记录: 用户ID={}, 客户ID={}", userId, customer.getId());
            } else {
                log.info("用户已存在Customer记录: 用户ID={}", userId);
            }
        } else {
            log.info("不满足创建Customer记录的条件: 审批状态={}, 有ROLE_USER角色={}", 
                    request.getApprovalStatus(), hasCustomerRole);
        }
    }

    /**
     * 根据用户ID获取用户详情
     */
    public User getUserById(Long userId) {
        log.info("获取用户详情: 用户ID={}", userId);
        
        User user = userRepository.findByIdWithRoles(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        log.info("查询到的用户角色数量: {}", user.getRoles() != null ? user.getRoles().size() : 0);
        
        // 如果角色没有加载，手动加载
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            log.info("角色为空，手动加载用户角色: 用户ID={}", userId);
            Set<Role> roles = userRoleRepository.findRolesByUserId(userId);
            log.info("手动加载的角色数量: {}", roles.size());
            
            // 直接设置角色到现有用户对象
            user.setRoles(roles);
        }
        
        // 打印最终的角色信息用于调试
        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> 
                log.info("最终角色详情: ID={}, Code={}, Name={}", role.getId(), role.getRoleCode(), role.getRoleName()));
        }
        
        return user;
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public User updateUser(Long userId, UserUpdateRequest request) {
        log.info("更新用户信息: 用户ID={}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // 更新用户基本信息
        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getApprovalStatus() != null) {
            user.setApprovalStatus(request.getApprovalStatus());
        }
        if (request.getActive() != null) {
            user.setStatus(request.getActive() ? User.UserStatus.ACTIVE : User.UserStatus.INACTIVE);
        }

        
        user.setUpdateTime(LocalDateTime.now());
        
        return userRepository.save(user);
    }
}
