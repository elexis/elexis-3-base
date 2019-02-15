/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *******************************************************************************/
package ch.elexis.base.ch.labortarif_2009.ui;

import java.util.List;

import ch.elexis.base.ch.labortarif.ILaborLeistung;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.LazyCommonViewerContentProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ControlFieldProvider;
import ch.elexis.labortarif2009.data.ModelServiceHolder;

public class Labor2009ContentProvider extends LazyCommonViewerContentProvider {
	
	private ControlFieldProvider controlFieldProvider;
	
	public Labor2009ContentProvider(CommonViewer commonViewer,
		ControlFieldProvider controlFieldProvider){
		super(commonViewer);
		this.controlFieldProvider = controlFieldProvider;
	}
	
	@Override
	public Object[] getElements(Object inputElement){
		IQuery<?> query = getBaseQuery();
		// apply filters from control field provider
		controlFieldProvider.setQuery(query);
		// apply additional filters like atc, mepha, ...
		applyQueryFilters(query);
		query.orderBy("code", ORDER.ASC);
		List<?> elements = query.execute();
		
		return elements.toArray(new Object[elements.size()]);
	}
	
	@Override
	protected IQuery<?> getBaseQuery(){
		IQuery<ILaborLeistung> query = ModelServiceHolder.get().getQuery(ILaborLeistung.class);
		query.and("id", COMPARATOR.NOT_EQUALS, "1");
		return query;
	}
}
