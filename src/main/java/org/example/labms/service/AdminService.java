package org.example.labms.service;

import org.example.labms.dto.request.UpdateAdminInfoRequest;
import org.example.labms.model.Admin;
import org.example.labms.model.Teacher;
import org.example.labms.model.User;
import org.example.labms.repository.AdminRepository;
import org.example.labms.repository.TeacherRepository;
import org.example.labms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // 根据ID获取管理员
    public Optional<Admin> getAdminByAdminId(String adminId) {
        return adminRepository.findByAdminId(adminId);
    }
    
    // 创建或更新管理员，同时加密密码
    public Admin saveAdmin(Admin admin) {
        // 如果密码未设置或者为空，则使用默认密码
        if (admin.getPassword() == null || admin.getPassword().isEmpty()) {
            admin.setPassword("123456");
        }
        
        // 加密密码
        String encodedPassword = passwordEncoder.encode(admin.getPassword());
        admin.setPassword(encodedPassword);
        
        // 设置创建时间和更新时间
        if (admin.getCreatedAt() == null || admin.getCreatedAt().isEmpty()) {
            String currentTime = java.time.LocalDateTime.now().toString();
            admin.setCreatedAt(currentTime);
            admin.setUpdatedAt(currentTime);
        } else if (admin.getUpdatedAt() == null || admin.getUpdatedAt().isEmpty()) {
            admin.setUpdatedAt(java.time.LocalDateTime.now().toString());
        }
        
        // 设置默认状态
        if (admin.getStatus() == null || admin.getStatus().isEmpty()) {
            admin.setStatus("在职");
        }
        
        // 设置默认头像
        if (admin.getAvatarUrl() == null || admin.getAvatarUrl().isEmpty()) {
            admin.setAvatarUrl("default_avatar.png");
        }
        
        // 设置角色
        admin.setRole("admin");
        admin.setRoleId("admin");
        
        return adminRepository.save(admin);
    }

    // 删除管理员
    public void deleteAdmin(int id) {
        adminRepository.deleteById((long) id);
    }

    // 更新管理员信息
    public boolean updateAdminInfo(UpdateAdminInfoRequest request) {
        Optional<Admin> adminOptional = adminRepository.findByAdminId(request.getUserId());
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            
            // 更新姓名
            if (request.getName() != null && !request.getName().trim().isEmpty()) {
                admin.setName(request.getName());
            }
            
            // 更新电话
            if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
                admin.setPhone(request.getPhone());
            }
            
            // 更新邮箱
            if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                admin.setEmail(request.getEmail());
            }
            
            adminRepository.save(admin);
            return true;
        }
        return false;
    }

    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    public boolean updatePasswordWithVerification(String userId, String roleId, String oldPassword, String newPassword) {
        // 验证roleId为admin
        if (!"admin".equals(roleId)) {
            return false;
        }

        // 根据userId查找用户
        Optional<Admin> adminOptional = adminRepository.findByAdminId(userId);
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();

            // 验证旧密码是否正确
            if (passwordEncoder.matches(oldPassword, admin.getPassword())) {
                // 加密新密码
                String encodedPassword = passwordEncoder.encode(newPassword);
                admin.setPassword(encodedPassword);
                adminRepository.save(admin);
                return true;
            }
        }
        return false;
    }

    /**
     * 重置指定用户的密码为默认密码"123456"
     * @param userId 用户ID
     * @param roleId 用户角色ID
     * @return 是否重置成功
     */
    public boolean resetUserPassword(String userId, String roleId) {
        try {
            switch (roleId) {
                case "admin":
                    Optional<Admin> adminOptional = adminRepository.findByAdminId(userId);
                    if (adminOptional.isPresent()) {
                        Admin admin = adminOptional.get();
                        String encodedPassword = passwordEncoder.encode("123456");
                        admin.setPassword(encodedPassword);
                        adminRepository.save(admin);
                        return true;
                    }
                    break;
                    
                case "teacher":
                    Optional<Teacher> teacherOptional = teacherRepository.findByTeacherId(userId);
                    if (teacherOptional.isPresent()) {
                        Teacher teacher = teacherOptional.get();
                        String encodedPassword = passwordEncoder.encode("123456");
                        teacher.setPassword(encodedPassword);
                        teacherRepository.save(teacher);
                        return true;
                    }
                    break;
                    
                case "student":
                    Optional<User> userOptional = userRepository.findByUserId(userId);
                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        String encodedPassword = passwordEncoder.encode("123456");
                        user.setPassword(encodedPassword);
                        userRepository.save(user);
                        return true;
                    }
                    break;
                    
                default:
                    return false;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}