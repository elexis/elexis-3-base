/*******************************************************************************
 * Copyright (c) 2006-2008, G. Weirich and Elexis
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ch.elexis.artikel_ch.data.Medikament;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.LabeledInputField;
import ch.elexis.core.ui.views.IDetailDisplay;
import ch.elexis.core.ui.views.artikel.Artikeldetail;
import ch.elexis.core.ui.views.controls.ArticleDefaultSignatureComposite;
import ch.elexis.core.ui.views.controls.StockDetailComposite;

public class MedikamentDetailDisplay implements IDetailDisplay {
	
	Medikament act;
	FormToolkit tk = UiDesk.getToolkit();
	ScrolledForm form;
	LabeledInputField.AutoForm tblArtikel;
	LabeledInputField ifName;
	Text tName;
	
	private StockDetailComposite sdc;
	private ArticleDefaultSignatureComposite adsc;
	
	public Composite createDisplay(final Composite parent, final IViewSite site){
		parent.setLayout(new FillLayout());
		form = tk.createScrolledForm(parent);
		Composite ret = form.getBody();
		ret.setLayout( new TableWrapLayout());
		
		ifName = new LabeledInputField(ret, "Name");
		ifName.setLayoutData(new TableWrapData(TableWrapData.FILL));
		tName = (Text) ifName.getControl();
		tName.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(final FocusEvent e){
				if (act != null) {
					act.setInternalName(tName.getText());
				}
				super.focusLost(e);
			}
			
		});
		tblArtikel =
			new LabeledInputField.AutoForm(ret, Artikeldetail.getFieldDefs(parent.getShell()));
		
		TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
		twd.grabHorizontal = true;
		tblArtikel.setLayoutData(twd);
		
		Group grpStockDetails = new Group(ret, SWT.NONE);
		grpStockDetails.setLayout(new GridLayout(1, false));
		grpStockDetails.setText("Lagerhaltung");
		grpStockDetails.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		sdc = new StockDetailComposite(grpStockDetails, SWT.NONE);
		sdc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Group grpDefaultSignature = new Group(ret, SWT.NONE);
		grpDefaultSignature.setLayout(new GridLayout(1, false));
		grpDefaultSignature.setText("Standard-Signatur");
		grpDefaultSignature.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		adsc = new ArticleDefaultSignatureComposite(grpDefaultSignature, SWT.NONE);
		adsc.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		adsc.initDataBindings(null);
		adsc.setEnabled(false);
		
		return ret;
	}
	
	public Class getElementClass(){
		return Medikament.class;
	}
	
	public void display(final Object obj){
		if (obj instanceof Medikament) {
			act = (Medikament) obj;
			form.setText(act.getLabel());
			tblArtikel.reload(act);
			ifName.setText(act.getInternalName());
			adsc.setArticleToBind(act);
			sdc.setArticle(act);
		} else {
			adsc.setArticleToBind(null);
			sdc.setArticle(null);
		}
		
	}
	
	public String getTitle(){
		return Messages.MedikamentDetailDisplay_Title;
	}
	
}
