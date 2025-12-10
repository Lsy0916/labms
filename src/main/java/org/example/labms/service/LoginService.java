package org.example.labms.service;

import org.example.labms.dto.AdminInfoDTO;
import org.example.labms.dto.request.LoginResponse;
import org.example.labms.dto.TeacherInfoDTO;
import org.example.labms.dto.UserInfoDTO;
import org.example.labms.model.Admin;
import org.example.labms.model.Teacher;
import org.example.labms.model.User;
import org.example.labms.repository.AdminRepository;
import org.example.labms.repository.TeacherRepository;
import org.example.labms.repository.UserRepository;
import org.example.labms.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // 用户登录验证
    public LoginResponse authenticate(String userId, String password, String roleId) {
        // 判断用户身份
        switch (roleId) {
            case "student":
                return authenticateUser(userId, password);
            case "teacher":
                return authenticateTeacher(userId, password);
            case "admin":
                return authenticateAdmin(userId, password);
            default:
                return new LoginResponse("无效的角色", null);
        }
    }

    // 学生身份验证
    private LoginResponse authenticateUser(String userId, String password) {
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 检查密码和角色是否匹配
            if (passwordEncoder.matches(password, user.getPassword()) && "student".equals(user.getRole())) {
                // 生成JWT token
                String token = jwtUtil.generateToken(user.getUserId());
                UserInfoDTO userInfo = new UserInfoDTO(user.getUserId(), user.getName(), user.getAvatarUrl(), user.getRoleId());
                return new LoginResponse(token, userInfo);
            } else if (!passwordEncoder.matches(password, user.getPassword())) {
                // 密码错误
                return new LoginResponse("密码错误", null);
            } else if (!"student".equals(user.getRole())) {
                // 角色不匹配
                return new LoginResponse("角色不匹配", null);
            }
        } else {
            // 用户不存在
            return new LoginResponse("用户不存在", null);
        }
        // 默认登录失败
        return new LoginResponse("登录失败", null);
    }

    // 教师身份验证
    private LoginResponse authenticateTeacher(String teacherId, String password) {
        Optional<Teacher> teacherOptional = teacherRepository.findByTeacherId(teacherId);
        if (teacherOptional.isPresent()) {
            Teacher user = teacherOptional.get();
            // 检查密码和角色是否匹配
            if (passwordEncoder.matches(password, user.getPassword()) && "teacher".equals(user.getRole())) {
                // 生成JWT token
                String token = jwtUtil.generateToken(user.getTeacherId());
                String userId = user.getTeacherId();
                TeacherInfoDTO userInfo = new TeacherInfoDTO(userId, user.getName(), user.getAvatarUrl(), user.getRoleId());
                return new LoginResponse(token, userInfo);
            } else if (!passwordEncoder.matches(password, user.getPassword())) {
                // 密码错误
                return new LoginResponse("密码错误", null);
            } else if (!"teacher".equals(user.getRole())) {
                // 角色不匹配
                return new LoginResponse("角色不匹配", null);
            }
        } else {
            // 用户不存在
            return new LoginResponse("用户不存在", null);
        }
        // 默认登录失败
        return new LoginResponse("登录失败", null);
    }

    // 管理员身份验证
    private LoginResponse authenticateAdmin(String adminId, String password) {
        Optional<Admin> adminOptional = adminRepository.findByAdminId(adminId);
        if (adminOptional.isPresent()) {
            Admin user = adminOptional.get();
            // 检查密码和角色是否匹配
            if (passwordEncoder.matches(password, user.getPassword()) && "admin".equals(user.getRole())) {
                // 生成JWT token
                String token = jwtUtil.generateToken(user.getAdminId());
                String userId = user.getAdminId();
                AdminInfoDTO userInfo = new AdminInfoDTO(userId, user.getName(), user.getAvatarUrl(), user.getRoleId());
                return new LoginResponse(token, userInfo);
            } else if (!passwordEncoder.matches(password, user.getPassword())) {
                // 密码错误
                return new LoginResponse("密码错误", null);
            } else if (!"admin".equals(user.getRole())) {
                // 角色不匹配
                return new LoginResponse("角色不匹配", null);
            }
        } else {
            // 用户不存在
            return new LoginResponse("用户不存在", null);
        }
        // 默认登录失败
        return new LoginResponse("登录失败", null);
    }
}