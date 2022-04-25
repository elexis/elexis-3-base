package ch.elexis.connect.sysmex.packages;

import java.util.Collections;
import java.util.ResourceBundle;

import ch.elexis.connect.sysmex.Messages;
import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.importer.div.importers.TransientLabResult;
import ch.elexis.core.importer.div.service.holder.LabImportUtilHolder;
import ch.elexis.core.model.ILabItem;
import ch.elexis.core.model.ILaboratory;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.types.LabItemTyp;
import ch.elexis.core.ui.importer.div.importers.DefaultLabImportUiHandler;
import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;

public class Value {
	private static final String KX21_BUNDLE_NAME = "ch.elexis.connect.sysmex.packages.valuetexts_KX21"; //$NON-NLS-1$
	private static final String KX21N_BUNDLE_NAME = "ch.elexis.connect.sysmex.packages.valuetexts_KX21N"; //$NON-NLS-1$
	private static final String POCH_BUNDLE_NAME = "ch.elexis.connect.sysmex.packages.valuetexts_pocH"; //$NON-NLS-1$
	private static final String UC1000_BUNDLE_NAME = "ch.elexis.connect.sysmex.packages.valuetexts_UC1000"; //$NON-NLS-1$

	private final ResourceBundle _bundle;
	ILaboratory _myLab;
	String _shortName;
	String _longName;
	String _unit;
	ILabItem _labItem;
	String _refMann;
	String _refFrau;

	public static Value getValueUC1000(final String paramName) throws PackageException {
		return new Value(paramName, UC1000_BUNDLE_NAME);
	}

	public static Value getValueKX21(final String paramName) throws PackageException {
		return new Value(paramName, KX21_BUNDLE_NAME);
	}

	public static Value getValueKX21N(final String paramName) throws PackageException {
		return new Value(paramName, KX21N_BUNDLE_NAME);
	}

	public static Value getValuePOCH(final String paramName) throws PackageException {
		return new Value(paramName, POCH_BUNDLE_NAME);
	}

	private Value(final String paramName, final String bundleName) throws PackageException {
		_bundle = ResourceBundle.getBundle(bundleName);
		_shortName = getString(paramName, "kuerzel"); //$NON-NLS-1$
		_longName = getString(paramName, "text"); //$NON-NLS-1$
		_unit = getString(paramName, "unit"); //$NON-NLS-1$
		_refMann = getString(paramName, "refM");//$NON-NLS-1$
		_refFrau = getString(paramName, "refF");//$NON-NLS-1$
	}

	private void initialize() {
		_myLab = LabImportUtilHolder.get().getOrCreateLabor(Messages.Sysmex_Value_LabKuerzel);

		_labItem = LabImportUtilHolder.get().getLabItem(_shortName, _myLab);
		if (_labItem == null) {
			_labItem = LabImportUtilHolder.get().createLabItem(_shortName, _longName, _myLab, _refMann, _refFrau, _unit,
					LabItemTyp.NUMERIC, Messages.Sysmex_Value_LabName, "50");
		}
	}

	public void fetchValue(Patient patient, String value, Integer flags, TimeTool date) {
		fetchValue(patient, value, flags, date, "");
	}

	public void fetchValue(Patient patient, String value, Integer flags, TimeTool date, String comment) {
		if (_labItem == null) {
			initialize();
		}
		IPatient iPatient = CoreModelServiceHolder.get().load(patient.getId(), IPatient.class).orElse(null);
		TransientLabResult tLabResult = null;
		// do not set flag, pathologic info could be calculated in
		// TransientLabResult#persist
		if (flags == -1) {
			tLabResult = new TransientLabResult.Builder(iPatient, _myLab, _labItem, value).date(date).comment(comment)
					.build(LabImportUtilHolder.get());
		} else {
			tLabResult = new TransientLabResult.Builder(iPatient, _myLab, _labItem, value).date(date).comment(comment)
					.flags(flags).build(LabImportUtilHolder.get());
		}
		LabImportUtilHolder.get().importLabResults(Collections.singletonList(tLabResult),
				new DefaultLabImportUiHandler());
	}

	public String get_shortName() {
		return _shortName;
	}

	public String get_longName() {
		return _longName;
	}

	private String getString(String paramName, String key) {
		return _bundle.getString(paramName + "." + key); //$NON-NLS-1$
	}
}