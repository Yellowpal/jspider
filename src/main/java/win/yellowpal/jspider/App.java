package win.yellowpal.jspider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import win.yellowpal.jspider.service.DoubanBookService;

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
		
		doubanBookService.crawl();
//		String url = "https://book.douban.com/1023045/reading/";
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
