/**
 * Copyright (c) 2010-2012, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 */

package ch.medelexis.templator.ui;

import java.io.File;
import java.util.List;

import javax.annotation.processing.Processor;
import javax.xml.validation.Schema;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.jdom.Element;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.text.ITextPlugin.ICallback;
import ch.elexis.core.ui.util.SWTHelper;
import ch.medelexis.templator.model.IProcessor;
import ch.medelexis.templator.model.ProcessingSchema;

public class ProcessingSchemaDisplay extends Composite {
	private static final String NO_SCHEMA_SELECTED = "Kein Schema ausgewählt";
	private static final String NO_PATIENT_SELECTED = "Kein Patient ausgewählt ";
	private IAction printAction, addAction, directOutputAction;
	ScrolledForm form;
	Composite cFields;
	ToolBar toolBar;
	ProcessingSchema proc;
	private boolean bSaveOnFocusLost = true;
	FocusSaver fs = new FocusSaver();
	// FileFieldEditor ffi;
	Combo cbProcessor;
	Text tTemplate;
	ICallback saveHandler;
	
	public ProcessingSchemaDisplay(Composite parent, ICallback handler){
		super(parent, SWT.NONE);
		if (parent.getLayout() instanceof GridLayout) {
			setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		}
		setLayout(new FillLayout());
		form = UiDesk.getToolkit().createScrolledForm(this);
		saveHandler = handler;
		Composite body = form.getBody();
		body.setLayout(new GridLayout());
		makeActions();
		ToolBarManager tbm = new ToolBarManager(SWT.HORIZONTAL);
		tbm.add(printAction);
		tbm.add(addAction);
		tbm.add(directOutputAction);
		tbm.createControl(body);
		
		Composite cButtons = new Composite(body, SWT.NONE);
		cButtons.setLayout(new GridLayout(3, false));
		cButtons.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		new Label(cButtons, SWT.NONE).setText("Schablone");
		tTemplate = new Text(cButtons, SWT.BORDER);
		tTemplate.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		Button bChoose = new Button(cButtons, SWT.PUSH);
		bChoose.setText("wählen");
		bChoose.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e){
				FileDialog fd = new FileDialog(getShell(), SWT.OPEN);
				fd.setFilterPath(CoreHub.localCfg.get(Preferences.PREF_TEMPLATEBASE,
					System.getProperty("user.home")));
				String fname = fd.open();
				if (fname != null) {
					File f = new File(fname);
					tTemplate.setText(f.getName());
				}
			}
			
		});
		new Label(cButtons, SWT.NONE).setText("Ausgabeprozessor");
		cbProcessor = new Combo(cButtons, SWT.SINGLE);
		cbProcessor.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		IProcessor[] processors = ProcessingSchema.getProcessors();
		for (IProcessor pro : processors) {
			cbProcessor.add(pro.getName());
		}
		cFields = new Composite(body, SWT.NONE);
		cFields.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		cFields.setLayout(new GridLayout(2, false));
		
	}
	
	private void makeActions(){
		printAction = new Action("Ausgeben") {
			{
				setImageDescriptor(Images.IMG_PRINTER.getImageDescriptor());
				setToolTipText("Gibt dieses Dokument mit dem konfigurierten Ausgabeprogramm aus");
			}
			
			@Override
			public void run(){
				save();
				IProcessor pr = proc.getProcessor();
				if (pr == null) {
					SWTHelper.alert("Es ist kein Prozessor definiert",
						"Die Vorlage hat keinen Prozessor. Ausgabe nicht möglich");
				} else {
					pr.doOutput(proc);
				}
				
			}
		};
		addAction = new Action("Variable hinzufügen") {
			{
				setImageDescriptor(Images.IMG_ADDITEM.getImageDescriptor());
				setToolTipText("Ein neues Name-Wert-Paar hinzufügen");
			}
			
			@Override
			public void run(){
				InputDialog id =
					new InputDialog(getShell(), "Neues Prozessorfeld",
						"Geben Sie bitte einen Namen für das Feld ein", "Textfeld",
						new IInputValidator() {
							
							@Override
							public String isValid(String newText){
								if (newText.matches("[a-zA-Z][a-zA-Z0-9-_]*")) {
									return null;
								}
								return "Der Text sollte mit einem Buchstaben anfangen und nur Buchstaben ohne Sonderzeichen oder Ziffern enthalten";
							}
						});
				if (id.open() == Dialog.OK) {
					String name = id.getValue();
					proc.addField(name);
					set(proc);
				}
				
			}
		};
		
		directOutputAction = new Action("Sofort ausgeben", Action.AS_CHECK_BOX) {
			{
				setToolTipText("Beim Drucken direkt zum Ausgabeprozessor senden");
				setImageDescriptor(Images.IMG_NEXT.getImageDescriptor());
			}
			
			@Override
			public void run(){
				proc.setDirectOutput(directOutputAction.isChecked());
			}
		};
		
	}
	
	public void set(ProcessingSchema schema){
		proc = schema;
		for (Control c : cFields.getChildren()) {
			c.removeFocusListener(fs);
			c.dispose();
		}
		for (Element e : schema.getFields()) {
			Label lbl = new Label(cFields, SWT.NONE);
			lbl.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
			Text text = new Text(cFields, SWT.BORDER);
			text.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
			text.addFocusListener(fs);
			String name = e.getAttributeValue("name");
			if (name == null) {
				name = "?";
			}
			lbl.setText(name);
			text.setText(e.getText());
			text.setData(name);
		}
		
		IProcessor p = schema.getProcessor();
		if (p == null) {
			if (cbProcessor.getItemCount() > 0) {
				cbProcessor.select(0);
			} else {
				cbProcessor.setText("unbekannt. kein Prozessor installiert");
			}
		} else {
			cbProcessor.setText(p.getName());
			
		}
		File fTempl = schema.getTemplateFile();
		tTemplate.setText(fTempl == null ? "unbekannt" : fTempl.getName());
		directOutputAction.setChecked(schema.getDirectOutput());
		cFields.layout();
	}
	
	public void save(){
		collect();
		saveHandler.save();
	}
	
	public void setSaveOnFocusLost(boolean bSave){
		bSaveOnFocusLost = bSave;
	}
	
	void collect(){
		for (Control c : cFields.getChildren()) {
			if (c instanceof Text) {
				Text text = (Text) c;
				String con = text.getText();
				String name = (String) text.getData();
				proc.getField(name).setText(con);
			}
		}
		String sProcessor = cbProcessor.getText();
		proc.setProcessor(sProcessor);
		File tmpl = new File(tTemplate.getText());
		proc.setDirectOutput(directOutputAction.isChecked());
		proc.setTemplate(tmpl.getName());
	}
	
	class FocusSaver extends FocusAdapter {
		@Override
		public void focusLost(FocusEvent e){
			if (bSaveOnFocusLost) {
				// proc.getProcessor().doOutput(proc);
				save();
			}
		}
		
	}
	
}
