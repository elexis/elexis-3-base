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
package at.medevit.elexis.inbox.ui.part.provider;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.model.IInboxElementService.State;
import at.medevit.elexis.inbox.ui.part.InboxView;
import at.medevit.elexis.inbox.ui.part.model.PatientInboxElements;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ContextServiceHolder;

public class InboxElementContentProvider implements IStructuredContentProvider {

	private static final int PAGING_FETCHSIZE = 100;

	private int page = 1;

	private List<IInboxElement> items;

	private InboxView inboxView;

	private InboxElementUiExtension extension = new InboxElementUiExtension();

	private String searchText;
	private List<IInboxElement> filteredItems;

	public InboxElementContentProvider(InboxView inboxView) {
		this.inboxView = inboxView;
	}

	public Object[] getElements(Object inputElement) {
		page = inboxView.getPagingComposite().getCurrentPage();
		List<IInboxElement> list = filteredItems != null ? filteredItems : items;
		if (list != null) {
			if (page > 0) {
				if (list.size() >= page * PAGING_FETCHSIZE) {
					return list.subList((page - 1) * PAGING_FETCHSIZE, page * PAGING_FETCHSIZE).toArray();
				} else {
					return list.subList((page - 1) * PAGING_FETCHSIZE, list.size() - 1).toArray();
				}
			} else {
				return list.toArray();
			}
		}
		return Collections.emptyList().toArray();
	}

	public void dispose() {
		// nothing to do
	}

	@SuppressWarnings("unchecked")
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof List<?>) {
			List<IInboxElement> input = (List<IInboxElement>) newInput;
			// refresh map and list
			Map<IPatient, PatientInboxElements> map = new HashMap<>();
			items = null;
			Display.getDefault().asyncExec(() -> {
				viewer.refresh();
			});
			Job job = new Job("Loading Inbox") {
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					monitor.beginTask("Lade Inbox", input.size());
					for (IInboxElement inboxElement : input) {
						IPatient patient = inboxElement.getPatient();
						PatientInboxElements patientInbox = map.get(patient);
						if (patientInbox == null) {
							patientInbox = new PatientInboxElements(patient);
							map.put(patient, patientInbox);
						}
						patientInbox.addElement(inboxElement);
						monitor.worked(1);
					}
					items = map.values().stream().flatMap(pie -> pie.getElements().stream()).filter(ie -> ie != null)
							.collect(Collectors.toList());
					items.sort((l, r) -> {
						LocalDate t1 = extension.getObjectDate(l);
						LocalDate t2 = extension.getObjectDate(r);
						t1 = (t1 == null ? LocalDate.EPOCH : t1);
						t2 = (t2 == null ? LocalDate.EPOCH : t2);
						return t2.compareTo(t1);
					});
					Display.getDefault().asyncExec(() -> {
						page = 1;
						inboxView.getPagingComposite().setup(page, items.size(), PAGING_FETCHSIZE);

						inboxView.getViewer().refresh();
					});
					return Status.OK_STATUS;
				}

			};
			job.schedule();
		}
	}

	public void refreshElement(IInboxElement element) {
		if (items != null) {
			if (element.getState() == State.SEEN) {
				items.remove(element);
			} else {
				IMandator activeMandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
				IMandator inboxMandant = element.getMandator();
				if (!inboxMandant.equals(activeMandant)) {
					items.remove(element);
				}
			}
		}
	}

	public void setSearchText(String search) {
		this.searchText = search;
		CompletableFuture.runAsync(() -> {
			if (searchText != null && searchText.length() > 2) {
				filteredItems = items.stream().filter(i -> filterPatient(i)).collect(Collectors.toList());
				Display.getDefault().asyncExec(() -> {
					page = 1;
					inboxView.getPagingComposite().setup(page, filteredItems.size(), PAGING_FETCHSIZE);
					inboxView.getViewer().refresh();
				});
			} else {
				filteredItems = null;
				Display.getDefault().asyncExec(() -> {
					page = 1;
					inboxView.getPagingComposite().setup(page, items.size(), PAGING_FETCHSIZE);
					inboxView.getViewer().refresh();
				});
			}
		});
	}

	private boolean filterPatient(IInboxElement i) {
		if (i.getPatient() != null) {
			return i.getPatient().getLabel().toLowerCase().contains(searchText.toLowerCase());
		}
		return false;
	}
}