package win.yellowpal.jspider.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

import win.yellowpal.jspider.service.HttpService;
import win.yellowpal.jspider.util.UserAgentUtils;

@Service
public class HttpServiceImpl implements HttpService{
	Logger logger = LoggerFactory.getLogger(HttpServiceImpl.class);

    @Value("${http.timeout}")
    private int timeout;

    @Value("${http.defaultCharset}")
    private String defaultCharset;

    @Value("${http.connection.maxTotal}")
    private int maxTotal;

    @Value("${http.connection.defaultMaxPerRoute}")
    private int defaultMaxPerRoute;

    private HttpClient httpClient;

    @PostConstruct
    public void init() {
        PoolingHttpClientConnectionManager connectionManager
                = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(maxTotal);
        connectionManager.setDefaultMaxPerRoute(defaultMaxPerRoute);
        httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager).build();
    }
    
    @Override
    public String doPost(String url, List<NameValuePair> parameters, String charset)
            throws IOException {
        charset = getCharset(charset);
        HttpPost postMethod = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).setRedirectsEnabled(false).build();
        postMethod.setConfig(requestConfig);
        if (parameters != null && parameters.size() > 0) {
            UrlEncodedFormEntity data = new UrlEncodedFormEntity(parameters,
                    charset);
            postMethod.setEntity(data);
        }
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "[HttpClientTemplate#doPost] request to url={}, param={}",
                    url, JSON.toJSON(parameters));
        }
        String result = requestAndParse(postMethod, charset);
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "[HttpClientTemplate#doPost] response from url={}, param={}, data={}",
                    url, JSON.toJSON(parameters), result);
        }
        return result;
    }

    @Override
    public String doGet(String url, List<NameValuePair> parameters, String charset)
            throws IOException {
        charset = getCharset(charset);
        String queryString = null;
        if (parameters != null && parameters.size() > 0) {
            queryString = URLEncodedUtils.format(parameters, charset);
        }
        String requestUrl = url;
        if (queryString != null) {
            if (!url.contains("?"))
                requestUrl += "?" + queryString;
            else
                requestUrl += "&" + queryString;
        }
        HttpGet getMethod = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).setRedirectsEnabled(false).build();
        getMethod.setConfig(requestConfig);
        if (logger.isDebugEnabled()) {
            logger.debug("[HttpClientTemplate#doGet] request to url={}",
                    requestUrl);
        }
        String result = requestAndParse(getMethod, charset);
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "[HttpClientTemplate#doGet] response from url={}, param={}, data={}",
                    url, JSON.toJSON(parameters), result);
        }
        return result;
    }

    private String getCharset(String charset) {
        if (StringUtils.isEmpty(charset)) {
            return defaultCharset;
        }
        return charset;
    }

    private String requestAndParse(HttpUriRequest httpRequest, String charset)
            throws IOException {
        HttpResponse httpResponse = httpClient.execute(httpRequest);
        StatusLine statusLine = httpResponse.getStatusLine();
        if (null == statusLine) {
            throw new IOException("status not specified");
        }
        int statusCode = statusLine.getStatusCode();
        if (statusCode < 200 || statusCode > 299) {
            EntityUtils.consumeQuietly(httpResponse.getEntity());
            throw new IOException("status code: " + statusCode);
        }
        HttpEntity entity = httpResponse.getEntity();
        if (null == entity) {
            return null;
        }
        return EntityUtils.toString(entity, charset);
    }

	@Override
	public String doPost(String url, Map<String, String> parameters, String charset) throws IOException {
		// 组装参数
    	List<NameValuePair> params = new ArrayList<>();
    	if(parameters != null){
    		for(Entry<String, String> entry : parameters.entrySet()){
    			params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
    		}
    	}
    	
    	String result = doPost(url, params, "utf-8");
    	
    	return result;
	}

	@Override
	public String doGet(String url, Map<String, String> parameters, String charset) throws IOException {
		// 组装参数
    	List<NameValuePair> params = new ArrayList<>();
    	if(parameters != null){
    		for(Entry<String, String> entry : parameters.entrySet()){
    			params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
    		}
    	}
    	
    	String result = doGet(url, params, "utf-8");
    	
    	return result;
	}

	@Override
	public String doGet(String url, String charset, Header[] headers) throws IOException {
		charset = getCharset(charset);
        String requestUrl = url;
        HttpGet getMethod = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom().setMaxRedirects(0)
                .setConnectTimeout(timeout).setConnectionRequestTimeout(timeout)
                .setSocketTimeout(timeout).setRedirectsEnabled(false).build();
        getMethod.setConfig(requestConfig);
        
        if(headers != null){
        	getMethod.setHeaders(headers);
        }
        
        if (logger.isDebugEnabled()) {
            logger.debug("[HttpClientTemplate#doGet] request to url={}",
                    requestUrl);
        }
        String result = requestAndParse(getMethod, charset);
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "[HttpClientTemplate#doGet] response from url={}, data={}",
                    url, result);
        }
        return result;
	}

	@Override
	public String doGetRandomUA(String url, String charset) throws IOException {
		
		List<Header> list = new ArrayList<>();
		Header ua = new BasicHeader("User-Agent", UserAgentUtils.randomUA());
		list.add(ua);
		
		Header[] headers = list.toArray(new BasicHeader[list.size()]); 
		
		return doGet(url,charset,headers);
	}

}
