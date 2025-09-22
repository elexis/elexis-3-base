package at.medevit.elexis.impfplan.ui.billing;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Display;

import at.medevit.elexis.impfplan.ui.preferences.PreferencePage;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.data.service.CodeElementServiceHolder;
import ch.elexis.core.model.IArticle;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.services.ICodeElementService.CodeElementTyp;
import ch.elexis.core.services.ICodeElementService.ContextKeys;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.core.services.holder.BillingServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.text.model.Samdas;
import ch.elexis.core.text.model.Samdas.Record;
import ch.elexis.core.ui.dialogs.SelectOrCreateOpenKonsDialog;
import ch.elexis.core.ui.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.locks.ILockHandler;
import ch.elexis.core.ui.services.EncounterServiceHolder;

public class AddVaccinationToKons {
	private static final String TARMED_5MIN_TARIF = "00.0010"; //$NON-NLS-1$

	private static Object selectKonsLock = new Object();
	private IEncounter actEncounter;
	private IPatient patient;
	private IArticle art;

	public AddVaccinationToKons(IPatient patient, IArticle art, String ean) {
		this.patient = patient;
		this.art = art;
		if (art == null) {
			CodeElementServiceHolder.get().getContribution(CodeElementTyp.ARTICLE, "Artikelstamm") //$NON-NLS-1$
					.ifPresent(contribution -> {
						Optional<ICodeElement> loaded = contribution.loadFromCode(ean);
						if (loaded.isPresent()) {
							this.art = (IArticle) loaded.get();
						}
					});
		}
	}

	public IEncounter findOrCreateKons() {
		initKonsultation();
		if (actEncounter == null || !EncounterServiceHolder.get().isEditable(actEncounter)) {
			return null;
		} else { // (kons != null && kons.isEditable(false)) {
			AcquireLockBlockingUi.aquireAndRun(actEncounter, new ILockHandler() {

				@Override
				public void lockFailed() {
					// do nothing

				}

				@Override
				public void lockAcquired() {
					if (actEncounter != null) {
						BillingServiceHolder.get().bill(art, actEncounter, 1);

						// update kons. text
						Samdas samdas = new Samdas(actEncounter.getVersionedEntry().getHead());
						Record rec = samdas.getRecord();
						String recText = rec.getText();
						recText += "\nImpfung - " + art.getName(); //$NON-NLS-1$
						rec.setText(recText);
						EncounterServiceHolder.get().updateVersionedEntry(actEncounter, samdas);

						if (ConfigServiceHolder.getUser(PreferencePage.VAC_AUTO_BILL, true)) {
							boolean addedCons = true;
							List<IBilled> leistungen = actEncounter.getBilled();
							for (IBilled verrechnet : leistungen) {
								IBillable verrechenbar = verrechnet.getBillable();
								if (verrechenbar != null && verrechenbar.getCodeSystemName().equals("Tarmed") //$NON-NLS-1$
										&& verrechenbar.getCode().equals(TARMED_5MIN_TARIF)) {
									addedCons = false;
									break;
								}
							}
							IBillable consVerrechenbar = getKonsVerrechenbar(actEncounter);
							if (addedCons && (consVerrechenbar != null)) {
								BillingServiceHolder.get().bill(consVerrechenbar, actEncounter, 1);
							}
						}
						ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, actEncounter);
					}
				}
			});
			return actEncounter;
		}
	}

	private IBillable getKonsVerrechenbar(IEncounter encounter) {
		LocalDate encounterDate = encounter.getDate();
		if (encounter.getCoverage() != null) {
			BillingLaw law = encounter.getCoverage().getBillingSystem().getLaw();
			Optional<ICodeElementServiceContribution> tarmedContribution = CodeElementServiceHolder.get()
					.getContribution(CodeElementTyp.SERVICE, "Tarmed"); //$NON-NLS-1$
			if (tarmedContribution.isPresent()) {
				Map<Object, Object> context = new HashMap<>();
				context.put(ContextKeys.DATE, encounterDate);
				context.put(ContextKeys.LAW, law.name());
				Optional<ICodeElement> loaded = tarmedContribution.get().loadFromCode(TARMED_5MIN_TARIF, context);
				if (loaded.isPresent()) {
					return (IBillable) loaded.get();
				}
			}
		}
		return null;
	}

	/**
	 * get existing kons or creates new one for patient
	 */
	private void initKonsultation() {
		synchronized (selectKonsLock) {
			actEncounter = EncounterServiceHolder.get().getLatestEncounter(patient, false).orElse(null);
			if (actEncounter == null || !EncounterServiceHolder.get().isEditable(actEncounter)) {
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						SelectOrCreateOpenKonsDialog dialog = new SelectOrCreateOpenKonsDialog(patient,
								"Konsultation für die automatische Verrechnung auswählen.");
						if (dialog.open() == Dialog.OK) {
							actEncounter = dialog.getKonsultation();
						}
					}
				});
			}
		}
	}

}
