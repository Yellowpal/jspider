package win.yellowpal.jspider.service;

import redis.clients.jedis.Jedis;

public interface RedisService {
	Jedis getJedis();
}
