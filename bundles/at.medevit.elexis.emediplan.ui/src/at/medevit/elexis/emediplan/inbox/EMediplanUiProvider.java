package at.medevit.elexis.emediplan.inbox;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.wb.swt.ResourceManager;

import at.medevit.elexis.emediplan.Startup;
import at.medevit.elexis.inbox.model.InboxElement;
import at.medevit.elexis.inbox.ui.part.provider.IInboxElementUiProvider;
import ch.elexis.data.NamedBlob;


public class EMediplanUiProvider implements IInboxElementUiProvider {
	private EMediplanLabelProvider labelProvider;
	private EMediplanViewerFilter filter;
	
	public EMediplanUiProvider(){
		labelProvider = new EMediplanLabelProvider();
		filter = new EMediplanViewerFilter();
	}
	
	@Override
	public ImageDescriptor getFilterImage(){
		return ResourceManager.getPluginImageDescriptor("at.medevit.elexis.emediplan.ui",
			"rsc/logo.png");
	}
	
	@Override
	public ViewerFilter getFilter(){
		return filter;
	}
	
	@Override
	public LabelProvider getLabelProvider(){
		return labelProvider;
	}
	
	@Override
	public IColorProvider getColorProvider(){
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean isProviderFor(InboxElement element){
		Object obj = element.getObject();
		if (obj instanceof NamedBlob && ((NamedBlob) obj).getId().startsWith("Med_")) {
			
			return true;
		}
		return false;
	}
	
	@Override
	public void doubleClicked(InboxElement element){
		Object obj = element.getObject();
		if (isProviderFor(element)) {
			NamedBlob document = (NamedBlob) obj;
			Startup.openEMediplanImportDialog(document.getString(), element.getPatient().getId());
		}
	}
	
}
