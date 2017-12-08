/*******************************************************************************
 * Copyright (c) 2008-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/
package ch.elexis.base.konsextension.bildanzeige;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.jdom.Element;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.exchange.IExchangeContributor;
import ch.elexis.core.ui.exchange.XChangeContainer;
import ch.elexis.core.ui.exchange.elements.DocumentElement;
import ch.elexis.core.ui.exchange.elements.MedicalElement;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.images.Bild;
import ch.rgw.tools.XMLTool;

public class XChangeContributor implements IExchangeContributor {
	public static final String PLUGIN_ID = "ch.elexis.base.konsextension.bildanzeige"; //$NON-NLS-1$
	
	public void exportHook(MedicalElement me){
		
		Patient p = (Patient) me.getContainer().getMapping(me);
		Query<Bild> qbe = new Query<Bild>(Bild.class);
		qbe.add("PatID", "=", p.getId()); //$NON-NLS-1$ //$NON-NLS-2$
		List<Bild> images = qbe.execute();
		for (Bild img : images) {
			byte[] data = img.getData();
			if (data != null && data.length > 0) {
				DocumentElement de = new DocumentElement();
				de.setMimetype("image/jpeg"); //$NON-NLS-1$
				de.setDate(XMLTool.dateToXmlDate(img.getDate()));
				de.setTitle(img.getTitle());
				de.setOriginator(CoreHub.actMandant);
				de.setHint(img.getInfo());
				de.setDefaultXid(img.getId());
				me.getSender().addBinary(img.getId(), data);
				me.getContainer().addChoice(de.getElement(), img.getLabel());
				me.addDocument(de);
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public void importHook(XChangeContainer container, PersistentObject context){
		String rootpath = container.getProperty("ROOTPATH"); //$NON-NLS-1$
		List<Element> records = container.getElements(rootpath + "/records/record"); //$NON-NLS-1$
		for (Element re : records) {
			/*
			 * List<Element> xrefs=re.getChildren("xref", container.getNamespace()); Samdas smd=new
			 * Samdas(k.getEintrag().getHead()); if(xrefs!=null){ for(Element e:xrefs){ String
			 * type=e.getAttributeValue("type"); if(type.equalsIgnoreCase("image/jpeg") ||
			 * type.equalsIgnoreCase("image/png")){ String content=e.getAttributeValue("content");
			 * String id=e.getAttributeValue("id"); byte[] data=null; if(content.equals("inline")){
			 * BASE64Decoder b64=new BASE64Decoder(); try { data=b64.decodeBuffer(e.getText()); }
			 * catch (IOException e1) { ExHandler.handle(e1); continue; } }else{
			 * data=container.getBinary(id); } Patient pat=k.getFall().getPatient(); Bild bild=new
			 * Bild(pat,"type",data); String spos=e.getAttributeValue("pos"); int pos=(spos==null) ?
			 * 0 : Integer.parseInt(spos); String slen=e.getAttributeValue("len"); int
			 * len=(slen==null) ? 0 : Integer.parseInt(slen); Samdas.Record rec=smd.getRecord();
			 * Samdas.XRef xr=new Samdas.XRef("bildanzeige",bild.getId(),pos,len); rec.add(xr);
			 * k.updateEintrag(smd.toString(), true); } } }
			 */
		}
		
	}
	
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
		throws CoreException{
		// Nothing
		
	}
	
	public boolean init(MedicalElement me, boolean bExport){
		if (bExport) {
			
		}
		return true;
	}
	
}
