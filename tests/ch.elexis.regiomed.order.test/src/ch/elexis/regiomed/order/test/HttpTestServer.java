package ch.elexis.regiomed.order.test;

import static javax.servlet.http.HttpServletResponse.SC_OK;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.Content;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.Callback;

public class HttpTestServer {
	public static final int HTTP_PORT = 50036;

	private Server server;
	private String orderResponseBody = "{}";
	private String tokenResponseBody = "{\"data\": {\"Token\": \"MOCK_TOKEN_XYZ_123\"}}";
	private String requestBody;

	private volatile String authHeader;

	private String mockResponseType = "application/json; charset=UTF-8";

	public HttpTestServer() {
	}

	public void start() throws Exception {
		server = new Server(HTTP_PORT);
		server.setHandler(getMockHandler());
		server.start();
	}

	public void stop() throws Exception {
		if (server != null) {
			server.stop();
			server.join();
			server = null;
		}
	}

	private Handler getMockHandler() {
		return new Handler.Abstract() {
			@Override
			public boolean handle(Request request, Response response, Callback callback) throws Exception {
				String body = IOUtils.toString(Request.asInputStream(request), StandardCharsets.UTF_8);
				setRequestBody(body);
				authHeader = request.getHeaders().get(HttpHeader.AUTHORIZATION);
				String bodyToSend;
				if (body.contains("B64Password")) {
					bodyToSend = tokenResponseBody;
				} else {
					bodyToSend = orderResponseBody;
				}
				response.setStatus(SC_OK);
				response.getHeaders().put(HttpHeader.CONTENT_TYPE, mockResponseType);
				Content.Sink.write(response, true, bodyToSend, callback);
				return true;
			}
		};
	}

	public void setResponseBody(String responseBody) {
		this.orderResponseBody = responseBody;
	}

	public String getResponseBody() {
		return orderResponseBody;
	}

	public String getRequestBody() {
		return requestBody;
	}

	private void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public String getAuthHeader() {
		return authHeader;
	}
}