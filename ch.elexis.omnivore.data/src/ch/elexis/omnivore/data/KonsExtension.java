package ch.elexis.omnivore.data;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.StyleRange;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.text.IRichTextDisplay;
import ch.elexis.core.ui.util.IKonsExtension;
import ch.elexis.data.Konsultation;
import ch.elexis.omnivore.Constants;

public class KonsExtension implements IKonsExtension {
	IRichTextDisplay mine;
	
	static final String DOCHANDLE_TITLE = "Dokument: ";
	
	@Override
	public String connect(IRichTextDisplay tf){
		mine = tf;
		mine.addDropReceiver(DocHandle.class, this);
		return Constants.PLUGIN_ID;
	}
	
	@Override
	public boolean doLayout(StyleRange styleRange, String provider, String id){
		styleRange.background = UiDesk.getColor(UiDesk.COL_LIGHTBLUE);
		return true;
	}
	
	@Override
	public boolean doXRef(String refProvider, String refID){
		DocHandle handle = DocHandle.load(refID);
		if (handle.exists()) {
			handle.execute();
			ElexisEventDispatcher.fireSelectionEvent(handle);
		}
		return true;
	}
	
	@Override
	public void insert(Object o, int pos){
		if (o instanceof DocHandle) {
			DocHandle handle = (DocHandle) o;
			final Konsultation k =
				(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
			
			mine.insertXRef(pos, DOCHANDLE_TITLE + handle.getLabel(), Constants.PLUGIN_ID,
				handle.getId());
			k.updateEintrag(mine.getContentsAsXML(), false);
			ElexisEventDispatcher.update(k);
		}
		
	}
	
	@Override
	public IAction[] getActions(){
		return null;
	}
	
	@Override
	public void removeXRef(String refProvider, String refID){}
	
	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName,
		Object data) throws CoreException{}
	
}
