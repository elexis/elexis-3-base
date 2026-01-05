package ch.elexis.regiomed.order.test;

import static javax.servlet.http.HttpServletResponse.SC_OK;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

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
		Handler handler = new AbstractHandler() {

			@Override
			public void handle(String target, Request request, HttpServletRequest servletRequest,
					HttpServletResponse response) throws IOException, ServletException {
				String body = IOUtils.toString(request.getInputStream(), Charset.defaultCharset());
				setRequestBody(body);
				authHeader = request.getHeader(HttpHeader.AUTHORIZATION.asString());
				String bodyToSend;
				if (body.contains("B64Password")) {
					bodyToSend = tokenResponseBody;
				} else {
					bodyToSend = orderResponseBody;
				}
				response.setStatus(SC_OK);
				response.addHeader(HttpHeader.CONTENT_TYPE.asString(), mockResponseType);

				response.setStatus(SC_OK);
				response.setContentType(mockResponseType != null ? mockResponseType : "text/xml;charset=utf-8");
				IOUtils.write(bodyToSend, response.getOutputStream(), Charset.defaultCharset());
				request.setHandled(true);
			}
		};
		return handler;
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