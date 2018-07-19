package win.yellowpal.jspider.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数字工具类
 * 
 * @author huangguangbin.
 * @Time 2018年7月18日
 * @Version 1.0
 */
public class NumberUtils {

	final static String DIGIT_REGEX = "\\d+";
	
	final static Pattern DIGIT_PATTERN = Pattern.compile(DIGIT_REGEX);
	
	/**
	 * 提取字符串中的第一个数字
	 * @param text
	 * @return <br>
	 * @author huangguangbin, 2018年7月18日.<br>
	 */
	public static long longValue(String text){
		if(text == null || text.trim().length() <= 0){
			return 0;
		}
		
		Matcher matcher = DIGIT_PATTERN.matcher(text);
		
		long digit = 0;
		
		if(matcher.find()){
			digit = Long.parseLong(matcher.group());
		}
		
		return digit;
	}
	
	public static void main(String[] args) {
		String url = "https://book.douban.com/subject/1023045/?icn=index-book250-subject";
		System.out.println(longValue(url));
	}
}
