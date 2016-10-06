package at.medevit.elexis.gdt.ui.dialog;

import java.util.Date;

import org.eclipse.core.databinding.conversion.IConverter;

import com.ibm.icu.text.SimpleDateFormat;

public class DateTimeTargetToModelUVS implements IConverter {
	
	@Override
	public Object getFromType(){
		return Date.class;
	}
	
	@Override
	public Object getToType(){
		return String.class;
	}
	
	@Override
	public Object convert(Object fromObject){
		Date dt = (Date) fromObject;
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		return sdf.format(dt);
	}
	
}
