package at.medevit.medelexis.text.msword.plugin.util;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Create Objects of this class to do dispose on all created OleWrapper objects. OleWrapper will add
 * it self in constructor. If OleWrapper is a return value, remove it to prevent disposal.
 * 
 * @author thomashu
 * 
 */
public class OleWrapperManager {
	private HashSet<OleWrapper> managedOleWrappers = new HashSet<OleWrapper>();
	
	public synchronized void add(OleWrapper wrapper){
		managedOleWrappers.add(wrapper);
	}
	
	public synchronized void dispose(){
		Iterator<OleWrapper> iter = managedOleWrappers.iterator();
		while (iter.hasNext()) {
			OleWrapper wrapper = iter.next();
			if (wrapper != null && wrapper.oleObj != null) {
				GlobalOleWordWrapperManager.dispose(wrapper.oleObj);
				wrapper.oleObj.dispose();
			}
		}
		managedOleWrappers.clear();
	}
	
	/**
	 * Remove this OleWrapper from being disposed.
	 * 
	 * @param wrapper
	 */
	public synchronized void remove(OleWrapper wrapper){
		managedOleWrappers.remove(wrapper);
	}
}
