/*******************************************************************************
 * Copyright (c) 2006-2016, G. Weirich
 * All rights reserved.
 * 
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/
package ch.elexis.omnivore.data;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.FileUtility;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.exchange.IExchangeContributor;
import ch.elexis.core.ui.exchange.XChangeContainer;
import ch.elexis.core.ui.exchange.elements.DocumentElement;
import ch.elexis.core.ui.exchange.elements.MedicalElement;
import ch.elexis.data.PersistentObject;
import ch.elexis.omnivore.Constants;
import ch.elexis.omnivore.data.service.internal.OmnivoreModelServiceHolder;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.rgw.tools.MimeTool;

public class xChangeContributor implements IExchangeContributor {
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	
	public void exportHook(MedicalElement me){
		XChangeContainer container = me.getContainer();
		PersistentObject pat = container.getMapping(me);
		IPatient iPatient =
			CoreModelServiceHolder.get().load(pat.getId(), IPatient.class).orElse(null);
		if (iPatient != null) {
			IQuery<IDocumentHandle> query =
				OmnivoreModelServiceHolder.get().getQuery(IDocumentHandle.class);
			query.and("kontakt", COMPARATOR.EQUALS, iPatient);
			List<IDocumentHandle> docs = query.execute();
			for (IDocumentHandle dh : docs) {
				try {
				DocumentElement de = new DocumentElement();
				de.setDefaultXid(dh.getId());
				de.setTitle(dh.getTitle());
				de.setOriginator(ElexisEventDispatcher.getSelectedMandator());
				de.setDate(dateFormat.format(dh.getCreated()));
				de.addMeta("Keywords", dh.getKeywords());
				de.addMeta("category", dh.getCategory().getName()); //$NON-NLS-1$
				de.addMeta("plugin", Constants.PLUGIN_ID); //$NON-NLS-1$
				de.setHint(Messages.xChangeContributor_thisIsAnOmnivoreDoc);
				de.setSubject(dh.getCategory().getName());
				String mime = dh.getMimeType();
				if (!mime.matches("[a-zA-Z-]+/[a-zA-Z-]+2")) { //$NON-NLS-1$
					mime = FileUtility.getFileExtension(mime);
					String m2 = MimeTool.getMimeType(mime.substring(1));
					if (m2.length() > 0) {
						mime = m2;
					}
				}
				de.setMimetype(mime);
				byte[] cnt = IOUtils.toByteArray(dh.getContent());
				me.getSender().addBinary(de.getID(), cnt);
				me.addDocument(de);
				container.addChoice(de, dh.getTitle(), dh);
				} catch (Exception e) {
					LoggerFactory.getLogger(getClass()).error("Error exporting document", e);
				}
			}
		}
	}
	
	public void importHook(XChangeContainer container, PersistentObject context){}
	
	public void setInitializationData(IConfigurationElement arg0, String arg1, Object arg2)
		throws CoreException{}
	
	public boolean init(MedicalElement me, boolean bExport){
		return true;
	}
	
}
