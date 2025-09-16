package ch.elexis.mednet.webapi.core.fhir.resources.util;

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.AllergyIntolerance;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Coverage;
import org.hl7.fhir.r4.model.DateTimeType;
import org.hl7.fhir.r4.model.FamilyMemberHistory;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Medication;
import org.hl7.fhir.r4.model.MedicationStatement;
import org.hl7.fhir.r4.model.Narrative;
import org.hl7.fhir.r4.model.Organization;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Practitioner;
import org.hl7.fhir.r4.model.PractitionerRole;
import org.hl7.fhir.r4.model.Resource;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import ch.elexis.mednet.webapi.core.constants.FHIRConstants;

public class AdjustBundleIdentifiers {

	private static final Gson gson = new Gson();

	private static final Pattern HAS_SCHEME = Pattern.compile("^[a-zA-Z][a-zA-Z0-9+.-]*://.*"); //$NON-NLS-1$
	private static final Pattern LOOKS_HOST = Pattern.compile("^([A-Za-z0-9-]+\\.)+[A-Za-z]{2,}(/.*)?$"); //$NON-NLS-1$

	private static boolean isAbsolute(String s) {
		return s != null && (HAS_SCHEME.matcher(s).matches() || s.startsWith("urn:")); //$NON-NLS-1$
	}

	private static String httpsify(String s) {
		if (s == null || s.isEmpty())
			return s;
		String val = s.trim();
		if (val.startsWith("www.")) //$NON-NLS-1$
			return "https://" + val; //$NON-NLS-1$
		if (!isAbsolute(val) && LOOKS_HOST.matcher(val).matches())
			return "https://" + val; //$NON-NLS-1$
		return val;
	}

	private static String normalizeSystem(String system) {
		if (system == null)
			return null;
		String s = system.trim();
		if ("https://www.xid.ch/id/ean".equals(s) || "www.xid.ch/id/ean".equals(s)) //$NON-NLS-1$ //$NON-NLS-2$
			return FHIRConstants.GLN_IDENTIFIER; // urn:oid:2.51.1.3
		if ("https://www.ahv.ch/xid".equals(s) || "www.ahv.ch/xid".equals(s)) //$NON-NLS-1$ //$NON-NLS-2$
			return FHIRConstants.AHV_IDENTIFIER; // urn:oid:2.16.756.5.32
		return httpsify(s);
	}

	public static String adjustBundleJsonString(String bundleJsonString) {
		JsonObject bundleJson = JsonParser.parseString(bundleJsonString).getAsJsonObject();
		adjustJsonObject(bundleJson);
		return gson.toJson(bundleJson);
	}

