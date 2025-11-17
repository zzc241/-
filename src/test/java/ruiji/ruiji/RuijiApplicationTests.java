package ruiji.ruiji;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@SpringBootTest
@EnableTransactionManagement
@Slf4j
class RuijiApplicationTests {

	@Autowired
	private RedisTemplate<Object , Object> redisTemplate;
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



}
