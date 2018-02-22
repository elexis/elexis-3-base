package ch.elexis.connect.reflotron.packages;

import java.util.ResourceBundle;

import ch.elexis.core.data.beans.ContactBean;
import ch.elexis.core.importer.div.importers.TransientLabResult;
import ch.elexis.core.model.LabResultConstants;
import ch.elexis.core.types.LabItemTyp;
import ch.elexis.core.ui.importer.div.importers.LabImportUtil;
import ch.elexis.data.LabItem;
import ch.elexis.data.Labor;
import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;

public class Value {
	private static final String BUNDLE_NAME = "ch.elexis.connect.reflotron.packages.valuetexts";
	
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	
	private static String getString(String paramName, String key){
		return RESOURCE_BUNDLE.getString(paramName + "." + key);
	}
	
	public static Value getValue(final String paramName, final String unit) throws PackageException{
		return new Value(paramName, unit);
	}
	
	String _shortName;
	String _longName;
	String _unit;
	LabItem _labItem;
	String _refMann;
	String _refFrau;
	Labor _labor;
	
	String warning = "";
	
	public String getWarning(){
		return this.warning;
	}
	
	public String get_shortName(){
		return _shortName;
	}
	
	public String get_longName(){
		return _longName;
	}
	
	Value(final String paramName, final String unit) throws PackageException{
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
	
	private void initialize(){
		_labor = LabImportUtil.getOrCreateLabor(Messages.getString("Value.LabKuerzel"));
		
		_labItem = LabImportUtil.getLabItem(_shortName, _labor);
		
		if (_labItem == null) {
			_labItem =
				new LabItem(_shortName, _longName, _labor, _refMann, _refFrau, _unit,
					LabItemTyp.NUMERIC, Messages.getString("Value.LabName"), "50");
		}
	}
	
	public TransientLabResult fetchValue(Patient patient, String value, String flags, TimeTool date){
		if (_labItem == null) {
			initialize();
		}
		
		LabImportUtil lu = new LabImportUtil();
		
		// do not set a flag or comment if none is given
		if (flags == null || flags.isEmpty()) {
			return new TransientLabResult.Builder(new ContactBean(patient), new ContactBean(_labor), _labItem, value).date(date)
				.build(lu);
		}
		
		String comment = "";
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
			comment = Messages.getString("Value.Error");
		}
		
		return new TransientLabResult.Builder(new ContactBean(patient), new ContactBean(_labor), _labItem, value).date(date)
			.comment(comment).flags(Integer.valueOf(resultFlags)).build(lu);
	}
}