/*******************************************************************************
 * Copyright (c) 2006-2016, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/

package ch.elexis.omnivore.data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ch.elexis.core.data.interfaces.text.IOpaqueDocument;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.rgw.tools.RegexpFilter;
import ch.rgw.tools.TimeSpan;
import ch.rgw.tools.TimeTool;

public class DocumentManagement implements IDocumentManager {
	
	public boolean addCategorie(String categorie){
		DocHandle.addMainCategory(categorie);
		return true;
	}
	
	public String addDocument(IOpaqueDocument doc) throws ElexisException{
		return addDocument(doc, false);
	}
	
	public String addDocument(IOpaqueDocument doc, boolean automaticBilling) throws ElexisException{
		DocHandle dh = new DocHandle(doc);
		if (automaticBilling) {
			if (AutomaticBilling.isEnabled()) {
				AutomaticBilling billing = new AutomaticBilling(dh);
				billing.bill();
			}
		}
		return dh.getId();
	}
	
	public String[] getCategories(){
		return DocHandle.getMainCategoryNames().toArray(new String[0]);
	}
	
	public InputStream getDocument(String id){
		DocHandle dh = DocHandle.load(id);
		byte[] cnt = dh.getContents();
		ByteArrayInputStream bais = new ByteArrayInputStream(cnt);
		return bais;
	}
	
	@Override
	public List<IOpaqueDocument> listDocuments(final Patient pat, final String categoryMatch,
		final String titleMatch, final String keywordMatch, final TimeSpan dateMatch,
		final String contentsMatch) throws ElexisException{
		Query<DocHandle> qbe = new Query<DocHandle>(DocHandle.class);
		if (pat != null) {
			qbe.add(DocHandle.FLD_PATID, Query.EQUALS, pat.getId());
		}
		if (dateMatch != null) {
			String from = dateMatch.from.toString(TimeTool.DATE_COMPACT);
			String until = dateMatch.until.toString(TimeTool.DATE_COMPACT);
			qbe.add(DocHandle.FLD_DATE, Query.GREATER_OR_EQUAL, from);
			qbe.add(DocHandle.FLD_DATE, Query.LESS_OR_EQUAL, until);
		}
		if (titleMatch != null) {
			if (titleMatch.matches("/.+/")) { //$NON-NLS-1$
				qbe.addPostQueryFilter(
					new RegexpFilter(titleMatch.substring(1, titleMatch.length() - 1)));
			} else {
				qbe.add(DocHandle.FLD_TITLE, Query.EQUALS, titleMatch);
			}
		}
		if (keywordMatch != null) {
			if (keywordMatch.matches("/.+/")) { //$NON-NLS-1$
				qbe.addPostQueryFilter(
					new RegexpFilter(keywordMatch.substring(1, keywordMatch.length() - 1)));
			} else {
				qbe.add(DocHandle.FLD_KEYWORDS, Query.LIKE, "%" + keywordMatch + "%"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		if (categoryMatch != null) {
			if (categoryMatch.matches("/.+/")) { //$NON-NLS-1$
				qbe.addPostQueryFilter(
					new RegexpFilter(categoryMatch.substring(1, categoryMatch.length() - 1)));
			} else {
				qbe.add(DocHandle.FLD_CAT, Query.EQUALS, categoryMatch);
			}
		}
		
		if (contentsMatch != null) {
			throw new ElexisException(getClass(),
				Messages.DocumentManagement_contentsMatchNotSupported,
				ElexisException.EE_NOT_SUPPORTED);
		}
		List<DocHandle> dox = qbe.execute();
		ArrayList<IOpaqueDocument> ret = new ArrayList<IOpaqueDocument>(dox.size());
		for (DocHandle doc : dox) {
			ret.add(doc);
		}
		return ret;
	}
	
	@Override
	public boolean removeDocument(String guid){
		DocHandle dh = DocHandle.load(guid);
		if (dh != null && dh.exists() && (!DocHandle.VERSION.equals(dh.getId()))) {
			return dh.delete();
		}
		return false;
	}
}
