/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.impfplan.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import at.medevit.elexis.impfplan.model.ArticleToImmunisationModel;
import at.medevit.elexis.impfplan.model.po.Vaccination;
import at.medevit.elexis.impfplan.model.vaccplans.ImpfplanSchweiz2013;
import at.medevit.elexis.impfplan.ui.preferences.PreferencePage;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeTool;

public class VaccinationView extends ViewPart {
	
	public static final String PART_ID = "at.medevit.elexis.impfplan.ui.ImpfplanViewPart";
	
	private static VaccinationPlanHeaderDefinition vaccinationHeaderDefinition;
	private static List<Vaccination> vaccinations;
	private VaccinationComposite vaccinationComposite;
	
	public static final String HEADER_ID_SHOW_ADMINISTERED = "HISA";
	private Patient pat;
	
	private ElexisEventListener eeli_pat = new ElexisUiEventListenerImpl(Patient.class) {
		public void runInUi(ElexisEvent ev){
			setPatient(ElexisEventDispatcher.getSelectedPatient());
		}
	};
	
	public VaccinationView(){
		ImpfplanSchweiz2013 is = new ImpfplanSchweiz2013();
		vaccinationHeaderDefinition =
			new VaccinationPlanHeaderDefinition(is.id, is.name, is.getOrderedBaseDiseases(),
				is.getOrderedExtendedDiseases());
		ElexisEventDispatcher.getInstance().addListeners(eeli_pat);
	}
	
	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent){
		parent.setLayout(new FillLayout(SWT.VERTICAL));
		
		vaccinationComposite = new VaccinationComposite(parent);
		Menu menu = new Menu(vaccinationComposite);
		MenuItem mDeleteEntry = new MenuItem(menu, SWT.PUSH);
		mDeleteEntry.setText("Eintrag löschen");
		mDeleteEntry.setImage(Images.IMG_DELETE.getImage());
		mDeleteEntry.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				VaccinationCompositePaintListener vcpl =
					vaccinationComposite.getVaccinationCompositePaintListener();
				Vaccination selVaccination = vcpl.getSelectedVaccination();
				selVaccination.delete();
				updateUi(true);
			}
		});
		vaccinationComposite.setMenu(menu);
		if (ElexisEventDispatcher.getSelectedPatient() != null) {
			setPatient(ElexisEventDispatcher.getSelectedPatient());
		}
	}
	
	private void setPatient(Patient selectedPatient){
		pat = selectedPatient;
		updateUi(true);
	}
	
	/**
	 * updates the ui
	 * 
	 * @param patientChanged
	 *            if this value is true all vacc's will be reloaded (via a query)
	 */
	public void updateUi(boolean patientChanged){
		if (pat == null) {
			vaccinations = Collections.emptyList();
			return;
		}
		
		if (patientChanged) {
			boolean sortDir = CoreHub.userCfg.get(PreferencePage.VAC_SORT_ORDER, false);
			Query<Vaccination> qbe = new Query<>(Vaccination.class);
			qbe.add("ID", Query.NOT_EQUAL, StringConstants.VERSION_LITERAL);
			qbe.add(Vaccination.FLD_PATIENT_ID, Query.EQUALS, pat.getId());
			qbe.orderBy(sortDir, Vaccination.FLD_DOA);
			vaccinations = qbe.execute();
			
		}
		
		if (vaccinationHeaderDefinition.id.equals(HEADER_ID_SHOW_ADMINISTERED)) {
			HashSet<String> atc = new HashSet<>();
			for (Vaccination vacc : vaccinations) {
				String atcCode = vacc.get(Vaccination.FLD_ATCCODE);
				if (atcCode.length() > 3) {
					List<String> immunisationForAtcCode =
						ArticleToImmunisationModel.getImmunisationForAtcCode(atcCode);
					atc.addAll(immunisationForAtcCode);
				} else {
					atc.addAll(Arrays.asList(vacc.get(Vaccination.FLD_VACC_AGAINST).split(",")));
				}
			}
			vaccinationHeaderDefinition =
				new VaccinationPlanHeaderDefinition(HEADER_ID_SHOW_ADMINISTERED,
					"Nur verabreichte Impfungen", new ArrayList<String>(atc),
					Collections.EMPTY_LIST);
		}
		vaccinationComposite.updateUi(vaccinationHeaderDefinition, vaccinations,
			new TimeTool(pat.getGeburtsdatum()));
	}
	
	@Override
	public void setFocus(){
		updateUi(false);
	}
	
	@Override
	public void dispose(){
		ElexisEventDispatcher.getInstance().removeListeners(eeli_pat);
		super.dispose();
	}
	
	public static void setVaccinationHeaderDefinition(
		VaccinationPlanHeaderDefinition vacccinationHeaderDefinition){
		VaccinationView.vaccinationHeaderDefinition = vacccinationHeaderDefinition;
		IViewReference viewReference =
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findViewReference(PART_ID);
		IWorkbenchPart part = viewReference.getPart(false);
		if (part != null) {
			part.setFocus();
		}
		
	}
	
	public static VaccinationPlanHeaderDefinition getVaccinationHeaderDefinition(){
		return vaccinationHeaderDefinition;
	}
	
	public VaccinationComposite getVaccinationComposite(){
		return vaccinationComposite;
	}
}
