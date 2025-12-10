package org.example.labms.service;

import org.example.labms.model.Teacher;
import org.example.labms.model.User;
import org.example.labms.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // 根据ID获取教师
    public Optional<Teacher> getTeacherByTeacherId(String teacherId) {
        return teacherRepository.findByTeacherId(teacherId);
    }
    
    // 创建或更新教师，同时加密密码
    public Teacher saveTeacher(Teacher teacher) {
        // 如果密码未设置或者为空，则使用默认密码
        if (teacher.getPassword() == null || teacher.getPassword().isEmpty()) {
            teacher.setPassword("123456");
        }
        
        // 加密密码
        String encodedPassword = passwordEncoder.encode(teacher.getPassword());
        teacher.setPassword(encodedPassword);
        
        // 设置创建时间和更新时间
        if (teacher.getCreatedAt() == null || teacher.getCreatedAt().isEmpty()) {
            String currentTime = java.time.LocalDateTime.now().toString();
            teacher.setCreatedAt(currentTime);
            teacher.setUpdatedAt(currentTime);
        } else if (teacher.getUpdatedAt() == null || teacher.getUpdatedAt().isEmpty()) {
            teacher.setUpdatedAt(java.time.LocalDateTime.now().toString());
        }
        
        // 设置默认状态
        if (teacher.getStatus() == null || teacher.getStatus().isEmpty()) {
            teacher.setStatus("在职");
        }
        
        // 设置默认头像
        if (teacher.getAvatarUrl() == null || teacher.getAvatarUrl().isEmpty()) {
            teacher.setAvatarUrl("default_avatar.png");
        }
        
        // 设置角色
        teacher.setRole("teacher");
        teacher.setRoleId("teacher");
        
        return teacherRepository.save(teacher);
    }

    // 删除教师
    public void deleteTeacher(int id) {
        teacherRepository.deleteById((long) id);
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public boolean updatePasswordWithVerification(String userId, String roleId, String oldPassword, String newPassword) {
        // 验证roleId为teacher
        if (!"teacher".equals(roleId)) {
            return false;
        }

        // 根据teacherId查找用户
        Optional<Teacher> teacherOptional = teacherRepository.findByTeacherId(userId);
        if (teacherOptional.isPresent()) {
            Teacher teacher = teacherOptional.get();

            // 验证旧密码是否正确
            if (passwordEncoder.matches(oldPassword, teacher.getPassword())) {
                // 加密新密码
                String encodedPassword = passwordEncoder.encode(newPassword);
                teacher.setPassword(encodedPassword);
                teacherRepository.save(teacher);
                return true;
            }
        }
        return false;
    }
}