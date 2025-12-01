package ruiji.ruiji;

import org.springframework.core.env.Environment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import ruiji.ruiji.service.impl.EmailService;

@SpringBootTest
@EnableTransactionManagement
@Slf4j
class RuijiApplicationTests {

	@Autowired
	private RedisTemplate<Object , Object> redisTemplate;

	@Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Autowired
    private Environment environment;
	@Test
	void contextLoads() {
	}
	@Test
	void testJedis(){
		log.info("测试");
		Jedis jedis = new Jedis("192.168.164.182",6379);

		jedis.auth("123456");

		log.info(jedis.ping());

		jedis.set("dongbeiyujie","yujie");

		log.info(jedis.get("dongbeiyujie"));

		jedis.close();
	}

	@Test
	void testJedisPool(){ 
		redisTemplate.opsForValue().set("dongbei123","yujie");

		String value = (String) redisTemplate.opsForValue().get("dongbei123");
		System.out.println(value);
	}

	@Test
    void debugMailConfig() {
        System.out.println("=== 邮件配置调试 ===");
        
        // 检查配置
        System.out.println("1. 检查配置属性:");
        System.out.println("spring.mail.host: " + environment.getProperty("spring.mail.host"));
        System.out.println("spring.mail.port: " + environment.getProperty("spring.mail.port"));
        System.out.println("spring.mail.username: " + environment.getProperty("spring.mail.username"));
        
        // 检查密码（注意安全，生产环境不要打印）
        String password = environment.getProperty("spring.mail.password");
        System.out.println("spring.mail.password配置: " + (password != null ? "已配置" : "未配置"));
        System.out.println("密码长度: " + (password != null ? password.length() : "N/A"));
        
        // 检查邮件发送器
        if (mailSender == null) {
            System.err.println("❌ JavaMailSender 未注入！");
            System.err.println("可能的原因:");
            System.err.println("1. 缺少spring-boot-starter-mail依赖");
            System.err.println("2. 邮件配置错误");
            System.err.println("3. Spring Boot自动配置失败");
        } else {
            System.out.println("✅ JavaMailSender 已注入");
            System.out.println("类型: " + mailSender.getClass().getName());
            
            if (mailSender instanceof JavaMailSenderImpl) {
                JavaMailSenderImpl impl = (JavaMailSenderImpl) mailSender;
                System.out.println("主机: " + impl.getHost());
                System.out.println("端口: " + impl.getPort());
                System.out.println("用户名: " + impl.getUsername());
                
                // 检查JavaMail属性
                System.out.println("JavaMail属性:");
                impl.getJavaMailProperties().forEach((key, value) -> 
                    System.out.println("  " + key + " = " + value)
                );
            }
        }
        
        // 测试环境
        System.out.println("\n2. 测试网络连接:");
        testNetworkConnection();
    }
    
    private void testNetworkConnection() {
        try {
            System.out.println("测试连接到QQ邮箱SMTP服务器...");
            java.net.Socket socket = new java.net.Socket();
            socket.connect(new java.net.InetSocketAddress("smtp.qq.com", 587), 5000);
            System.out.println("✅ 可以连接到 smtp.qq.com:587");
            socket.close();
        } catch (Exception e) {
            System.err.println("❌ 无法连接到 smtp.qq.com:587 - " + e.getMessage());
            
            // 尝试465端口
            try {
                System.out.println("尝试465端口...");
                java.net.Socket socket = new java.net.Socket();
                socket.connect(new java.net.InetSocketAddress("smtp.qq.com", 465), 5000);
                System.out.println("✅ 可以连接到 smtp.qq.com:465");
                socket.close();
            } catch (Exception e2) {
                System.err.println("❌ 也无法连接到 smtp.qq.com:465 - " + e2.getMessage());
            }
        }
    }

    
	
}
