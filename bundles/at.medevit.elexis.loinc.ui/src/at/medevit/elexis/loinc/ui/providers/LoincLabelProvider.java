package at.medevit.elexis.loinc.ui.providers;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import at.medevit.elexis.loinc.model.LoincCode;

public class LoincLabelProvider extends LabelProvider implements ILabelProvider {

	@Override
	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getText(Object element) {
		LoincCode code = (LoincCode) element;
		return code.getLabel();
	}
}
