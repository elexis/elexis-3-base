package ch.elexis.connect.reflotron.packages;

import java.util.List;
import java.util.ResourceBundle;

import ch.elexis.data.LabItem;
import ch.elexis.data.LabResult;
import ch.elexis.data.Labor;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
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
		Labor myLab;
		
		Query<Labor> qbe = new Query<Labor>(Labor.class);
		qbe.add("Kuerzel", "LIKE", "%" + Messages.getString("Value.LabKuerzel") + "%");
		List<Labor> list = qbe.execute();
		
		if (list.size() < 1) {
			myLab =
				new Labor(Messages.getString("Value.LabKuerzel"),
					Messages.getString("Value.LabName"));
		} else {
			myLab = list.get(0);
		}
		
		Query<LabItem> qli = new Query<LabItem>(LabItem.class);
		qli.add("kuerzel", "=", _shortName);
		qli.and();
		qli.add("LaborID", "=", myLab.get("ID"));
		
		List<LabItem> itemList = qli.execute();
		if (itemList.size() < 1) {
			_labItem =
				new LabItem(_shortName, _longName, myLab, _refMann, _refFrau, _unit,
					LabItem.typ.NUMERIC, Messages.getString("Value.LabName"), "50");
		} else {
			_labItem = itemList.get(0);
		}
	}
	
	public void fetchValue(Patient patient, String value, String flags, TimeTool date){
		if (_labItem == null) {
			initialize();
		}
		
		String comment = "";
		int resultFlags = 0;
		if (flags.equals("1")) {
			// comment = Messages.getString("Value.High");
			resultFlags |= LabResult.PATHOLOGIC;
		}
		if (flags.equals("2")) {
			// comment = Messages.getString("Value.Low");
			resultFlags |= LabResult.PATHOLOGIC;
		}
		if (flags.equals("*") || flags.equals("E")) {
			comment = Messages.getString("Value.Error");
		}
		
		LabResult lr = new LabResult(patient, date, _labItem, value, comment);
		lr.set("Quelle", Messages.getString("Value.LabKuerzel"));
		lr.setFlag(resultFlags, true);
	}
}