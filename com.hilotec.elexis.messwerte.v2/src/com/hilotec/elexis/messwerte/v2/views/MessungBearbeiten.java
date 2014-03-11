/*******************************************************************************
 * 
 * The authorship of this code and the accompanying materials is held by 
 * medshare GmbH, Switzerland. All rights reserved. 
 * http://medshare.net
 * 
 * This code and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0
 * 
 * Year of publication: 2012
 * 
 *******************************************************************************/

package com.hilotec.elexis.messwerte.v2.views;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.PlatformHelper;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;

import com.hilotec.elexis.messwerte.v2.data.Messung;
import com.hilotec.elexis.messwerte.v2.data.MessungKonfiguration;
import com.hilotec.elexis.messwerte.v2.data.MessungTyp;
import com.hilotec.elexis.messwerte.v2.data.Messwert;
import com.hilotec.elexis.messwerte.v2.data.MesswertBase;
import com.hilotec.elexis.messwerte.v2.data.Panel;
import com.hilotec.elexis.messwerte.v2.data.typen.IMesswertTyp;
import com.hilotec.elexis.messwerte.v2.data.typen.MesswertTypCalc;
import com.hilotec.elexis.messwerte.v2.data.typen.MesswertTypDate;
import com.tiff.common.ui.datepicker.DatePickerCombo;

/**
 * Dialog um eine Messung zu bearbeiten oder neu zu erstellen
 * 
 * @author Patrick Chaubert
 */
public class MessungBearbeiten extends TitleAreaDialog {
	private final Messung messung;
	private List<Messwert> messwerte;
	private final List<Messwert> shownMesswerte;
	private final List<Messwert> calcFields;
	private DatePickerCombo dateWidget;
	private String tabtitle;
	
	private final Listener listener = new Listener() {
		public void handleEvent(Event event){
			Boolean validValues = true;
			for (Messwert mw : shownMesswerte) {
				IMesswertTyp typ = mw.getTyp();
				if (!typ.checkInput(mw, typ.getValidpattern())) {
					setErrorMessage(typ.getTitle() + ": " + typ.getInvalidmessage()); //$NON-NLS-1$
					validValues = false;
					break;
				}
			}
			if (validValues) {
				setErrorMessage(null);
				for (Messwert mw : calcFields) {
					((MesswertTypCalc) mw.getTyp()).calcNewValue(mw);
				}
			}
		}
	};
	
	public MessungBearbeiten(final Shell parent, Messung m){
		super(parent);
		messung = m;
		shownMesswerte = new ArrayList<Messwert>();
		calcFields = new ArrayList<Messwert>();
	}
	
