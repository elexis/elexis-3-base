package at.medevit.ch.artikelstamm.medcalendar.ui;

import java.util.List;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import at.medevit.ch.artikelstamm.medcalendar.MedCalendar;
import at.medevit.ch.artikelstamm.medcalendar.MedCalendarSection;
import at.medevit.ch.artikelstamm.medcalendar.ui.provider.MedCalArtikelstammFlatDataLoader;
import ch.artikelstamm.elexis.common.ArtikelstammItem;
import ch.elexis.core.ui.util.viewers.CommonViewer;

public class MedCalMenuContribution extends ContributionItem {
	private CommonViewer cov;
	private MedCalArtikelstammFlatDataLoader fdl;
	
	public MedCalMenuContribution(CommonViewer cov, MedCalArtikelstammFlatDataLoader fdl){
		this.cov = cov;
		this.fdl = fdl;
	}
	
	@Override
	public void fill(Menu menu, int index){
		StructuredSelection structuredSelection = new StructuredSelection(cov.getSelection());
		Object element = structuredSelection.getFirstElement();
		MedCalendar medCal = MedCalendar.getInstance();
		
		if (element instanceof ArtikelstammItem) {
			final ArtikelstammItem ai = (ArtikelstammItem) element;
			List<MedCalendarSection> medCalHierarchy =
				medCal.getHierarchyForMedCal(ai.getATCCode());
			
			for (MedCalendarSection section : medCalHierarchy) {
				MenuItem temp = new MenuItem(menu, SWT.PUSH);
				temp.setText(section.getCode() + " " + section.getName());
				
				final MedCalendarSection tmpSection = section;
				temp.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e){
						fdl.setUseMedCalQueryFilter(true);
						fdl.setMedCalQueryFilterValue(tmpSection);
					};
				});
			}
		}
	}
	
	@Override
	public boolean isDynamic(){
		return true;
	}
}
