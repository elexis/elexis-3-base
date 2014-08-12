package at.medevit.elexis.inbox.ui.part.provider;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;

import at.medevit.elexis.inbox.model.InboxElement;

public interface IInboxElementUiProvider {
	/**
	 * Image that will be placed on the filter action.
	 * 
	 * @return ImageDescriptor or null
	 */
	public ImageDescriptor getFilterImage();
	
	/**
	 * Filter that will be applied with the filter action.
	 * 
	 * @return ViewerFilter or null
	 */
	public ViewerFilter getFilter();
	
	/**
	 * LabelProvider used by the inbox viewer.
	 * 
	 * @return LabelProvider or null
	 */
	public LabelProvider getLabelProvider();
	
	/**
	 * ColorProvider used by the inbox viewer.
	 * 
	 * @return IColorProvider or null
	 */
	public IColorProvider getColorProvider();
	
	/**
	 * Test if this provider shall be used for the element.
	 * 
	 * @param element
	 * @return
	 */
	public boolean isProviderFor(InboxElement element);
	
	/**
	 * Method called when element is double clicked.
	 * 
	 * @param element
	 */
	public void doubleClicked(InboxElement element);
}
