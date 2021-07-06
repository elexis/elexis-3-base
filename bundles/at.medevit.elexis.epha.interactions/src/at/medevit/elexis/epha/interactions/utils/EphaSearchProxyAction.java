package at.medevit.elexis.epha.interactions.utils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.StyleRange;

import at.medevit.elexis.epha.interactions.preference.EphaConstants;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.text.IRichTextDisplay;
import ch.elexis.core.ui.util.IKonsExtension;

public class EphaSearchProxyAction extends Action implements IKonsExtension {
	
	private EphaSearchAction webSearchAction;
	private EphaApiSearchAction apiSearchAction;
	
	public EphaSearchProxyAction(){
		webSearchAction = new EphaSearchAction();
		apiSearchAction = new EphaApiSearchAction();
	}
	
	@Override
	public void run(){
		if (ConfigServiceHolder.get().get(EphaConstants.CFG_USE_REST, false)) {
			apiSearchAction.run();
		} else {
			webSearchAction.run();
		}
	}
	
	public String connect(IRichTextDisplay tf){
		return "at.medevit.elexis.epha.interactions.EphaSearchAction"; //$NON-NLS-1$
	}
	
	public boolean doLayout(StyleRange n, String provider, String id){
		return false;
	}
	
	public boolean doXRef(String refProvider, String refID){
		return false;
	}
	
	public IAction[] getActions(){
		return new IAction[] {
			this
		};
	}
	
	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName,
		Object data) throws CoreException{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void insert(Object o, int pos){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void removeXRef(String refProvider, String refID){
		// TODO Auto-generated method stub
		
	}
}
