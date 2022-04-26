/*******************************************************************************
 * Copyright (c) 2006-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/

package ch.elexis.base.befunde;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Dieses Plugin und diese Perspektive sind eine Demonstration dafür, wie man
 * eigene Kategorien etc. in Elexis einbauen kann. Dieses Beispiel erstelt eine
 * eigene Perspektive namens "Befunde", bindet diese in der Startleiste ein und
 * fügt eine View "Messwerte" hinzu. Ausserdem wird eine Preference- Seite
 * erzeugt, in der der Anwender einstellen kann, was für Messwerte er eingeben
 * will.
 *
 * @author gerry
 *
 *         This Plugin and Perspective are showing how to add its own categories
 *         etc.. in Elexis This example sets a perspective named "Befunde"
 *         (which means "Findings" i.e. for a patient) binds it to the initial
 *         layout and defines a view named "Messwerte" (which means "mesured
 *         values"). Moreover, there will be a Preference page where the user
 *         can specify which "mesured values" he wants to enter. please note the
 *         entries in plugin.xml for this perspective and the view
 *
 */
public class BefundePerspektive implements IPerspectiveFactory {
	public static final String ID = "ch.elexis.befunde.perspektive"; //$NON-NLS-1$

	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(false);

		IFolderLayout folder = layout.createFolder("folder", IPageLayout.TOP, 1.0f, IPageLayout.ID_EDITOR_AREA); //$NON-NLS-1$
		// folder.addView(MesswerteView.ID);
		folder.addView(FindingsView.ID);
	}

}
