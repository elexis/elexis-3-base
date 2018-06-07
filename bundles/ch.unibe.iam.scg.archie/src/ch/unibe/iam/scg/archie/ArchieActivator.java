/*******************************************************************************
 * Copyright (c) 2008 Dennis Schenk, Peter Siska.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Dennis Schenk - initial implementation
 *     Peter Siska	 - initial implementation
 *******************************************************************************/
package ch.unibe.iam.scg.archie;

import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.util.Log;
import ch.unibe.iam.scg.archie.Messages;
import ch.unibe.iam.scg.archie.model.AbstractDataProvider;

/**
 * <p>
 * The activator class controls the plug-in life cycle and holds an image
 * registry for images used throughout the PLUGIN.
 * </p>
 * 
 * $Id: ArchieActivator.java 747 2009-07-23 09:14:53Z peschehimself $
 * 
 * @author Peter Siska
 * @author Dennis Schenk
 * @version $Rev: 747 $
 */
public class ArchieActivator extends AbstractUIPlugin {

	/** The shared instance */
	private static ArchieActivator PLUGIN;

	/** The plug-in ID */
	public static final String PLUGIN_ID = "ch.unibe.iam.scg.archie"; //$NON-NLS-1$

	/** Human readable PLUGIN name. */
	public static final String PLUGIN_NAME = "Archie"; //$NON-NLS-1$

	// Images
	public static final String IMG_NEW_QUERY = "query"; //$NON-NLS-1$
	public static final String IMG_COFFEE = "coffee"; //$NON-NLS-1$
	public static final String IMG_IMPORTANT = "important"; //$NON-NLS-1$
	public static final String IMG_WARNING = "warningt"; //$NON-NLS-1$
	public static final String IMG_ERROR = "error"; //$NON-NLS-1$
	public static final String IMG_INFO = "info"; //$NON-NLS-1$
	public static final String IMG_CANCEL = "cancel"; //$NON-NLS-1$
	public static final String IMG_BUTTON_CALENDAR = "buttoCalendar"; //$NON-NLS-1$
	public static final String IMG_DEC_VALID = "decorationValid"; //$NON-NLS-1$
	public static final String IMG_CHART_PIE_BIG = "chartPieBig"; //$NON-NLS-1$
	public static final String IMG_CHART_BAR_BIG = "chartBarBig"; //$NON-NLS-1$
	public static final String IMG_PATIENT_MALE = "user"; //$NON-NLS-1$
	public static final String IMG_PATIENT_FEMALE = "user_female"; //$NON-NLS-1$
	public static final String IMG_GO = "go"; //$NON-NLS-1$
	public static final String IMG_REFRESH = "refresh"; //$NON-NLS-1$

	/** Preference store for this PLUGIN. */
	private static IPreferenceStore PREFERENCE_STORE = null;

	/** Log for this plugin. */
	public static final Log LOG = Log.get(ArchieActivator.PLUGIN_NAME);

	/**
	 * List of all available providers. <b>This variable is built-up upon the
	 * first request in the corresponding getter method to save resources, not
	 * upon activation of the plugin.</b>
	 */
	private TreeMap<String, AbstractDataProvider> providers;

	/**
	 * Map of available provider categories. Category IDs are being mapped to
	 * their names. <b>This variable is built-up upon plugin activation to
	 * ensure that the categories are available later when building the list of
	 * available providers.</b>
	 */
	private Hashtable<String, String> categories;

	// /////////////////////////////////////////////////////////////////////////////
	// CONSTRUCTOR
	// /////////////////////////////////////////////////////////////////////////////

	/** The constructor */
	public ArchieActivator() {
		ArchieActivator.PLUGIN = this;
		ArchieActivator.LOG.log(Messages.ARCHIE_STARTED, Log.SYNCMARK);

		this.initializeAvailableCategories();
	}

