package at.medevit.ch.artikelstamm.elexis.common.ui.decorator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wb.swt.ResourceManager;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import ch.elexis.core.ui.UiDesk;

public class ArtikelstammDecorator implements ILightweightLabelDecorator {

	private static ImageDescriptor warning = PlatformUI.getWorkbench().getSharedImages()
			.getImageDescriptor(ISharedImages.IMG_DEC_FIELD_WARNING);

	private static ImageDescriptor ol_gGruen = ResourceManager.getPluginImageDescriptor("at.medevit.ch.artikelstamm.ui",
			"rsc/icons/generic_ol_white.png");
	private static ImageDescriptor ol_oBlue = ResourceManager.getPluginImageDescriptor("at.medevit.ch.artikelstamm.ui",
			"rsc/icons/original_ol_white.png");

	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void decorate(Object element, IDecoration decoration) {
		IArtikelstammItem item = (IArtikelstammItem) element;
		if (item.getPurchasePrice().isZero() && item.getSellingPrice().isZero()) {
			decoration.addOverlay(warning, IDecoration.TOP_LEFT);
		}
		if (item.isBlackBoxed()) {
			decoration.setForegroundColor(UiDesk.getColor(UiDesk.COL_WHITE));
			decoration.setBackgroundColor(UiDesk.getColor(UiDesk.COL_BLACK));
		}
		String genericType = item.getGenericType();
		if (genericType != null) {
			if (genericType.startsWith("G")) {
				decoration.addOverlay(ol_gGruen, IDecoration.BOTTOM_LEFT);
			} else if (genericType.startsWith("O")) {
				decoration.addOverlay(ol_oBlue, IDecoration.BOTTOM_LEFT);
			}
		}
	}
}
