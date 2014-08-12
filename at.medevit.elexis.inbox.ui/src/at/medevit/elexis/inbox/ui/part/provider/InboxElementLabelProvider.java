package at.medevit.elexis.inbox.ui.part.provider;

import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import at.medevit.elexis.inbox.model.InboxElement;
import at.medevit.elexis.inbox.ui.part.model.PatientInboxElements;

public class InboxElementLabelProvider extends LabelProvider implements IColorProvider {
	
	private InboxElementUiExtension extension;
	
	public InboxElementLabelProvider(){
		extension = new InboxElementUiExtension();
	}
	
	@Override
	public String getText(Object element){
		if (element instanceof PatientInboxElements) {
			return ((PatientInboxElements) element).toString();
		} else if (element instanceof InboxElement) {
			String text = extension.getText((InboxElement) element);
			if (text != null) {
				return text;
			}
		}
		return super.getText(element);
	}
	
	@Override
	public Image getImage(Object element){
		if (element instanceof InboxElement) {
			Image image = extension.getImage((InboxElement) element);
			if (image != null) {
				return image;
			}
		}
		return null;
	}
	
	public Color getForeground(Object element){
		if (element instanceof InboxElement) {
			Color color = extension.getForeground((InboxElement) element);
			if (color != null) {
				return color;
			}
		}
		return Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	}
	
	public Color getBackground(Object element){
		if (element instanceof InboxElement) {
			Color color = extension.getBackground((InboxElement) element);
			if (color != null) {
				return color;
			}
		}
		return Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	}
}