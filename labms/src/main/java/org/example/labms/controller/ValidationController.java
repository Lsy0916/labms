package org.example.labms.controller;

import org.example.labms.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api")
public class ValidationController {

    @Autowired
    private MailService mailService;

    /**
     * 发送验证码接口
     *
     * @param roleId 角色ID
     * @param userId 用户ID
     * @param email 邮箱地址
     * @return 发送结果
     */
    @PostMapping("/sendCode")
    public ResponseEntity<String> sendCode(
            @RequestParam String roleId,
            @RequestParam String userId,
            @RequestParam String email) {
        try {
            mailService.sendCode(roleId, userId, email);
            return ResponseEntity.ok("验证码已发送");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("发送验证码失败");
        }
    }
}