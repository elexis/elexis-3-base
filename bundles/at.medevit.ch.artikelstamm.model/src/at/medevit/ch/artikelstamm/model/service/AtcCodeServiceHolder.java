package at.medevit.ch.artikelstamm.model.service;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import at.medevit.atc_codes.ATCCodeService;

@Component
public class AtcCodeServiceHolder {
	private static ATCCodeService atcCodeService = null;

	@Reference
	public void setATCCodeService(ATCCodeService consumer) {
		AtcCodeServiceHolder.atcCodeService = consumer;
	}

	public static Optional<ATCCodeService> get() {
		return Optional.ofNullable(atcCodeService);
	}
}
