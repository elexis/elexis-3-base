package ch.elexis.mednet.webapi.core.fhir.resources.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.hl7.fhir.r4.model.*;

import java.util.Map;

public class AdjustBundleIdentifiers {

	private static final Gson gson = new Gson();

	public static String adjustBundleJsonString(String bundleJsonString) {
		JsonObject bundleJson = JsonParser.parseString(bundleJsonString).getAsJsonObject();
		adjustJsonObject(bundleJson);
		return gson.toJson(bundleJson);
	}

	private static void adjustJsonObject(JsonObject jsonObject) {
		for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String key = entry.getKey();
			JsonElement value = entry.getValue();

			if (value.isJsonObject()) {
				adjustJsonObject(value.getAsJsonObject());
			} else if (value.isJsonArray()) {
				adjustJsonArray(value.getAsJsonArray());
			} else if (value.isJsonPrimitive() && value.getAsString().startsWith("www.")) {
				jsonObject.addProperty(key, "https://" + value.getAsString());
			} else if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
				String adjustedValue = value.getAsString().replaceAll("\\s+$", "");
				jsonObject.addProperty(key, adjustedValue);
			}
		}
	}

	private static void adjustJsonArray(JsonArray jsonArray) {
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonElement element = jsonArray.get(i);
			if (element.isJsonObject()) {
				adjustJsonObject(element.getAsJsonObject());
			} else if (element.isJsonArray()) {
				adjustJsonArray(element.getAsJsonArray());
			} else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
				// Remove trailing whitespace from strings
				String adjustedValue = element.getAsString().replaceAll("\\s+$", "");
				jsonArray.set(i, new JsonPrimitive(adjustedValue));
			}
		}
	}


	public static void adjustBundleIdentifiers(Bundle bundle) {
		for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
			Resource resource = entry.getResource();

			if (resource instanceof Patient) {
				adjustPatientIdentifiers((Patient) resource);
			} else if (resource instanceof Organization) {
				adjustOrganizationIdentifiers((Organization) resource);
			} else if (resource instanceof Coverage) {
				adjustCoverageIdentifiers((Coverage) resource);
			}
			else if (resource instanceof Medication) {
				removeMedicationExtensions((Medication) resource);
			}
		}
	}

	private static void removeMedicationExtensions(Medication medication) {
		if (medication.hasExtension()) {
			medication.setExtension(null);
		}
	}

	private static void adjustPatientIdentifiers(Patient patient) {
		for (Identifier identifier : patient.getIdentifier()) {
			if (identifier.getSystem() != null && identifier.getSystem().startsWith("www.")) {
				identifier.setSystem("https://" + identifier.getSystem());
			}

			if ("https://www.ahv.ch/xid".equals(identifier.getSystem())) {
				identifier.setSystem("urn:oid:2.16.756.5.32");
			}
			if ("https://www.elexis.info/patnr".equals(identifier.getSystem())) {
				CodeableConcept type = new CodeableConcept();
				type.addCoding(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v2-0203").setCode("MR")
						.setDisplay("Medical record number"));
				identifier.setType(type);
			}
			if ("https://www.xid.ch/id/ean".equals(identifier.getSystem())) {
				identifier.setSystem("urn:oid:2.51.1.3");
			}
		}
		if (patient.hasExtension()) {
			patient.setExtension(null);
		}

		if (patient.hasMaritalStatus()) {
			CodeableConcept maritalStatus = patient.getMaritalStatus();
			if (maritalStatus.hasCoding()) {
				for (Coding coding : maritalStatus.getCoding()) {
					if (coding.getSystem() == null || coding.getSystem().isEmpty()) {
						coding.setSystem("http://terminology.hl7.org/CodeSystem/v3-MaritalStatus");
					}
				}
			}
		}
	}

	private static void adjustOrganizationIdentifiers(Organization organization) {
		for (Identifier identifier : organization.getIdentifier()) {
			if (identifier.getSystem() != null && identifier.getSystem().startsWith("www.")) {
				identifier.setSystem("https://" + identifier.getSystem());
			}

			if ("https://www.xid.ch/id/ean".equals(identifier.getSystem())) {
				identifier.setSystem("urn:oid:2.51.1.3");
			}
		}

		if (organization.hasAddress()) {
			for (Address address : organization.getAddress()) {
				if ("home".equalsIgnoreCase(address.getUse().toString())) {
					address.setUse(Address.AddressUse.WORK);
				}

			}
		}
	}


	private static void adjustCoverageIdentifiers(Coverage coverage) {
		String newValue = null;

		for (Identifier identifier : coverage.getIdentifier()) {
			if (identifier.getSystem() != null && identifier.getSystem().startsWith("www.")) {
				identifier.setSystem("https://" + identifier.getSystem());
			}

			if ("https://www.xid.ch/id/ean".equals(identifier.getSystem())) {
				identifier.setSystem("urn:oid:2.51.1.3");
			}
		}

		if (coverage.hasType() && coverage.getType().hasCoding()) {
			for (Coding coding : coverage.getType().getCoding()) {
				if ("https://www.elexis.info/coverage/type".equals(coding.getSystem())) {
					coding.setSystem("http://fhir.ch/ig/ch-orf/CodeSystem/ch-orf-cs-coveragetype");
				}
			}
		}

		CodeableConcept type = coverage.getType();
		if (type == null || !type.hasCoding()) {
			type = new CodeableConcept();
			type.addCoding(new Coding().setSystem("http://fhir.ch/ig/ch-orf/CodeSystem/ch-orf-cs-coveragetype")
					.setCode("KVG").setDisplay("According to KVG"));
			coverage.setType(type);
		}

		for (Identifier identifier : coverage.getIdentifier()) {
			if ("urn:oid:2.16.756.5.30.1.123.100.1.1.1".equals(identifier.getSystem())) {
				newValue = identifier.getValue();
				break;
			}
		}
		for (Identifier identifier : coverage.getIdentifier()) {
			if ("https://www.elexis.info/objid".equals(identifier.getSystem()) && newValue != null) {
				identifier.setValue(newValue);
			}
		}
		if (coverage.hasText()) {
			coverage.setText(null);
		}
	}

	public static class AdjustJsonString {

		public static String adjustBundleJsonString(String bundleJsonString) {
			JsonObject bundleJson = JsonParser.parseString(bundleJsonString).getAsJsonObject();

			adjustJsonObject(bundleJson);

			return gson.toJson(bundleJson);
		}

		private static void adjustJsonObject(JsonObject jsonObject) {
			for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
				String key = entry.getKey();
				JsonElement value = entry.getValue();

				if (value.isJsonObject()) {
					JsonObject childObject = value.getAsJsonObject();
					if (key.equals("text") && childObject.has("div")) {
						String divContent = childObject.get("div").getAsString();
						String adjustedDivContent = divContent.replaceAll("<div[^>]*>", "").replaceAll("</div>", "")
								.trim();
						jsonObject.addProperty("text", adjustedDivContent);
					} else {
						adjustJsonObject(childObject);
					}
				} else if (value.isJsonArray()) {
					adjustJsonArray(value.getAsJsonArray());
				} else if (value.isJsonPrimitive() && value.getAsString().startsWith("www.")) {
					jsonObject.addProperty(key, "https://" + value.getAsString());

				} else if (value.isJsonPrimitive() && value.getAsJsonPrimitive().isString()) {
					String adjustedValue = value.getAsString().replaceAll("\\s+$", "");
		            jsonObject.addProperty(key, adjustedValue);
				}
			}
		}

		private static void adjustJsonArray(JsonArray jsonArray) {
			for (int i = 0; i < jsonArray.size(); i++) {
				JsonElement element = jsonArray.get(i);
				if (element.isJsonObject()) {
					adjustJsonObject(element.getAsJsonObject());
				} else if (element.isJsonArray()) {
					adjustJsonArray(element.getAsJsonArray());
				}
			}
		}
	}
}
