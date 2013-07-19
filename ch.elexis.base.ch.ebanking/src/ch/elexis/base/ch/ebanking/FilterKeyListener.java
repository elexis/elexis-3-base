package ch.elexis.base.ch.ebanking;

import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Text;

/**
 * Listener for the txtFilter text control. Updates the search text filter in
 * {@link ContactSelectorTextFieldViewerFilter} on key press.
 */
public class FilterKeyListener extends KeyAdapter {
	private Text text;
	private StructuredViewer viewer;
	
	FilterKeyListener(Text filterTxt, StructuredViewer viewer){
		text = filterTxt;
		this.viewer = viewer;
	}
	
	public void keyReleased(KeyEvent ke){
		String txt = text.getText();
		
		if (txt.length() > 1) {
			FilterSearchField.getInstance().setSearchText(txt);
			viewer.getControl().setRedraw(false);
			viewer.refresh();
			viewer.getControl().setRedraw(true);
		} else {
			FilterSearchField.getInstance().setSearchText(null);
			viewer.getControl().setRedraw(false);
			viewer.refresh();
			viewer.getControl().setRedraw(true);
		}
	}
}