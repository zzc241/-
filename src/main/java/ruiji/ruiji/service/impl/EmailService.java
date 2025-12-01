package ruiji.ruiji.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.Random;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    /**
     * 生成6位随机验证码
     */
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
    public void sendVerificationCode(String toEmail) {
        try {
            System.out.println("=== EmailService.sendVerificationCode ===");
            System.out.println("收件人: " + toEmail);
            System.out.println("mailSender: " + mailSender);
            
            if (mailSender == null) {
                System.err.println("mailSender 是 null!");
                return;
            }
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("2714156899@qq.com");
            message.setTo(toEmail);
            message.setSubject("测试");
            message.setText("测试内容");
            
            System.out.println("发送前...");
            mailSender.send(message);
            System.out.println("发送成功!");
            
        } catch (Exception e) {
            System.err.println("发送失败:");
            e.printStackTrace();
            throw new RuntimeException("发送验证码失败", e);
        }
    }
    
    /**
     * 发送验证码到邮箱
     */
    // public void sendVerificationCode(String toEmail) {
    //     try {
    //         System.out.println("=== 邮件发送调试信息 ===");
    //         System.out.println("目标邮箱: " + toEmail);
            
    //         String verificationCode = generateVerificationCode();
    //         System.out.println("生成验证码: " + verificationCode);
            
    //         SimpleMailMessage message = new SimpleMailMessage();
    //         message.setFrom("2714156899@qq.com"); // 使用你的QQ邮箱
    //         message.setTo(toEmail);
    //         message.setSubject("验证码通知");
    //         message.setText("您的验证码是：" + verificationCode + "，有效期5分钟，请勿泄露给他人。");
            
    //         System.out.println("发件人: " + message.getFrom());
    //         System.out.println("收件人: " + message.getTo()[0]);
    //         System.out.println("邮件主题: " + message.getSubject());
            
    //         // 测试邮箱发送器是否正常
    //         System.out.println("MailSender类型: " + (mailSender != null ? mailSender.getClass().getName() : "NULL"));
            
    //         // 发送邮件
    //         System.out.println("正在发送邮件...");
    //         mailSender.send(message);
    //         System.out.println("邮件发送成功!");
            
    //     } catch (Exception e) {
    //         System.err.println("=== 邮件发送失败详细信息 ===");
    //         System.err.println("异常类型: " + e.getClass().getName());
    //         System.err.println("异常信息: " + e.getMessage());
    //         e.printStackTrace();
            
    //         // 如果是特定的邮件异常，打印更多信息
    //         if (e instanceof javax.mail.AuthenticationFailedException) {
    //             System.err.println("认证失败！请检查：");
    //             System.err.println("1. QQ邮箱用户名是否正确: 2714156899@qq.com");
    //             System.err.println("2. 授权码是否正确（不是QQ密码）");
    //             System.err.println("3. 是否已开启SMTP服务");
    //         } else if (e instanceof javax.mail.MessagingException) {
    //             System.err.println("邮件消息异常，可能是配置问题");
    //         }
            
    //         throw new RuntimeException("发送验证码失败: " + e.getMessage(), e);
    //     }
    // }
}