	private static void adjustJsonObject(JsonObject obj) {
		for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
			String key = e.getKey();
			JsonElement val = e.getValue();

			if (val.isJsonObject()) {
				JsonObject child = val.getAsJsonObject();
				adjustJsonObject(child);
				if ("extension".equals(key) && child.has("url") && child.get("url").isJsonPrimitive()) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					String url = child.get("url").getAsString(); //$NON-NLS-1$
					child.addProperty("url", httpsify(url)); //$NON-NLS-1$
				}

			} else if (val.isJsonArray()) {
				adjustJsonArray(val.getAsJsonArray(), key);

			} else if (val.isJsonPrimitive() && val.getAsJsonPrimitive().isString()) {
				String s = val.getAsString();
				String trimmed = s.replaceAll("\\s+$", StringUtils.EMPTY); //$NON-NLS-1$
				if (isSystemKey(key)) {
					obj.addProperty(key, normalizeSystem(trimmed));
				} else if (isUrlKey(key)) {
					obj.addProperty(key, httpsify(trimmed));
				} else {
					obj.addProperty(key, trimmed);
				}
			}
		}
	}

	private static final Set<String> SYSTEM_KEYS = Set.of("system");  //$NON-NLS-1$
	private static final Set<String> URL_KEYS = Set.of("url"); //$NON-NLS-1$

	private static boolean isSystemKey(String key) {
		return SYSTEM_KEYS.contains(key);
	}

	private static boolean isUrlKey(String key) {
		return URL_KEYS.contains(key);
	}

	private static void adjustJsonArray(JsonArray arr, String parentKey) {
		for (int i = 0; i < arr.size(); i++) {
			JsonElement el = arr.get(i);
			if (el.isJsonObject()) {
				JsonObject child = el.getAsJsonObject();
				if ("extension".equals(parentKey) && child.has("url") && child.get("url").isJsonPrimitive()) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					child.addProperty("url", httpsify(child.get("url").getAsString())); //$NON-NLS-1$ //$NON-NLS-2$
				}
				if (child.has("system") && child.get("system").isJsonPrimitive()) { //$NON-NLS-1$ //$NON-NLS-2$
					child.addProperty("system", normalizeSystem(child.get("system").getAsString())); //$NON-NLS-1$ //$NON-NLS-2$
				}
				adjustJsonObject(child);
			} else if (el.isJsonArray()) {
				adjustJsonArray(el.getAsJsonArray(), parentKey);
			} else if (el.isJsonPrimitive() && el.getAsJsonPrimitive().isString()) {
				String adjustedValue = el.getAsString().replaceAll("\\s+$", StringUtils.EMPTY); //$NON-NLS-1$
				arr.set(i, new JsonPrimitive(adjustedValue));
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
			} else if (resource instanceof Medication) {
				removeMedicationExtensions((Medication) resource);
			} else if (resource instanceof Practitioner) {
				adjustPractitioner((Practitioner) resource);
			} else if (resource instanceof PractitionerRole) {
				adjustPractitionerRole((PractitionerRole) resource);
			} else if (resource instanceof AllergyIntolerance) {
				adjustAllergyIntolerance((AllergyIntolerance) resource);
			} else if (resource instanceof FamilyMemberHistory) {
				adjustFamilyMemberHistory((FamilyMemberHistory) resource);
			} else if (resource instanceof MedicationStatement) {
				adjustMedicationStatement((MedicationStatement) resource);
			}
		}
	}

	private static void adjustPractitioner(Practitioner practitioner) {
		practitioner.getMeta().addProfile(FHIRConstants.PROFILE_PRACTITIONER);
		practitioner.getIdentifier().forEach(id -> {
			if (id.hasSystem())
				id.setSystem(normalizeSystem(id.getSystem()));
		});
		practitioner.getQualification()
				.forEach(q -> q.getCode().getCoding().forEach(cd -> cd.setSystem(normalizeSystem(cd.getSystem()))));
	}

	private static void adjustPractitionerRole(PractitionerRole pr) {
		pr.getMeta().addProfile(FHIRConstants.PROFILE_PRACTITIONER_ROLE);
		pr.getCode().forEach(cc -> cc.getCoding().forEach(cd -> {
			if (cd.hasSystem() && cd.getSystem().startsWith("www.")) { //$NON-NLS-1$
				cd.setSystem("https://" + cd.getSystem()); //$NON-NLS-1$
			}
		}));
		java.util.List<CodeableConcept> cleaned = new java.util.ArrayList<>();
		for (CodeableConcept cc : pr.getCode()) {
			CodeableConcept kept = new CodeableConcept();
			if (cc.hasText())
				kept.setText(cc.getText());

			for (Coding cd : cc.getCoding()) {
				String sys = cd.getSystem();
				if ("https://www.elexis.info/practitioner/role".equals(sys)) { //$NON-NLS-1$
					continue;
				}
				kept.addCoding(cd);
			}
			if (!kept.getCoding().isEmpty() || kept.hasText()) {
				cleaned.add(kept);
			}
		}
		pr.setCode(cleaned);
		if (!pr.hasCode() || pr.getCodeFirstRep().getCoding().isEmpty()) {
			pr.setCode(java.util.List.of(new CodeableConcept()
					.addCoding(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/practitioner-role") //$NON-NLS-1$
							.setCode("doctor").setDisplay("Doctor")))); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private static void adjustAllergyIntolerance(AllergyIntolerance ai) {
		ai.getMeta().addProfile(FHIRConstants.PROFILE_ALLERGY_INTOLERANCE);
		if (!ai.hasClinicalStatus()) {
			ai.setClinicalStatus(new CodeableConcept().addCoding(new Coding(
					FHIRConstants.CLINICAL_STATUS_SYSTEM, FHIRConstants.CLINICAL_STATUS_ACTIVE_CODE,
					FHIRConstants.CLINICAL_STATUS_ACTIVE_DISPLAY)));
		}
		if (!ai.hasVerificationStatus()) {
			ai.setVerificationStatus(new CodeableConcept()
					.addCoding(new Coding("http://terminology.hl7.org/CodeSystem/allergyintolerance-verification", //$NON-NLS-1$
							"confirmed", "Confirmed"))); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (ai.hasCode() && ai.getCode().hasText()) {
			ai.getCode().setText(ai.getCode().getText().trim());
		}
	}

	private static void adjustMedicationStatement(MedicationStatement ms) {
		ms.getMeta().addProfile(FHIRConstants.PROFILE_MEDICATION_STATEMENT);
		if (ms.hasEffectivePeriod()) {
			var p = ms.getEffectivePeriod();
			if (!p.hasEnd())
				ms.setEffective(new DateTimeType(p.hasStart() ? p.getStart() : new java.util.Date()));
		}
		ms.getReasonCode().forEach(cc -> cc.getCoding().forEach(cd -> cd.setSystem(normalizeSystem(cd.getSystem()))));
	}

	private static void adjustFamilyMemberHistory(FamilyMemberHistory fmh) {
		fmh.getMeta().addProfile(FHIRConstants.PROFILE_FAMALY_MEMBER_HISTORY);
		if (!fmh.hasStatus())
			fmh.setStatus(FamilyMemberHistory.FamilyHistoryStatus.COMPLETED);
		if (!fmh.hasRelationship()) {
			fmh.setRelationship(new CodeableConcept().addCoding(
					new Coding(FHIRConstants.ROLE_CODE_SYSTEM, "FAMMEMB", "family member"))); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (!fmh.hasCondition()) {
			var cond = new FamilyMemberHistory.FamilyMemberHistoryConditionComponent();
			cond.setCode(new CodeableConcept().setText("unknown condition")); //$NON-NLS-1$
			fmh.addCondition(cond);
		}
		if (!fmh.hasText() || !fmh.getText().hasDiv()) {
			fmh.getText().setStatus(Narrative.NarrativeStatus.GENERATED)
					.setDivAsString("<div xmlns=\"http://www.w3.org/1999/xhtml\">Family history</div>"); //$NON-NLS-1$
		}
	}

	private static void removeMedicationExtensions(Medication medication) {
		if (medication.hasExtension())
			medication.setExtension(null);
		medication.getCode().getCoding().forEach(cd -> cd.setSystem(normalizeSystem(cd.getSystem())));
	}

	private static void adjustPatientIdentifiers(Patient patient) {
		for (Identifier identifier : patient.getIdentifier()) {
			identifier.setSystem(normalizeSystem(identifier.getSystem()));
			if (FHIRConstants.AHV_IDENTIFIER.equals(identifier.getSystem())) {
				identifier.setSystem(FHIRConstants.AHV_IDENTIFIER);
			}
			if ("https://www.elexis.info/patnr".equals(identifier.getSystem())) { //$NON-NLS-1$
				CodeableConcept type = new CodeableConcept();
				type.addCoding(new Coding().setSystem("http://terminology.hl7.org/CodeSystem/v2-0203").setCode("MR") //$NON-NLS-1$ //$NON-NLS-2$
						.setDisplay("Medical record number")); //$NON-NLS-1$
				identifier.setType(type);
			}
		}
		if (patient.hasExtension())
			patient.setExtension(null);

		if (patient.hasMaritalStatus() && patient.getMaritalStatus().hasCoding()) {
			for (Coding coding : patient.getMaritalStatus().getCoding()) {
				if (coding.getSystem() == null || coding.getSystem().isEmpty()) {
					coding.setSystem("http://terminology.hl7.org/CodeSystem/v3-MaritalStatus"); //$NON-NLS-1$
				}
				if ("UNK".equals(coding.getCode())) { //$NON-NLS-1$
					coding.setCode("U"); //$NON-NLS-1$
					coding.setDisplay("unmarried"); //$NON-NLS-1$
				}
			}
		}
	}

	private static void adjustOrganizationIdentifiers(Organization organization) {
		for (Identifier identifier : organization.getIdentifier()) {
			identifier.setSystem(normalizeSystem(identifier.getSystem()));
			if (FHIRConstants.GLN_IDENTIFIER.equals(identifier.getSystem())) {
				identifier.setSystem(FHIRConstants.GLN_IDENTIFIER);
			}
		}
		if (organization.hasAddress()) {
			for (Address address : organization.getAddress()) {
				if ("home".equalsIgnoreCase(address.getUse().toString())) { //$NON-NLS-1$
					address.setUse(Address.AddressUse.WORK);
				}
			}
		}
	}

	private static void adjustCoverageIdentifiers(Coverage coverage) {
		String newValue = null;
		for (Identifier identifier : coverage.getIdentifier()) {
			identifier.setSystem(normalizeSystem(identifier.getSystem()));
		}
		if (coverage.hasType()) {
			CodeableConcept type = coverage.getType();
			if (type.hasCoding()) {
				java.util.List<Coding> keep = new java.util.ArrayList<>();
				for (Coding cd : type.getCoding()) {
					String sys = normalizeSystem(cd.getSystem());
					if ("https://www.elexis.info/coverage/reason".equals(sys)) { //$NON-NLS-1$
						continue;
					}
					if ("https://www.elexis.info/coverage/type".equals(sys) //$NON-NLS-1$
							|| "www.elexis.info/coverage/type".equals(sys)) { //$NON-NLS-1$
						sys = FHIRConstants.COVERAGE_TYPE_SYSTEM;
					}

					cd.setSystem(sys);
					if (!cd.hasDisplay() && cd.hasCode()) {
						cd.setDisplay(getCoverageDisplay(cd.getCode()));
					}
					keep.add(cd);
				}
				type.setCoding(keep);
			}
		}

		for (Identifier identifier : coverage.getIdentifier()) {
			if ("urn:oid:2.16.756.5.30.1.123.100.1.1.1".equals(identifier.getSystem())) { //$NON-NLS-1$
				newValue = identifier.getValue();
				break;
			}
		}
		for (Identifier identifier : coverage.getIdentifier()) {
			if ("https://www.elexis.info/objid".equals(identifier.getSystem()) && newValue != null) { //$NON-NLS-1$
				identifier.setValue(newValue);
			}
		}

		if (coverage.hasDependent()) {
			coverage.setDependent(null);
		}

		if (coverage.hasText())
			coverage.setText(null);
		coverage.getMeta().addProfile(FHIRConstants.PROFILE_COVERAGE);
	}


	public static String getCoverageDisplay(String code) {
		if (code == null) {
			return "Other"; //$NON-NLS-1$
		}

		return switch (code) {
		case "KVG" -> "According to KVG"; //$NON-NLS-1$ //$NON-NLS-2$
		case "UVG" -> "According to UVG"; //$NON-NLS-1$ //$NON-NLS-2$
		case "VVG" -> "According to VVG"; //$NON-NLS-1$ //$NON-NLS-2$
		case "IVG" -> "According to IVG"; //$NON-NLS-1$ //$NON-NLS-2$
		case "MVG" -> "According to MVG"; //$NON-NLS-1$ //$NON-NLS-2$
		case "Self" -> "Self"; //$NON-NLS-1$ //$NON-NLS-2$
		case "Other" -> "Other"; //$NON-NLS-1$ //$NON-NLS-2$
		default -> "Other";
		};
	}
}
