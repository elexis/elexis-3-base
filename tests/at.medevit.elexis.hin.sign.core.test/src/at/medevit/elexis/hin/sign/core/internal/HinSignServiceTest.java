package at.medevit.elexis.hin.sign.core.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import at.medevit.elexis.hin.sign.core.IHinSignService;
import at.medevit.elexis.hin.sign.core.IHinSignService.Mode;
import at.medevit.elexis.hin.sign.core.test.AllPluginTests;
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
	public void createAndVerifyPrescription() throws IOException {
		String chmed = IOUtils.toString(getClass().getResourceAsStream("/rsc/chmed2.txt"), "UTF-8");
		Map<String, String> chmedWithId = AllPluginTests.getChmedWithNewId(chmed);
		ObjectStatus<?> status = service.createPrescription(chmedWithId.get("chmed"));
		assertTrue(status.get() instanceof String);
		ObjectStatus<?> verifyStatus = service.verifyPrescription((String) status.get());
		assertTrue(verifyStatus.isOK());
	}

	@Test
	public void revokePrescription() throws IOException, InterruptedException {
		String chmed = IOUtils.toString(getClass().getResourceAsStream("/rsc/chmed2.txt"), "UTF-8");
		Map<String, String> chmedWithId = AllPluginTests.getChmedWithNewId(chmed);
		ObjectStatus<?> status = service.createPrescription(chmedWithId.get("chmed"));
		assertTrue(status.get() instanceof String);

		ObjectStatus<?> verifyStatus = service.verifyPrescription((String) status.get());
		assertTrue(verifyStatus.isOK());
		assertEquals(Boolean.FALSE, ((Map) verifyStatus.get()).get("revoked"));

		ObjectStatus<?> revokeStatus = service.revokePrescription(service.getChmedId(chmedWithId.get("chmed")).get());
		assertTrue(revokeStatus.isOK());

		verifyStatus = service.verifyPrescription((String) status.get());
		assertTrue(verifyStatus.isOK());
		assertEquals(Boolean.TRUE, ((Map) verifyStatus.get()).get("revoked"));
	}
}
