package win.yellowpal.jspider.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.JedisPool;
import win.yellowpal.jspider.service.RedisService;

@Service
public class RedisServiceImpl implements RedisService{
	
	@Autowired
	JedisPool jedisPool;
	
}
