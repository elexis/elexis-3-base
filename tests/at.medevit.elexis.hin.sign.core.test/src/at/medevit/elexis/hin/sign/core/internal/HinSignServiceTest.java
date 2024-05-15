package at.medevit.elexis.hin.sign.core.internal;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import at.medevit.elexis.hin.sign.core.IHinSignService;
import ch.elexis.core.utils.OsgiServiceUtil;

public class HinSignServiceTest {

	private static HINSignService service;

	@BeforeClass
	public static void before() throws IOException {
		service = (HINSignService) OsgiServiceUtil.getService(IHinSignService.class).get();
	}

	@Test
	public void getADSwissAuthToken() {
		Optional<String> token = service.getADSwissAuthToken();
		assertTrue(token.isPresent());
	}

	@Test
	public void getEPDAuthHandle() {
		Optional<String> token = service.getADSwissAuthToken();
		assertTrue(token.isPresent());
		Optional<String> handle = service.getEPDAuthHandle(token.get());
		assertTrue(handle.isPresent());
	}
}
