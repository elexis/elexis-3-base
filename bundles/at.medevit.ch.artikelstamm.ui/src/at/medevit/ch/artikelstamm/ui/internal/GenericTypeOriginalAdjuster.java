package at.medevit.ch.artikelstamm.ui.internal;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.component.annotations.Component;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.extinfo.ArticleIndicationInfo;
import at.medevit.ch.artikelstamm.model.common.preference.PreferenceConstants;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.services.IBilledAdjuster;
import ch.elexis.core.services.INativeQuery;
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
			if (item.isPm()) {
				Optional<ArticleIndicationInfo> indicationInfo = item.getIndicationInfo();
				if (indicationInfo.isPresent() && !indicationInfo.get().getIndications().isEmpty()) {
					IndicationCodeSelectionDialog dialog = new IndicationCodeSelectionDialog(item,
							Display.getDefault().getActiveShell());

					Optional<String> indicationCodeHistory = getLastIndicationCode(item,
							billed.getEncounter().getPatient());
					indicationCodeHistory.ifPresent(code -> {
						dialog.setSelectedCode(code);
					});
					if (dialog.open() == Window.OK) {
						if (dialog.getSelectedCode() instanceof String selectedCode) {
							billed.setExtInfo(Constants.FLD_EXT_INDICATIONCODE, selectedCode);
							CoreModelServiceHolder.get().save(billed);
						}
					}
				}
			}
		}
	}

	// @formatter:off
	private static final String VERRECHNET_BYPATIENT_ANDCODE = "SELECT leistungen.ID FROM leistungen, behandlungen, faelle"
	+ " WHERE leistungen.deleted = '0'"
	+ " AND leistungen.deleted = behandlungen.deleted"
	+ " AND leistungen.BEHANDLUNG = behandlungen.ID"
	+ " AND leistungen.KLASSE = 'ch.artikelstamm.elexis.common.ArtikelstammItem'"
	+ " AND faelle.ID = behandlungen.fallID"
	+ " AND faelle.PatientID = ?1"
	+ " AND leistungen.LEISTG_CODE like ?2"
	+ " ORDER BY behandlungen.Datum DESC";
	// @formatter:on

	private Optional<String> getLastIndicationCode(IArtikelstammItem item, IPatient patient) {
		INativeQuery nativeQuery = CoreModelServiceHolder.get().getNativeQuery(VERRECHNET_BYPATIENT_ANDCODE);
		String itemIdStart = item.getId().substring(0, item.getId().indexOf(item.getCode()) + item.getCode().length());
		Map<Integer, Object> parameterMap = nativeQuery.getIndexedParameterMap(Integer.valueOf(1), patient.getId(),
				Integer.valueOf(2), itemIdStart + "%");
		Iterator<?> result = nativeQuery.executeWithParameters(parameterMap).iterator();
		while (result.hasNext()) {
			String next = result.next().toString();
			IBilled billed = CoreModelServiceHolder.get().load(next, IBilled.class).get();
			if (billed.getExtInfo(Constants.FLD_EXT_INDICATIONCODE) instanceof String indicationCode
					&& StringUtils.isNotBlank(indicationCode)) {
				return Optional.of(indicationCode);
			}
		}
		return Optional.empty();
	}
}
