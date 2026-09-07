package at.medevit.ch.artikelstamm.model.history;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class PriceChangeEntry {

	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

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

	public static class EncounterInfo {
		@SerializedName("encounterId")
		private String encounterId;
		@SerializedName("encounterDate")
		private String encounterDate;
		@SerializedName("patientName")
		private String patientName;
		@SerializedName("mandatorName")
		private String mandatorName;

		public EncounterInfo(String encounterId, String encounterDate, String patientName, String mandatorName) {
			this.encounterId = encounterId;
			this.encounterDate = encounterDate;
			this.patientName = patientName;
			this.mandatorName = mandatorName;
		}
	}
}