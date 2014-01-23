package at.medevit.ch.artikelstamm.ui.internal;

import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.swt.widgets.Text;

public class DatabindingTextResizeConverter implements IConverter {
	
	private Text text;
	
	public DatabindingTextResizeConverter(Text text){
		this.text = text;
	}
	
	@Override
	public Object getFromType(){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object getToType(){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Object convert(Object fromObject){
		text.getParent().getParent().layout();
		
		return null;
	}
	
}
