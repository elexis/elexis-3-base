package at.medevit.ch.artikelstamm.model.history;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class PriceChangeEntry {

	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"); //$NON-NLS-1$

	@SerializedName("action")
	private String action;

	@SerializedName("timestamp")
	private String timestamp;

	@SerializedName("userId")
	private String userId;

	@SerializedName("details")
	private String details;

	@SerializedName("extraInfo")
	private String extraInfo;

	@SerializedName("updatedEncounters")
	private List<EncounterInfo> updatedEncounters;

	// Leerer Konstruktor für GSON/Reflexion
	public PriceChangeEntry() {
	}

	public PriceChangeEntry(String action, String userId, String details, String extraInfo,
			List<EncounterInfo> updatedEncounters) {
		this.action = action;
		this.timestamp = LocalDateTime.now().format(TIME_FORMAT);
		this.userId = userId;
		this.details = details;
		this.extraInfo = extraInfo;
		this.updatedEncounters = updatedEncounters;
	}

	public String toJson() {
		return new Gson().toJson(this);
	}

	// --- GETTER & SETTER (Hier haben sie gefehlt) ---

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	public List<EncounterInfo> getUpdatedEncounters() {
		return updatedEncounters;
	}

	public void setUpdatedEncounters(List<EncounterInfo> updatedEncounters) {
		this.updatedEncounters = updatedEncounters;
	}

	// --- Inner Class EncounterInfo ---

	public static class EncounterInfo {
		@SerializedName("encounterId")
		private String encounterId;
		@SerializedName("encounterDate")
		private String encounterDate;
		@SerializedName("patientName")
		private String patientName;
		@SerializedName("mandatorName")
		private String mandatorName;

		public EncounterInfo() {
		}

		public EncounterInfo(String encounterId, String encounterDate, String patientName, String mandatorName) {
			this.encounterId = encounterId;
			this.encounterDate = encounterDate;
			this.patientName = patientName;
			this.mandatorName = mandatorName;
		}

		public String getEncounterId() {
			return encounterId;
		}

		public void setEncounterId(String encounterId) {
			this.encounterId = encounterId;
		}

		public String getEncounterDate() {
			return encounterDate;
		}

		public void setEncounterDate(String encounterDate) {
			this.encounterDate = encounterDate;
		}

		public String getPatientName() {
			return patientName;
		}

		public void setPatientName(String patientName) {
			this.patientName = patientName;
		}

		public String getMandatorName() {
			return mandatorName;
		}

		public void setMandatorName(String mandatorName) {
			this.mandatorName = mandatorName;
		}
	}
}