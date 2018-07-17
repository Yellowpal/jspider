package win.yellowpal.jspider.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

public interface HttpService {

	String doPost(String url, Map<String, String> parameters, String charset) throws IOException;

	String doPost(String url, List<NameValuePair> parameters, String charset) throws IOException;

	String doGet(String url, List<NameValuePair> parameters, String charset) throws IOException;

	String doGet(String url, Map<String, String> parameters, String charset) throws IOException;

	String doGet(String url, String charset) throws IOException;
	
	String doGet(String url) throws IOException;

}
