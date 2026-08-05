package at.medevit.ch.artikelstamm.ui.internal;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.component.annotations.Component;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.extinfo.ArticleIndicationInfo;
import at.medevit.ch.artikelstamm.model.common.preference.PreferenceConstants;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.services.IBilledAdjuster;
import ch.elexis.core.services.holder.BillingServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

@Component
public class GenericTypeOriginalAdjuster implements IBilledAdjuster {

	@Override
	public void adjust(IBilled billed) {
		IBillable billable = billed.getBillable();
		if (billable instanceof IArtikelstammItem item) {
			if (ConfigServiceHolder.get().get(PreferenceConstants.PREF_SHOW_WARN_ORIGINAL_ARTICLES, false)) {
				String autoAddValue = (String) item.getExtInfo(Constants.FLD_EXT_ORIGINALNOSUBSTITUTE);
				if ("true".equals(autoAddValue)) { //$NON-NLS-1$
					billed.setExtInfo(Constants.FLD_EXT_ORIGINALNOSUBSTITUTE, "true"); //$NON-NLS-1$
					CoreModelServiceHolder.get().save(billed);
				} else {
					if ("O".equals(item.getGenericType())) { //$NON-NLS-1$
						Display.getDefault().syncExec(() -> {
							int answer = MessageDialog.open(MessageDialog.WARNING,
									Display.getDefault().getActiveShell(), "Originalpräparat",
									billable.getLabel() + " ist ein Originalpräparat mit " + item.getDeductible()
											+ "% Selbstbehalt. Soll dieses Präparat verrechnet werden?",
									SWT.NONE, "Ja", "Ja, mit Substitution nicht möglich", "Nein");
							if (answer == 1) {
								billed.setExtInfo(Constants.FLD_EXT_ORIGINALNOSUBSTITUTE, "true"); //$NON-NLS-1$
								CoreModelServiceHolder.get().save(billed);
							} else if (answer == 2) {
								BillingServiceHolder.get().removeBilled(billed, billed.getEncounter());
							}
						});
					}
				}
			}
			// apply indication code on billed if needed
			if (item.isPm()) {
				Optional<ArticleIndicationInfo> indicationInfo = item.getIndicationInfo();
				if (indicationInfo.isPresent() && !indicationInfo.get().getIndications().isEmpty()) {
					System.out.println("ADD INDICATION CODE WITH UI BILLED " + item.getLabel());
					// lookup in prescriptions, and use that value without user interaction
					Optional<String> indicationCodeHistory = IndicationCodeUtil.getLastIndicationCode(item,
							billed.getEncounter().getPatient(), Arrays.asList(EntryType.FIXED_MEDICATION,
									EntryType.RESERVE_MEDICATION, EntryType.SYMPTOMATIC_MEDICATION));
					if (indicationCodeHistory.isPresent()) {
						billed.setExtInfo(Constants.FLD_EXT_INDICATIONCODE, indicationCodeHistory.get());
						CoreModelServiceHolder.get().save(billed);
						return;
					}
					// direct apply single indication code
					Optional<String> singleIndicationCode = IndicationCodeUtil.getSingleIndicationCode(item);
					if (singleIndicationCode.isPresent()) {
						billed.setExtInfo(Constants.FLD_EXT_INDICATIONCODE, singleIndicationCode.get());
						CoreModelServiceHolder.get().save(billed);
						return;
					}

					Optional<String> selection = IndicationCodeUtil
							.getIndicationCodeSelection(billed.getEncounter().getPatient(), item, null, billed);
					if (selection.isPresent()) {
						billed.setExtInfo(Constants.FLD_EXT_INDICATIONCODE, selection.get());
						CoreModelServiceHolder.get().save(billed);
						IndicationCodeUtil.applyToMedicationIfMissing(billed.getEncounter().getPatient(), item,
								selection.get());
					}
				}
			}
		}
	}
}
