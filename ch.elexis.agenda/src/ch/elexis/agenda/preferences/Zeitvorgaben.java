/*******************************************************************************
 * Copyright (c) 2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/
package ch.elexis.agenda.preferences;

import java.util.Hashtable;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.TableCursor;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.agenda.Messages;
import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.util.Plannables;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.util.SWTHelper;

/**
 * Einstellen von Zeitvorgaben für jeden Termintyp und jeden Mandanten Unter agenda/zeitvorgaben ist
 * für jeden mandanten ein String der Form std=x[::Termintyp=x]... abgelegt, wobei std die
 * Zeitvorgabe ist, wenn keine der folgenden Termintypen passt. (Es muss nicht für alle Termintypen
 * eine Vorgabe bei jedem mandanten gemacht werden). Wenn eine Vorgabe 0 ist, dann hat dieser
 * Mandant den entsprechenden Termintyp gar nicht.
 * 
 * @author gerry
 * 
 */
public class Zeitvorgaben extends PreferencePage implements IWorkbenchPreferencePage {
	
	Table table;
	TableColumn[] cols;
	TableItem[] rows;
	TableCursor cursor;
	ControlEditor editor;
	String[] bereiche;
	
	public Zeitvorgaben(){
		super(Messages.Zeitvorgaben_timePrefs);
	}
	
	@Override
	protected Control createContents(Composite parent){
		// parent.setLayout(new GridLayout());
		Composite check = new Composite(parent, SWT.BORDER);
		check.setLayout(new GridLayout());
		bereiche =
			CoreHub.globalCfg.get(PreferenceConstants.AG_BEREICHE, Messages.Zeitvorgaben_praxis)
				.split(",");
		
		table = new Table(check, SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
		cols = new TableColumn[bereiche.length + 1];
		cols[0] = new TableColumn(table, SWT.NONE);
		cols[0].setText(Messages.Zeitvorgaben_terminTypes);
		cols[0].setWidth(70);
		
		cursor = new TableCursor(table, SWT.NONE);
		editor = new ControlEditor(cursor);
		editor.grabHorizontal = true;
		editor.grabVertical = true;
		
		cursor.addSelectionListener(new SelectionAdapter() {
			// Tabellenauswahl soll dem Cursor folgen
			public void widgetSelected(SelectionEvent e){
				table.setSelection(new TableItem[] {
					cursor.getRow()
				});
			}
			
			// Eingabetaste
			public void widgetDefaultSelected(SelectionEvent e){
				TableItem row = cursor.getRow();
				int column = cursor.getColumn();
				doEdit(row.getText(column));
			}
		});
		// Sonstige Taste
		cursor.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e){
				if (e.character > 0x30) {
					StringBuilder sb = new StringBuilder();
					sb.append(e.character);
					doEdit(sb.toString());
				}
			}
		});
		
		int i = 1;
		for (String bereich : bereiche) {
			cols[i] = new TableColumn(table, SWT.NONE);
			cols[i].setWidth(70);
			cols[i++].setText(bereich);
		}
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableItem t0 = new TableItem(table, SWT.NONE);
		for (String typ : Termin.TerminTypes) {
			if (typ.equals(Termin.typFrei()) || typ.equals(Termin.typReserviert())) {
				continue;
			}
			TableItem it = new TableItem(table, SWT.NONE);
			it.setText(0, typ);
			i = 1;
			for (String bereich : bereiche) {
				Hashtable<String, String> map = Plannables.getTimePrefFor(bereich);
				String tStd = map.get("std"); //$NON-NLS-1$
				String tTyp = map.get(typ);
				t0.setText(i, tStd);
				if (tTyp == null) {
					tTyp = tStd;
				}
				it.setText(i++, tTyp);
			}
		}
		
		table.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		return check;
	}
	
	public void init(IWorkbench workbench){
		// TODO Auto-generated method stub
		
	}
	
	private void doEdit(String inp){
		final Text text = new Text(cursor, SWT.BORDER);
		text.setText(inp);
		text.setSelection(inp.length());
		text.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e){
				if ((e.character == SWT.CR) || (e.keyCode == SWT.ARROW_DOWN)) {
					TableItem it = cursor.getRow();
					int idx = cursor.getColumn(); // Spalte der Anzeige
					String ntext = text.getText();
					it.setText(idx, text.getText());
					Hashtable<String, String> map = Plannables.getTimePrefFor(cols[idx].getText());
					map.put(it.getText(0), ntext);
					Plannables.setTimePrefFor(cols[idx].getText(), map);
					text.dispose();
					// cursorDown();
				}
				// close the text editor when the user hits "ESC"
				if (e.character == SWT.ESC) {
					text.dispose();
				}
			}
		});
		editor.setEditor(text);
		text.setFocus();
	}
	
}
