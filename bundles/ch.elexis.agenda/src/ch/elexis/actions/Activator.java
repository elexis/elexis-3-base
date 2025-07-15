/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation, adapted from JavaAgenda
 *
 *******************************************************************************/
package ch.elexis.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.agenda.BereichSelectionHandler;
import ch.elexis.agenda.Messages;
import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.data.interfaces.scripting.Interpreter;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Kontakt;
import ch.rgw.tools.TimeTool;

/**
 * Einen Activator braucht man immer dann, wenn man irgendwelche Dinge sicher zu
 * Beginn der Plugin-Aktivierung ausgeführt haben will. Wir verwenden das hier,
 * um die AgendaActions zu initialisieren.
 */
public class Activator extends AbstractUIPlugin {

	private static final Logger log = LoggerFactory.getLogger(Activator.class);

	// The plug-in ID
	public static final String PLUGIN_ID = "ch.elexis.agenda"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	public static final String IMG_HOME = "ch.elexis.agenda.home"; //$NON-NLS-1$
	public static final String IMG_RECURRING_DATE = "ch.elexis.agenda.series"; //$NON-NLS-1$
	private String actResource;
	private TimeTool actDate;

	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		// log.log("activated", Log.DEBUGMSG);
		UiDesk.runIfWorkbenchRunning(() -> {
			UiDesk.getImageRegistry().put(IMG_HOME, getImageDescriptor("icons/calendar_view_day.png")); //$NON-NLS-1$
			UiDesk.getImageRegistry().put(IMG_RECURRING_DATE, getImageDescriptor("icons/arrow-repeat.png")); //$NON-NLS-1$
		});
		// enable scripting access to classes
		Interpreter.classLoaders.add(Activator.class.getClassLoader());
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative
	 * path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.agenda", path); //$NON-NLS-1$
	}

	public String[] getResources() {
		return ConfigServiceHolder.getGlobal(PreferenceConstants.AG_BEREICHE, Messages.TagesView_14).split(","); //$NON-NLS-1$
	}

	public String getActResource() {
		if (actResource == null) {
			actResource = Activator.getDefault().getResources()[0];
		}
		return actResource;
	}

	public void setActResource(final String resname) {
		actResource = resname;
		ConfigServiceHolder.setUser(PreferenceConstants.AG_BEREICH, resname);
		UiDesk.asyncExec(new Runnable() {
			@Override
			public void run() {
				BereichSelectionHandler.updateListeners();
			}
		});
	}

	public TimeTool getActDate() {
		if (actDate == null) {
			actDate = new TimeTool();
		}
		return new TimeTool(actDate);
	}

	public void setActDate(final String date) {
		if (actDate == null) {
			actDate = new TimeTool();
		}
		actDate.set(date);
	}

	public void setActDate(final TimeTool date) {
		if (actDate == null) {
			actDate = new TimeTool();
		}
		actDate.set(date);
	}

	public TimeTool addDays(final int day) {
		if (actDate == null) {
			actDate = new TimeTool();
		}
		actDate.addDays(day);
		return new TimeTool(actDate);
	}

	/**
	 * propagate a termin selection through the system
	 *
	 * @param termin
	 */
	public void dispatchTermin(final Termin termin) {
		Kontakt contact = null;
		if (termin.isRecurringDate()) {
			Termin rootTermin = Termin.load(termin.get(Termin.FLD_LINKGROUP));
			if (rootTermin != null && rootTermin.exists()) {
				contact = rootTermin.getKontakt();
			}
		} else {
			contact = termin.getKontakt();
		}
		ContextServiceHolder.get().setTyped(NoPoUtil.loadAsIdentifiable(termin, IAppointment.class).orElse(null));
		if (contact != null) {
			if (contact.istPatient()) {
				ContextServiceHolder.get()
						.setActivePatient(NoPoUtil.loadAsIdentifiable(contact, IPatient.class).orElse(null));
			} else {
				ContextServiceHolder.get().setTyped(NoPoUtil.loadAsIdentifiable(contact, IContact.class).orElse(null));
			}

		}
	}
}
