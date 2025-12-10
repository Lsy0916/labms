package org.example.labms.service;

import org.example.labms.dto.request.UpdateUserInfoRequest;
import org.example.labms.dto.UserInfoResponseDTO;
import org.example.labms.model.Classes;
import org.example.labms.model.Teacher;
import org.example.labms.model.User;
import org.example.labms.repository.TeacherRepository;
import org.example.labms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private ClassesService classesService;

    // 获取所有用户
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // 查找所有用户(不包含敏感信息)
    public List<UserInfoResponseDTO> getAllUsersWithoutSensitiveInfo() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> new UserInfoResponseDTO(
                user.getId(),
                user.getUserId(),
                user.getName(),
                user.getGender(),
                user.getClassId(),
                user.getMajor(),
                user.getPhone(),
                user.getEmail(),
                user.getRoleId(),
                user.getRole()
        )).collect(Collectors.toList());
    }
    
    // 根据ID获取用户
    public Optional<User> getUserByUserId(String userId) {
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 创建一个新的用户对象用于返回，避免修改原始数据
            User returnUser = new User();
            // 复制所有属性
            returnUser.setId(user.getId());
            returnUser.setUserId(user.getUserId());
            returnUser.setName(user.getName());
            returnUser.setGender(user.getGender());
            returnUser.setClassId(user.getClassId()); // 保留原始classId
            returnUser.setMajor(user.getMajor());
            returnUser.setPhone(user.getPhone());
            returnUser.setEmail(user.getEmail());
            returnUser.setRoleId(user.getRoleId());
            returnUser.setRole(user.getRole());
            returnUser.setPassword(user.getPassword());
            returnUser.setCreatedAt(user.getCreatedAt());
            returnUser.setUpdatedAt(user.getUpdatedAt());
            returnUser.setAvatarUrl(user.getAvatarUrl());
            
            // 将classId替换为教室名称（仅用于显示）
            if (user.getClassId() != null && !user.getClassId().isEmpty()) {
                Optional<Classes> classOptional = classesService.getClassByClassId(user.getClassId());
                if (classOptional.isPresent()) {
                    // 将classId替换为教室名称（只在返回的对象上修改）
                    returnUser.setClassId(classOptional.get().getName());
                }
            }
            return Optional.of(returnUser);
        }
        return userOptional;
    }
    
    // 创建或更新用户，同时加密密码
    public User saveUser(User user) {
        // 如果密码未设置或者为空，则使用默认密码
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword("123456");
        }
        
        // 加密密码
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        
        // 设置创建时间和更新时间
        if (user.getCreatedAt() == null || user.getCreatedAt().isEmpty()) {
            String currentTime = java.time.LocalDateTime.now().toString();
            user.setCreatedAt(currentTime);
            user.setUpdatedAt(currentTime);
        } else if (user.getUpdatedAt() == null || user.getUpdatedAt().isEmpty()) {
            user.setUpdatedAt(java.time.LocalDateTime.now().toString());
        }
        
        return userRepository.save(user);
    }
    
    // 添加学生
    public User addStudent(User student) {
        // 设置默认角色
        student.setRoleId("student");
        student.setRole("student");
        
        // 设置默认密码为123456
        String defaultPassword = "123456";
        String encodedPassword = passwordEncoder.encode(defaultPassword);
        student.setPassword(encodedPassword);
        
        // 设置创建时间和更新时间
        String currentTime = java.time.LocalDateTime.now().toString();
        student.setCreatedAt(currentTime);
        student.setUpdatedAt(currentTime);
        
        // 默认头像
        if (student.getAvatarUrl() == null || student.getAvatarUrl().isEmpty()) {
            student.setAvatarUrl("default_avatar.png");
        }
        
        return userRepository.save(student);
    }
    
    // 根据ID删除学生
    public void deleteStudent(int id) {
        userRepository.deleteById((long) id);
    }
    
    // 根据userId删除学生
    public boolean deleteStudentByUserId(String userId) {
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 确保删除的是学生
            if ("student".equals(user.getRoleId())) {
                userRepository.delete(user);
                return true;
            }
        }
        return false;
    }
    
    // 更新用户信息（手机号和邮箱）
    public boolean updateUserInfo(UpdateUserInfoRequest request) {
        // 根据roleId判断是更新学生还是教师
        if ("student".equals(request.getRoleId())) {
            // 更新学生信息
            Optional<User> userOptional = userRepository.findByUserId(request.getUserId());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                // 只有当phone不为空且不为null时才更新
                if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
                    user.setPhone(request.getPhone());
                }
                
                // 只有当email不为空且不为null时才更新
                if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                    user.setEmail(request.getEmail());
                }
                
                userRepository.save(user);
                return true;
            }
        } else if ("teacher".equals(request.getRoleId())) {
            // 更新教师信息
            Optional<Teacher> teacherOptional = teacherRepository.findByTeacherId(request.getUserId());
            if (teacherOptional.isPresent()) {
                Teacher teacher = teacherOptional.get();
                
                // 只有当phone不为空且不为null时才更新
                if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
                    teacher.setPhone(request.getPhone());
                }
                
                // 只有当email不为空且不为null时才更新
                if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                    teacher.setEmail(request.getEmail());
                }
                
                teacherRepository.save(teacher);
                return true;
            }
        }
        return false;
    }

    // 根据邮箱更新密码
    public boolean updatePassword(String email, String newPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 加密新密码
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    
    // 检查邮箱是否存在
    public boolean isEmailExists(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.isPresent();
    }
    
    // 验证旧密码并更新新密码（仅限学生）
    public boolean updatePasswordWithVerification(String userId, String roleId, String oldPassword, String newPassword) {
        // 验证roleId为student
        if (!"student".equals(roleId)) {
            return false;
        }
        
        // 根据userId查找用户
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // 验证旧密码是否正确
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                // 加密新密码
                String encodedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encodedPassword);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }
}