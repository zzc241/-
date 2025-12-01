package ruiji.ruiji.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import ruiji.ruiji.common.R;
import ruiji.ruiji.pojo.User;
import ruiji.ruiji.service.UserService;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    /**
     * 发送QQ邮箱验证码
     * @param params 包含email参数
     * @param session
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody Map<String, String> params, HttpSession session) {
        // 获取邮箱地址
        String email = params.get("phone");
        
        if (email == null || email.trim().isEmpty()) {
            return R.error("邮箱地址不能为空");
        }
        
        // 验证邮箱格式（简单验证）
        if (!isValidEmail(email)) {
            return R.error("邮箱格式不正确，请输入有效的QQ邮箱");
        }
        
        // 可选：验证是否是QQ邮箱
        if (!isQQEmail(email)) {
            log.warn("非QQ邮箱尝试获取验证码: {}", email);
            // 这里可以根据需要决定是否允许非QQ邮箱
            // return R.error("请使用QQ邮箱");
        }
        
        try {
            // 生成随机的6位验证码
            Random r = new Random();
            String code = String.valueOf(100000 + r.nextInt(900000));
            log.info("为邮箱 {} 生成验证码: {}", email, code);
            
            // 发送验证码到QQ邮箱
            boolean sendResult = sendVerificationCodeToEmail(email, code);
            
            if (sendResult) {
                // 保存验证码到Redis（推荐）
                String redisKey = "EMAIL_VERIFY_CODE:" + email;
                redisTemplate.opsForValue().set(redisKey, code, 5, TimeUnit.MINUTES);
                
                // 同时保存到Session（兼容性）
                session.setAttribute(email, code);
                session.setMaxInactiveInterval(300); // 5分钟
                
                log.info("验证码已发送到邮箱: {}", email);
                return R.success("验证码已发送到您的QQ邮箱，请查收");
            } else {
                return R.error("验证码发送失败，请稍后重试");
            }
            
        } catch (Exception e) {
            log.error("发送验证码失败", e);
            return R.error("验证码发送失败: " + e.getMessage());
        }
    }
    
    /**
     * 发送验证码到指定邮箱
     * @param email 收件人邮箱
     * @param code 验证码
     * @return 是否发送成功
     */
    private boolean sendVerificationCodeToEmail(String email, String code) {
        try {
            // 创建邮件消息
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("2714156899@qq.com"); // 发件人（你的QQ邮箱）
            message.setTo(email);                 // 收件人（用户输入的QQ邮箱）
            message.setSubject("【瑞吉外卖】登录验证码");
            message.setText(
                "尊敬的用户，您好！\n\n" +
                "您正在使用邮箱 " + email + " 进行登录操作。\n" +
                "验证码：" + code + "\n" +
                "有效期：5分钟\n\n" +
                "重要提示：请勿将验证码泄露给他人。\n" +
                "如非本人操作，请忽略此邮件。\n\n" +
                "瑞吉外卖团队\n" +
                "时间：" + java.time.LocalDateTime.now()
            );
            
            // 发送邮件
            mailSender.send(message);
            log.info("验证码邮件已成功发送到: {}", email);
            return true;
            
        } catch (Exception e) {
            log.error("发送验证码邮件失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 邮箱验证码登录
     * @param params 包含email和code参数
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> params, HttpSession session) {
        log.info("邮箱登录请求: {}", params);
        
        // 获取邮箱
        String email = params.get("phone");
        // 获取验证码
        String code = params.get("code");
        
        if (email == null || email.trim().isEmpty()) {
            return R.error("邮箱地址不能为空");
        }
        
        if (code == null || code.trim().isEmpty()) {
            return R.error("验证码不能为空");
        }
        
        // 验证邮箱格式
        if (!isValidEmail(email)) {
            return R.error("邮箱格式不正确");
        }
        
        try {
            // 从Redis获取保存的验证码
            String redisKey = "EMAIL_VERIFY_CODE:" + email;
            String savedCode = redisTemplate.opsForValue().get(redisKey);
            
            // 如果Redis中没有，尝试从Session获取
            if (savedCode == null) {
                Object codeInSession = session.getAttribute(email);
                if (codeInSession != null) {
                    savedCode = codeInSession.toString();
                }
            }
            
            log.info("邮箱: {}, 输入验证码: {}, 保存的验证码: {}", email, code, savedCode);
            
            // 进行验证码的比对
            if (savedCode != null && savedCode.equals(code)) {
                // 验证码正确，登录成功
                
                // 删除已使用的验证码
                if (redisTemplate.hasKey(redisKey)) {
                    redisTemplate.delete(redisKey);
                }
                session.removeAttribute(email);
                
                // 查询用户（根据邮箱查询）
                LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(User::getPhone, email);
                
                User user = userService.getOne(queryWrapper);
                if (user == null) {
                    // 如果是新用户，自动完成注册
                    user = new User();
                    user.setPhone(email);
                    user.setStatus(1);
                    
                    // 从邮箱提取用户名
                    String username = extractUsernameFromEmail(email);
                    user.setName(username);
                    
                    userService.save(user);
                    log.info("新用户注册: {}", email);
                }
                
                // 将用户信息存入Session
                session.setAttribute("user", user.getId());
                session.setAttribute("userEmail", email);
                
                log.info("用户登录成功: {}", email);
                return R.success(user);
            } else {
                log.warn("验证码错误: 邮箱={}, 输入={}, 保存={}", email, code, savedCode);
                return R.error("验证码错误或已过期");
            }
            
        } catch (Exception e) {
            log.error("登录失败", e);
            return R.error("登录失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证邮箱格式
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // 简单的邮箱格式验证
        String reg = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(reg);
    }
    
    /**
     * 检查是否是QQ邮箱
     */
    private boolean isQQEmail(String email) {
        return email != null && email.toLowerCase().endsWith("@qq.com");
    }
    
    /**
     * 从邮箱地址提取用户名
     */
    private String extractUsernameFromEmail(String email) {
        if (email == null || !email.contains("@")) {
            return "用户";
        }
        String username = email.substring(0, email.indexOf("@"));
        return "用户_" + username;
    }
    
    // /**
    //  * 兼容原有的登录接口（如果有需要）
    //  */
    // @PostMapping("/login")
    // public R<User> login(@RequestBody Map<String, String> params, HttpSession session) {
    //     // 如果参数中有email，使用邮箱登录
    //     if (params.containsKey("email")) {
    //         return loginByEmail(params, session);
    //     }
    //     // 否则使用原有的手机号登录（如果有需要）
    //     else if (params.containsKey("phone")) {
    //         return R.error("请使用邮箱登录");
    //     }
        
    //     return R.error("请提供邮箱地址");
    // }
}