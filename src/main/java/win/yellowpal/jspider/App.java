package win.yellowpal.jspider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Hello world!
 *
 */
public class App {
	static Logger logger = LoggerFactory.getLogger(App.class);
	public static void main(String[] args) {
		String path = "applicationContext.xml";
		ClassPathXmlApplicationContext content = new ClassPathXmlApplicationContext(path);
		JedisPool jedisPool = content.getBean(JedisPool.class);
		Jedis jedis = jedisPool.getResource();
		
		System.out.println(jedis.keys("movie*"));
		logger.info("hahahah {}",456);
	}
}
