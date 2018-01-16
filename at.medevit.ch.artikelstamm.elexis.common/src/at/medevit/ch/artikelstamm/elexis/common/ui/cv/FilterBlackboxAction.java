package at.medevit.ch.artikelstamm.elexis.common.ui.cv;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.ResourceManager;

import at.medevit.ch.artikelstamm.BlackBoxReason;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.ui.actions.PersistentObjectLoader.QueryFilter;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;

/**
 * Filter blackboxed items out. 
 * @see BlackBoxReason
 * @author gerry
 *
 */
public class FilterBlackboxAction extends Action {
	ArtikelstammFlatDataLoader fdl;
	BlackBoxQueryFilter bbqf;
	private ImageDescriptor blackBoxedImage = ResourceManager.getPluginImageDescriptor("at.medevit.ch.artikelstamm.ui",
			"/rsc/icons/flag-black.png");
	
	public FilterBlackboxAction(ArtikelstammFlatDataLoader fdl){
		super("Backbox filtern",Action.AS_CHECK_BOX);
		
		setToolTipText("Nicht lieferbare Artikel ausblenden");
		setImageDescriptor(blackBoxedImage);
		this.fdl=fdl;
		bbqf=new BlackBoxQueryFilter();
		fdl.addQueryFilter(bbqf);
		setChecked(false);
	}
	
	@Override
	public void run() {
		if(isChecked()) {
			fdl.removeQueryFilter(bbqf);
			
		}else {
			fdl.addQueryFilter(bbqf);
		}
		fdl.changed(null);
	}
	private class BlackBoxQueryFilter implements QueryFilter{

		@Override
		public void apply(Query<? extends PersistentObject> qbe){
			qbe.add(ArtikelstammItem.FLD_BLACKBOXED,Query.EQUALS,"0");
			
		}
		
	}
}
