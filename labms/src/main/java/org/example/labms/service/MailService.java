package org.example.labms.service;

import org.example.labms.model.Admin;
import org.example.labms.model.Teacher;
import org.example.labms.model.User;
import org.example.labms.repository.AdminRepository;
import org.example.labms.repository.TeacherRepository;
import org.example.labms.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TeacherRepository teacherRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 生成6位随机验证码，存入Redis，并通过邮件发送
     *
     * @param roleId 角色ID
     * @param userId 用户ID
     * @param email 邮箱地址
     */
    public void sendCode(String roleId, String userId, String email) throws MessagingException {
        // 检查roleId、userId和email是否匹配
        if (!isUserEmailMatch(roleId, userId, email)) {
            throw new IllegalArgumentException("用户信息不匹配");
        }

        // 生成6位随机验证码
        String code = generateCode(6);

        // 存入Redis，key为email_reset，TTL为180秒
        redisTemplate.opsForValue().set("email_reset:" + email, code, 180, TimeUnit.SECONDS);

        // 发送HTML邮件
        MimeMessage message = mailSender.createMimeMessage(); // 创建MimeMessage对象
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // 创建MimeMessageHelper对象
        helper.setFrom(fromEmail);  // 设置发件人
        helper.setTo(email); // 设置收件人
        helper.setSubject("密码重置验证码");

        String content = "尊敬的用户，您的密码重置验证码为：<b>" + code + "</b>，3分钟内有效。";
        helper.setText(content, true);

        mailSender.send(message);
    }

    /**
     * 验证roleId、userId和email是否匹配
     *
     * @param roleId 角色ID
     * @param userId 用户ID
     * @param email 邮箱地址
     * @return 是否匹配
     */
    private boolean isUserEmailMatch(String roleId, String userId, String email) {
        // 根据roleId判断是学生还是教师
        if ("student".equals(roleId)) {
            // 检查学生信息
            Optional<User> userOptional = userRepository.findByUserId(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                return email.equals(user.getEmail());
            }
        } else if ("teacher".equals(roleId)) {
            // 检查教师信息
            Optional<Teacher> teacherOptional = teacherRepository.findByTeacherId(userId);
            if (teacherOptional.isPresent()) {
                Teacher teacher = teacherOptional.get();
                return email.equals(teacher.getEmail());
            }
        } else if ("admin".equals(roleId)) {
            // 检查管理员信息
            Optional<Admin> adminOptional = adminRepository.findByAdminId(userId);
            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();
                return email.equals(admin.getEmail());
            }
        }
        return false;
    }

    /**
     * 验证验证码
     *
     * @param email 邮箱地址
     * @param code 验证码
     * @return 验证结果
     */
    public boolean verifyCode(String email, String code) {
        String key = "email_reset:" + email;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode != null && storedCode.equals(code)) {
            // 验证成功后删除key
            redisTemplate.delete(key);
            return true;
        }
        return false;
    }

    /**
     * 生成指定长度的随机数字验证码
     *
     * @param length 验证码长度
     * @return 随机验证码
     */
    private String generateCode(int length) {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}