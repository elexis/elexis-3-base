/*******************************************************************************
 * Copyright (c) 2011-2016 Medevit OG, Medelexis AG
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Descher, initial API and implementaion
 *     Lucia Amman, bug fixes and improvements
 * Sponsors: M. + P. Richter
 *******************************************************************************/
package at.medevit.elexis.gdt.ui;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.ResourceManager;

import at.medevit.elexis.gdt.data.GDTProtokoll;
import at.medevit.elexis.gdt.ui.table.util.ColumnBuilder;
import at.medevit.elexis.gdt.ui.table.util.ColumnBuilder.ICellFormatter;
import at.medevit.elexis.gdt.ui.table.util.IValue;
import at.medevit.elexis.gdt.ui.table.util.IValueFormatter;
import at.medevit.elexis.gdt.ui.table.util.SortColumnComparator;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListenerImpl;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;

public class GDTProtokollView extends ViewPart {
	
	public static final String ID = "at.medevit.elexis.gdt.ui.GDTProtokollView";
	
	private TableViewer tableViewer;
	private Table table;
	private ElexisEventListenerImpl eeli_pat;
	
	public GDTProtokollView(){
		eeli_pat = new ElexisUiEventListenerImpl(Patient.class) {
			@Override
			public void runInUi(ElexisEvent ev){
				reload();
			}
		};
		ElexisEventDispatcher.getInstance().addListeners(eeli_pat);
	}
	
	public void reload(){
		if(!table.isVisible()) return;
		Patient pat = ElexisEventDispatcher.getSelectedPatient();
		if (pat != null)
			tableViewer.setInput(GDTProtokoll.getEntriesForPatient(pat));
	}
	
	@Override
	public void createPartControl(Composite parent){
		initTableViewer(parent);// new TableViewerBuilder(parent);
		
		ColumnBuilder messageDirection = createColumn("");
		messageDirection.bindToProperty("messageDirection");
		messageDirection.format(new ICellFormatter() {
			@Override
			public void formatCell(ViewerCell cell, Object value){
				String direction = (String) value;
				cell.setText("");
				if (direction.equalsIgnoreCase(GDTProtokoll.MESSAGE_DIRECTION_IN)) {
					cell.setImage(ResourceManager.getPluginImage("at.medevit.elexis.gdt",
						"rsc/icons/incoming.png"));
				} else if (direction.equalsIgnoreCase(GDTProtokoll.MESSAGE_DIRECTION_OUT)) {
					cell.setImage(ResourceManager.getPluginImage("at.medevit.elexis.gdt",
						"rsc/icons/outgoing.png"));
				}
			}
		});
		messageDirection.setPixelWidth(23);
		messageDirection.build();
		
		ColumnBuilder entryTime = createColumn("Datum/Uhrzeit");
		entryTime.bindToProperty("entryTime");
		entryTime.format(new IValueFormatter<TimeTool, String>() {
			@Override
			public String format(TimeTool obj){
				return obj.toString(TimeTool.FULL_GER);
			}
			
			@Override
			public TimeTool parse(String obj){
				return new TimeTool(obj);
			}
		});
		entryTime.setPercentWidth(15);
		entryTime.useAsDefaultSortColumn();
		entryTime.build();
		
		ColumnBuilder gdtSatz = createColumn("Satzart");
		gdtSatz.bindToValue(new BaseValue<GDTProtokoll>() {
			@Override
			public Object get(GDTProtokoll entry){
				return entry.get(GDTProtokoll.FLD_MESSAGE_TYPE);
			}
		});
		gdtSatz.setPercentWidth(5);
		gdtSatz.build();
		
		ColumnBuilder bezeichnung = createColumn("Bezeichnung");
		bezeichnung.bindToProperty("bezeichnung");
		bezeichnung.makeEditable();
		bezeichnung.setPercentWidth(25);
		bezeichnung.build();
		
		ColumnBuilder bemerkungen = createColumn("Bemerkungen");
		bemerkungen.bindToProperty("bemerkungen");
		bemerkungen.makeEditable();
		bemerkungen.setPercentWidth(25);
		bemerkungen.build();
		
		ColumnBuilder patient = createColumn("Patient");
		patient.bindToProperty("entryRelatedPatient");
		patient.format(new IValueFormatter<Patient, String>() {
			@Override
			public String format(Patient obj){
				return obj.getLabel();
			}
			
			@Override
			public Patient parse(String obj){
				return null;
			}
			
		});
		patient.setPercentWidth(10);
		patient.build();
		
		ColumnBuilder gegenstelle = createColumn("Gegenstelle");
		gegenstelle.bindToProperty("gegenstelle");
		gegenstelle.setPercentWidth(15);
		gegenstelle.build();
		
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		tableViewer.setInput(null);
		
		MenuManager menuManager = new MenuManager();
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		table.setMenu(menuManager.createContextMenu(table));
		getSite().registerContextMenu(menuManager, tableViewer);
		getSite().setSelectionProvider(tableViewer);
	}
	
	private void initTableViewer(Composite parent){
		this.tableViewer =
			new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER
				| SWT.FULL_SELECTION);
		this.table = tableViewer.getTable();
		
		// set TableColumnLayout to table parent
		this.table.getParent().setLayout(new TableColumnLayout());
		
		// headers / lines visible by default
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);
		
		// sorting
		tableViewer.setComparator(new SortColumnComparator());
	}
	
	@Override
	public void setFocus(){
		reload();
	}
	
	/**
	 * Creates a new ColumnBuilder that can be used to configure the table column. When you have
	 * finished configuring the column, call build() on the ColumnBuilder to create the actual
	 * column.
	 */
	public ColumnBuilder createColumn(String columnHeaderText){
		return new ColumnBuilder(tableViewer, columnHeaderText);
	}
	
	/**
	 * Base class for IValue. This is for values you implement yourself. The generic argument T
	 * refers the type of the element this value can be used on. Implementing set is optional.
	 * 
	 * @author Ralf Ebert <info@ralfebert.de>
	 */
	public abstract class BaseValue<T> implements IValue {
		
		@SuppressWarnings("unchecked")
		public final Object getValue(Object element){
			return get((T) element);
		}
		
		@SuppressWarnings("unchecked")
		public void setValue(Object element, Object value){
			set((T) element, value);
		}
		
		public abstract Object get(T element);
		
		public Object set(T element, Object value){
			throw new UnsupportedOperationException("Overwrite value.set() to set values!");
		}
	}
	
	@Override
	public void dispose(){
		ElexisEventDispatcher.getInstance().removeListeners(eeli_pat);
		super.dispose();
	}
	
}
