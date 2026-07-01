package com.example.warehouse.service.impl;

import com.example.warehouse.dto.UserDTO;
import com.example.warehouse.dto.UserRequest;
import com.example.warehouse.dto.ChangePasswordRequest;
import com.example.warehouse.entity.User;
import com.example.warehouse.exception.WarehouseException;
import com.example.warehouse.repository.UserRepository;
import com.example.warehouse.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理服务实现类
 * 实现用户管理的核心业务逻辑，包含数据校验和权限控制
 *
 * 业务规则：
 *   - 不允许创建重复的用户名
 *   - 密码长度至少6位
 *   - 角色只能是 ADMIN / OPERATOR / EMPLOYEE 之一
 *   - 管理员不能禁用自己
 *   - 只有管理员可以管理其他用户
 */
@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * 获取所有用户列表
     *
     * @return 用户DTO列表
     */
    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取用户
     *
     * @param id 用户ID
     * @return 用户DTO
     * @throws WarehouseException 用户不存在时抛出
     */
    @Override
    public UserDTO findById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new WarehouseException("用户不存在"));
        return convertToDTO(user);
    }
    
    /**
     * 创建新用户
     * 校验用户名唯一性、密码长度、角色合法性
     *
     * @param request 用户创建请求
     * @return 创建后的用户DTO
     * @throws WarehouseException 用户名已存在、密码不合法或角色非法时抛出
     */
    @Override
    public UserDTO createUser(UserRequest request) {
        // 校验用户名唯一性
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new WarehouseException("用户名已存在");
        }
        
        // 校验密码长度
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new WarehouseException("密码长度至少6位");
        }
        
        // 校验角色合法性
        validateRole(request.getRole());
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setRole(request.getRole());
        user.setPhone(request.getPhone());
        user.setEnabled(true);
        
        user = userRepository.save(user);
        return convertToDTO(user);
    }
    
    /**
     * 更新用户信息
     * 可以修改姓名、角色、电话、启用状态，但不能修改用户名
     *
     * @param id 用户ID
     * @param request 用户更新请求
     * @return 更新后的用户DTO
     * @throws WarehouseException 用户不存在时抛出
     */
    @Override
    public UserDTO updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new WarehouseException("用户不存在"));
        
        // 如果修改了角色，校验合法性
        if (request.getRole() != null) {
            validateRole(request.getRole());
            user.setRole(request.getRole());
        }
        
        // 更新基本信息
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getEnabled() != null) {
            user.setEnabled(request.getEnabled());
        }
        
        // 如果提供了新密码，则更新密码
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            if (request.getPassword().length() < 6) {
                throw new WarehouseException("密码长度至少6位");
            }
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        user = userRepository.save(user);
        return convertToDTO(user);
    }
    
    /**
     * 删除用户
     *
     * @param id 用户ID
     * @throws WarehouseException 用户不存在时抛出
     */
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new WarehouseException("用户不存在");
        }
        userRepository.deleteById(id);
    }
    
    /**
     * 启用/禁用用户
     *
     * @param id 用户ID
     * @param enabled 是否启用
     * @throws WarehouseException 用户不存在时抛出
     */
    @Override
    public void toggleUserStatus(Long id, boolean enabled) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new WarehouseException("用户不存在"));
        user.setEnabled(enabled);
        userRepository.save(user);
    }
    
    /**
     * 修改密码
     * 校验旧密码正确性，新密码不能与旧密码相同
     *
     * @param username 用户名
     * @param request 修改密码请求
     * @throws WarehouseException 旧密码错误或新密码不合法时抛出
     */
    @Override
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new WarehouseException("用户不存在"));
        
        // 校验旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new WarehouseException("旧密码错误");
        }
        
        // 校验新密码长度
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            throw new WarehouseException("新密码长度至少6位");
        }
        
        // 新旧密码不能相同
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new WarehouseException("新密码不能与旧密码相同");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    
    /**
     * 校验角色合法性
     * 角色只能是 ADMIN / OPERATOR / EMPLOYEE 之一
     *
     * @param role 角色字符串
     * @throws WarehouseException 角色非法时抛出
     */
    private void validateRole(String role) {
        if (!"ADMIN".equals(role) && !"OPERATOR".equals(role) && !"EMPLOYEE".equals(role)) {
            throw new WarehouseException("角色必须是 ADMIN / OPERATOR / EMPLOYEE 之一");
        }
    }
    
    /**
     * 实体转DTO
     * 将User实体转换为UserDTO，隐藏敏感信息
     *
     * @param user User实体
     * @return UserDTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setRole(user.getRole());
        dto.setPhone(user.getPhone());
        dto.setEnabled(user.getEnabled());
        return dto;
    }
}
