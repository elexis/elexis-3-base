package at.medevit.elexis.hin.sign.core.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import at.medevit.elexis.hin.sign.core.IHinSignService;
import at.medevit.elexis.hin.sign.core.IHinSignService.Mode;
import ch.elexis.core.status.ObjectStatus;
import ch.elexis.core.utils.OsgiServiceUtil;

public class HinSignServiceTest {

	private static HINSignService service;

	@BeforeClass
	public static void before() throws IOException {
		service = (HINSignService) OsgiServiceUtil.getService(IHinSignService.class).get();
		service.setMode(Mode.TEST);
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
		String firstHandle = handle.get();
		handle = service.getEPDAuthHandle(token.get());
		assertEquals(firstHandle, handle.get());
	}

	@Test
	public void verifyPrescription() throws IOException {
		ObjectStatus<?> status = service
				.verifyPrescription(IOUtils.toString(getClass().getResourceAsStream("/rsc/chmed1.txt"), "UTF-8"));
		assertNotNull(status);
		assertTrue(status.isOK());
	}

	@Test
	public void createPrescription() throws IOException {
		ObjectStatus<?> status = service
				.createPrescription(IOUtils.toString(getClass().getResourceAsStream("/rsc/chmed1.txt"), "UTF-8"));
		assertNotNull(status);
		assertTrue(status.isOK());
	}

	@Test
	public void revokePrescription() throws IOException {
		ObjectStatus<?> status = service
				.revokePrescription(IOUtils.toString(getClass().getResourceAsStream("/rsc/chmed1.txt"), "UTF-8"));
		assertNotNull(status);
		assertTrue(status.isOK());
	}
}
