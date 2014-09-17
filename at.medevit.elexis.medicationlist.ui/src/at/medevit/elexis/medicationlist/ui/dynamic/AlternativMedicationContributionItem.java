package at.medevit.elexis.medicationlist.ui.dynamic;

import java.util.List;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import at.medevit.ch.artikelstamm.ui.ArtikelstammLabelProvider;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.data.Artikel;
import ch.elexis.data.Prescription;

public class AlternativMedicationContributionItem extends ContributionItem {
	
	private TableViewer tv;
	private ArtikelstammLabelProvider alp;
	
	public AlternativMedicationContributionItem(String id){
		super(id);
	}
	
	public AlternativMedicationContributionItem(TableViewer tv){
		this.tv = tv;
		alp = new ArtikelstammLabelProvider();
	}

	@Override
	public void fill(Menu menu, int index){
		StructuredSelection ss = (StructuredSelection) tv.getSelection();
		Prescription presc = (Prescription) ss.getFirstElement();
		if(presc==null) {
			notApplicable(menu);
			return;
		}
		
		Artikel artikel = presc.getArtikel();
		if(artikel==null || !(artikel instanceof ArtikelstammItem)) {
			notApplicable(menu);
			return;
		}
		
		ArtikelstammItem ai = (ArtikelstammItem) artikel;
		List<ArtikelstammItem> alternatives = ai.getAlternativeArticlesByATCGroup();
		if(alternatives.isEmpty()) {
			notApplicable(menu);
			return;
		}
		
		MenuItem header = new MenuItem(menu, SWT.CHECK, index);
		header.setText("Über ATC ("+ai.getATC_code()+")");
		header.setEnabled(false);
		
		for (ArtikelstammItem artikelstammItem : alternatives) {
			MenuItem item = new MenuItem(menu, SWT.CHECK);
			item.setText(alp.getText(artikelstammItem));
			item.setImage(alp.getImage(artikelstammItem));
		}
		
	}
	
	private void notApplicable(Menu menu){
		MenuItem mi = new MenuItem(menu, SWT.NONE);
		mi.setText("Nicht verfügbar");
		mi.setEnabled(false);
		
	}

	@Override
	public boolean isDynamic(){
		return true;
	}
	
}
