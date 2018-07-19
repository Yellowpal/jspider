package win.yellowpal.jspider.entity.douban;

import org.springframework.data.mongodb.core.mapping.Document;

import com.alibaba.fastjson.JSONObject;

@Document(collection="book")
public class Book extends JSONObject{

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -4005147431552146137L;

}
