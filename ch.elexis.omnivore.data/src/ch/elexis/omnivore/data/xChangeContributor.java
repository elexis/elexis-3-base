/*******************************************************************************
 * Copyright (c) 2006-2016, G. Weirich
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

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.FileUtility;
import ch.elexis.core.ui.exchange.IExchangeContributor;
import ch.elexis.core.ui.exchange.XChangeContainer;
import ch.elexis.core.ui.exchange.elements.DocumentElement;
import ch.elexis.core.ui.exchange.elements.MedicalElement;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.omnivore.Constants;
import ch.rgw.tools.MimeTool;

public class xChangeContributor implements IExchangeContributor {
	
	public void exportHook(MedicalElement me){
		XChangeContainer container = me.getContainer();
		PersistentObject pat = container.getMapping(me);
		Query<DocHandle> qbe = new Query<DocHandle>(DocHandle.class);
		qbe.add(DocHandle.FLD_PATID, Query.EQUALS, pat.getId());
		qbe.add(DocHandle.FLD_CAT, "", null); //$NON-NLS-1$
		List<DocHandle> docs = qbe.execute();
		for (DocHandle dh : docs) {
			DocumentElement de = new DocumentElement();
			de.setDefaultXid(dh.getId());
			de.setTitle(dh.getTitle());
			de.setOriginator(ElexisEventDispatcher.getSelectedMandator());
			de.setDate(dh.getDate());
			de.addMeta(DocHandle.FLD_KEYWORDS, dh.getKeywords());
			de.addMeta("category", dh.getCategoryName()); //$NON-NLS-1$
			de.addMeta("plugin", Constants.PLUGIN_ID); //$NON-NLS-1$
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
	
	public void importHook(XChangeContainer container, PersistentObject context){}
	
	public void setInitializationData(IConfigurationElement arg0, String arg1, Object arg2)
		throws CoreException{}
	
	public boolean init(MedicalElement me, boolean bExport){
		return true;
	}
	
}
