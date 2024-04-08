package at.medevit.ch.artikelstamm.ui.internal;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.component.annotations.Component;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.model.common.preference.PreferenceConstants;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.services.IBilledAdjuster;
import ch.elexis.core.services.holder.BillingServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

@Component
public class GenericTypeOriginalAdjuster implements IBilledAdjuster {

	@Override
	public void adjust(IBilled billed) {
		if (ConfigServiceHolder.get().get(PreferenceConstants.PREF_SHOW_WARN_ORIGINAL_ARTICLES, false)) {
			IBillable billable = billed.getBillable();
			if (billable instanceof IArtikelstammItem) {
				String autoAddValue = (String) ((IArtikelstammItem) billable)
						.getExtInfo(Constants.FLD_EXT_ORIGINALNOSUBSTITUTE);
				if ("true".equals(autoAddValue)) { //$NON-NLS-1$
					billed.setExtInfo(Constants.FLD_EXT_ORIGINALNOSUBSTITUTE, "true"); //$NON-NLS-1$
					CoreModelServiceHolder.get().save(billed);
				} else {
					if ("O".equals(((IArtikelstammItem) billable).getGenericType())) { //$NON-NLS-1$
						Display.getDefault().syncExec(() -> {
							int answer = MessageDialog.open(MessageDialog.WARNING,
									Display.getDefault().getActiveShell(), "Originalpräparat",
									billable.getLabel() + " ist ein Originalpräparat mit "
											+ ((IArtikelstammItem) billable).getDeductible()
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
		}
	}
}
