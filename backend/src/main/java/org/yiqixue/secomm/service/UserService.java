package org.yiqixue.secomm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yiqixue.secomm.dto.user.AddressRequest;
import org.yiqixue.secomm.dto.user.UserProfileUpdateRequest;
import org.yiqixue.secomm.entity.Address;
import org.yiqixue.secomm.entity.User;
import org.yiqixue.secomm.exception.ResourceNotFoundException;
import org.yiqixue.secomm.exception.BusinessException;
import org.yiqixue.secomm.repository.AddressRepository;
import org.yiqixue.secomm.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    /**
     * 根据用户ID获取用户信息
     */
    public User getUserById(Long userId) {
        log.info("获取用户信息: 用户ID={}", userId);
        
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }

    /**
     * 获取用户审批状态
     */
    public User.ApprovalStatus getUserApprovalStatus(Long userId) {
        log.info("获取用户审批状态: 用户ID={}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        return user.getApprovalStatus();
    }

    /**
     * 更新用户个人信息
     */
    @Transactional
    public User updateUserProfile(Long userId, UserProfileUpdateRequest request) {
        log.info("更新用户个人信息: 用户ID={}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // 更新用户信息
        if (request.getRealName() != null) {
            user.setRealName(request.getRealName());
        }
        if (request.getPhone() != null) {
            // 检查手机号是否已被其他用户使用
            if (!user.getPhone().equals(request.getPhone()) && 
                userRepository.existsByPhone(request.getPhone())) {
                throw new BusinessException("手机号已被其他用户使用");
            }
            user.setPhone(request.getPhone());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        
        user.setUpdateTime(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    /**
     * 更新用户手机号
     */
    @Transactional
    public void updateUserPhone(Long userId, String phone) {
        log.info("更新用户手机号: 用户ID={}, 新手机号={}", userId, phone);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // 检查手机号是否已被其他用户使用
        if (!user.getPhone().equals(phone) && userRepository.existsByPhone(phone)) {
            throw new BusinessException("手机号已被其他用户使用");
        }
        
        user.setPhone(phone);
        user.setUpdateTime(LocalDateTime.now());
        
        userRepository.save(user);
    }

    /**
     * 获取用户所有地址
     */
    public List<Address> getUserAddresses(Long userId) {
        log.info("获取用户地址列表: 用户ID={}", userId);
        
        // 验证用户是否存在
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        return addressRepository.findByUserIdOrderByIdDesc(userId);
    }

    /**
     * 更新用户地址
     */
    @Transactional
    public Address updateUserAddress(Long userId, AddressRequest request, Address.AddressType addressType) {
        log.info("更新用户地址: 用户ID={}, 地址类型={}", userId, addressType);
        
        // 验证用户是否存在
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        
        // 查找现有地址或创建新地址
        Address address = addressRepository.findByUserIdAndAddressType(userId, addressType)
                .orElse(Address.builder()
                        .userId(userId)
                        .addressType(addressType)
                        .build());
        
        // 更新地址信息
        address.setCountry(request.getCountry());
        address.setState(request.getState());
        address.setCity(request.getCity());
        address.setStreet(request.getStreet());
        address.setZipCode(request.getZipCode());
        
        return addressRepository.save(address);
    }

    /**
     * 删除用户地址
     */
    @Transactional
    public void deleteUserAddress(Long userId, Long addressId) {
        log.info("删除用户地址: 用户ID={}, 地址ID={}", userId, addressId);
        
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        
        // 验证地址是否属于当前用户
        if (!address.getUserId().equals(userId)) {
            throw new BusinessException("无权限删除该地址");
        }
        
        addressRepository.delete(address);
    }
}