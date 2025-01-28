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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.model.IInboxElementService.State;
import at.medevit.elexis.inbox.ui.part.InboxView;
import at.medevit.elexis.inbox.ui.part.model.PatientInboxElements;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.e4.fieldassist.PatientSearchToken;

public class InboxElementContentProvider implements IStructuredContentProvider {

	private static final int PAGING_FETCHSIZE = 100;

	private int page = 1;

	private List<IInboxElement> items;

	private InboxView inboxView;

	private String searchText;
	private List<IInboxElement> filteredItems;

	private Job currentJob;

	public InboxElementContentProvider(InboxView inboxView) {
		this.inboxView = inboxView;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		page = inboxView.getPagingComposite().getCurrentPage();
		List<IInboxElement> list = filteredItems != null ? filteredItems : items;
		if (list != null) {
			if (page > 0 && list.size() > 0) {
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

	@Override
	public void dispose() {
		// nothing to do
	}

	@Override
	@SuppressWarnings("unchecked")
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof List<?>) {
			List<IInboxElement> input = (List<IInboxElement>) newInput;
			// refresh map and list
			LoggerFactory.getLogger(getClass()).info("items empty [" + input.size() + "]");
			items = Collections.emptyList();
			filteredItems = null;
			Display.getDefault().asyncExec(() -> {
				viewer.refresh();
			});
			if (currentJob != null) {
				currentJob.cancel();
			}
			currentJob = new LoadingInboxJob(input);
			currentJob.schedule();
		}
	}

	private class LoadingInboxJob extends Job {

		private List<IInboxElement> input;

		public LoadingInboxJob(List<IInboxElement> input) {
			super("Loading Inbox");
			this.input = input;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			monitor.beginTask("Lade Inbox", input.size());
			Map<IPatient, PatientInboxElements> map = new HashMap<>();

			for (IInboxElement inboxElement : input) {
				IPatient patient = inboxElement.getPatient();
				PatientInboxElements patientInbox = map.get(patient);
				if (patientInbox == null) {
					patientInbox = new PatientInboxElements(patient);
					map.put(patient, patientInbox);
				}
				patientInbox.addElement(inboxElement);
				monitor.worked(1);
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
			}
			items = map.values().stream().flatMap(pie -> pie.getElements().stream()).filter(ie -> ie != null)
					.collect(Collectors.toList());
			items.sort((l, r) -> {
				return r.getLastupdate().compareTo(l.getLastupdate());
			});
			if (monitor.isCanceled()) {
				return Status.CANCEL_STATUS;
			}
			// refresh filtered list
			if (StringUtils.isNotBlank(searchText)) {
				setSearchText(searchText);
			}
			Display.getDefault().asyncExec(() -> {
				page = 1;
				inboxView.getPagingComposite().setup(page, items.size(), PAGING_FETCHSIZE);
				inboxView.getViewer().refresh();
			});
			currentJob = null;
			return Status.OK_STATUS;
		}
	}

	public void refreshElement(IInboxElement element) {
		if (items != null) {
			if (element.getState() == State.SEEN) {
				items.remove(element);
				if (filteredItems != null) {
					filteredItems.remove(element);
				}
			} else {
				IMandator activeMandant = ContextServiceHolder.get().getActiveMandator().orElse(null);
				IMandator inboxMandant = element.getMandator();
				if (!inboxMandant.equals(activeMandant)) {
					items.remove(element);
					if (filteredItems != null) {
						filteredItems.remove(element);
					}
				} else if (currentJob == null) {
					items.addFirst(element);
					Display.getDefault().asyncExec(() -> {
						page = 1;
						inboxView.getPagingComposite().setup(page, items.size(), PAGING_FETCHSIZE);
						inboxView.getViewer().refresh();
					});
				}
			}
		}
	}

	public void setSearchText(String search) {
		this.searchText = search;
		CompletableFuture.runAsync(() -> {
			if (searchText != null && searchText.length() > 1) {
				List<PatientSearchToken> searchParts = PatientSearchToken
						.getPatientSearchTokens(searchText.toLowerCase().split(StringUtils.SPACE));
				filteredItems = items.stream().filter(i -> filterPatient(searchParts, i)).collect(Collectors.toList());
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

	private boolean filterPatient(List<PatientSearchToken> searchParts, IInboxElement i) {
		if (i.getPatient() != null) {
			return searchParts.parallelStream().allMatch(st -> st.test(i.getPatient()));
		}
		return false;
	}
}