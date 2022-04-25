package at.medevit.elexis.outbox.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.program.Program;

import at.medevit.elexis.outbox.model.IOutboxElement;
import at.medevit.elexis.outbox.model.OutboxElementType;
import at.medevit.elexis.outbox.ui.part.provider.IOutboxElementUiProvider;
import ch.elexis.core.ui.icons.Images;

public class DefaultOutboxElementLabelProvider implements IOutboxElementUiProvider {

	private DefaultLabelProvider labelProvider;

	public DefaultOutboxElementLabelProvider() {
		labelProvider = new DefaultLabelProvider();
	}

	@Override
	public ImageDescriptor getFilterImage() {
		return null;
	}

	@Override
	public ViewerFilter getFilter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LabelProvider getLabelProvider() {
		return labelProvider;
	}

	@Override
	public IColorProvider getColorProvider() {
		return null;
	}

	@Override
	public boolean isProviderFor(IOutboxElement element) {
		OutboxElementType elementType = OutboxElementType.parseType(element.getUri());
		return OutboxElementType.FILE.equals(elementType);
	}

	@Override
	public void doubleClicked(IOutboxElement element) {
		OutboxElementType elementType = OutboxElementType.parseType(((IOutboxElement) element).getUri());
		if (OutboxElementType.FILE.equals(elementType)) {
			Program.launch(((IOutboxElement) element).getUri());
		}
	}

	class DefaultLabelProvider extends LabelProvider {
		@Override
		public String getText(Object element) {
			return ((IOutboxElement) element).getLabel();
		}

		@Override
		public Image getImage(Object element) {
			OutboxElementType elementType = OutboxElementType.parseType(((IOutboxElement) element).getUri());
			if (OutboxElementType.FILE.equals(elementType)) {
				return Images.IMG_DOCUMENT.getImage();
			}
			return null;
		}
	}

}
