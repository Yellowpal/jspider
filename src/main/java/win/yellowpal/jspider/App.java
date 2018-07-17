package win.yellowpal.jspider;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import win.yellowpal.jspider.service.DoubanMovieService;
import win.yellowpal.jspider.service.HttpService;

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
		
		jedis.sadd("test_set", "1");
		System.out.println(jedis.sismember("test_set", "1"));
		System.out.println(DoubanMovieService.class.getSimpleName());
		
		
		HttpService httpService = content.getBean(HttpService.class);
		String url = "https://api.douban.com/v2/movie/1292052";
		
		try {
			String response = httpService.doGet(url);
			System.out.println(response);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
