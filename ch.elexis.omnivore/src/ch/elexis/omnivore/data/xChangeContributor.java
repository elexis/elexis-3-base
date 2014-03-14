/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich
 * All rights reserved.
 * 
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/
package ch.elexis.omnivore.data;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.util.FileUtility;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.core.ui.exchange.IExchangeContributor;
import ch.elexis.core.ui.exchange.XChangeContainer;
import ch.elexis.core.ui.exchange.elements.DocumentElement;
import ch.elexis.core.ui.exchange.elements.MedicalElement;
import ch.rgw.tools.MimeTool;

public class xChangeContributor implements IExchangeContributor {
	
	public xChangeContributor(){
	// TODO Auto-generated constructor stub
	}
	
	public void exportHook(MedicalElement me){
		XChangeContainer container = me.getContainer();
		PersistentObject pat = container.getMapping(me);
		Query<DocHandle> qbe = new Query<DocHandle>(DocHandle.class);
		qbe.add("PatID", "=", pat.getId()); //$NON-NLS-1$ //$NON-NLS-2$
		qbe.add("Cat", "", null); //$NON-NLS-1$ //$NON-NLS-2$
		List<DocHandle> docs = qbe.execute();
		for (DocHandle dh : docs) {
			DocumentElement de = new DocumentElement();
			de.setDefaultXid(dh.getId());
			de.setTitle(dh.getTitle());
			de.setOriginator(CoreHub.actMandant);
			de.setDate(dh.getDate());
			de.addMeta("keywords", dh.getKeywords()); //$NON-NLS-1$
			de.addMeta("category", dh.getCategoryName()); //$NON-NLS-1$
			de.addMeta("plugin", "ch.elexis.omnivoredirect"); //$NON-NLS-1$ //$NON-NLS-2$
			de.setHint(Messages.xChangeContributor_thisIsAnOmnivoreDoc);
			de.setSubject(dh.getCategoryName());
			String mime = dh.getMimetype();
			if (!mime.matches("[a-zA-Z-]+/[a-zA-Z-]+2")) { //$NON-NLS-1$
				mime = FileUtility.getFileExtension(mime);
				String m2 = MimeTool.getMimeType(mime.substring(1));
				if (m2.length() > 0) {
					mime = m2;
				}
			}
			de.setMimetype(mime);
			byte[] cnt = dh.getContents();
			me.getSender().addBinary(de.getID(), cnt);
			me.addDocument(de);
			container.addChoice(de, dh.getTitle(), dh);
		}
	}
	
	public void importHook(XChangeContainer container, PersistentObject context){
	// TODO Auto-generated method stub
	
	}
	
	public void setInitializationData(IConfigurationElement arg0, String arg1, Object arg2)
		throws CoreException{
	// TODO Auto-generated method stub
	
	}
	
	public boolean init(MedicalElement me, boolean bExport){
		return true;
	}
	
}
