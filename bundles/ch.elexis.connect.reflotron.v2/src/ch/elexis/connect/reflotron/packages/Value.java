package ch.elexis.connect.reflotron.packages;

import org.apache.commons.lang3.StringUtils;
import java.util.ResourceBundle;

import ch.elexis.connect.reflotron.Messages;
import ch.elexis.core.data.service.CoreModelServiceHolder;
import ch.elexis.core.importer.div.importers.TransientLabResult;
import ch.elexis.core.importer.div.service.holder.LabImportUtilHolder;
import ch.elexis.core.model.ILabItem;
import ch.elexis.core.model.ILaboratory;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.LabResultConstants;
import ch.elexis.core.types.LabItemTyp;
import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;

public class Value {
	private static final String BUNDLE_NAME = "ch.elexis.connect.reflotron.packages.valuetexts";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private static String getString(String paramName, String key) {
		return RESOURCE_BUNDLE.getString(paramName + "." + key);
	}

	public static Value getValue(final String paramName, final String unit) throws PackageException {
		return new Value(paramName, unit);
	}

	String _shortName;
	String _longName;
	String _unit;
	ILabItem _labItem;
	String _refMann;
	String _refFrau;
	ILaboratory _labor;

	String warning = StringUtils.EMPTY;

	public String getWarning() {
		return this.warning;
	}

	public String get_shortName() {
		return _shortName;
	}

	public String get_longName() {
		return _longName;
	}

	Value(final String paramName, final String unit) throws PackageException {
		_shortName = getString(paramName, "kuerzel");
		_longName = getString(paramName, "text");
		String valueTextUnit = getString(paramName, "unit");
		if (unit != null && !unit.equals(valueTextUnit)) {
			this.warning = "Einheit ist verschieden: " + unit + " - " + valueTextUnit;
		}
		_unit = unit;
		_refMann = getString(paramName, "refM");
		_refFrau = getString(paramName, "refF");
	}

	private void initialize() {
		_labor = LabImportUtilHolder.get().getOrCreateLabor(Messages.Reflotron_Value_LabKuerzel);

		_labItem = LabImportUtilHolder.get().getLabItem(_shortName, _labor);

		if (_labItem == null) {
			_labItem = LabImportUtilHolder.get().createLabItem(_shortName, _longName, _labor, _refMann, _refFrau, _unit,
					LabItemTyp.NUMERIC, Messages.Reflotron_Value_LabName, "50");
		}
	}

	public TransientLabResult fetchValue(Patient patient, String value, String flags, TimeTool date) {
		if (_labItem == null) {
			initialize();
		}
		IPatient iPatient = CoreModelServiceHolder.get().load(patient.getId(), IPatient.class).orElse(null);
		// do not set a flag or comment if none is given
		if (flags == null || flags.isEmpty()) {
			return new TransientLabResult.Builder(iPatient, _labor, _labItem, value).date(date)
					.build(LabImportUtilHolder.get());
		}

		String comment = StringUtils.EMPTY;
		int resultFlags = 0;
		if (flags.equals("1")) {
			// comment = Messages.getString("Value.High");
			resultFlags |= LabResultConstants.PATHOLOGIC;
		}
		if (flags.equals("2")) {
			// comment = Messages.getString("Value.Low");
			resultFlags |= LabResultConstants.PATHOLOGIC;
		}
		if (flags.equals("*") || flags.equals("E")) {
			comment = Messages.Reflotron_Value_Error;
		}

		return new TransientLabResult.Builder(iPatient, _labor, _labItem, value).date(date).comment(comment)
				.flags(Integer.valueOf(resultFlags)).build(LabImportUtilHolder.get());
	}
}