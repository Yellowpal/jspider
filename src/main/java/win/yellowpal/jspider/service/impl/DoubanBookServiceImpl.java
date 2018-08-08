package win.yellowpal.jspider.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import redis.clients.jedis.Jedis;
import win.yellowpal.jspider.entity.douban.Book;
import win.yellowpal.jspider.service.DoubanBookService;
import win.yellowpal.jspider.service.HttpService;
import win.yellowpal.jspider.service.RedisService;
import win.yellowpal.jspider.util.NumberUtils;

@Service
public class DoubanBookServiceImpl implements DoubanBookService{
	
	Logger logger = LoggerFactory.getLogger(DoubanBookServiceImpl.class);
	
	@Value("${douban.book.api}")
	private String doubanBookApi;

	@Autowired
	HttpService httpService;
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
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
	
	final String bookLinkRegex = "^http(s)?://book.douban.com/subject/(\\d+)/(\\?(.*)|$)";
	
	final Pattern bookLinkPattern = Pattern.compile(bookLinkRegex);
	
	@Override
	public void parseUrl(String url) {
		
		logger.info("parseUrl:{}",url);
		
		if(!StringUtils.isEmpty(url)){
			Jedis jedis = redisService.getJedis();
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
				}else{
					jedis.lpush(redisRequestsKey, url);
				}
				long id = NumberUtils.longValue(url);
				Book book = getFromApi(id);
				if(book != null){
					Query query = new Query();
					
					query.addCriteria(Criteria.where("id").is(id+""));
					org.bson.Document document = org.bson.Document.parse(book.toString());
					Update update = Update.fromDocument(document);
					mongoTemplate.upsert(query, update, Book.class);
					
//					logger.info("upsert,getModifiedCount:{},getMatchedCount:{},getUpsertedId:{}",result.getModifiedCount(),result.getMatchedCount(),result.getUpsertedId());
					
				}else{
					jedis.lpush(redisRequestsKey, url);
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
	public Book getFromApi(long id) {
		if(id <= 0){
			return null;
		}
		String url = doubanBookApi + id;
		
		try {
			String response = httpService.doGetRandomUA(url, null);
			if(!StringUtils.isEmpty(response)){
				JSONObject json = JSONObject.parseObject(response);
				Book book = new Book();
				book.putAll(json);
				return book;
			}else{
				logger.warn("getFromApi request return null! url:{}",url);
			}
		} catch (IOException e) {
			logger.error("getFromApi,exception:{},id:{}",e.getMessage(),id);
		}
		return null;
	}

	@Override
	public void crawl() {
		ExecutorService executorService = Executors.newCachedThreadPool();
		
		try {
			for(int i=0;i<4;i++){
				
				executorService.execute(new Runnable() {
					
					@Override
					public void run() {
						Jedis jedis = redisService.getJedis();
						
						try {
							while(jedis.llen(redisRequestsKey) > 0){
								String url = jedis.lpop(redisRequestsKey);
								if(!StringUtils.isEmpty(url)){
									parseUrl(url);
								}
								Thread.sleep(10000);//常规停10s
								if(jedis.llen(redisRequestsKey) <= 0){//等待10s
									Thread.sleep(10000);
								}
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						}finally {
							jedis.close();
						}
						
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Book parseFromHtml(String html, String url) {
		if(StringUtils.isEmpty(html)){
			return null;
		}
		long id = NumberUtils.longValue(url);
		Document document = Jsoup.parse(html);
		Book book = new Book();
		book.put("id", id);
		//title
		Element element = document.selectFirst("#wrapper h1 span[property='v:itemreviewed']");
		if(element != null){
			book.put("title", element.ownText().trim());
		}
		//numRaters
		element = document.selectFirst("span[property='v:votes']");
		if(element != null){
			book.put("numRaters", element.ownText().trim());
		}
		//average
		element = document.selectFirst(".rating_num[property='v:average']");
		if(element != null){
			book.put("average", element.ownText().trim());
		}
		//images
		element = document.selectFirst("#content a.nbg");
		if(element != null){
			String link = element.attr("href");
			JSONObject json = new JSONObject();
			json.put("large", link);
			
			book.put("images", json);
			
			//image
			book.put("image", link);
		}
		
		//info
		element = document.selectFirst("#content #info");
		Elements authorElements = null;
		if(element != null){
			List<TextNode> list = element.textNodes();
			List<Element> nodes = element.select("span.pl");
			List<String> values = new ArrayList<>();
			List<String> keys = new ArrayList<>();
			for(Element node : nodes){
				String key = node.ownText().trim().replaceAll(":", "");
				if(authorElements == null){
					authorElements = new Elements();
					Element a = node.nextElementSibling();
					while(a.tagName() == "a"){
						authorElements.add(a);
						a = a.nextElementSibling();
					}
				}
				
				keys.add(key);
			}
			if(authorElements != null && authorElements.size() > 0){
				String[] authors = new String[authorElements.size()];
				int i = 0;
				for(Element author : authorElements){
					authors[i] = author.ownText();
					i++;
				}
				
				values.add(JSON.toJSONString(authors));
			}
			
			for(TextNode textNode : list){
				String text = textNode.text().trim().replaceAll("\\s", "");
				if(StringUtils.isEmpty(text)){
					continue;
				}
				values.add(text);
			}
			
			JSONObject info = new JSONObject();
			if(keys.size() == values.size()){
				for(int i=0;i<keys.size();i++){
					info.put(keys.get(i), values.get(i));
				}
			}
			
			book.put("info", info);
		}
		
		//summary & author_intro
		Elements elements = document.select(".related_info .indent .intro");
		if(elements != null && elements.size() == 2){
			book.put("summary", elements.get(0).text());
			book.put("author_intro", elements.get(1).text());
		}
		
		return book;
	}

}
