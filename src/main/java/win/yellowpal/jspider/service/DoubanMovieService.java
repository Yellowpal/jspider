package win.yellowpal.jspider.service;

import com.alibaba.fastjson.JSONObject;

/**
 * 豆瓣电影
 * @author Yellow.
 * @Time 2018年7月18日
 * @Version 1.0
 */
public interface DoubanMovieService {
	/**
	 * 请求解析url
	 * @param url <br>
	 * @author Yellow, 2018年7月18日.<br>
	 */
	void parseUrl(String url);
	
	/**
	 * 从豆瓣接口读取数据
	 * @param id
	 * @return <br>
	 * @author huangguangbin, 2018年7月19日.<br>
	 */
	JSONObject getFromApi(long id);
}
