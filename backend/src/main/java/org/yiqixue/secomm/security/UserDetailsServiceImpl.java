package org.yiqixue.secomm.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yiqixue.secomm.entity.Role;
import org.yiqixue.secomm.entity.User;
import org.yiqixue.secomm.repository.UserRepository;
import org.yiqixue.secomm.repository.UserRoleRepository;

import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * 自定义用户详情服务
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserOnly(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        // 使用UserRoleRepository查询角色
        Set<Role> roles = userRoleRepository.findRolesByUserId(user.getId());
        
        log.info("用户 {} 的角色数量: {}", username, roles.size());
        
        // 直接构建authorities，不依赖user.getRoles()
        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleCode()))
                .collect(Collectors.toList());

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getPhone(),
                user.getGender(),
                user.getPassword(),
                authorities
        ) {
            @Override
            public boolean isAccountNonExpired() {
                return true;
            }

            @Override
            public boolean isAccountNonLocked() {
                return true;
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true;
            }

            @Override
            public boolean isEnabled() {
                return true;
            }
        };
    }

}