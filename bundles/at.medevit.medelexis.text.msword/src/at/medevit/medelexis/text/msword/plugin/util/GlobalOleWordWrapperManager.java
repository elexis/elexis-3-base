package at.medevit.medelexis.text.msword.plugin.util;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.swt.ole.win32.OleAutomation;

public class GlobalOleWordWrapperManager {

	private static HashSet<OleAutomation> managedOleAutos = new HashSet<OleAutomation>();

	public static void add(OleAutomation auto) {
		synchronized (managedOleAutos) {
			managedOleAutos.add(auto);
		}
	}

	public static void remove(OleAutomation auto) {
		synchronized (managedOleAutos) {
			managedOleAutos.remove(auto);
		}
	}

	public static void dispose(OleAutomation auto) {
		synchronized (managedOleAutos) {
			managedOleAutos.remove(auto);
		}
	}

	public static void disposeAll() {
		synchronized (managedOleAutos) {
			Iterator<OleAutomation> iter = managedOleAutos.iterator();
			while (iter.hasNext()) {
				OleAutomation oleObj = iter.next();
				if (oleObj != null) {
					oleObj.dispose();
				}
			}
			managedOleAutos.clear();
		}
	}
}
