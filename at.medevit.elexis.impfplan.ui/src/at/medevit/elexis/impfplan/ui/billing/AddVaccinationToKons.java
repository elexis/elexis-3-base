package at.medevit.elexis.impfplan.ui.billing;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.core.ui.dialogs.SelectOrCreateOpenKonsDialog;
import ch.elexis.data.Artikel;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.TarmedLeistung;
import ch.elexis.data.Verrechnet;

public class AddVaccinationToKons {
	private static final String TARMED_5MIN_TARIF = "00.0010";
	
	private static Object selectKonsLock = new Object();
	private Konsultation kons;
	private static IVerrechenbar consVerrechenbar;
	private Patient patient;
	private Artikel art;
	
	public AddVaccinationToKons(Patient patient, Artikel art, String ean){
		this.patient = patient;
		this.art = art;
		if (art == null) {
			art = ArtikelstammItem.findByEANorGTIN(ean);
		}
		
		if (consVerrechenbar == null) {
			consVerrechenbar = TarmedLeistung.getFromCode(TARMED_5MIN_TARIF);
		}
	}
	
	public boolean findOrCreateKons(){
		// TODO anbieten, dass Tarifpositionen vorab bestimmt werden können
// String l = CoreHub.userCfg.get(PreferencePage.VAC_BILLING_POS, "");
// String[] billingPos = l.split(",");
		
		initKonsultation();
		if (kons != null && kons.isEditable(false)) {
			kons.addLeistung(art);
			
			boolean addedCons = true;
			List<Verrechnet> leistungen = kons.getLeistungen();
			for (Verrechnet verrechnet : leistungen) {
				IVerrechenbar verrechenbar = verrechnet.getVerrechenbar();
				if (verrechenbar != null && verrechenbar.getCodeSystemName().equals("Tarmed")
					&& verrechenbar.getCode().equals(TARMED_5MIN_TARIF)) {
					kons.updateEintrag(
						kons.getEintrag().getHead() + "\nImpfung - " + art.getName(), true);
					addedCons = false;
					break;
				}
			}
			if (addedCons) {
				kons.updateEintrag("Impfung - " + art.getName(), true);
				if (consVerrechenbar != null) {
					kons.addLeistung(consVerrechenbar);
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * get existing kons or creates new one for patient
	 */
	private void initKonsultation(){
		synchronized (selectKonsLock) {
			kons = patient.getLetzteKons(false);
			if (kons == null || !kons.isEditable(false)) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run(){
						SelectOrCreateOpenKonsDialog dialog =
							new SelectOrCreateOpenKonsDialog(patient,
								"Konsultation für die automatische Verrechnung auswählen.");
						if (dialog.open() == Dialog.OK) {
							kons = dialog.getKonsultation();
						}
					}
				});
			}
		}
	}
	
}
