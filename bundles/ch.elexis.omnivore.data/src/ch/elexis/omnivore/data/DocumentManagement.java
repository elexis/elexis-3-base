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

import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.elexis.core.data.interfaces.text.IOpaqueDocument;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.data.Patient;
import ch.elexis.omnivore.model.IDocumentHandle;
import ch.elexis.omnivore.data.model.OpaqueDocumentAdapter;
import ch.elexis.omnivore.model.TransientCategory;
import ch.elexis.omnivore.model.util.CategoryUtil;
import ch.elexis.omnivore.data.service.internal.OmnivoreModelServiceHolder;
import ch.rgw.tools.RegexpFilter;
import ch.rgw.tools.TimeSpan;
import ch.rgw.tools.TimeTool;

public class DocumentManagement implements IDocumentManager {
	
	public boolean addCategorie(String categorie){
		CategoryUtil.addCategory(categorie);
		return true;
	}
	
	public String addDocument(IOpaqueDocument doc) throws ElexisException{
		return addDocument(doc, false);
	}
	
	public String addDocument(IOpaqueDocument doc, boolean automaticBilling) throws ElexisException{
		IPatient iPatient = CoreModelServiceHolder.get()
			.load(doc.getPatient().getId(), IPatient.class).orElse(null);
		if (iPatient != null) {
			IDocumentHandle docHandle =
				OmnivoreModelServiceHolder.get().create(IDocumentHandle.class);
			OmnivoreModelServiceHolder.get().setEntityProperty("id", doc.getGUID(), docHandle);
			String category = doc.getCategory();
			if (category == null || category.length() < 1) {
				category = CategoryUtil.getDefaultCategory().getName();
			} else {
				CategoryUtil.ensureCategoryAvailability(category);
			}
			docHandle.setCategory(new TransientCategory(category));
			docHandle.setPatient(iPatient);
			
			docHandle.setCreated(new TimeTool(doc.getCreationDate()).getTime());
			docHandle.setTitle(doc.getTitle());
			docHandle.setKeywords(doc.getKeywords());
			docHandle.setMimeType(doc.getMimeType());
			docHandle.setContent(doc.getContentsAsStream());
			OmnivoreModelServiceHolder.get().save(docHandle);
			
			if (automaticBilling) {
				if (AutomaticBilling.isEnabled()) {
					AutomaticBilling billing = new AutomaticBilling(docHandle);
					billing.bill();
				}
			}
			return docHandle.getId();
		} else {
			throw new IllegalStateException("No patient available");
		}
	}
	
	public String[] getCategories(){
		return CategoryUtil.getCategoriesNames().toArray(new String[0]);
	}
	
	public InputStream getDocument(String id){
		IDocumentHandle dh =
			OmnivoreModelServiceHolder.get().load(id, IDocumentHandle.class).orElse(null);
		if (dh != null) {
			return dh.getContent();
		}
		return null;
	}
	
	@Override
	public List<IOpaqueDocument> listDocuments(final Patient pat, final String categoryMatch,
		final String titleMatch, final String keywordMatch, final TimeSpan dateMatch,
		final String contentsMatch) throws ElexisException{
		
		IQuery<IDocumentHandle> qbe =
			OmnivoreModelServiceHolder.get().getQuery(IDocumentHandle.class);
		if (pat != null) {
			IPatient iPatient =
				CoreModelServiceHolder.get().load(pat.getId(), IPatient.class).orElse(null);
			qbe.and("kontakt", COMPARATOR.EQUALS, iPatient);
		}
		if (dateMatch != null) {
			LocalDate from = dateMatch.from.toLocalDate();
			LocalDate until = dateMatch.until.toLocalDate();
			qbe.and("creationDate", COMPARATOR.GREATER_OR_EQUAL, from);
			qbe.and("creationDate", COMPARATOR.LESS_OR_EQUAL, until);
		}
		List<RegexpFilter> filters = new ArrayList<>();
		if (titleMatch != null) {
			if (titleMatch.matches("/.+/")) { //$NON-NLS-1$
				filters.add(new RegexpFilter(titleMatch.substring(1, titleMatch.length() - 1)));
			} else {
				qbe.and("title", COMPARATOR.EQUALS, titleMatch);
			}
		}
		if (keywordMatch != null) {
			if (keywordMatch.matches("/.+/")) { //$NON-NLS-1$
				filters.add(new RegexpFilter(keywordMatch.substring(1, keywordMatch.length() - 1)));
			} else {
				qbe.and("keywords", COMPARATOR.LIKE, "%" + keywordMatch + "%"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		if (categoryMatch != null) {
			if (categoryMatch.matches("/.+/")) { //$NON-NLS-1$
				filters.add(new RegexpFilter(categoryMatch.substring(1, categoryMatch.length() - 1)));
			} else {
				qbe.and("category", COMPARATOR.EQUALS, categoryMatch);
			}
		}
		
		if (contentsMatch != null) {
			throw new ElexisException(getClass(),
				Messages.DocumentManagement_contentsMatchNotSupported,
				ElexisException.EE_NOT_SUPPORTED);
		}
		List<IDocumentHandle> dox = qbe.execute();
		if (!filters.isEmpty()) {
			dox = dox.parallelStream().filter(d -> applyFilters(d, filters))
				.collect(Collectors.toList());
		}
		ArrayList<IOpaqueDocument> ret = new ArrayList<IOpaqueDocument>(dox.size());
		for (IDocumentHandle doc : dox) {
			ret.add(new OpaqueDocumentAdapter(doc));
		}
		return ret;
	}
	
	private boolean applyFilters(IDocumentHandle d, List<RegexpFilter> filters){
		for (RegexpFilter regexpFilter : filters) {
			if (!regexpFilter.select(d)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean removeDocument(String id){
		IDocumentHandle dh =
			OmnivoreModelServiceHolder.get().load(id, IDocumentHandle.class).orElse(null);
		if (dh != null) {
			OmnivoreModelServiceHolder.get().delete(dh);
			return true;
		}
		return false;
	}
}
