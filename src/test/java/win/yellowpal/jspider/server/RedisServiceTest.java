package win.yellowpal.jspider.server;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import win.yellowpal.jspider.service.RedisService;

public class RedisServiceTest {
	ClassPathXmlApplicationContext context = null;
	
	@Before
	public void before(){
		String path = "applicationContext.xml";
		this.context = new ClassPathXmlApplicationContext(path);
	}
	
	@Test
	public void getKeys(){
		RedisService redisService = context.getBean(RedisService.class);
		System.out.println("==");
	}
}
