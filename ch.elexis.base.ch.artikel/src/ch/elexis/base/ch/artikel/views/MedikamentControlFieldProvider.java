/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.elexis.base.ch.artikel.views;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;

import ch.elexis.base.ch.artikel.data.Medikament;
import ch.elexis.core.data.Konsultation;
import ch.elexis.core.data.Query;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.actions.ScannerEvents;
import ch.elexis.core.ui.text.ElexisText;
import ch.elexis.core.ui.util.IScannerListener;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultControlFieldProvider;
import ch.elexis.core.ui.views.KonsDetailView;

public class MedikamentControlFieldProvider extends DefaultControlFieldProvider implements
		IScannerListener {
	
	public MedikamentControlFieldProvider(CommonViewer viewer, String[] flds){
		super(viewer, flds);
	}
	
	public Composite createControl(final Composite parent){
		Composite composite = super.createControl(parent);
		for (final ElexisText selector : selectors) {
			selector.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e){
					if (e.character == SWT.CR) {
						String text = selector.getText();
						text = text.replaceAll(new Character(SWT.CR).toString(), ""); //$NON-NLS-1$
						text = text.replaceAll(new Character(SWT.LF).toString(), ""); //$NON-NLS-1$
						text = text.replaceAll(new Character((char) 0).toString(), ""); //$NON-NLS-1$
						Event scannerEvent = new Event();
						scannerEvent.text = selector.getText();
						scannerEvent.widget = selector.getWidget();
						scannerInput(scannerEvent);
					}
				}
			});
		}
		return composite;
	}
	
	private KonsDetailView getKonsDetailView(){
		IViewReference[] viewReferences =
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getViewReferences();
		for (IViewReference viewRef : viewReferences) {
			if (KonsDetailView.ID.equals(viewRef.getId())) {
				return (KonsDetailView) viewRef.getPart(false);
			}
		}
		return null;
	}
	
	public void scannerInput(Event e){
		KonsDetailView detailView = getKonsDetailView();
		Text text = null;
		if (e.widget instanceof Text) {
			text = (Text) e.widget;
		}
		if (text != null) {
			Query<Medikament> query = new Query<Medikament>(Medikament.class);
			query.add("EAN", "=", e.text); //$NON-NLS-1$
			List<Medikament> medikamentList = query.execute();
			if (medikamentList.size() == 0) {
				ScannerEvents.beep();
			}
			for (Medikament medikament : medikamentList) {
				Konsultation kons =
					(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
				if (kons != null) {
					detailView.addToVerechnung(medikament);
				} else {
					ScannerEvents.beep();
				}
			}
			text.selectAll();
		} else {
			ScannerEvents.beep();
		}
	}
}
