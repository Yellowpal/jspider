package win.yellowpal.jspider;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import win.yellowpal.jspider.entity.douban.Book;
import win.yellowpal.jspider.service.DoubanBookService;
import win.yellowpal.jspider.service.HttpService;
import win.yellowpal.jspider.service.impl.HttpServiceImpl;

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
		
		DoubanBookService doubanBookService = context.getBean(DoubanBookService.class);
		MongoTemplate mongoTemplate = context.getBean(MongoTemplate.class);
		HttpService httpService = context.getBean(HttpServiceImpl.class);
		
		ExecutorService executorService = Executors.newCachedThreadPool();
		String url = "https://book.douban.com/subject/1023045/?icn=index-book250-subject";
		
		try {
			String text = httpService.doGetRandomUA(url, null);
			Book book = doubanBookService.parseFromHtml(text, url);
			System.out.println(book);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		doubanBookService.crawl();
//		doubanBookService.parseUrl(url);
		
		
//		final String bookLinkRegex = "^http(s)?://book.douban.com/subject/(\\d+)/(\\?(.*)|$)";
//		
//		final Pattern bookLinkPattern = Pattern.compile(bookLinkRegex);
//		String href = "https://book.douban.com/1023045/reading/";
//		Matcher matcher = bookLinkPattern.matcher(href);
//		if(matcher.find()){
//			String link = matcher.group();
//			System.out.println(link);
//		}
//		
	}
}
