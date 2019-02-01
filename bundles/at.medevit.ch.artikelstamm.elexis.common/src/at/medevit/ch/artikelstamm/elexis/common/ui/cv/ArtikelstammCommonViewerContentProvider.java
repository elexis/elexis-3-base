package at.medevit.ch.artikelstamm.elexis.common.ui.cv;

import java.util.List;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.elexis.common.service.ModelServiceHolder;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.ui.util.viewers.AbstractCommonViewerContentProvider;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ControlFieldProvider;

public class ArtikelstammCommonViewerContentProvider extends AbstractCommonViewerContentProvider {
	
	private ControlFieldProvider controlFieldProvider;
	
	public ArtikelstammCommonViewerContentProvider(CommonViewer commonViewer,
		ControlFieldProvider controlFieldProvider){
		super(commonViewer);
		this.controlFieldProvider = controlFieldProvider;
	}
	
	@Override
	public Object[] getElements(Object arg0){
		IQuery<?> query = getBaseQuery();
		applyQueryFilters(query);
		List<?> elements = query.execute();
		return elements.toArray(new Object[elements.size()]);
	}
	
	@Override
	protected IQuery<?> getBaseQuery(){
		return ModelServiceHolder.get().getQuery(IArtikelstammItem.class);
	}
	
	public void setAddAtcElements(boolean checked){
		// TODO Auto-generated method stub
		
	}
}
