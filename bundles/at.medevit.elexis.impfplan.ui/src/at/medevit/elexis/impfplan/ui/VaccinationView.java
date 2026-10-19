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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import at.medevit.elexis.impfplan.model.ArticleToImmunisationModel;
import at.medevit.elexis.impfplan.model.po.Vaccination;
import at.medevit.elexis.impfplan.model.vaccplans.ImpfplanSchweiz2019;
import at.medevit.elexis.impfplan.ui.dialogs.EditVaccinationDialog;
import at.medevit.elexis.impfplan.ui.preferences.PreferencePage;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.constants.Preferences;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.events.RefreshingPartListener;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeTool;

public class VaccinationView extends ViewPart implements IRefreshable {

	public static final String PART_ID = "at.medevit.elexis.impfplan.ui.ImpfplanViewPart"; //$NON-NLS-1$

	private static VaccinationPlanHeaderDefinition vaccinationHeaderDefinition;
	private static List<Vaccination> vaccinations;
	private VaccinationComposite vaccinationComposite;
	private VaccinationCompositePaintListener vcPaintListener;

	public static final String HEADER_ID_SHOW_ADMINISTERED = "HISA"; //$NON-NLS-1$
	private Patient pat;
	/**
	 * knowledge if the sortByVaccination icon is active
	 */
	private boolean sortByVaccinationName = false;

	private RefreshingPartListener udpateOnVisible = new RefreshingPartListener(this);

	@Inject
	void activePatient(@Optional IPatient patient) {
		CoreUiUtil.runAsyncIfActive(() -> {
			setPatient((Patient) NoPoUtil.loadAsPersistentObject(patient));
		}, vaccinationComposite);
	}

	@Optional
	@Inject
	void crudVaccination(@UIEventTopic(ElexisEventTopics.BASE_MODEL + "*") Vaccination vaccination) {
		updateUi(true);
	}

	private ScrolledComposite scrolledComposite;
	private Composite headerViewport;
	private Canvas headerCanvas;
	private Composite content;

	public VaccinationView() {
		ImpfplanSchweiz2019 is = new ImpfplanSchweiz2019();
		vaccinationHeaderDefinition = new VaccinationPlanHeaderDefinition(is.id, is.name, is.getOrderedBaseDiseases(),
				is.getOrderedExtendedDiseases());
	}

