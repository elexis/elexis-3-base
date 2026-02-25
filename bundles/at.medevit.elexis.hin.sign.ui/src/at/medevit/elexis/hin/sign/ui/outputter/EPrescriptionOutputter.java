package at.medevit.elexis.hin.sign.ui.outputter;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.hin.sign.core.IHinSignService;
import ch.elexis.core.data.interfaces.IOutputter;
import ch.elexis.core.model.IRecipe;
import ch.elexis.core.rcp.utils.OsgiServiceUtil;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.status.ObjectStatus;
import ch.elexis.data.Rezept;

public class EPrescriptionOutputter implements IOutputter {

	private static final String PLUGIN_ID = "at.medevit.elexis.hin.sign.ui";

	private IHinSignService hinSignService;

	@Override
	public String getOutputterID() {
		return "at.medevit.elexis.hin.sign.ui.eprescription.outputter";
	}

	@Override
	public String getOutputterDescription() {
		return "HIN eRezept";
	}

	@Override
	public Object getSymbol() {
		return loadImage("/rsc/hin_erezept_16x16.png");
	}

	public static Image loadImage(String path) {
		ImageRegistry imageRegistry = JFaceResources.getImageRegistry();
		Image image = imageRegistry.get(PLUGIN_ID + path);
		if (image == null) {
			Bundle bundle = Platform.getBundle(PLUGIN_ID);
			URL url = FileLocator.find(bundle, new Path(path), null);
			ImageDescriptor imageDesc = ImageDescriptor.createFromURL(url);
			image = imageDesc.createImage();
			imageRegistry.put(PLUGIN_ID + path, image);
		}
		return image;
	}

	@Override
	public Optional<String> getInfo(Object outputted) {
		if(outputted instanceof Rezept) {
			outputted = CoreModelServiceHolder.get().load(((Rezept) outputted).getId(), IRecipe.class).orElse(null);
		}
		if (outputted instanceof IRecipe) {
			String chmedUrl = getHinSignService().getPrescriptionUrl((IRecipe) outputted).orElse(null);
			if (chmedUrl != null) {
				ObjectStatus<?> verification = getHinSignService().verifyPrescription(chmedUrl);
				if (verification.isOK()) {
					Map<?, ?> verificationMap = (Map<?, ?>) verification.getObject();
					if ((Boolean) verificationMap.get("valid")) {
						LocalDateTime issuedAt = getAsLocalDateTime((String) verificationMap.get("issued_at"));
						StringBuilder sb = new StringBuilder();
						sb.append("HIN eRezept");
						if(issuedAt != null) {
							sb.append(" ausgestellt am " + issuedAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
						}
						sb.append("\nvon " + verificationMap.get("issued_by"));
						Map<?, ?> dispenseEvent = getDispenseEvent(verificationMap);
						if (dispenseEvent != null) {
							LocalDateTime dispensedAt = getAsLocalDateTime((String) dispenseEvent.get("timestamp"));
							if (dispensedAt != null) {
								sb.append("\nabgegeben am "
										+ dispensedAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
							}
							sb.append("\nvon " + dispenseEvent.get("actor_name"));
						} else {
							sb.append("\nnoch nicht abgegeben.");
						}
						return Optional.of(sb.toString());
					} else {
						return Optional.of("HIN eRezept ist nicht valide.");
					}
				} else {
					return Optional.of("HIN eRezept konnte nicht verifiziert werden.");
				}
			} else {
				return Optional.of("Kein HIN eRezept gefunden.");
			}
		}
		return Optional.empty();
	}

	private LocalDateTime getAsLocalDateTime(String string) {
		try {
			if (string.length() > 19) {
				string = string.substring(0, 19);
			}
			return LocalDateTime.parse(string, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
		} catch (DateTimeParseException e) {
			LoggerFactory.getLogger(getClass()).error("Error parsing local date", e);
		}
		return null;
	}

	private Map<?, ?> getDispenseEvent(Map<?, ?> verificationMap) {
		List<?> events = (List<?>) verificationMap.get("events");
		for (Object object : events) {
			if (object instanceof Map) {
				Map<?, ?> eventMap = (Map<?, ?>) object;
				if ("full_dispense".equals(eventMap.get("type")) || "partial_dispense".equals(eventMap.get("type"))) {
					return (Map<?, ?>) object;
				}
			}
		}
		return null;
	}

	private IHinSignService getHinSignService() {
		if (hinSignService == null) {
			hinSignService = OsgiServiceUtil.getService(IHinSignService.class).orElse(null);
		}
		return hinSignService;
	}
}
