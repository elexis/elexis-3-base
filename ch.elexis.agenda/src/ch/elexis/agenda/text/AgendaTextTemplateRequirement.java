package ch.elexis.agenda.text;

import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.text.ITextTemplateRequirement;

public class AgendaTextTemplateRequirement implements ITextTemplateRequirement {
	public static final String TT_APPOINTMENT_CARD = CoreHub.localCfg.get(
		PreferenceConstants.AG_PRINT_APPOINTMENTCARD_TEMPLATE,
		PreferenceConstants.AG_PRINT_APPOINTMENTCARD_TEMPLATE_DEFAULT);
	public static final String TT_APPOINTMENT_CARD_DESC =
		"Vorlage für Terminkarte mit kommenden Terminen des Patienten";
	
	public static final String TT_AGENDA_LIST = "AgendaListe";
	public static final String TT_AGENDA_LIST_DESC =
		"Liste der Konsultationen für aktuellen Mandanten heute";
	
	@Override
	public String[] getNamesOfRequiredTextTemplate(){
		return new String[] {
			TT_APPOINTMENT_CARD, TT_AGENDA_LIST
		};
	}
	
	@Override
	public String[] getDescriptionsOfRequiredTextTemplate(){
		return new String[] {
			TT_APPOINTMENT_CARD_DESC, TT_AGENDA_LIST_DESC
		};
	}
	
}
