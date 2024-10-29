package at.medevit.elexis.agenda.ui.handler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.rgw.tools.StringTool;

public class AppointmentHistoryManager {

	private IAppointment appointment;

	public AppointmentHistoryManager(IAppointment appointment) {
		this.appointment = appointment;
	}

	/**
	 * Fügt einen neuen Eintrag zur Status-Historie des Termins hinzu
	 * 
	 * @param action Beschreibt die durchgeführte Aktion
	 */
	public void addHistoryEntry(String action) {

		String currentHistory = appointment.getStateHistory();
		String user = getCurrentUser();

		if (!currentHistory.isEmpty()) {
			currentHistory += StringTool.lf;
		}

		String timestamp = toMinutesTimeStamp(LocalDateTime.now());
		String entry = timestamp + ";" + action + " [" + user + "]";

		appointment.setStateHistory(currentHistory + entry);
		CoreModelServiceHolder.get().save(appointment);

	}

	/**
	 * Protokolliert das Verschieben eines Termins, inklusive des Bereichswechsels
	 * 
	 * @param oldStartTime Alte Startzeit
	 * @param newStartTime Neue Startzeit
	 * @param oldArea      Alter Bereich
	 * @param newArea      Neuer Bereich
	 */
	public void logAppointmentMove(LocalDateTime oldStartTime, LocalDateTime newStartTime, String oldArea,
			String newArea) {
		String entry = "Verschoben von " + formatDateTime(oldStartTime) + " (" + oldArea + ") " + "auf "
				+ formatDateTime(newStartTime) + " (" + newArea + ")";
		addHistoryEntry(entry);
	}

	/**
	 * Protokolliert das Kopieren eines Termins von-bis
	 * 
	 * @param originalId Die ID des Originaltermins
	 * @param newId      Die ID des neuen Termins
	 */
	public void logAppointmentCopyFromTo(String originalId, String newId) {
		String entry = "Termin kopiert von ID " + "{{" + originalId + "}}" + " auf ID " + "{{" + newId + "}}";
		addHistoryEntry(entry);
	}

	/**
	 * Protokolliert das Kopieren eines Termins
	 * 
	 * @param originalId Die ID des Originaltermins
	 * @param newId      Die ID des neuen Termins
	 */
	public void logAppointmentCopy(String originalId) {
		String entry = "Termin kopiert von ID " + "{{" + originalId + "}}";
		addHistoryEntry(entry);
	}

	/**
	 * Protokolliert die Änderung der Dauer eines Termins
	 * 
	 * @param oldEndTime Alte Endzeit
	 * @param newEndTime Neue Endzeit
	 */
    public void logAppointmentDurationChange(LocalDateTime oldEndTime, LocalDateTime newEndTime) {
        String entry = "Dauer geändert von " + formatDateTime(oldEndTime) + " auf " + formatDateTime(newEndTime);
        addHistoryEntry(entry);
    }

	/**
	 * Gibt die formattierte Historie zurück
	 * 
	 * @param formatPattern Das Formatmuster für das Datum
	 * @return Eine formatierte Historie als String
	 */
    public String getFormattedHistory(String formatPattern) {
        StringBuilder sb = new StringBuilder();
        String history = appointment.getStateHistory();

        if (StringUtils.isNotBlank(history)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatPattern);
            String[] lines = history.split(StringTool.lf);

            for (String line : lines) {
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    LocalDateTime timestamp = fromMinutesTimeStamp(parts[0]);
					sb.append(formatter.format(timestamp)).append(": ").append(parts[1]).append(StringTool.lf);
				}
			}
		}
		return sb.toString();
	}

	private String getCurrentUser() {
		return ContextServiceHolder.isAvailable() && ContextServiceHolder.get().getActiveUser().isPresent()
				? ContextServiceHolder.get().getActiveUser().get().getLabel()
				: "Unbekannt";
	}

	private String formatDateTime(LocalDateTime dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
		return formatter.format(dateTime);
	}

	private LocalDateTime fromMinutesTimeStamp(String timestamp) {
		long minutes = Long.parseLong(timestamp);
		return LocalDateTime.ofEpochSecond(minutes * 60, 0, ZoneId.systemDefault().getRules().getOffset(Instant.now()));
	}

	private String toMinutesTimeStamp(LocalDateTime localDateTime) {
		long minutes = localDateTime.toEpochSecond(ZoneId.systemDefault().getRules().getOffset(localDateTime)) / 60;
		return Long.toString(minutes);
	}

	/**
	 * Protokolliert die Bearbeitung eines Termins
	 */
	public void logAppointmentEdit() {
		String timestamp = formatDateTime(LocalDateTime.now());
		String entry = "Termin bearbeitet am " + timestamp + " durch";
		addHistoryEntry(entry);
	}

	/**
	 * Protokolliert das Löschen eines Termins
	 */
	public void logAppointmentDeletion() {
		String formattedDateTime = formatDateTime(LocalDateTime.now());
		String entry = "Termin gelöscht am " + formattedDateTime + " durch";
		addHistoryEntry(entry);
	}


}
