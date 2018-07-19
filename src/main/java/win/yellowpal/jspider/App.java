package win.yellowpal.jspider;

import java.io.IOException;

import org.bson.BSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;

import win.yellowpal.jspider.service.DoubanBookService;
import win.yellowpal.jspider.service.HttpService;

/**
 * Hello world!
 *
 */
public class App {
	static Logger logger = LoggerFactory.getLogger(App.class);
	/**
	 * @param args <br>
	 * @author huangguangbin, 2018年7月18日.<br>
	 */
	public static void main(String[] args) {
		String path = "applicationContext.xml";
		@SuppressWarnings("resource")
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(path);
		
//		HttpService httpService = context.getBean(HttpService.class);
		DoubanBookService doubanBookService = context.getBean(DoubanBookService.class);
		MongoTemplate mongoTemplate = context.getBean(MongoTemplate.class);
//		String url = "https://book.douban.com/subject/1023045/?icn=index-book250-subject";
		
//		doubanBookService.parseUrl(url);
		
//		JSONObject json = new JSONObject();
//		json.put("code", 1);
//		json.put("id", 1);
//		json.put("data", "阿道夫adf!");
		
//		mongoTemplate.insert(json, "test");
		
//		Criteria criteria = new Criteria();
//		criteria.andOperator(Criteria.where("code").is(1));
//		Query query = new Query(criteria);
//		
//		System.out.println(mongoTemplate.findOne(query, JSONObject.class,"test").get("_id"));
//		System.out.println(mongoTemplate.getCollectionNames());
//		System.out.println(mongoTemplate.find(query, JSONObject.class, "test"));
		
		System.out.println(doubanBookService.getFromApi(1023045L));
	}
}
