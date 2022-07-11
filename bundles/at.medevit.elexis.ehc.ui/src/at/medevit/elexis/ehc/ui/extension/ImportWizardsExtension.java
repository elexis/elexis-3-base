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
package at.medevit.elexis.ehc.ui.extension;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.ehc.ui.dialog.WizardCategory;
import at.medevit.elexis.ehc.ui.dialog.WizardDescriptor;

public class ImportWizardsExtension {
	private static Logger logger = LoggerFactory.getLogger(ImportWizardsExtension.class);

	private static final String OTHER_CATEGORYID = "at.medevit.elexis.ehc.ui.OtherCategoryId"; //$NON-NLS-1$

	private static List<IWizardCategory> cacheCategoriesList;

	private static List<IWizardDescriptor> cacheWizardsList;

	public static List<IWizardCategory> getCategories(boolean refresh) {
		if (refresh || cacheCategoriesList == null)
			refreshCache();

		return cacheCategoriesList;
	}

	public static List<IWizardDescriptor> getWizards(boolean refresh) {
		if (refresh || cacheWizardsList == null)
			refreshCache();

		return cacheWizardsList;
	}

	public static void setImportDocument(InputStream document) {
		List<IWizardDescriptor> wizards = getWizards(false);
		for (IWizardDescriptor iWizardDescriptor : wizards) {
			try {
				IImportWizard wizard = (IImportWizard) iWizardDescriptor.createWizard();
				wizard.setDocument(document);
			} catch (CoreException e) {
				logger.error("Could not create import wizard.", e); //$NON-NLS-1$
			}
		}
	}

	private static void refreshCache() {
		cacheCategoriesList = new ArrayList<IWizardCategory>();

		cacheWizardsList = new ArrayList<IWizardDescriptor>();

		logger.info("Initializing or refreshing Import Wizards."); //$NON-NLS-1$
		IExtensionRegistry exr = Platform.getExtensionRegistry();
		IExtensionPoint exp = exr.getExtensionPoint("at.medevit.elexis.ehc.ui.ehcimport"); //$NON-NLS-1$
		if (exp != null) {
			IExtension[] extensions = exp.getExtensions();
			logger.info("Initializing or refreshing Import Wizards found " + extensions.length + " implementations."); //$NON-NLS-1$ //$NON-NLS-2$
			for (IExtension ex : extensions) {
				IConfigurationElement[] elems = ex.getConfigurationElements();
				for (IConfigurationElement el : elems) {
					if (el.getName().equals("category")) { //$NON-NLS-1$
						WizardCategory category = new WizardCategory(el);
						cacheCategoriesList.add(category);
					}
				}
			}
			for (IExtension ex : extensions) {
				IConfigurationElement[] elems = ex.getConfigurationElements();
				for (IConfigurationElement el : elems) {
					if (el.getName().equals("wizard")) { //$NON-NLS-1$
						WizardDescriptor descriptor = new WizardDescriptor(el);
						IWizardCategory matchingCategory = null;
						// find matching category
						for (IWizardCategory category : cacheCategoriesList) {
							if (category.getId().equals(descriptor.getCategoryId())) {
								matchingCategory = category;
								break;
							}
						}
						// find other category
						if (matchingCategory == null) {
							for (IWizardCategory category : cacheCategoriesList) {
								if (category.getId().equals(OTHER_CATEGORYID)) {
									matchingCategory = category;
									break;
								}
							}
						}
						// create other category
						if (matchingCategory == null) {
							WizardCategory category = new WizardCategory(OTHER_CATEGORYID, "Other");
							cacheCategoriesList.add(category);
							matchingCategory = category;
						}
						matchingCategory.addWizard(descriptor);
						cacheWizardsList.add(descriptor);
					}
				}
			}
		}
	}
}
