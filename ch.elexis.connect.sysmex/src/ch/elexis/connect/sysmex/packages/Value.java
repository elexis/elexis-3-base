package ch.elexis.connect.sysmex.packages;

import java.util.Collections;
import java.util.ResourceBundle;

import ch.elexis.core.ui.importer.div.importers.DefaultLabImportUiHandler;
import ch.elexis.core.ui.importer.div.importers.LabImportUtil;
import ch.elexis.core.ui.importer.div.importers.LabImportUtil.TransientLabResult;
import ch.elexis.data.LabItem;
import ch.elexis.data.Labor;
import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;

public class Value {
	private static final String KX21_BUNDLE_NAME =
		"ch.elexis.connect.sysmex.packages.valuetexts_KX21"; //$NON-NLS-1$
	private static final String KX21N_BUNDLE_NAME =
		"ch.elexis.connect.sysmex.packages.valuetexts_KX21N"; //$NON-NLS-1$
	private static final String POCH_BUNDLE_NAME =
		"ch.elexis.connect.sysmex.packages.valuetexts_pocH"; //$NON-NLS-1$
	
	private final ResourceBundle _bundle;
	Labor _myLab;
	String _shortName;
	String _longName;
	String _unit;
	LabItem _labItem;
	String _refMann;
	String _refFrau;
	
	public static Value getValueKX21(final String paramName) throws PackageException{
		return new Value(paramName, KX21_BUNDLE_NAME);
	}
	
	public static Value getValueKX21N(final String paramName) throws PackageException{
		return new Value(paramName, KX21N_BUNDLE_NAME);
	}
	
	public static Value getValuePOCH(final String paramName) throws PackageException{
		return new Value(paramName, POCH_BUNDLE_NAME);
	}
	
	private Value(final String paramName, final String bundleName) throws PackageException{
		_bundle = ResourceBundle.getBundle(bundleName);
		_shortName = getString(paramName, "kuerzel"); //$NON-NLS-1$
		_longName = getString(paramName, "text"); //$NON-NLS-1$
		_unit = getString(paramName, "unit"); //$NON-NLS-1$
		_refMann = getString(paramName, "refM");//$NON-NLS-1$
		_refFrau = getString(paramName, "refF");//$NON-NLS-1$
	}
	
	private void initialize(){
		_myLab = LabImportUtil.getOrCreateLabor(Messages.getString("Value.LabKuerzel"));
		
		_labItem = LabImportUtil.getLabItem(_shortName, _myLab);
		if (_labItem == null) {
			_labItem =
				new LabItem(_shortName, _longName, _myLab, _refMann, _refFrau, _unit,
					LabItem.typ.NUMERIC, Messages.getString("Value.LabName"), "50");
		}
	}
	
	public void fetchValue(Patient patient, String value, String flags, TimeTool date){
		if (_labItem == null) {
			initialize();
		}
		
		TransientLabResult tLabResult =
			new TransientLabResult.Builder(patient, _myLab, _labItem, value).date(date).build();
		LabImportUtil.importLabResults(Collections.singletonList(tLabResult),
			new DefaultLabImportUiHandler());
	}
	
	public String get_shortName(){
		return _shortName;
	}
	
	public String get_longName(){
		return _longName;
	}
	
	private String getString(String paramName, String key){
		return _bundle.getString(paramName + "." + key); //$NON-NLS-1$
	}
}