	public MessungBearbeiten(Shell shell, Messung m, String text){
		this(shell, m);
		tabtitle = text;
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		
		Composite comp = null;
		Composite row = null;
		Label lbl = null;
		
		ScrolledComposite scroll =
			new ScrolledComposite(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		scroll.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		comp = new Composite(scroll, SWT.NONE);
		scroll.setContent(comp);
		
		comp.setLayout(new GridLayout());
		
		row = new Composite(comp, SWT.NONE);
		row.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Patient pat = ElexisEventDispatcher.getSelectedPatient();
		
		lbl = new Label(row, SWT.NONE);
		lbl.setText(Messages.MessungBearbeiten_PatientLabel);
		lbl.setLayoutData(new RowData(90, SWT.DEFAULT));
		
		lbl = new Label(row, SWT.NONE);
		lbl.setText(pat.getLabel() + " (" + pat.getAlter() + ") - [" //$NON-NLS-1$ //$NON-NLS-2$
			+ pat.get(Patient.FLD_PATID).toString() + "]"); //$NON-NLS-1$
		
		row = new Composite(comp, SWT.NONE);
		row.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		lbl = new Label(row, SWT.NONE);
		lbl.setText(Messages.MessungBearbeiten_MessungLabel);
		lbl.setLayoutData(new RowData(90, SWT.DEFAULT));
		
		dateWidget = new DatePickerCombo(row, SWT.NONE);
		dateWidget.setFormat(new SimpleDateFormat(MesswertTypDate.DATE_FORMAT)); //$NON-NLS-1$
		dateWidget.setDate(new TimeTool(messung.getDatum()).getTime());
		dateWidget.setLayoutData(new RowData(60, SWT.DEFAULT));
		
		Label shadow_sep_h = new Label(comp, SWT.SEPARATOR | SWT.SHADOW_OUT | SWT.HORIZONTAL);
		shadow_sep_h.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		
		messwerte = messung.getMesswerte();
		MessungTyp typ = messung.getTyp();
		
		Panel p = typ.getPanel();
		if (p != null) {
			createCompositeWithLayout(typ.getPanel(), comp);
		} else {
			createCompositeWithoutLayout(comp);
		}
		comp.setSize(comp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		return scroll;
	}
	
	private Composite createCompositeWithoutLayout(Composite parent){
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout());
		for (Messwert messwert : messung.getMesswerte()) {
			Label l = new Label(c, SWT.NONE);
			IMesswertTyp dft = messwert.getTyp();
			String labelText = dft.getTitle();
			if (!dft.getUnit().equals("")) { //$NON-NLS-1$
				labelText += " [" + dft.getUnit() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			l.setText(labelText);
			dft.createWidget(c, messwert);
		}
		return c;
	}
	
	private Composite createCompositeWithLayout(Panel p, Composite parent){
		Composite c = new Composite(parent, SWT.NONE);
		String panelType = MessungKonfiguration.ELEMENT_LAYOUTPANEL_PLAIN;
		if (p != null)
			panelType = p.getType();
		if (panelType.equals(MessungKonfiguration.ELEMENT_LAYOUTPANEL_PLAIN)) {
			c.setLayout(new GridLayout());
		} else if (panelType.equals(MessungKonfiguration.ELEMENT_LAYOUTDISPLAY)) {
			c.setLayout(new GridLayout());
			Browser browser = new Browser(c, SWT.NONE);
			String url = p.getAttribute(MessungKonfiguration.ELEMENT_LAYOUTDISPLAY_URL);
			browser.setUrl(url == null ? "" : url); //$NON-NLS-1$
			GridData gd = SWTHelper.getFillGridData(1, true, 1, true);
			String bounds = p.getAttribute(MessungKonfiguration.ELEMENT_LAYOUTDISPLAY_SIZE);
			if (bounds != null) {
				String[] coord = bounds.trim().split("\\s*,\\s*"); //$NON-NLS-1$
				if (coord.length == 2) {
					gd = new GridData(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]));
					gd.grabExcessHorizontalSpace = true;
					gd.grabExcessVerticalSpace = true;
				}
			}
			browser.setLayoutData(gd);
		} else if (panelType.equals(MessungKonfiguration.ELEMENT_LAYOUTLABEL)) {
			c.setLayout(new GridLayout());
			Label l = new Label(c, SWT.WRAP);
			l.setText(p.getAttribute(MessungKonfiguration.ELEMENT_LAYOUTLABEL_TEXT));
		} else if (panelType.equals(MessungKonfiguration.ELEMENT_LAYOUTGRID)) {
			String cols = p.getAttribute(MessungKonfiguration.ELEMENT_LAYOUTGRID_COLUMNS);
			if (cols == null) {
				c.setLayout(new GridLayout());
			} else {
				c.setLayout(new GridLayout(Integer.parseInt(cols), false));
			}
			
		} else if (panelType.equals(MessungKonfiguration.ELEMENT_LAYOUTFIELD)) {
			String fieldref = p.getAttribute(MessungKonfiguration.ELEMENT_LAYOUTFIELD_REF);
			Messwert mw = getMesswert(fieldref);
			if (mw != null) {
				IMesswertTyp dft = mw.getTyp();
				
				boolean bEditable = true;
				String attr = p.getAttribute(MessungKonfiguration.ELEMENT_LAYOUTFIELD_EDITABLE);
				if (attr != null && attr.equals("false")) { //$NON-NLS-1$
					bEditable = false;
				}
				dft.setEditable(bEditable);
				
				String validpattern =
					p.getAttribute(MessungKonfiguration.ELEMENT_LAYOUTFIELD_VALIDPATTERN);
				if (validpattern == null) {
					validpattern = "[\u0000-\uFFFF]*"; //$NON-NLS-1$
				}
				dft.setValidpattern(validpattern);
				String invalidMsg =
					p.getAttribute(MessungKonfiguration.ELEMENT_LAYOUTFIELD_INVALIDMESSAGE);
				if (invalidMsg == null) {
					invalidMsg = Messages.MessungBearbeiten_InvalidValue;
				}
				dft.setInvalidmessage(invalidMsg);
				
				c.setLayout(new GridLayout());
				
				String labelText = dft.getTitle();
				if (!dft.getUnit().equals("")) { //$NON-NLS-1$
					labelText += " [" + dft.getUnit() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
				}
				
				Composite labelRow = new Composite(c, SWT.NONE);
				labelRow.setLayout(new GridLayout(2, false));
				labelRow.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
				
				Label tl = new Label(labelRow, SWT.NONE);
				tl.setText(labelText);
				tl.setLayoutData(SWTHelper.getFillGridData(1, false, 1, false));
				
				Image image =
					new Image(c.getDisplay(),
						PlatformHelper.getBasePath("com.hilotec.elexis.messwerte.v2") //$NON-NLS-1$
							+ File.separator + "rsc" + File.separator //$NON-NLS-1$
							+ MesswertBase.ICON_TRANSPARENT);
				// label für icons, wenn Wert ausserhalb des konfigurierten Bereichs liegt
				Label il = new Label(labelRow, SWT.NONE);
				il.setImage(image);
				il.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, true, 1, 1));
				mw.setIconLabel(il);
				
				Widget w = dft.createWidget(c, mw);
				
				if (dft instanceof MesswertTypCalc) {
					calcFields.add(mw);
				} else {
					w.addListener(SWT.Selection, listener);
					w.addListener(SWT.Modify, listener);
				}
				IMesswertTyp typ = mw.getTyp();
				typ.checkInput(mw, typ.getValidpattern());
				shownMesswerte.add(mw);
				setLayoutData(c);
			}
		}
		if (p != null) {
			for (Panel panel : p.getPanels()) {
				setLayoutData(createCompositeWithLayout(panel, c));
			}
		}
		return c;
	}
	
	private void setLayoutData(Composite c){
		if (c.getParent().getLayout() instanceof GridLayout) {
			c.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		}
		c.pack();
	}
	
	public Messwert getMesswert(String name){
		for (Messwert m : messwerte) {
			if (m.getName().equals(name)) {
				return m;
			}
		}
		return null;
	}
	
	@Override
	public void create(){
		super.create();
		getShell().setText(Messages.MessungBearbeiten_EditMessung);
		String title = messung.getTyp().getTitle();
		String descr = messung.getTyp().getDescription();
		if (!"".equals(descr)) //$NON-NLS-1$
			title = title + ": " + descr; //$NON-NLS-1$
		setTitle(title);
	}
	
	@Override
	protected Control createContents(Composite parent){
		Control contents = super.createContents(parent);
		setTitle(tabtitle);
		return contents;
	}
	
	@Override
	public void okPressed(){
		boolean validValues = true;
		TimeTool tt = new TimeTool(dateWidget.getDate().getTime());
		messung.setDatum(tt.toString(TimeTool.DATE_GER));
		for (Messwert mw : shownMesswerte) {
			IMesswertTyp typ = mw.getTyp();
			if (!typ.checkInput(mw, typ.getValidpattern())) {
				setErrorMessage(typ.getTitle() + ": " + typ.getInvalidmessage()); //$NON-NLS-1$
				validValues = false;
				break;
			} else {
				typ.saveInput(mw);
			}
		}
		if (validValues) {
			messung.set("deleted", "0"); //$NON-NLS-1$ //$NON-NLS-2$
			close();
		}
	}
	
	@Override
	public boolean close(){
		for (Messwert mw : shownMesswerte) {
			mw.getTyp().setShown(false);
		}
		return super.close();
	}
}
