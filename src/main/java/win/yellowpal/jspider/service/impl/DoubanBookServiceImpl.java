package win.yellowpal.jspider.service.impl;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import win.yellowpal.jspider.service.DoubanBookService;
import win.yellowpal.jspider.service.HttpService;

@Service
public class DoubanBookServiceImpl implements DoubanBookService{
	
	Logger logger = LoggerFactory.getLogger(DoubanBookServiceImpl.class);
	
	@Value("${douban.book.api}")
	private String doubanBookApi;

	@Autowired
	HttpService httpService;
	
	@Autowired
	JedisPool jedisPool;
	
//	Jedis jedis;
	
	final String redisKey = "doubanBook";
	
	final String separator = ":";
	
	/**
	 * 去重
	 */
	final String redisFilterKey = redisKey + separator + "filter";
	
	/**
	 * 待请求
	 */
	final String redisRequestsKey = redisKey + separator + "requests";
	
	final String bookLinkRegex = "^http(s)?://book.douban.com/subject/(\\d+)/(\\?(.*))?";
	
	final Pattern bookLinkPattern = Pattern.compile(bookLinkRegex);
	
//	@PostConstruct
//	public void init(){
//		jedis = jedisPool.getResource();
//	}
//	
//	@PreDestroy
//	public void destroy(){
//		jedis.close();
//	}
	

	@Override
	public void parseUrl(String url) {
		if(!StringUtils.isEmpty(url)){
			Jedis jedis = jedisPool.getResource();
			try {
				String response = httpService.doGetRandomUA(url,null);
				if(!StringUtils.isEmpty(response)){
					Document document = Jsoup.parse(response);
					//获取所有链接
					Elements as = document.getElementsByTag("a");
					for(Element a : as){
						String href = a.absUrl("href");
						
						Matcher matcher = bookLinkPattern.matcher(href);
						if(matcher.find()){
							String link = matcher.group();
							
							if(!jedis.sismember(redisFilterKey, link)){//不存在
								jedis.sadd(redisFilterKey, link);
								jedis.lpush(redisRequestsKey, link);
							}
						}
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("parseUrl,exception:{},url:{}",e.getMessage(),url);
			} finally {
				jedis.close();
			}
		}
		
		
	}
	
	@Override
	public JSONObject getFromApi(long id) {
		if(id <= 0){
			return null;
		}
		String url = doubanBookApi + id;
		
		try {
			String response = httpService.doGetRandomUA(url, null);
			if(!StringUtils.isEmpty(response)){
				JSONObject json = JSONObject.parseObject(response);
				return json;
			}
		} catch (IOException e) {
			logger.error("getFromApi,exception:{},id:{}",e.getMessage(),id);
		}
		return null;
	}

}
