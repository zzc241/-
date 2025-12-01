package ruiji.ruiji.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import java.util.Random;

@Service
public class HtmlEmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    /**
     * 发送HTML格式的验证码邮件
     */
    public void sendHtmlVerificationCode(String email) throws Exception {
        String verificationCode = generateVerificationCode();
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setFrom("your_qq_email@qq.com");
        helper.setTo(email);
        helper.setSubject("验证码通知 - 您的验证码");
        
        String htmlContent = "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "    <meta charset='UTF-8'>"
                + "    <title>验证码</title>"
                + "</head>"
                + "<body style='font-family: Arial, sans-serif;'>"
                + "    <div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd;'>"
                + "        <h2 style='color: #333;'>尊敬的用户，您好！</h2>"
                + "        <p>您正在进行身份验证，验证码如下：</p>"
                + "        <div style='background-color: #f5f5f5; padding: 15px; margin: 20px 0; text-align: center;'>"
                + "            <span style='font-size: 24px; font-weight: bold; color: #e74c3c;'>" + verificationCode + "</span>"
                + "        </div>"
                + "        <p>验证码有效期：<strong>5分钟</strong></p>"
                + "        <p>如果不是您本人操作，请忽略此邮件。</p>"
                + "        <hr>"
                + "        <p style='color: #999; font-size: 12px;'>此为系统自动发送邮件，请勿回复。</p>"
                + "    </div>"
                + "</body>"
                + "</html>";
        
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
        System.out.println("HTML验证码邮件已发送到：" + email);
    }
    
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
