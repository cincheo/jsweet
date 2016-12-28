package org.jsweet.util;

import java.net.ProxySelector;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class Server {

	private static final Logger logger = Logger.getLogger(Server.class);

	private final String baseUrl;
	private final HttpHost host;

	private final Map<String, String> permanentHeaders = new HashMap<>();
	private final CookieStore cookieStore = new BasicCookieStore();

	private UsernamePasswordCredentials credentials;

	public Server(String protocol, String hostname, int port) {
		this.host = new HttpHost(hostname, port, protocol);
		this.baseUrl = protocol + "://" + this.host.toHostString();
	}

	public String get(String uri) {
		return request(new HttpGet(baseUrl + uri));
	}

	public String post(String uri, String... parameterPairs) {
		HttpPost post = new HttpPost(uri);

		// builds json params
		StringBuilder paramsBuilder = new StringBuilder("{");
		for (int i = 0; i < parameterPairs.length; i += 2) {
			if (i != 0) {
				paramsBuilder.append(",");
			}
			paramsBuilder.append(String.format("\"%s\": \"%s\"", parameterPairs[i], parameterPairs[i + 1]));
		}
		paramsBuilder.append("}");

		HttpEntity entity = new StringEntity(paramsBuilder.toString(), ContentType.APPLICATION_JSON);
		post.setEntity(entity);

		return request(post);
	}

	public Server addPermanentHeader(String name, String value) {
		permanentHeaders.put(name, value);
		return this;
	}

	public Server setCredentials(String username, String password) {
		this.credentials = new UsernamePasswordCredentials(username, password);
		return this;
	}

	public String request(HttpRequestBase requestObject) {

		try {

			for (Map.Entry<String, String> headerEntry : permanentHeaders.entrySet()) {
				requestObject.addHeader(headerEntry.getKey(), headerEntry.getValue());
			}

			logger.info("request: " + requestObject);
			try (CloseableHttpClient httpClient = createHttpClient()) {

				CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
				credentialsProvider.setCredentials(new AuthScope(host.getHostName(), AuthScope.ANY_PORT), credentials);

				AuthCache authCache = new BasicAuthCache();
				// Generate BASIC scheme object and add it to the local auth
				// cache
				BasicScheme basicAuth = new BasicScheme();
				authCache.put(host, basicAuth);

				HttpClientContext context = HttpClientContext.create();
				context.setCredentialsProvider(credentialsProvider);
				context.setAuthCache(authCache);

				try (CloseableHttpResponse response = httpClient.execute(host, requestObject, context)) {

					logger.info("status: " + response.getStatusLine());
					int statusCode = response.getStatusLine().getStatusCode();
					if (statusCode < 200 || statusCode > 226) {
						throw new RuntimeException("http error response: " + response.getStatusLine());
					}

					return EntityUtils.toString(response.getEntity());
				}
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