	// /////////////////////////////////////////////////////////////////////////////
	// STATIC METHODS
	// /////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns an instance of this activator object.
	 * 
	 * @return The shared instance
	 */
	public static ArchieActivator getInstance() {
		return ArchieActivator.PLUGIN;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Returns an image from this PLUGIN's image registry based on the given
	 * descriptor string.
	 * 
	 * @param descriptor
	 *            Image descriptor string.
	 * @return Image under that given descriptor from the registry.
	 */
	public static Image getImage(String descriptor) {
		return ArchieActivator.getInstance().getImageRegistry().get(descriptor);
	}

	// ////////////////////////////////////////////////////////////////////////////
	// OVERRIDE METHODS
	// ////////////////////////////////////////////////////////////////////////////

	/** {@inheritDoc} */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/** {@inheritDoc} */
	@Override
	public void stop(BundleContext context) throws Exception {
		ArchieActivator.PLUGIN = null;
		super.stop(context);
	}

	/** {@inheritDoc} */
	@Override
	public IPreferenceStore getPreferenceStore() {
		if (ArchieActivator.PREFERENCE_STORE == null) {
			ArchieActivator.PREFERENCE_STORE = new SettingsPreferenceStore(CoreHub.globalCfg);
		}
		return ArchieActivator.PREFERENCE_STORE;
	}

	/** {@inheritDoc} */
	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		super.initializeImageRegistry(registry);

		// Put images into the image registry for this plugin.
		registry.put(IMG_NEW_QUERY, ArchieActivator.getImageDescriptor("icons/database_go.png"));
		registry.put(IMG_COFFEE, ArchieActivator.getImageDescriptor("icons/kteatime.png"));
		registry.put(IMG_IMPORTANT, ArchieActivator.getImageDescriptor("icons/important.png"));
		registry.put(IMG_WARNING, ArchieActivator.getImageDescriptor("icons/warning.png"));
		registry.put(IMG_ERROR, ArchieActivator.getImageDescriptor("icons/error.png"));
		registry.put(IMG_INFO, ArchieActivator.getImageDescriptor("icons/info.png"));
		registry.put(IMG_CANCEL, ArchieActivator.getImageDescriptor("icons/cancel.png"));
		registry.put(IMG_BUTTON_CALENDAR, ArchieActivator.getImageDescriptor("icons/calendar.png"));
		registry.put(IMG_DEC_VALID, ArchieActivator.getImageDescriptor("icons/tick.png"));
		registry.put(IMG_CHART_PIE_BIG, ArchieActivator.getImageDescriptor("icons/chart_pie_big.png"));
		registry.put(IMG_CHART_BAR_BIG, ArchieActivator.getImageDescriptor("icons/chart_bar_big.png"));
		registry.put(IMG_PATIENT_MALE, ArchieActivator.getImageDescriptor("icons/user.png"));
		registry.put(IMG_PATIENT_FEMALE, ArchieActivator.getImageDescriptor("icons/user_female.png"));
		registry.put(IMG_GO, ArchieActivator.getImageDescriptor("icons/control.png"));
		registry.put(IMG_REFRESH, ArchieActivator.getImageDescriptor("icons/arrow_circle_double.png"));
	}

	// ///////////////////////////////////////////////////////////////////////////
	// PUBLIC OBJECT METHODS
	// ///////////////////////////////////////////////////////////////////////////

	/**
	 * Returns the table of available data providers. This method builds up the
	 * table of providers upon first request.
	 * 
	 * @return Table containing all available data providers.
	 */
	public TreeMap<String, AbstractDataProvider> getProviderTable() {
		if (this.providers == null) {
			this.providers = new TreeMap<String, AbstractDataProvider>();

			IExtensionRegistry reg = Platform.getExtensionRegistry();
			IConfigurationElement[] extensions = reg
					.getConfigurationElementsFor("ch.unibe.iam.scg.archie.dataprovider");
			for (int i = 0; i < extensions.length; i++) {
				IConfigurationElement element = extensions[i];
				// only DataProvider elements, as only they have the class
				// attribute
				if ("DataProvider".equals(element.getName())) {
					try {
						Object executable = element.createExecutableExtension("class");

						// check if we have the right class
						if (executable instanceof AbstractDataProvider) {

							// compose category prefix
							String category = element.getAttribute("category") == null ? "" : this
									.getCategoryNameFromId(element.getAttribute("category")) + ": ";

							// add to list of available statistics
							AbstractDataProvider provider = (AbstractDataProvider) executable;
							this.providers.put(category + provider.getName(), provider);
						}
					} catch (CoreException e) {
						String errorMessage = "Error while trying to load the data provider: " + element.getName()
								+ "\n" + e.getLocalizedMessage();
						ArchieActivator.LOG.log(errorMessage, Log.WARNINGS);
						e.printStackTrace();
					}
				}
			}
		}

		// return providers
		return ArchieActivator.getInstance().providers;
	}

	// ///////////////////////////////////////////////////////////////////////////
	// PRIVATE OBJECT METHODS
	// ///////////////////////////////////////////////////////////////////////////

	/**
	 * Fills the categories hash map with available categories and their IDs.
	 * This method needs to be executed before the initialization of the data
	 * provider table in order for the providers to check for their category.
	 */
	private void initializeAvailableCategories() {
		if (this.categories == null) {
			this.categories = new Hashtable<String, String>();

			IExtensionRegistry reg = Platform.getExtensionRegistry();
			IConfigurationElement[] elements = reg.getConfigurationElementsFor("ch.unibe.iam.scg.archie.dataprovider");
			for (int i = 0; i < elements.length; i++) {
				IConfigurationElement element = elements[i];

				// only category elements
				if ("category".equals(element.getName())) {
					this.categories.put(element.getAttribute("id"), element.getAttribute("name"));
				}
			}
		}
	}

	/**
	 * Retrieves the category name from the categories table based on the given
	 * ID.
	 * 
	 * @return The corresponding category name or an empty string if no category
	 *         with the given ID is in the table.
	 */
	private String getCategoryNameFromId(String categoryId) {
		if (this.categories == null) {
			String error = "Provider categories have to be initialized first.";
			ArchieActivator.LOG.log(error, Log.ERRORS);
			throw new IllegalStateException("Provider categories have to be initialized first.");
		}
		for (Entry<String, String> category : this.categories.entrySet()) {
			if (category.getKey().equals(categoryId)) {
				return category.getValue();
			}
		}
		return "";
	}
}