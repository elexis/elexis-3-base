/*******************************************************************************
 * Copyright (c) 2007-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.icpc.views;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import ch.elexis.core.ui.constants.UiResourceConstants;
import ch.elexis.core.ui.fastview.ElexisFastViewUtil;
import ch.elexis.core.ui.laboratory.views.LaborView;
import ch.elexis.core.ui.medication.views.DauerMediView;
import ch.elexis.core.ui.views.AUF2;
import ch.elexis.core.ui.views.AUFZeugnis;
import ch.elexis.core.ui.views.FaelleView;
import ch.elexis.core.ui.views.FallDetailView;
import ch.elexis.core.ui.views.KompendiumView;
import ch.elexis.core.ui.views.KonsDetailView;
import ch.elexis.core.ui.views.KonsListe;
import ch.elexis.core.ui.views.PatHeuteView;
import ch.elexis.core.ui.views.ReminderView;
import ch.elexis.core.ui.views.RezeptBlatt;
import ch.elexis.core.ui.views.RezepteView;
import ch.elexis.core.ui.views.TextView;
import ch.elexis.core.ui.views.codesystems.DiagnosenView;
import ch.elexis.core.ui.views.codesystems.LeistungenView;

public class IcpcPerspektive implements IPerspectiveFactory {
	public static final String ID = "ch.elexis.icpc.perspective";
	
	public void createInitialLayout(IPageLayout layout){
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(false);
		layout.addView(EpisodesView.ID, IPageLayout.LEFT, 0.2f, editorArea);
		IFolderLayout ifr = layout.createFolder("zentrum", IPageLayout.RIGHT, 0.95f, editorArea); //$NON-NLS-1$
		IFolderLayout obenrechts =
			layout.createFolder("obenrechts", IPageLayout.RIGHT, 0.7f, "zentrum");
		obenrechts.addView(EncounterView.ID);
		obenrechts.addView(ReminderView.ID);
		IFolderLayout untenlinks =
			layout.createFolder("untenlinks", IPageLayout.BOTTOM, 0.4f, EpisodesView.ID);
		untenlinks.addView(FaelleView.ID);
		untenlinks.addView(ch.elexis.core.ui.medication.views.DauerMediView.ID);
		// layout.addView(FaelleView.ID, IPageLayout.BOTTOM, 0.5f, EpisodesView.ID);
		IFolderLayout untenrechts =
			layout.createFolder("untenrechts", IPageLayout.BOTTOM, 0.3f, "obenrechts");
		// layout.addView(KonsListe.ID,IPageLayout.BOTTOM,0.3f,EncounterView.ID);
		untenrechts.addView(KonsListe.ID);
		untenrechts.addView(PatHeuteView.ID);
		ifr.addView(KonsDetailView.ID);
		ifr.addView(KompendiumView.ID);
		ifr.addView(LaborView.ID);
		ifr.addPlaceholder(RezeptBlatt.ID);
		ifr.addPlaceholder(AUFZeugnis.ID);
		ifr.addPlaceholder(TextView.ID);
		ifr.addPlaceholder(FallDetailView.ID);
		
		IFolderLayout bfr = layout.createFolder("unten", IPageLayout.BOTTOM, 0.7f, "zentrum");
		bfr.addView(AUF2.ID);
		bfr.addView(RezepteView.ID);
		
		ElexisFastViewUtil.addToFastViewAfterPerspectiveOpened(ID,
			UiResourceConstants.PatientenListeView_ID, DiagnosenView.ID, LeistungenView.ID);
		
		layout.addShowViewShortcut(EpisodesView.ID);
		layout.addShowViewShortcut(EncounterView.ID);
		layout.addShowViewShortcut(KonsDetailView.ID);
//		layout.addShowViewShortcut(LaborView.ID);
		layout.addShowViewShortcut(KonsListe.ID);
		layout.addShowViewShortcut(ReminderView.ID);
		layout.addShowViewShortcut(DauerMediView.ID);
	}
}
