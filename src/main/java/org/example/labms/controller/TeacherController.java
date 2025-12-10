package org.example.labms.controller;

import org.example.labms.model.Teacher;
import org.example.labms.model.User;
import org.example.labms.service.MailService;
import org.example.labms.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class TeacherController {
    
    @Autowired
    private TeacherService teacherService;

    @Autowired
    private MailService mailService;

    // 获取所有教师信息
    @GetMapping("/teachers")
    public List<Teacher> getAllTeachers() {
        return teacherService.getAllTeachers();
    }
    // 根据teacherId获取教师信息
    @GetMapping("/teacher/info_id")
    public ResponseEntity<Teacher> getTeacherByTeacherId(@RequestParam String userId) {
        Optional<Teacher> teacher = teacherService.getTeacherByTeacherId(userId);
        return teacher.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    // 创建或更新教师（包括密码加密）
    @PostMapping("/teacher")
    public Teacher saveTeacher(@RequestBody Teacher teacher) {
        return teacherService.saveTeacher(teacher);
    }

    // 修改密码
    @PostMapping("/teacher/update_password")
    public ResponseEntity<String> updatePassword(
            @RequestParam String userId,
            @RequestParam String roleId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String emailVerification) {

        // 验证roleId为student
        if (!"teacher".equals(roleId)) {
            return ResponseEntity.badRequest().body("仅支持教师角色修改密码");
        }

        // 先获取用户邮箱用于验证验证码
        Optional<Teacher> teacherOptional = teacherService.getTeacherByTeacherId(userId);
        if (!teacherOptional.isPresent()) {
            return ResponseEntity.badRequest().body("用户不存在");
        }

        Teacher teacher = teacherOptional.get();
        String email = teacher.getEmail();

        // 验证邮箱验证码
        if (!mailService.verifyCode(email, emailVerification)) {
            return ResponseEntity.badRequest().body("邮箱验证码错误或已过期");
        }

        // 调用UserService更新密码
        boolean success = teacherService.updatePasswordWithVerification(userId, roleId, oldPassword, newPassword);
        if (success) {
            return ResponseEntity.ok("密码修改成功");
        } else {
            return ResponseEntity.badRequest().body("旧密码错误或用户不存在");
        }
    }
}