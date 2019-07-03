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

import org.apache.commons.io.FileUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.inbox.model.IInboxElementService;
import at.medevit.elexis.inbox.model.IInboxUpdateListener;
import at.medevit.elexis.inbox.model.InboxElement;
import at.medevit.elexis.inbox.model.InboxElementType;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.Identifiable;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;

@Component(immediate = true)
public class InboxElementService implements IInboxElementService {
	
	HashSet<IInboxUpdateListener> listeners = new HashSet<IInboxUpdateListener>();
	
	@Override
	public void createInboxElement(Patient patient, Kontakt mandant, PersistentObject object){
		InboxElement element = new InboxElement(patient, mandant, object);
		fireUpdate(element);
	}
	
	@Override
	public void createInboxElement(IPatient patient, IMandator mandant, Identifiable object){
		InboxElement element = new InboxElement(patient, mandant, object);
		fireUpdate(element);
	}
	
	@Override
	public void removeUpdateListener(IInboxUpdateListener listener){
		synchronized (listeners) {
			listeners.remove(listener);
		}
	}
	
	@Override
	public void addUpdateListener(IInboxUpdateListener listener){
		synchronized (listeners) {
			listeners.add(listener);
		}
	}
	
	@Override
	public List<InboxElement> getInboxElements(Mandant mandant, Patient patient, State state){
		Query<InboxElement> qie = new Query<InboxElement>(InboxElement.class);
		if (mandant != null) {
			qie.add(InboxElement.FLD_MANDANT, "=", mandant.getId());
		}
		if (patient != null) {
			qie.add(InboxElement.FLD_PATIENT, "=", patient.getId());
		}
		if (state != null) {
			qie.add(InboxElement.FLD_STATE, "=", Integer.toString(state.ordinal()));
		}
		return qie.execute();
	}
	
	@Override
	public void changeInboxElementState(InboxElement element, State state){
		element.set(InboxElement.FLD_STATE, Integer.toString(state.ordinal()));
		fireUpdate(element);
	}
	
	private void fireUpdate(InboxElement element){
		synchronized (listeners) {
			for (IInboxUpdateListener listener : listeners) {
				listener.update(element);
			}
		}
	}
	
	@Activate
	public void activate(){
		activateProviders();
	}
	
	@Deactivate
	public void deactivate(){
		deactivateProviders();
	}
	
	@Override
	public void createInboxElement(Patient patient, Kontakt mandant, String file, boolean copyFile){
		String path = file;
		if (path != null) {
			File src = new File(path);
			if (src.exists()) {
				if (copyFile) {
					try {
						StringBuilder pathBuilder = new StringBuilder();
						pathBuilder.append("inbox");
						pathBuilder.append(File.separator);
						pathBuilder.append(patient.getPatCode());
						pathBuilder.append("_");
						pathBuilder.append(System.currentTimeMillis());
						pathBuilder.append("_");
						pathBuilder.append(src.getName());
						File dest = new File(CoreHub.getWritableUserDir(), pathBuilder.toString());
						FileUtils.copyFile(src, dest);
						path = dest.getAbsolutePath();
					} catch (IOException e) {
						LoggerFactory.getLogger(InboxElementService.class).error("file copy error",
							e);
						return;
					}
				}
				InboxElement element =
					new InboxElement(patient, mandant, InboxElementType.FILE.getPrefix() + path);
				fireUpdate(element);
			}
		}
	}
	
	@Override
	public void deactivateProviders(){
		LoggerFactory.getLogger(getClass()).info("Deactivating all ElementProviders");
		ElementsProviderExtension.deactivateAll();
	}
	
	@Override
	public void activateProviders(){
		LoggerFactory.getLogger(getClass()).info("Activating all ElementProviders");
		ElementsProviderExtension.activateAll();
	}
}
