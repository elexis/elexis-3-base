package ch.elexis.connect.sysmex.packages;

import ch.elexis.connect.sysmex.Preferences;
import ch.elexis.core.data.activator.CoreHub;
import ch.rgw.tools.TimeTool;

public class KX21Data extends AbstractData {
	
	public int getSize(){
		return 119;
	}
	
	protected int getDataIndex(){
		return 29;
	}
	
	protected TimeTool getDate(final String content){
		int year = Integer.parseInt(content.substring(3, 5));
		int month = Integer.parseInt(content.substring(5, 7));
		int day = Integer.parseInt(content.substring(7, 9));
		TimeTool timetool = new TimeTool();
		timetool.set(year, month - 1, day);
		return timetool;
	}
	
	private boolean isRdwSd(){
		String rdw = CoreHub.localCfg.get(Preferences.RDW_TYP, Preferences.RDW_SD);
		return Preferences.RDW_SD.equals(rdw);
	}
	
	protected String getRDWSD(final String content){
		if (!isRdwSd()) {
			return null;
		}
		int pos = getDataIndex() + 70;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
		
	}
	
	protected String getRDWCV(final String content){
		if (isRdwSd()) {
			return null;
		}
		int pos = getDataIndex() + 70;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected String getPDW(final String content){
		int pos = getDataIndex() + 75;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected String getMPV(final String content){
		int pos = getDataIndex() + 80;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected String getPLCR(final String content){
		int pos = getDataIndex() + 85;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected Value getValue(final String paramName) throws PackageException{
		return Value.getValueKX21(paramName);
	}
}
