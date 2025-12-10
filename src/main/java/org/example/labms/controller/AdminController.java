package org.example.labms.controller;

import org.example.labms.dto.request.UpdateAdminInfoRequest;
import org.example.labms.model.Admin;
import org.example.labms.model.Teacher;
import org.example.labms.model.User;
import org.example.labms.service.AdminService;
import org.example.labms.service.TeacherService;
import org.example.labms.service.UserService;
import org.example.labms.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AdminController {
    
    @Autowired
    private AdminService adminService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;
    
    // 获取所有管理员信息
    @GetMapping("/admins")
    public List<Admin> getAllAdmins() {
        return adminService.getAllAdmins();
    }
    // 根据adminId获取管理员信息
    @GetMapping("/admin/info_id")
    public ResponseEntity<Admin> getAdminByAdminId(@RequestParam String userId) {
        Optional<Admin> admin = adminService.getAdminByAdminId(userId);
        return admin.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    // 创建或更新管理员（包括密码加密）
    @PostMapping("/admin")
    public Admin saveAdmin(@RequestBody Admin admin) {
        return adminService.saveAdmin(admin);
    }

    // 新增用户（根据roleId创建不同类型的用户）
    @PostMapping("/admin/create")
    public ResponseEntity<?> createUser(
            // 通用字段
            @RequestParam String roleId,
            @RequestParam String name,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String avatarUrl,
            
            // 管理员特有字段
            @RequestParam(required = false) String adminId,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) String hireDate,
            @RequestParam(required = false) String status,
            
            // 教师特有字段
            @RequestParam(required = false) String teacherId,
            
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String degree,
            
            // 学生特有字段
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String classId,
            @RequestParam(required = false) String major
    ) {
        // 根据不同的角色类型进行查重验证
        if ("admin".equals(roleId)) {
            // 检查管理员ID是否已存在
            if (adminId == null || adminId.isEmpty()) {
                return ResponseEntity.badRequest().body("管理员ID不能为空");
            }
            
            Optional<Admin> existingAdmin = adminService.getAdminByAdminId(adminId);
            if (existingAdmin.isPresent()) {
                return ResponseEntity.badRequest().body("管理员ID已存在: " + adminId);
            }
            
            // 创建管理员
            Admin admin = new Admin();
            admin.setAdminId(adminId);
            admin.setName(name);
            admin.setGender(gender);
            admin.setDepartment(department);
            admin.setPosition(position);
            admin.setPhone(phone);
            admin.setEmail(email);
            admin.setHireDate(hireDate);
            admin.setStatus(status != null ? status : "在职");
            admin.setPassword(password != null ? password : "123456");
            admin.setRole("admin");
            admin.setRoleId("admin");
            admin.setAvatarUrl(avatarUrl != null ? avatarUrl : "default_avatar.png");
            admin.setCreatedAt(LocalDateTime.now().toString());
            admin.setUpdatedAt(LocalDateTime.now().toString());
            
            Admin savedAdmin = adminService.saveAdmin(admin);
            return ResponseEntity.ok(savedAdmin);
        } else if ("teacher".equals(roleId)) {
            // 检查教师ID是否已存在
            if (teacherId == null || teacherId.isEmpty()) {
                return ResponseEntity.badRequest().body("教师ID不能为空");
            }
            
            Optional<Teacher> existingTeacher = teacherService.getTeacherByTeacherId(teacherId);
            if (existingTeacher.isPresent()) {
                return ResponseEntity.badRequest().body("教师ID已存在: " + teacherId);
            }
            
            // 创建教师
            Teacher teacher = new Teacher();
            teacher.setTeacherId(teacherId);
            teacher.setName(name);
            teacher.setGender(gender);
            teacher.setDepartment(department);
            teacher.setTitle(title);
            teacher.setPhone(phone);
            teacher.setEmail(email);
            teacher.setDegree(degree);
            teacher.setHireDate(hireDate);
            teacher.setStatus(status != null ? status : "在职");
            teacher.setPassword(password != null ? password : "123456");
            teacher.setRole("teacher");
            teacher.setRoleId("teacher");
            teacher.setAvatarUrl(avatarUrl != null ? avatarUrl : "default_avatar.png");
            teacher.setCreatedAt(LocalDateTime.now().toString());
            teacher.setUpdatedAt(LocalDateTime.now().toString());
            
            Teacher savedTeacher = teacherService.saveTeacher(teacher);
            return ResponseEntity.ok(savedTeacher);
        } else if ("student".equals(roleId)) {
            // 检查学生ID是否已存在
            if (userId == null || userId.isEmpty()) {
                return ResponseEntity.badRequest().body("学生ID不能为空");
            }
            
            Optional<User> existingUser = userService.getUserByUserId(userId);
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest().body("学生ID已存在: " + userId);
            }
            
            // 创建学生
            User user = new User();
            user.setUserId(userId);
            user.setName(name);
            user.setGender(gender);
            user.setClassId(classId);
            user.setMajor(major);
            user.setPhone(phone);
            user.setEmail(email);
            user.setRoleId("student");
            user.setRole("student");
            user.setPassword(password != null ? password : "123456");
            user.setAvatarUrl(avatarUrl != null ? avatarUrl : "default_avatar.png");
            user.setCreatedAt(LocalDateTime.now().toString());
            user.setUpdatedAt(LocalDateTime.now().toString());
            
            User savedUser = userService.saveUser(user);
            return ResponseEntity.ok(savedUser);
        } else {
            return ResponseEntity.badRequest().body("无效的roleId: " + roleId);
        }
    }

    // 删除用户（根据roleId和userId删除对应的用户）
    @DeleteMapping("/admin/delete")
    public ResponseEntity<String> deleteUser(
            @RequestParam String roleId,
            @RequestParam String userId) {
        
        boolean isDeleted = false;
        
        switch (roleId) {
            case "admin":
                // 删除管理员
                Optional<Admin> adminOptional = adminService.getAdminByAdminId(userId);
                if (adminOptional.isPresent()) {
                    adminService.deleteAdmin(adminOptional.get().getId());
                    isDeleted = true;
                }
                break;
                
            case "teacher":
                // 删除教师
                Optional<Teacher> teacherOptional = teacherService.getTeacherByTeacherId(userId);
                if (teacherOptional.isPresent()) {
                    teacherService.deleteTeacher(teacherOptional.get().getId());
                    isDeleted = true;
                }
                break;
                
            case "student":
                // 删除学生
                Optional<User> userOptional = userService.getUserByUserId(userId);
                if (userOptional.isPresent()) {
                    userService.deleteStudent(userOptional.get().getId());
                    isDeleted = true;
                }
                break;
                
            default:
                return ResponseEntity.badRequest().body("无效的roleId: " + roleId);
        }
        
        if (isDeleted) {
            return ResponseEntity.ok("用户删除成功");
        } else {
            return ResponseEntity.badRequest().body("未找到对应的用户: " + userId);
        }
    }

    // 更新管理员信息
    @PostMapping("/admin/update")
    public ResponseEntity<String> updateAdminInfo(
            @RequestParam String userId,
            @RequestParam String name,
            @RequestParam String roleId,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email) {
        
        UpdateAdminInfoRequest request = new UpdateAdminInfoRequest();
        request.setUserId(userId);
        request.setName(name);
        request.setRoleId(roleId);
        request.setPhone(phone);
        request.setEmail(email);
        
        boolean success = adminService.updateAdminInfo(request);
        if (success) {
            return ResponseEntity.ok("管理员信息更新成功");
        } else {
            return ResponseEntity.badRequest().body("未找到对应管理员或更新失败");
        }
    }

    // 修改密码
    @PostMapping("/admin/update_password")
    public ResponseEntity<String> updatePassword(
            @RequestParam String userId,
            @RequestParam String roleId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String emailVerification) {

        // 验证roleId为student
        if (!"admin".equals(roleId)) {
            return ResponseEntity.badRequest().body("仅支持管理员角色修改密码");
        }

        // 先获取用户邮箱用于验证验证码
        Optional<Admin> adminOptional = adminService.getAdminByAdminId(userId);
        if (!adminOptional.isPresent()) {
            return ResponseEntity.badRequest().body("用户不存在");
        }

        Admin admin = adminOptional.get();
        String email = admin.getEmail();

        // 验证邮箱验证码
        if (!mailService.verifyCode(email, emailVerification)) {
            return ResponseEntity.badRequest().body("邮箱验证码错误或已过期");
        }

        // 调用UserService更新密码
        boolean success = adminService.updatePasswordWithVerification(userId, roleId, oldPassword, newPassword);
        if (success) {
            return ResponseEntity.ok("密码修改成功");
        } else {
            return ResponseEntity.badRequest().body("旧密码错误或用户不存在");
        }
    }

    // 更新用户信息
    @PostMapping("/admin/update_user")
    public ResponseEntity<String> updateUserInfo(
            // 通用字段
            @RequestParam String roleId,
            @RequestParam String userId,
            @RequestParam(required = false) String studentId,
            @RequestParam(required = false) String teacherId,
            @RequestParam(required = false) String adminId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email,
            
            // 学生特有字段
            @RequestParam(required = false) String classId,
            @RequestParam(required = false) String major,
            
            // 教师特有字段
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String degree,
            @RequestParam(required = false) String hireDate,
            
            // 管理员特有字段
            @RequestParam(required = false) String position
    ) {
        try {
            switch (roleId) {
                case "student":
                    // 更新学生信息
                    Optional<User> userOptional = userService.getUserByUserId(userId);
                    if (!userOptional.isPresent()) {
                        return ResponseEntity.badRequest().body("未找到对应的学生用户");
                    }
                    
                    User user = userOptional.get();
                    if (studentId != null && !studentId.isEmpty()) user.setUserId(studentId);
                    if (name != null && !name.isEmpty()) user.setName(name);
                    if (gender != null && !gender.isEmpty()) user.setGender(gender);
                    if (phone != null && !phone.isEmpty()) user.setPhone(phone);
                    if (email != null && !email.isEmpty()) user.setEmail(email);
                    if (classId != null && !classId.isEmpty()) user.setClassId(classId);
                    if (major != null && !major.isEmpty()) user.setMajor(major);
                    
                    userService.saveUser(user);
                    break;
                    
                case "teacher":
                    // 更新教师信息
                    Optional<Teacher> teacherOptional = teacherService.getTeacherByTeacherId(userId);
                    if (!teacherOptional.isPresent()) {
                        return ResponseEntity.badRequest().body("未找到对应的教师用户");
                    }
                    
                    Teacher teacher = teacherOptional.get();
                    if (teacherId != null && !teacherId.isEmpty()) teacher.setTeacherId(teacherId);
                    if (name != null && !name.isEmpty()) teacher.setName(name);
                    if (gender != null && !gender.isEmpty()) teacher.setGender(gender);
                    if (phone != null && !phone.isEmpty()) teacher.setPhone(phone);
                    if (email != null && !email.isEmpty()) teacher.setEmail(email);
                    if (department != null && !department.isEmpty()) teacher.setDepartment(department);
                    if (title != null && !title.isEmpty()) teacher.setTitle(title);
                    if (degree != null && !degree.isEmpty()) teacher.setDegree(degree);
                    if (hireDate != null && !hireDate.isEmpty()) teacher.setHireDate(hireDate);
                    
                    teacherService.saveTeacher(teacher);
                    break;
                    
                case "admin":
                    // 更新管理员信息
                    Optional<Admin> adminOptional = adminService.getAdminByAdminId(userId);
                    if (!adminOptional.isPresent()) {
                        return ResponseEntity.badRequest().body("未找到对应的管理员用户");
                    }
                    
                    Admin admin = adminOptional.get();
                    if (adminId != null && !adminId.isEmpty()) admin.setAdminId(adminId);
                    if (name != null && !name.isEmpty()) admin.setName(name);
                    if (gender != null && !gender.isEmpty()) admin.setGender(gender);
                    if (phone != null && !phone.isEmpty()) admin.setPhone(phone);
                    if (email != null && !email.isEmpty()) admin.setEmail(email);
                    if (department != null && !department.isEmpty()) admin.setDepartment(department);
                    if (position != null && !position.isEmpty()) admin.setPosition(position);
                    if (hireDate != null && !hireDate.isEmpty()) admin.setHireDate(hireDate);
                    
                    adminService.saveAdmin(admin);
                    break;
                    
                default:
                    return ResponseEntity.badRequest().body("无效的roleId: " + roleId);
            }
            
            return ResponseEntity.ok("用户信息更新成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("更新失败: " + e.getMessage());
        }
    }

    // 重置用户密码
    @PostMapping("/admin/reset_password")
    public ResponseEntity<String> resetUserPassword(
            @RequestParam String roleId,
            @RequestParam String userId) {
        
        boolean success = adminService.resetUserPassword(userId, roleId);
        if (success) {
            return ResponseEntity.ok("用户密码重置成功，已重置为默认密码123456");
        } else {
            return ResponseEntity.badRequest().body("用户密码重置失败，请检查用户ID和角色ID是否正确");
        }
    }
}