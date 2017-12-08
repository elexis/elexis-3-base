package ch.elexis.connect.afinion.packages;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Diese Klasse ist Platzhalter für eine SubRecord
 * 
 * @author immi
 * 
 */
public class SubRecordPart extends AbstractPart {
	private float min;
	private float max;
	private float result;
	private int decimals;
	private boolean valid;
	private boolean outOfRange;
	private String unit;
	private String kuerzel;
	
	public SubRecordPart(final byte[] bytes, final int pos){
		parse(bytes, pos);
	}
	
	public void parse(final byte[] bytes, final int pos){
		min = getFloat(bytes, pos);
		max = getFloat(bytes, pos + 4);
		result = getFloat(bytes, pos + 8);
		decimals = getInteger(bytes, pos + 12);
		valid = (getInteger(bytes, pos + 16) > 0);
		unit = getString(bytes, pos + 20, 9);
		kuerzel = getString(bytes, pos + 29, 9);
		
		outOfRange = false;
		if (result < min || result > max) {
			outOfRange = true;
		}
	}
	
	@Override
	public int length(){
		return 40;
	}
	
	public double getMin(){
		return min;
	}
	
	public double getMax(){
		return max;
	}
	
	public double getResult(){
		return result;
	}
	
	public String getResultStr(){
		if (result < min) {
			return "<" + new DecimalFormat("#.##").format(min);
		}
		if (result > max) {
			return ">" + new DecimalFormat("#.##").format(max);
		}
		return new DecimalFormat("#.##").format(result);
	}
	
	public int getDecimals(){
		return decimals;
	}
	
	public boolean isValid(){
		return valid;
	}
	
	public boolean isOutOfRange(){
		return outOfRange;
	}
	
	public String getUnit(){
		return unit;
	}
	
	public String getKuerzel(){
		return kuerzel;
	}
	
	public String toString(){
		NumberFormat nf = new DecimalFormat("###.##");
		
		String str = "";
		str += " " + kuerzel + ": " + nf.format(result) + unit + ";";
		str += " Min:" + nf.format(min) + ";";
		str += " Max:" + nf.format(max) + ";";
		str += " Decimals:" + decimals + ";";
		str += " OutOfRange:" + outOfRange + ";";
		str += " Valid:" + valid + ";";
		return str;
	}
}
