/*******************************************************************************
 * Copyright (c) 2007-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *******************************************************************************/

package ch.elexis.base.ch.medikamente.bag.views;

import java.io.File;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;

import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.selectors.FieldDescriptor.Typ;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.artikel.ArtikelContextMenu;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.medikamente.bag.data.BAGMedi;
import ch.elexis.medikamente.bag.data.BAGMediFactory;

public class BAGMediSelector extends CodeSelectorFactory {
	public static final String FIELD_NAME = "Name";
	public static final String FIELD_SUBSTANCE = "Substanz";
	public static final String FIELD_NOTES = "Notizen";
	
	private IAction sameOfGroupAction, genericsAction, onStockAction;
	CommonViewer cv;
	SelectorPanelProvider slp;
	FieldDescriptor<?>[] fields = {
		new FieldDescriptor<BAGMedi>("Name", FIELD_NAME, FieldDescriptor.Typ.STRING, null),
		new FieldDescriptor<BAGMedi>("Substanz", FIELD_SUBSTANCE, Typ.STRING, null),
		new FieldDescriptor<BAGMedi>("Notizen", FIELD_NOTES, Typ.STRING, null)
	};
	BagMediContentProvider fdl;
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		makeActions();
		ArtikelContextMenu menu =
			new ArtikelContextMenu((BAGMedi) new BAGMediFactory().createTemplate(BAGMedi.class), cv);
		menu.addAction(sameOfGroupAction);
		slp = new SelectorPanelProvider(fields, true);
		slp.addActions(genericsAction, onStockAction);
		fdl = new BagMediContentProvider(cv, new Query<BAGMedi>(BAGMedi.class));
		
		this.cv = cv;
		return new ViewerConfigurer(fdl, new BAGMediLabelProvider(), slp,
			new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
				SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, null));
		
	}
	
	@Override
	public void dispose(){
		cv.dispose();
		fdl.dispose();
	}
	
	@Override
	public String getCodeSystemName(){
		return BAGMedi.CODESYSTEMNAME;
	}
	
	@Override
	public Class<? extends PersistentObject> getElementClass(){
		return BAGMedi.class;
	}
	
	private void makeActions(){
		sameOfGroupAction = new Action("Selbe therap. Gruppe") {
			{
				setImageDescriptor(Images.IMG_FILTER.getImageDescriptor());
				setToolTipText("Zeige alle Medikamente derselben therapeutischen Gruppe");
			}
			
			@Override
			public void run(){
				// ContentProvider cp = (ContentProvider) cv.getConfigurer().getContentProvider();
				BAGMedi selected = (BAGMedi) cv.getSelection()[0];
				fdl.setGroup(selected.get("Gruppe"));
				slp.fireChangedEvent();
			}
			
		};
		genericsAction = new Action("Nur Generika") {
			ImageDescriptor image_off, image_on;
			{
				String img_off = "icons" + File.separator + "ggruen.png";
				String img_on = "icons" + File.separator + "ggruen_on.png";
				image_off = BAGMediFactory.loadImageDescriptor(img_off);
				image_on = BAGMediFactory.loadImageDescriptor(img_on);
				setImageDescriptor(image_off);
				setToolTipText("Nur Generika anzeigen");
			}
			
			@Override
			public void run(){
				if (fdl.toggleGenericsOnly()) {
					setImageDescriptor(image_on);
				} else {
					setImageDescriptor(image_off);
				}
				slp.fireChangedEvent();
			}
		};
		onStockAction = new Action("Nur Lagerartikel") {
			ImageDescriptor image_on = null;
			ImageDescriptor image_off = null;
			{
				String img_off = "icons" + File.separator + "lager.png";
				String img_on = "icons" + File.separator + "lager_on.png";
				image_off = BAGMediFactory.loadImageDescriptor(img_off);
				image_on = BAGMediFactory.loadImageDescriptor(img_on);
				setImageDescriptor(image_off);
				setToolTipText("Nur Lagerartikel anzeigen");
			}
			
			@Override
			public void run(){
				
				if (fdl.toggleStockOnly()) {
					setImageDescriptor(image_on);
				} else {
					setImageDescriptor(image_off);
				}
				slp.fireChangedEvent();
			}
		};
	}
}
