package ch.elexis.connect.sysmex.packages;

public class PocH100iData extends KX21NData {
	
	protected Value getValue(final String paramName) throws PackageException{
		return Value.getValuePOCH(paramName);
	}
}
