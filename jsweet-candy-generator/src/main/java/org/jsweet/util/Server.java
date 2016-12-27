package org.jsweet.util;

import java.net.ProxySelector;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class Server {

	private static final Logger logger = Logger.getLogger(Server.class);
	
	private final String baseUrl;

	private final Map<String, String> permanentHeaders = new HashMap<>();
	private final CookieStore cookieStore = new BasicCookieStore();

	public Server(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String get(String uri) {
		return request(new HttpGet(baseUrl + uri));
	}

	public String post(String uri) {
		return request(new HttpPost(baseUrl + uri));
	}


	public void addPermanentHeader(String name, String value) {
		permanentHeaders.put(name, value);
	}

	public String request(HttpRequestBase requestObject) {

		try {

			for (Map.Entry<String, String> headerEntry : permanentHeaders.entrySet()) {
				requestObject.addHeader(headerEntry.getKey(), headerEntry.getValue());
			}

			logger.info("request: " + requestObject);
			try (CloseableHttpClient httpClient = createHttpClient()) {

				HttpResponse response = httpClient.execute(requestObject);

				logger.info("status: " + response.getStatusLine());
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != 200) {
					throw new RuntimeException("error in http - line: " + response.getStatusLine());
				}

				return EntityUtils.toString(response.getEntity());
			}

		} catch (Throwable t) {
			throw new RuntimeException(t.getMessage(), t);
		}
	}

	private CloseableHttpClient createHttpClient() {
		logger.debug("create http client");
		return HttpClientBuilder.create() //
				.evictExpiredConnections().setDefaultCookieStore(cookieStore) //
				.setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault())) //
				.setDefaultRequestConfig(RequestConfig.custom() //
						.setSocketTimeout(0) //
						.build()) //
				.build();
	}
}
