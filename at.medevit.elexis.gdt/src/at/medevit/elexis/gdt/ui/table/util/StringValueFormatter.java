package at.medevit.elexis.gdt.ui.table.util;

import java.text.Format;
import java.text.ParseException;

/**
 * ValueFormatter for String.valueOf / java.text.Format.
 * 
 * @author Ralf Ebert <info@ralfebert.de>
 */
public class StringValueFormatter implements IValueFormatter<Object, String> {
	
	public static final StringValueFormatter INSTANCE = new StringValueFormatter();
	
	private final Format format;
	
	private StringValueFormatter(){
		this.format = null;
	}
	
	public StringValueFormatter(Format format){
		this.format = format;
	}
	
	public String format(Object obj){
		if (format == null)
			return String.valueOf(obj);
		return format.format(obj);
	}
	
	public Object parse(String str){
		if (format == null) {
			return str;
		}
		try {
			return format.parseObject(str);
		} catch (ParseException e) {
			throw new RuntimeException("INVALID VALUE: " + e.getMessage() + " for \"" + str + "\"",
				e);
		}
	}
}
