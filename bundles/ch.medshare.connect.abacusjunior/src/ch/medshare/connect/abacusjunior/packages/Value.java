package ch.medshare.connect.abacusjunior.packages;

import org.apache.commons.lang3.StringUtils;
import java.util.ResourceBundle;

import ch.elexis.core.importer.div.importers.TransientLabResult;
import ch.elexis.core.importer.div.service.holder.LabImportUtilHolder;
import ch.elexis.core.model.ILabItem;
import ch.elexis.core.model.ILaboratory;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.LabResultConstants;
import ch.elexis.core.types.LabItemTyp;
import ch.medshare.connect.abacusjunior.Messages;
import ch.rgw.tools.TimeTool;

public class Value {
	private static final String BUNDLE_NAME = "ch.medshare.connect.abacusjunior.packages.valuetexts";

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private static String getString(String paramName, String key) {
		return RESOURCE_BUNDLE.getString(paramName + "." + key);
	}

	public static Value getValue(String paramName) {
		return new Value(paramName);
	}

	ILaboratory _myLab;
	String _shortName;
	String _longName;
	String _unit;
	ILabItem _labItem;
	String _refMann;
	String _refFrau;

	public String get_shortName() {
		return _shortName;
	}

	public String get_longName() {
		return _longName;
	}

	Value(String paramName) {
		_shortName = getString(paramName, "kuerzel");
		_longName = getString(paramName, "text");
		_unit = getString(paramName, "unit");
		_refMann = getString(paramName, "refM");
		_refFrau = getString(paramName, "refF");
	}

	private void initialize() {
		_myLab = LabImportUtilHolder.get().getOrCreateLabor(Messages.AbacusJunior_Value_LabKuerzel);
		_labItem = LabImportUtilHolder.get().getLabItem(_shortName, _myLab);

		if (_labItem == null) {
			_labItem = LabImportUtilHolder.get().createLabItem(_shortName, _longName, _myLab, _refMann, _refFrau, _unit,
					LabItemTyp.NUMERIC, Messages.AbacusJunior_Value_LabName, "50");
		}
	}

	public TransientLabResult fetchValue(IPatient patient, String value, String flags, TimeTool date) {
		if (_labItem == null) {
			initialize();
		}
		// do not set a flag or comment if none is given
		if (flags == null || flags.isEmpty()) {
			return new TransientLabResult.Builder(patient, _myLab, _labItem, value).date(date)
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
			comment = Messages.AbacusJunior_Value_Error;
		}

		return new TransientLabResult.Builder(patient, _myLab, _labItem, value).date(date).comment(comment)
				.flags(Integer.valueOf(resultFlags)).build(LabImportUtilHolder.get());

	}
}