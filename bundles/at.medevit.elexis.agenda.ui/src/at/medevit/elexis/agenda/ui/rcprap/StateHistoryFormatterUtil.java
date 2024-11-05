package at.medevit.elexis.agenda.ui.rcprap;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.elexis.core.model.IAppointment;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class StateHistoryFormatterUtil {

	public static String replaceIdsWithLabels(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}
		String regex = "\\{\\{([0-9a-fA-F]{25})\\}\\}";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		StringBuffer result = new StringBuffer();
		while (matcher.find()) {
			String id = matcher.group(1);
			String label = getLabelFromId(id);
			matcher.appendReplacement(result, label);
		}
		matcher.appendTail(result);
		return result.toString();
	}

	public static String formatStateHistory(String stateHistory) {
		return formatStateHistory(stateHistory, 3, 11, 10);
	}

	public static String formatStateHistoryFull(String stateHistory) {
		return formatStateHistory(stateHistory, -1, 12, 11);
	}

	private static String formatStateHistory(String stateHistory, int numEntries, int dateFontSize,
			int messageFontSize) {
		if (stateHistory == null || stateHistory.isEmpty()) {
			return stateHistory;
		}
		StringBuilder formattedHistory = new StringBuilder();
		formattedHistory.append("<style>").append(".date-part { text-align: left; font-size: ").append(dateFontSize)
				.append("px; font-weight: bold; }").append(".message-part { text-align: left; font-size: ")
				.append(messageFontSize).append("px; }").append("</style>");

		String[] allLines = stateHistory.split("<br />");
		int start = 0;

		if (numEntries > 0 && numEntries < allLines.length) {
			start = allLines.length - numEntries;
		}

		Pattern pattern = Pattern.compile("^(\\d{2}\\.\\d{2}\\.\\d{4} \\d{2}:\\d{2}:\\d{2}):\\s*(.*)$");

		for (int i = start; i < allLines.length; i++) {
			String line = allLines[i];
			Matcher matcher = pattern.matcher(line);
			if (matcher.matches()) {
				String datePart = matcher.group(1) + ":";
				String messagePart = matcher.group(2);
				formattedHistory.append("<div class=\"date-part\">").append(datePart).append("</div>");
				formattedHistory.append("<div class=\"message-part\">").append(messagePart).append("</div>");
			} else {
				formattedHistory.append(line);
			}
			formattedHistory.append("<br />");
		}
		return formattedHistory.toString();
	}

	private static String getLabelFromId(String id) {
		Optional<IAppointment> appointment = CoreModelServiceHolder.get().load(id, IAppointment.class, true, false);
		if (appointment.isPresent()) {
			String label = appointment.get().getLabel();
			return label;
		} else {
			return "[Unbekannter Termin: ID " + id + "]";
		}
	}
}
