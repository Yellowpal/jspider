package win.yellowpal.jspider.service;

import win.yellowpal.jspider.entity.douban.Book;

/**
 * 豆瓣读书
 * 
 * @author huangguangbin.
 * @Time 2018年7月18日
 * @Version 1.0
 */
public interface DoubanBookService {
	/**
	 * 请求解析url
	 * @param url <br> https://book.douban.com/subject/1023045/?icn=index-book250-subject
	 * @author huangguangbin, 2018年7月18日.<br>
	 */
	void parseUrl(String url);
	/**
	 * 从豆瓣接口读取数据
	 * @param id
	 * @return <br>
	 * @author huangguangbin, 2018年7月19日.<br>
	 */
	Book getFromApi(long id);
	
	/**
	 * 入口
	 *  <br>
	 * @author huangguangbin, 2018年7月19日.<br>
	 */
	void crawl();
	
	Book parseFromHtml(String html,String url);
}
