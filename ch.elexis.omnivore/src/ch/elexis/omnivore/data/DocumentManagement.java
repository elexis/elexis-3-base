/*******************************************************************************
 * Copyright (c) 2006-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/

package ch.elexis.omnivore.data;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.interfaces.text.IOpaqueDocument;
import ch.rgw.tools.RegexpFilter;
import ch.rgw.tools.TimeSpan;
import ch.rgw.tools.TimeTool;

public class DocumentManagement implements IDocumentManager {
	
	@Override
	public boolean addCategorie(String categorie){
		return false;
	}
	
	public String addDocument(IOpaqueDocument doc) throws ElexisException{
		DocHandle dh =
			new DocHandle(doc.getContentsAsBytes(), doc.getPatient(), doc.getTitle(),
				doc.getMimeType(), doc.getKeywords());
		return dh.getId();
	}
	
	/*
	 * @Override public String addDocument(Patient pat, InputStream is, String name, String
	 * category, String keywords, String date) throws ElexisException { try { ByteArrayOutputStream
	 * baos = new ByteArrayOutputStream(); FileTool.copyStreams(is, baos); DocHandle dh = new
	 * DocHandle(baos.toByteArray(), pat, name, name, keywords); if (date != null) { dh.set("Datum",
	 * date); } return dh.getId(); } catch (Exception ex) { throw new
	 * ElexisException(this.getClass(), ex.getMessage(), 1); } }
	 */
	/*
	 * @Override public boolean addDocument(Patient pat, String name, String catecory, String
	 * keywords, File file, String date) { try { FileInputStream fis = new FileInputStream(file);
	 * addDocument(pat, fis, name, null, keywords, date); return true; } catch (Exception ex) {
	 * ExHandler.handle(ex); return false; } }
	 */
	@Override
	public String[] getCategories(){
		return null;
	}
	
	@Override
	public InputStream getDocument(String id){
		DocHandle dh = DocHandle.load(id);
		return dh.getContentsAsStream();
	}
	
	@Override
	public List<IOpaqueDocument> listDocuments(final Patient pat, final String categoryMatch,
		final String titleMatch, final String keywordMatch, final TimeSpan dateMatch,
		final String contentsMatch) throws ElexisException{
		Query<DocHandle> qbe = new Query<DocHandle>(DocHandle.class);
		if (pat != null) {
			qbe.add("PatID", Query.EQUALS, pat.getId()); //$NON-NLS-1$
		}
		if (dateMatch != null) {
			String from = dateMatch.from.toString(TimeTool.DATE_COMPACT);
			String until = dateMatch.until.toString(TimeTool.DATE_COMPACT);
			qbe.add("Datum", Query.GREATER_OR_EQUAL, from); //$NON-NLS-1$
			qbe.add("Datum", Query.LESS_OR_EQUAL, until); //$NON-NLS-1$
		}
		if (titleMatch != null) {
			if (titleMatch.matches("/.+/")) { //$NON-NLS-1$
				qbe.addPostQueryFilter(new RegexpFilter(titleMatch.substring(1,
					titleMatch.length() - 1)));
			} else {
				qbe.add("Titel", Query.EQUALS, titleMatch); //$NON-NLS-1$
			}
		}
		if (keywordMatch != null) {
			if (keywordMatch.matches("/.+/")) { //$NON-NLS-1$
				qbe.addPostQueryFilter(new RegexpFilter(keywordMatch.substring(1,
					keywordMatch.length() - 1)));
			} else {
				qbe.add("Keywords", Query.LIKE, "%" + keywordMatch + "%"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
		/*
		 * if(categoryMatch!=null){ if(categoryMatch.matches("/.+/")){ qbe.addPostQueryFilter(new
		 * RegexpFilter(categoryMatch.substring(1, categoryMatch.length()-1))); }else{
		 * qbe.add("Category", Query.EQUALS, titleMatch); } }
		 */
		if (contentsMatch != null) {
			throw new ElexisException(getClass(),
				"ContentsMatch not supported", ElexisException.EE_NOT_SUPPORTED); //$NON-NLS-1$
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
		if (dh.exists()) {
			return dh.delete();
		}
		return false;
	}
}
