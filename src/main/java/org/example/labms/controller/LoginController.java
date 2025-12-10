package org.example.labms.controller;

import org.example.labms.dto.request.LoginRequest;
import org.example.labms.dto.request.LoginResponse;
import org.example.labms.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private LoginService loginService;

    // 学生登录
    @PostMapping("/student/login")
    public ResponseEntity<LoginResponse> studentLogin(
            @RequestParam String userId,
            @RequestParam String password,
            @RequestParam String roleId) {
        return getLoginResponseResponseEntity(userId, password, roleId);
    }

    // 教师登录
    @PostMapping("/teacher/login")
    public ResponseEntity<LoginResponse> teacherLogin(
            @RequestParam String userId,
            @RequestParam String password,
            @RequestParam String roleId) {
        return getLoginResponseResponseEntity(userId, password, roleId);
    }

    // 管理员登录
    @PostMapping("/admin/login")
    public ResponseEntity<LoginResponse> adminLogin(
            @RequestParam String userId,
            @RequestParam String password,
            @RequestParam String roleId) {
        return getLoginResponseResponseEntity(userId, password, roleId);
    }
    private ResponseEntity<LoginResponse> getLoginResponseResponseEntity(@RequestParam String userId, @RequestParam String password, @RequestParam String roleId) {
        LoginRequest loginRequest = new LoginRequest(userId, password, roleId);
        LoginResponse response = loginService.authenticate(
                loginRequest.getUserId(),
                loginRequest.getPassword(),
                loginRequest.getRoleId()
        );
        if (response.getUserInfo() != null) {
            // 登录成功
            return ResponseEntity.ok(response);
        } else {
            // 登录失败，返回详细的错误信息
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}