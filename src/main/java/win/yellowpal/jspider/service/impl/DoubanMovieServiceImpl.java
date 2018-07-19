package win.yellowpal.jspider.service.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;

import win.yellowpal.jspider.service.DoubanMovieService;
import win.yellowpal.jspider.service.HttpService;

/**
 * 
 * @author Yellow.
 * @Time 2018年7月18日
 * @Version 1.0
 */
@Service
public class DoubanMovieServiceImpl implements DoubanMovieService{
	
	private Logger logger = LoggerFactory.getLogger(DoubanMovieServiceImpl.class);
	
	@Value("${douban.book.api}")
	private String doubanBookApi;

	@Autowired
	HttpService httpService;
	
	@Override
	public void parseUrl(String url) {
		if(!StringUtils.isEmpty(url)){
			try {
				String response = httpService.doGetRandomUA(url,null);
				if(!StringUtils.isEmpty(response)){
					
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				logger.error("parseUrl,exception:{},url:{}",e.getMessage(),url);
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
