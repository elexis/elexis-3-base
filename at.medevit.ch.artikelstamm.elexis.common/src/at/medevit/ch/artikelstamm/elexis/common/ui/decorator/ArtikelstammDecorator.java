package at.medevit.ch.artikelstamm.elexis.common.ui.decorator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wb.swt.ResourceManager;

import ch.artikelstamm.elexis.common.ArtikelstammItem;

public class ArtikelstammDecorator implements ILightweightLabelDecorator {
	
	private static ImageDescriptor warning = PlatformUI.getWorkbench().getSharedImages()
		.getImageDescriptor(ISharedImages.IMG_DEC_FIELD_WARNING);
	
	private static ImageDescriptor ol_gGruen = ResourceManager.getPluginImageDescriptor(
		"at.medevit.ch.artikelstamm.ui", "rsc/icons/generic_ol_white.png");
	private static ImageDescriptor ol_oBlue = ResourceManager.getPluginImageDescriptor(
		"at.medevit.ch.artikelstamm.ui", "rsc/icons/original_ol_white.png");
	
	@Override
	public void addListener(ILabelProviderListener listener){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void dispose(){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean isLabelProperty(Object element, String property){
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void removeListener(ILabelProviderListener listener){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void decorate(Object element, IDecoration decoration){
		ArtikelstammItem item = (ArtikelstammItem) element;
		if (item.getExFactoryPrice() == 0.0 && item.getPublicPrice() == 0.0) {
			decoration.addOverlay(warning, IDecoration.TOP_LEFT);
		}
		if (item.isBlackBoxed()) {
			
		}
		String genericType = item.getGenericType();
		if (genericType.startsWith("G")) {
			decoration.addOverlay(ol_gGruen, IDecoration.BOTTOM_LEFT);
		} else if (genericType.startsWith("O")) {
			decoration.addOverlay(ol_oBlue, IDecoration.BOTTOM_LEFT);
		}
	}
}
