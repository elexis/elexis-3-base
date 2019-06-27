package ch.elexis.omnivore.ui.inbox;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.wb.swt.ResourceManager;

import at.medevit.elexis.inbox.model.InboxElement;
import at.medevit.elexis.inbox.ui.part.provider.IInboxElementUiProvider;
import ch.elexis.omnivore.data.DocHandle;

public class DocHandleUiProvider implements IInboxElementUiProvider {
	private DocHandleLabelProvider labelProvider;
	private DocHandleViewerFilter filter;
	
	public DocHandleUiProvider(){
		labelProvider = new DocHandleLabelProvider();
		filter = new DocHandleViewerFilter();
	}
	
	@Override
	public ImageDescriptor getFilterImage(){
		return ResourceManager.getPluginImageDescriptor("ch.elexis.omnivore.ui", "icons/fressen.gif");
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
		return null;
	}
	
	@Override
	public boolean isProviderFor(InboxElement element){
		Object obj = element.getObject();
		if (obj instanceof DocHandle) {
			return true;
		}
		return false;
	}
	
	@Override
	public void doubleClicked(InboxElement element){
		Object obj = element.getObject();
		if (isProviderFor(element)) {
			DocHandle document = (DocHandle) obj;
			document.execute();
		}
	}
	
}
