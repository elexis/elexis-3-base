package ch.elexis.views;

import java.util.HashMap;

import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ICommonViewerContentProvider;
import ch.elexis.data.PandemieLeistung;
import ch.elexis.data.Query;

public class PandemicContentProvider implements ICommonViewerContentProvider {
	
	private Query<PandemieLeistung> query;
	
	public PandemicContentProvider(){
		query = new Query<PandemieLeistung>(PandemieLeistung.class, null, null,
			PandemieLeistung.TABLENAME, new String[] {
				PandemieLeistung.FLD_VALIDFROM, PandemieLeistung.FLD_VALIDUNTIL,
				PandemieLeistung.FLD_CODE, PandemieLeistung.FLD_TITLE
			});
	}
	
	@Override
	public Object[] getElements(Object arg0){
		return query.execute().toArray();
	}
	
	@Override
	public void changed(HashMap<String, String> values){
	}
	
	@Override
	public void reorder(String field){
	}
	
	@Override
	public void selected(){
	}
	
	@Override
	public void init(){
	}
	
	@Override
	public void startListening(){
	}
	
	@Override
	public void stopListening(){
	}
}