	/**
	 * Create contents of the view part.
	 *
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;
		parent.setLayout(layout);
		headerViewport = new Composite(parent, SWT.NONE);
		GridData headerData = new GridData(SWT.BEGINNING, SWT.TOP, false, false);
		headerData.exclude = true;
		headerData.heightHint = 0;
		headerViewport.setLayoutData(headerData);
		headerViewport.setVisible(false);
		scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		content = new Composite(scrolledComposite, SWT.NONE);
		vaccinationComposite = new VaccinationComposite(content);
		scrolledComposite.setContent(content);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		vcPaintListener = vaccinationComposite.getVaccinationCompositePaintListener();
		headerCanvas = new Canvas(headerViewport, SWT.DOUBLE_BUFFERED);
		headerCanvas.setBackground(vaccinationComposite.getBackground());
		headerCanvas.addPaintListener(e -> vcPaintListener.paintControl(e.gc, e.display,
				headerCanvas.getSize().x, vaccinationComposite.getSize().y, false, -1));
		headerCanvas.addMouseListener(new VaccinationCompositeMouseListener(vcPaintListener));
		headerCanvas.addMouseMoveListener(new VaccinationCompositeMouseMoveListener(vcPaintListener));
		content.addListener(SWT.Resize, e -> updateContentBounds());
		scrolledComposite.getHorizontalBar().addListener(SWT.Selection, e -> syncHeader());
		scrolledComposite.addListener(SWT.Resize, e -> syncHeader());
		parent.addListener(SWT.Resize, e -> updateLayout());
		updateLayout();
		vaccinationComposite.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				Vaccination selVaccination = vcPaintListener.getSelectedVaccination();
				editVaccination(selVaccination);
			}
		});

		MenuManager menuManager = new MenuManager();
		menuManager.add(new Action() {
			@Override
			public String getText() {
				return "Eintrag löschen";
			}

			@Override
			public ImageDescriptor getImageDescriptor() {
				return Images.IMG_DELETE.getImageDescriptor();
			}

			@Override
			public void run() {
				Vaccination selVaccination = vcPaintListener.getSelectedVaccination();
				if (selVaccination != null) {
					selVaccination.delete();
				}
			}
		});
		menuManager.add(new Action() {
			@Override
			public String getText() {
				return "Impfung editieren";
			}

			@Override
			public ImageDescriptor getImageDescriptor() {
				return Images.IMG_EDIT.getImageDescriptor();
			}

			@Override
			public void run() {
				Vaccination selVaccination = vcPaintListener.getSelectedVaccination();
				editVaccination(selVaccination);
			}
		});

		vaccinationComposite.setMenu(menuManager.createContextMenu(vaccinationComposite));
		getSite().registerContextMenu(PART_ID + ".contextMenu", menuManager, vaccinationComposite); //$NON-NLS-1$
		getSite().setSelectionProvider(vaccinationComposite);

		getSite().getPage().addPartListener(udpateOnVisible);
	}

	private void updateLayout() {
		Point size = vaccinationComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		int headerHeight = vcPaintListener.getHeaderHeight();
		boolean fixedHeader = headerHeight > 0 && scrolledComposite.getParent().getClientArea().height > headerHeight
				+ 3 * vcPaintListener.getEntryHeight();
		GridData headerData = (GridData) headerViewport.getLayoutData();
		headerData.exclude = !fixedHeader;
		headerData.heightHint = fixedHeader ? headerHeight : 0;
		headerViewport.setVisible(fixedHeader);
		scrolledComposite.setMinSize(size.x, size.y - (fixedHeader ? headerHeight : 0));
		scrolledComposite.getParent().layout(true, true);
		updateContentBounds();
		syncHeader();
	}

	private void updateContentBounds() {
		Rectangle area = content.getClientArea();
		int headerHeight = ((GridData) headerViewport.getLayoutData()).exclude ? 0 : vcPaintListener.getHeaderHeight();
		vaccinationComposite.setBounds(0, -headerHeight, area.width, area.height + headerHeight);
		syncHeader();
	}

	private void syncHeader() {
		Rectangle area = scrolledComposite.getClientArea();
		GridData headerData = (GridData) headerViewport.getLayoutData();
		if (headerData.widthHint != area.width || headerData.horizontalIndent != scrolledComposite.getBorderWidth()) {
			headerData.widthHint = area.width;
			headerData.horizontalIndent = scrolledComposite.getBorderWidth();
			scrolledComposite.getParent().layout(true, true);
		}
		if (!headerData.exclude) {
			headerCanvas.setBounds(-scrolledComposite.getOrigin().x, 0, content.getClientArea().width,
					vcPaintListener.getHeaderHeight());
		}
	}

	private void editVaccination(Vaccination selVaccination) {
		if (selVaccination != null) {
			EditVaccinationDialog evd = new EditVaccinationDialog(vaccinationComposite.getShell(), selVaccination);
			evd.open();
		}
	}

	private void setPatient(Patient selectedPatient) {
		pat = selectedPatient;
		updateUi(true);
	}

	/**
	 * updates the ui
	 *
	 * @param patientChanged if this value is true all vacc's will be reloaded (via
	 *                       a query)
	 */
	public void updateUi(boolean patientChanged) {
		if (pat == null) {
			vaccinations = Collections.emptyList();
			return;
		}

		if (patientChanged) {
			boolean sortDir = ConfigServiceHolder.getUser(PreferencePage.VAC_SORT_ORDER, false);
			Query<Vaccination> qbe = new Query<>(Vaccination.class);
			qbe.add("ID", Query.NOT_EQUAL, StringConstants.VERSION_LITERAL); //$NON-NLS-1$
			qbe.add(Vaccination.FLD_PATIENT_ID, Query.EQUALS, pat.getId());
			qbe.orderBy(sortDir, Vaccination.FLD_DOA);
			vaccinations = qbe.execute();

		}

		if (sortByVaccinationName) {
			sortVaccinationsByName();
		}

		if (vaccinationHeaderDefinition.id.equals(HEADER_ID_SHOW_ADMINISTERED)) {
			HashSet<String> atc = new HashSet<>();
			for (Vaccination vacc : vaccinations) {
				String atcCode = vacc.get(Vaccination.FLD_ATCCODE);
				if (atcCode.length() > 3) {
					List<String> immunisationForAtcCode = ArticleToImmunisationModel.getImmunisationForAtcCode(atcCode);
					atc.addAll(immunisationForAtcCode);
				} else {
					atc.addAll(Arrays.asList(vacc.get(Vaccination.FLD_VACC_AGAINST).split(","))); //$NON-NLS-1$
				}
			}
			vaccinationHeaderDefinition = new VaccinationPlanHeaderDefinition(HEADER_ID_SHOW_ADMINISTERED,
					"Nur verabreichte Impfungen", new ArrayList<String>(atc), Collections.emptyList());
		}
		vaccinationComposite.updateUi(vaccinationHeaderDefinition, vaccinations, new TimeTool(pat.getGeburtsdatum()));
		updateLayout();
		headerCanvas.redraw();
		// workaround for layout after patient changed
		if (patientChanged) {
			vaccinationComposite.update();
			vaccinationComposite.redraw();
		}
	}

	public void sortVaccinationsByName() {
		Collections.sort(vaccinations, new Comparator<Vaccination>() {
			@Override
			public int compare(Vaccination vac1, Vaccination vac2) {
				String name1 = vac1.getShortBusinessName();
				String name2 = vac2.getShortBusinessName();
				return name1.compareTo(name2);
			}
		});
	}

	public void setSortByVaccinationName(boolean sort) {
		sortByVaccinationName = sort;
		updateUi(!sort);
	}

	@Override
	public void setFocus() {
		updateUi(false);
	}

	@Override
	public void dispose() {
		getSite().getPage().removePartListener(udpateOnVisible);
		super.dispose();
	}

	@Optional
	@Inject
	public void setFixLayout(MPart part, @Named(Preferences.USR_FIX_LAYOUT) boolean currentState) {
		CoreUiUtil.updateFixLayout(part, currentState);
	}

	@Override
	public void refresh() {
		activePatient(ContextServiceHolder.get().getActivePatient().orElse(null));
	}

	public static void setVaccinationHeaderDefinition(VaccinationPlanHeaderDefinition vacccinationHeaderDefinition) {
		VaccinationView.vaccinationHeaderDefinition = vacccinationHeaderDefinition;
		IViewReference viewReference = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.findViewReference(PART_ID);
		IWorkbenchPart part = viewReference.getPart(false);
		if (part != null) {
			part.setFocus();
		}

	}

	public static VaccinationPlanHeaderDefinition getVaccinationHeaderDefinition() {
		return vaccinationHeaderDefinition;
	}

	public VaccinationComposite getVaccinationComposite() {
		return vaccinationComposite;
	}
}
