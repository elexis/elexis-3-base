/*******************************************************************************
 * Copyright (c) 2015, Daniel Ludin
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Daniel Ludin (ludin@hispeed.ch) - initial implementation
 *******************************************************************************/
package ch.gpb.elexis.cst;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ch.elexis.core.ui.UiDesk;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	public static final String IMG_ARROW_UP_NAME = "arrow-up";
	public static final String IMG_ARROW_UP_PATH = "icons/arrow-up.png";
	public static final String IMG_ARROW_DOWN_NAME = "arrow-down";
	public static final String IMG_ARROW_DOWN_PATH = "icons/arrow-down.png";
	public static final String IMG_ACTIVE_NAME = "active";
	public static final String IMG_ACTIVE_PATH = "icons/active.png";
	public static final String IMG_PASSIVE_NAME = "passive";
	public static final String IMG_PASSIVE_PATH = "icons/inactive.png";
	public static final String IMG_PDF_NAME = "pdficon_large-32";
	public static final String IMG_PDF_PATH = "icons/pdficon_large-32.png";
	public static final String IMG_PNG_NAME = "png-icon-32";
	public static final String IMG_PNG_PATH = "icons/png-icon-32.png";
	public static final String IMG_LINE_NAME = "line-2px";
	public static final String IMG_LINE_PATH = "icons/line-2px-transp.png";
	public static final String IMG_POINTER_NAME = "pointer.png";
	public static final String IMG_POINTER_PATH = "icons/pointer.png";
	public static final String IMG_CSTGROUP_NAME = "icon-cat-1-16.png";
	public static final String IMG_CSTGROUP_PATH = "icons/icon-cat-1-16.png";
	public static final String IMG_CSTPROFILE_NAME = "icon-cat-2-16.png";
	public static final String IMG_CSTPROFILE_PATH = "icons/icon-cat-2-16.png";

	public static final String IMG_REMINDER_ACTION_NAME = "reminder_action.png";
	public static final String IMG_REMINDER_ACTION_PATH = "icons/reminder_action.png";

	public static final String IMG_REMINDER_DECISION_NAME = "reminder_decision.png";
	public static final String IMG_REMINDER_DECISION_PATH = "icons/reminder_decision.png";

	public static final String IMG_REMINDER_TRIGGER_NAME = "reminder_trigger.png";
	public static final String IMG_REMINDER_TRIGGER_PATH = "icons/reminder_trigger.png";

	public static final String IMG_REMINDER_REMINDER_NAME = "reminder_reminder.png";
	public static final String IMG_REMINDER_REMINDER_PATH = "icons/reminder_reminder.png";

	public static final String IMG_HEART_1_NAME = "heart-icon-sm-1.png";
	public static final String IMG_HEART_1_PATH = "icons/heart-icon-sm-1.png";

	public static final String IMG_HEART_2_NAME = "heart-icon-sm-2.png";
	public static final String IMG_HEART_2_PATH = "icons/heart-icon-sm-2.png";

	public static final String IMG_HEART_3_NAME = "heart-icon-sm-3.png";
	public static final String IMG_HEART_3_PATH = "icons/heart-icon-sm-3.png";

	public static final String IMG_HEART_A_NAME = "heart-icon-sm-a.png";
	public static final String IMG_HEART_A_PATH = "icons/heart-icon-sm-a.png";

	public static final String IMG_HEART_B_NAME = "heart-icon-sm-b.png";
	public static final String IMG_HEART_B_PATH = "icons/heart-icon-sm-b.png";

	public static final String IMG_HEART_C_NAME = "heart-icon-sm-c.png";
	public static final String IMG_HEART_C_PATH = "icons/heart-icon-sm-c.png";

	public static final String IMG_HEART_D_NAME = "heart-icon-sm-d.png";
	public static final String IMG_HEART_D_PATH = "icons/heart-icon-sm-d.png";

	public static final String IMG_HEART_E_NAME = "heart-icon-sm-e.png";
	public static final String IMG_HEART_E_PATH = "icons/heart-icon-sm-e.png";

	public static final String IMG_EXCLAM_NAME = "bell-exclamation.png";
	public static final String IMG_EXCLAM_PATH = "icons/bell-exclamation.png";

	public static final String IMG_DISPLAYONCE_NAME = "displayonce.png";
	public static final String IMG_DISPLAYONCE_PATH = "icons/displayonce.png";

	public static final String IMG_TEST_NAME = "hohe-view-50000h.png";
	public static final String IMG_TEST_PATH = "icons/hohe-view-50000h.png";

	// The plug-in ID png-icon-32
	public static final String PLUGIN_ID = "ch.gpb.elexis.cst"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		UiDesk.runIfWorkbenchRunning(() -> {
			UiDesk.getImageRegistry().put(IMG_ACTIVE_NAME, Activator.getImageDescriptor(IMG_ACTIVE_PATH));
			UiDesk.getImageRegistry().put(IMG_PASSIVE_NAME, Activator.getImageDescriptor(IMG_PASSIVE_PATH));
			UiDesk.getImageRegistry().put(IMG_ARROW_UP_NAME, Activator.getImageDescriptor(IMG_ARROW_UP_PATH));
			UiDesk.getImageRegistry().put(IMG_ARROW_DOWN_NAME, Activator.getImageDescriptor(IMG_ARROW_DOWN_PATH));
			UiDesk.getImageRegistry().put(IMG_PDF_NAME, Activator.getImageDescriptor(IMG_PDF_PATH));
			UiDesk.getImageRegistry().put(IMG_PNG_NAME, Activator.getImageDescriptor(IMG_PNG_PATH));
			UiDesk.getImageRegistry().put(IMG_LINE_NAME, Activator.getImageDescriptor(IMG_LINE_PATH));
			UiDesk.getImageRegistry().put(IMG_POINTER_NAME, Activator.getImageDescriptor(IMG_POINTER_PATH));
			UiDesk.getImageRegistry().put(IMG_REMINDER_ACTION_NAME,
					Activator.getImageDescriptor(IMG_REMINDER_ACTION_PATH));
			UiDesk.getImageRegistry().put(IMG_REMINDER_DECISION_NAME,
					Activator.getImageDescriptor(IMG_REMINDER_DECISION_PATH));
			UiDesk.getImageRegistry().put(IMG_REMINDER_TRIGGER_NAME,
					Activator.getImageDescriptor(IMG_REMINDER_TRIGGER_PATH));
			UiDesk.getImageRegistry().put(IMG_REMINDER_REMINDER_NAME,
					Activator.getImageDescriptor(IMG_REMINDER_REMINDER_PATH));

			UiDesk.getImageRegistry().put(IMG_HEART_1_NAME, Activator.getImageDescriptor(IMG_HEART_1_PATH));
			UiDesk.getImageRegistry().put(IMG_HEART_2_NAME, Activator.getImageDescriptor(IMG_HEART_2_PATH));
			UiDesk.getImageRegistry().put(IMG_HEART_3_NAME, Activator.getImageDescriptor(IMG_HEART_3_PATH));
			UiDesk.getImageRegistry().put(IMG_HEART_A_NAME, Activator.getImageDescriptor(IMG_HEART_A_PATH));
			UiDesk.getImageRegistry().put(IMG_HEART_B_NAME, Activator.getImageDescriptor(IMG_HEART_B_PATH));
			UiDesk.getImageRegistry().put(IMG_HEART_C_NAME, Activator.getImageDescriptor(IMG_HEART_C_PATH));
			UiDesk.getImageRegistry().put(IMG_HEART_D_NAME, Activator.getImageDescriptor(IMG_HEART_D_PATH));
			UiDesk.getImageRegistry().put(IMG_HEART_E_NAME, Activator.getImageDescriptor(IMG_HEART_E_PATH));
			UiDesk.getImageRegistry().put(IMG_EXCLAM_NAME, Activator.getImageDescriptor(IMG_EXCLAM_PATH));
			UiDesk.getImageRegistry().put(IMG_DISPLAYONCE_NAME, Activator.getImageDescriptor(IMG_DISPLAYONCE_PATH));

			UiDesk.getImageRegistry().put(IMG_TEST_NAME, Activator.getImageDescriptor(IMG_TEST_PATH));
		});
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
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
	 * path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
