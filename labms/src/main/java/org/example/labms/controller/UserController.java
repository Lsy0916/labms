package org.example.labms.controller;

import org.example.labms.dto.UserInfoResponseDTO;
import org.example.labms.dto.request.UpdateUserInfoRequest;
import org.example.labms.model.Classes;
import org.example.labms.model.User;
import org.example.labms.service.MailService;
import org.example.labms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {
    
    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    /**
     * 获取所有用户信息
     * @return 用户信息列表
     */
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * 获取所有用户信息(不包含敏感信息)
     * @return 用户信息列表（无敏感信息）
     */
    @GetMapping("/usersInfo")
    public List<UserInfoResponseDTO> getAllUsersInfo() {
        return userService.getAllUsersWithoutSensitiveInfo();
    }
    
    /**
     * 根据userId获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    @GetMapping("/user/info_id")
    public ResponseEntity<User> getUserByUserId(@RequestParam String userId) {
        Optional<User> user = userService.getUserByUserId(userId);
        return user.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * 创建或更新用户（包括密码加密）
     * @param user 用户对象
     * @return 保存后的用户对象
     */
    @PostMapping("/user")
    public User saveUser(@RequestBody User user) {
        return userService.saveUser(user);
    }
    
    /**
     * 更新用户信息（手机号和邮箱）
     * @param userId 用户ID
     * @param roleId 角色ID
     * @param phone 电话
     * @param email 邮箱
     * @return 更新结果信息
     */
    @PostMapping("/user/update")
    public ResponseEntity<String> updateUserInfo(
            @RequestParam String userId,
            @RequestParam String roleId,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String email) {
        
        UpdateUserInfoRequest request = new UpdateUserInfoRequest();
        request.setUserId(userId);
        request.setRoleId(roleId);
        request.setPhone(phone);
        request.setEmail(email);
        
        boolean success = userService.updateUserInfo(request);
        if (success) {
            return ResponseEntity.ok("用户信息更新成功");
        } else {
            return ResponseEntity.badRequest().body("未找到对应用户或更新失败");
        }
    }
    
    /**
     * 新增学生
     * @param userId 学生ID
     * @param name 姓名
     * @param gender 性别
     * @param classId 班级ID
     * @param major 专业
     * @param phone 电话
     * @param email 邮箱
     * @return 创建的学生对象或错误信息
     */
    @PostMapping("/user/add_student")
    public ResponseEntity<?> addStudent(
            @RequestParam String userId,
            @RequestParam String name,
            @RequestParam String gender,
            @RequestParam String classId,
            @RequestParam String major,
            @RequestParam String phone,
            @RequestParam String email) {
        
        // 检查userId是否已经存在
        if (userService.getUserByUserId(userId).isPresent()) {
            return ResponseEntity.badRequest().body("该学号已存在");
        }
        
        // 创建学生对象
        User student = new User();
        student.setUserId(userId);
        student.setName(name);
        student.setGender(gender);
        student.setClassId(classId);
        student.setMajor(major);
        student.setPhone(phone);
        student.setEmail(email);
        
        // 保存学生信息
        User savedStudent = userService.addStudent(student);
        return ResponseEntity.ok(savedStudent);
    }
    
    /**
     * 删除学生信息
     * @param userId 学生ID
     * @return 删除结果信息
     */
    @DeleteMapping("/user/delete_student")
    public ResponseEntity<String> deleteStudent(@RequestParam String userId) {
        boolean deleted = userService.deleteStudentByUserId(userId);
        if (deleted) {
            return ResponseEntity.ok("学生信息删除成功");
        } else {
            return ResponseEntity.badRequest().body("未找到对应学生或删除失败");
        }
    }
    
    /**
     * 修改密码
     * @param userId 用户ID
     * @param roleId 角色ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @param emailVerification 邮箱验证码
     * @return 修改结果信息
     */
    @PostMapping("/user/update_password")
    public ResponseEntity<String> updatePassword(
            @RequestParam String userId,
            @RequestParam String roleId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String emailVerification) {
        
        // 验证roleId为student
        if (!"student".equals(roleId)) {
            return ResponseEntity.badRequest().body("仅支持学生角色修改密码");
        }
        
        // 先获取用户邮箱用于验证验证码
        Optional<User> userOptional = userService.getUserByUserId(userId);
        if (!userOptional.isPresent()) {
            return ResponseEntity.badRequest().body("用户不存在");
        }
        
        User user = userOptional.get();
        String email = user.getEmail();
        
        // 验证邮箱验证码
        if (!mailService.verifyCode(email, emailVerification)) {
            return ResponseEntity.badRequest().body("邮箱验证码错误或已过期");
        }
        
        // 调用UserService更新密码
        boolean success = userService.updatePasswordWithVerification(userId, roleId, oldPassword, newPassword);
        if (success) {
            return ResponseEntity.ok("密码修改成功");
        } else {
            return ResponseEntity.badRequest().body("旧密码错误或用户不存在");
        }
    }
}