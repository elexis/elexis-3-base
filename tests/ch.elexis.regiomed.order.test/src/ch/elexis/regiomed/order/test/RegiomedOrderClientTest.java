package ch.elexis.regiomed.order.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ch.elexis.regiomed.order.client.RegiomedOrderClient;
import ch.elexis.regiomed.order.config.RegiomedConfig;
import ch.elexis.regiomed.order.model.RegiomedOrderRequest;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse;

public class RegiomedOrderClientTest {

	private HttpTestServer server;
	private RegiomedOrderClient client;

	@Before
	public void setUp() throws Exception {
		server = new HttpTestServer();
		server.start();
		client = new RegiomedOrderClient();
	}

	@After
	public void tearDown() throws Exception {
		if (server != null) {
			try {
				server.stop();
				Thread.sleep(50);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testSendOrderSuccess() throws Exception {
		String hybridMockResponse = "{"
				+ "\"data\": { \"tokenRaw\": \"MOCK_TOKEN_XYZ_123\", \"token\": \"MOCK_TOKEN_XYZ_123\" },"
				+ "\"checkSuccess\": true," + "\"orderSent\": true," + "\"articlesOK\": 1," + "\"articlesNOK\": 0,"
				+ "\"message\": \"Order processed successfully\"," + "\"articles\": ["
				+ "  {\"pharmaCode\": 12345, \"success\": true, \"info\": \"Available\"}" + "]" + "}";

		server.setResponseBody(hybridMockResponse);
		String serverUrl = "http://localhost:" + HttpTestServer.HTTP_PORT;
		RegiomedConfig testConfig = new RegiomedConfig(serverUrl, "test-client-id", "test@praxis.ch", "secretPassword",
				true, false, serverUrl);

		RegiomedOrderRequest request = new RegiomedOrderRequest();
		RegiomedOrderResponse response = client.sendOrderWithToken(testConfig, request);

		assertNotNull("Response sollte nicht null sein", response);
		assertTrue("Order sollte als sent markiert sein", response.isOrderSent());
		assertEquals(1, response.getArticlesOK());
		String authHeader = server.getAuthHeader();
		assertNotNull("Authorization Header fehlt (war null)!", authHeader);
		assertTrue("Header '" + authHeader + "' enthält den Token 'MOCK_TOKEN_XYZ_123' nicht!",
				authHeader.contains("MOCK_TOKEN_XYZ_123"));
	}
}