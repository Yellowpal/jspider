package win.yellowpal.jspider.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class UserAgentUtils {
	static List<String> USER_AGENT_LIST = new ArrayList<String>();
	
	static{
		USER_AGENT_LIST.add("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36");
		USER_AGENT_LIST.add("Googlebot/2.X (+http://www.googlebot.com/bot.html)");
		USER_AGENT_LIST.add("Baiduspider");
	}
	
	public static String randomUA(){
		Random random = new Random();
		int index = random.nextInt(USER_AGENT_LIST.size());
		return USER_AGENT_LIST.get(index);
	}
}
