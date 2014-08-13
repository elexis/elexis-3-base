/*******************************************************************************
 * Copyright (c) 2014 MEDEVIT.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     T. Huster - initial API and implementation
 *******************************************************************************/
package at.medevit.elexis.inbox.ui.part;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import at.medevit.elexis.inbox.model.IInboxElementService;
import at.medevit.elexis.inbox.model.IInboxElementService.State;
import at.medevit.elexis.inbox.model.IInboxUpdateListener;
import at.medevit.elexis.inbox.model.InboxElement;
import at.medevit.elexis.inbox.ui.InboxServiceComponent;
import at.medevit.elexis.inbox.ui.part.action.InboxFilterAction;
import at.medevit.elexis.inbox.ui.part.model.PatientInboxElements;
import at.medevit.elexis.inbox.ui.part.provider.IInboxElementUiProvider;
import at.medevit.elexis.inbox.ui.part.provider.InboxElementContentProvider;
import at.medevit.elexis.inbox.ui.part.provider.InboxElementLabelProvider;
import at.medevit.elexis.inbox.ui.part.provider.InboxElementUiExtension;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;

public class InboxView extends ViewPart {
	
	private Text filterText;
	private CheckboxTreeViewer viewer;
	
	private InboxElementViewerFilter filter = new InboxElementViewerFilter();
	
	private ElexisUiEventListenerImpl mandantChanged = new ElexisUiEventListenerImpl(Mandant.class,
		ElexisEvent.EVENT_MANDATOR_CHANGED) {
		@Override
		public void runInUi(ElexisEvent ev){
			reload();
		}
	};
	
	@Override
	public void createPartControl(Composite parent){
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		
		Composite filterComposite = new Composite(composite, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		filterComposite.setLayoutData(data);
		filterComposite.setLayout(new GridLayout(2, false));
		
		filterText = new Text(filterComposite, SWT.SEARCH);
		filterText.setMessage("Filter");
		data = new GridData(GridData.FILL_HORIZONTAL);
		filterText.setLayoutData(data);
		filterText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e){
				if (filterText.getText().length() > 1) {
					filter.setSearchText(filterText.getText());
					viewer.refresh();
				} else {
					filter.setSearchText("");
					viewer.refresh();
				}
			}
		});
		
		ToolBarManager menuManager = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL | SWT.WRAP);
		menuManager.createControl(filterComposite);
		
		viewer =
			new CheckboxTreeViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		viewer.getControl().setLayoutData(gd);
		
		ViewerFilter[] filters = new ViewerFilter[1];
		filters[0] = filter;
		viewer.setFilters(filters);
		
		viewer.setContentProvider(new InboxElementContentProvider());
		
		viewer.setLabelProvider(new InboxElementLabelProvider());
		
		viewer.addCheckStateListener(new ICheckStateListener() {
			
			public void checkStateChanged(CheckStateChangedEvent event){
				if (event.getElement() instanceof PatientInboxElements) {
					PatientInboxElements patientInbox = (PatientInboxElements) event.getElement();
					for (InboxElement inboxElement : patientInbox.getElements()) {
						State newState = toggleInboxElementState(inboxElement);
						if (newState == State.NEW) {
							viewer.setChecked(inboxElement, false);
						} else {
							viewer.setChecked(inboxElement, true);
						}
					}
				} else if (event.getElement() instanceof InboxElement) {
					InboxElement inboxElement = (InboxElement) event.getElement();
					toggleInboxElementState(inboxElement);
				}
				reload();
			}
		});
		
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event){
				StructuredSelection selection = (StructuredSelection) viewer.getSelection();
				if (!selection.isEmpty()) {
					Object selectedObj = selection.getFirstElement();
					if (selectedObj instanceof InboxElement) {
						InboxElementUiExtension extension = new InboxElementUiExtension();
						extension.fireDoubleClicked((InboxElement) selectedObj);
					}
				}
			}
		});
		
		addFilterActions(menuManager);
		
		InboxServiceComponent.getService().addUpdateListener(new IInboxUpdateListener() {
			public void update(InboxElement element){
				Display.getDefault().asyncExec(new Runnable() {
					public void run(){
						reload();
					}
				});
			}
		});
		
		reload();
		
		ElexisEventDispatcher.getInstance().addListeners(mandantChanged);
	}
	
	private void addFilterActions(ToolBarManager menuManager){
		InboxElementUiExtension extension = new InboxElementUiExtension();
		List<IInboxElementUiProvider> providers = extension.getProviders();
		for (IInboxElementUiProvider iInboxElementUiProvider : providers) {
			ViewerFilter extensionFilter = iInboxElementUiProvider.getFilter();
			if (extensionFilter != null) {
				InboxFilterAction action =
					new InboxFilterAction(viewer, extensionFilter,
						iInboxElementUiProvider.getFilterImage());
				menuManager.add(action);
			}
		}
		menuManager.update(true);
	}
	
	private State toggleInboxElementState(InboxElement inboxElement){
		if (inboxElement.getState() == State.NEW) {
			inboxElement.setState(State.SEEN);
			return State.SEEN;
		} else if (inboxElement.getState() == State.SEEN) {
			inboxElement.setState(State.NEW);
			return State.NEW;
		}
		return State.NEW;
	}
	
	@Override
	public void setFocus(){
		filterText.setFocus();
	}
	
	@SuppressWarnings("deprecation")
	private ArrayList<PatientInboxElements> getOpenInboxElements(){
		HashMap<Patient, PatientInboxElements> map = new HashMap<Patient, PatientInboxElements>();
		List<InboxElement> openElements =
			InboxServiceComponent.getService().getInboxElements(CoreHub.actMandant, null,
				IInboxElementService.State.NEW);
		// create PatientInboxElements for all InboxElements
		for (InboxElement inboxElement : openElements) {
			Patient patient = inboxElement.getPatient();
			PatientInboxElements patientInbox = map.get(patient);
			if (patientInbox == null) {
				patientInbox = new PatientInboxElements(patient);
				map.put(patient, patientInbox);
			}
			patientInbox.addElement(inboxElement);
		}
		return new ArrayList<PatientInboxElements>(map.values());
	}
	
	private class InboxElementViewerFilter extends ViewerFilter {
		protected String searchString;
		protected LabelProvider labelProvider = new InboxElementLabelProvider();
		
		public void setSearchText(String s){
			// Search must be a substring of the existing value
			this.searchString = ".*" + s + ".*";
		}
		
		private boolean isSelect(Object leaf){
			String label = labelProvider.getText(leaf);
			if (label != null && label.matches(searchString)) {
				return true;
			}
			return false;
		}
		
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element){
			if (searchString == null || searchString.length() == 0) {
				return true;
			}
			
			StructuredViewer sviewer = (StructuredViewer) viewer;
			ITreeContentProvider provider = (ITreeContentProvider) sviewer.getContentProvider();
			Object[] children = provider.getChildren(element);
			if (children != null && children.length > 0) {
				for (Object child : children) {
					if (select(viewer, element, child)) {
						return true;
					}
				}
			}
			return isSelect(element);
		}
	}
	
	public void reload(){
		viewer.setInput(getOpenInboxElements());
		viewer.expandAll();
	}
	
	@Override
	public void dispose(){
		ElexisEventDispatcher.getInstance().removeListeners(mandantChanged);
		super.dispose();
	}
}
