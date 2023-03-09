package com.noah;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.noah.config.RedisConfig;
import com.noah.pojo.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;


@SpringBootTest
class Redis02SpringbootApplicationTests {

	@Autowired
	@Qualifier("redisTemplate")
	RedisTemplate redisTemplate;


	@Test
	void contextLoads() {

		//操作不同类型
		//操作字符串，类似String
//		redisTemplate.opsForValue();
//		redisTemplate.opsForList();
//		redisTemplate.opsForHash();
//		redisTemplate.opsForSet();
//		redisTemplate.opsForZSet();
//		redisTemplate.opsForGeo();
//		redisTemplate.opsForHyperLogLog();

		//除了基本的操作，redisTemplate里也有常用的方法可以直接使用，比如事务和基本的增删改查
//		redisTemplate.multi();
//		redisTemplate.discard();
//		redisTemplate.exec();

		//获取redis的连接对象
//		RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
//		connection.flushDb();
//		connection.flushAll();

		redisTemplate.opsForValue().set("name","noah");
		System.out.println(redisTemplate.opsForValue().get("name"));
	}

	@Test
	public void test() throws JsonProcessingException {
		redisTemplate.getConnectionFactory().getConnection().flushDb();
		//真实开发一般都是用json传递对象
		User user = new User("柯基", 1);
//		String jsonUser = new ObjectMapper().writeValueAsString(user);
		redisTemplate.opsForValue().set("user",user);
		System.out.println(redisTemplate.opsForValue().get("user"));
	}
}
