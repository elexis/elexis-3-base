package ch.elexis.connect.sysmex.packages;

import ch.rgw.tools.TimeTool;

public class KX21NData extends AbstractData {
	
	public int getSize(){
		return 129;
	}
	
	protected int getDataIndex(){
		return 34;
	}
	
	protected TimeTool getDate(final String content){
		int year = Integer.parseInt(content.substring(3, 7));
		int month = Integer.parseInt(content.substring(7, 9));
		int day = Integer.parseInt(content.substring(9, 11));
		TimeTool timetool = new TimeTool();
		timetool.set(year, month - 1, day);
		return timetool;
	}
	
	protected String getRDWSD(final String content){
		int pos = getDataIndex() + 70;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected String getRDWCV(final String content){
		int pos = getDataIndex() + 75;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected String getPDW(final String content){
		int pos = getDataIndex() + 80;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected String getMPV(final String content){
		int pos = getDataIndex() + 85;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected String getPLCR(final String content){
		int pos = getDataIndex() + 90;
		return getValueStr(content, pos, "XXX.XF"); //$NON-NLS-1$
	}
	
	protected Value getValue(final String paramName) throws PackageException{
		return Value.getValueKX21N(paramName);
	}
}
