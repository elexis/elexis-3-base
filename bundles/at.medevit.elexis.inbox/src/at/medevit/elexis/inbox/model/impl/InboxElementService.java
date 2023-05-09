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
package at.medevit.elexis.inbox.model.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.model.IInboxElementService;
import at.medevit.elexis.inbox.model.IInboxUpdateListener;
import at.medevit.elexis.inbox.model.InboxElementType;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.core.services.IStoreToStringService;
import ch.elexis.data.PersistentObject;

@Component(immediate = true)
public class InboxElementService implements IInboxElementService {

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=at.medevit.elexis.inbox.model)")
	private IModelService modelService;

	@Reference
	private IStoreToStringService storeToString;

	HashSet<IInboxUpdateListener> listeners = new HashSet<IInboxUpdateListener>();

	@Override
	public void createInboxElement(IPatient patient, IMandator mandator, Identifiable object) {
		// InboxElement element = new InboxElement(patient, mandant, object);
		IInboxElement element = modelService.create(IInboxElement.class);
		element.setPatient(patient);
		element.setMandator(mandator);
		storeToString.storeToString(object).ifPresent(sts -> {
			element.setObject(sts);
		});
		element.setState(State.NEW);
		modelService.save(element);
		fireUpdate(element);
	}

	@Override
	public void createInboxElement(IPatient patient, IMandator mandator, PersistentObject object) {
		// InboxElement element = new InboxElement(patient, mandant, object);
		IInboxElement element = modelService.create(IInboxElement.class);
		element.setPatient(patient);
		element.setMandator(mandator);
		element.setObject(object.storeToString());
		element.setState(State.NEW);
		modelService.save(element);
		fireUpdate(element);
	}

	@Override
	public void removeUpdateListener(IInboxUpdateListener listener) {
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}

	@Override
	public void addUpdateListener(IInboxUpdateListener listener) {
		synchronized (listeners) {
			listeners.add(listener);
		}
	}

	@Override
	public List<IInboxElement> getInboxElements(IMandator mandant, IPatient patient, State state) {
		IQuery<IInboxElement> query = modelService.getQuery(IInboxElement.class);
		if (mandant != null) {
			query.and("mandant", COMPARATOR.EQUALS, mandant); //$NON-NLS-1$
		}
		if (patient != null) {
			query.and("patient", COMPARATOR.EQUALS, patient); //$NON-NLS-1$
		}
		if (state != null) {
			query.and("state", COMPARATOR.EQUALS, Integer.toString(state.ordinal())); //$NON-NLS-1$
		}
		query.orderBy("lastupdate", ORDER.DESC);
		return query.execute();
	}

	@Override
	public void changeInboxElementState(IInboxElement element, State state) {
		element.setState(state);
		modelService.save(element);
		fireUpdate(element);
	}

	private void fireUpdate(IInboxElement element) {
		synchronized (listeners) {
			for (IInboxUpdateListener listener : listeners) {
				listener.update(element);
			}
		}
	}

	@Activate
	public void activate() {
		activateProviders();
	}

	@Deactivate
	public void deactivate() {
		deactivateProviders();
	}

	@Override
	public void createInboxElement(IPatient patient, IMandator mandant, String file, boolean copyFile) {
		String path = file;
		if (path != null) {
			File src = new File(path);
			if (src.exists()) {
				if (copyFile) {
					try {
						StringBuilder pathBuilder = new StringBuilder();
						pathBuilder.append("inbox"); //$NON-NLS-1$
						pathBuilder.append(File.separator);
						pathBuilder.append(patient.getPatientNr());
						pathBuilder.append("_"); //$NON-NLS-1$
						pathBuilder.append(System.currentTimeMillis());
						pathBuilder.append("_"); //$NON-NLS-1$
						pathBuilder.append(src.getName());
						File dest = new File(CoreHub.getWritableUserDir(), pathBuilder.toString());
						FileUtils.copyFile(src, dest);
						path = dest.getAbsolutePath();
					} catch (IOException e) {
						LoggerFactory.getLogger(InboxElementService.class).error("file copy error", e); //$NON-NLS-1$
						return;
					}
				}
				IInboxElement element = modelService.create(IInboxElement.class);
				element.setPatient(patient);
				element.setMandator(mandant);
				element.setObject(InboxElementType.FILE.getPrefix() + path);
				element.setState(State.NEW);
				modelService.save(element);

				fireUpdate(element);
			}
		}
	}

	@Override
	public void deactivateProviders() {
		LoggerFactory.getLogger(getClass()).info("Deactivating all ElementProviders"); //$NON-NLS-1$
		ElementsProviderExtension.deactivateAll();
	}

	@Override
	public void activateProviders() {
		// async activation as element provider bundles may have dependency on
		// IInboxElementService
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(() -> {
			LoggerFactory.getLogger(getClass()).info("Activating all ElementProviders"); //$NON-NLS-1$
			ElementsProviderExtension.activateAll();
		});
		executor.shutdown();
	}
}
