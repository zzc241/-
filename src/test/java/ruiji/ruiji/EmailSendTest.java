package ruiji.ruiji;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
    "spring.mail.host=smtp.qq.com",
    "spring.mail.port=587",
    "spring.mail.username=2714156899@qq.com",
    "spring.mail.password=ycznndddlbmwdhac",
    "spring.mail.properties.mail.smtp.auth=true",
    "spring.mail.properties.mail.smtp.starttls.enable=true",
    "spring.mail.properties.mail.smtp.starttls.required=true",
    "spring.mail.properties.mail.debug=true"
})
@SpringBootTest
class EmailSendTest {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Test
    void sendTestEmail() {
        System.out.println("======= QQ邮箱SMTP发送测试 =======");
        
        if (mailSender == null) {
            System.err.println("ERROR: JavaMailSender is null!");
            return;
        }
        
        try {
            // 创建邮件
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("2714156899@qq.com");
            message.setTo("2714156899@qq.com"); // 发送给自己
            message.setSubject("【测试】Spring Boot邮件功能测试");
            message.setText(
                "尊敬的2714156899@qq.com用户：\n\n" +
                "这是一封测试邮件，用于验证您的Spring Boot应用程序的邮件发送功能是否配置正确。\n\n" +
                "如果收到此邮件，说明：\n" +
                "1. QQ邮箱SMTP服务配置正确\n" +
                "2. 授权码验证通过\n" +
                "3. Spring Boot邮件功能正常\n\n" +
                "发送时间: " + new java.util.Date() + "\n" +
                "测试ID: " + System.currentTimeMillis()
            );
            
            System.out.println("📧 邮件信息:");
            System.out.println("发件人: " + message.getFrom());
            System.out.println("收件人: " + message.getTo()[0]);
            System.out.println("主题: " + message.getSubject());
            
            System.out.println("\n🚀 正在发送邮件...");
            
            // 发送邮件
            mailSender.send(message);
            
            System.out.println("✅ 邮件发送成功！");
            System.out.println("📨 请登录QQ邮箱查看是否收到测试邮件。");
            
        } catch (org.springframework.mail.MailAuthenticationException e) {
            System.err.println("❌ 认证失败！请检查:");
            System.err.println("1. QQ邮箱: 2714156899@qq.com");
            System.err.println("2. 授权码: ycznndddlbmwdhac 是否正确");
            System.err.println("3. 是否已开启QQ邮箱SMTP服务");
            e.printStackTrace();
            
        } catch (Exception e) {
            System.err.println("❌ 发送失败:");
            e.printStackTrace();
        }
    }
    
    @Test
    void sendMultipleTestEmails() {
        System.out.println("======= 发送多封测试邮件 =======");
        
        for (int i = 1; i <= 3; i++) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("2714156899@qq.com");
                message.setTo("2714156899@qq.com");
                message.setSubject("测试邮件 #" + i + " - " + System.currentTimeMillis());
                message.setText("这是第 " + i + " 封测试邮件。\n时间: " + new java.util.Date());
                
                System.out.println("发送邮件 #" + i + "...");
                mailSender.send(message);
                System.out.println("✅ 邮件 #" + i + " 发送成功");
                
                // 短暂等待，避免发送太快
                Thread.sleep(1000);
                
            } catch (Exception e) {
                System.err.println("❌ 邮件 #" + i + " 发送失败: " + e.getMessage());
            }
        }
    }
}
