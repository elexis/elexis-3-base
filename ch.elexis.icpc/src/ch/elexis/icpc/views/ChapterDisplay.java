/*******************************************************************************
 * Copyright (c) 2008-2010, G. Weirich and Elexis
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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.CodeSelectorHandler;
import ch.elexis.core.ui.actions.ICodeSelectorTarget;
import ch.elexis.data.PersistentObject;
import ch.elexis.icpc.IcpcCode;
import ch.elexis.core.ui.contacts.preferences.UserSettings2;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.viewers.CommonContentProviderAdapter;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;

public class ChapterDisplay extends Composite {
	private static final String UC2_HEADING = "ICPCChapter/";
	FormToolkit tk = UiUiDesk.getToolkit();
	ScrolledForm fLeft;
	String chapter;
	ExpandableComposite[] ec;
	Text tCrit, tIncl, tExcl, tNote;
	
	public ChapterDisplay(Composite parent, final String chapter){
		super(parent, SWT.NONE);
		this.chapter = chapter;
		setLayout(new GridLayout(2, false));
		fLeft = tk.createScrolledForm(this);
		fLeft.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		fLeft.setText(chapter);
		Composite cLeft = fLeft.getBody();
		cLeft.setLayout(new GridLayout());
		cLeft.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		final Composite cRight = tk.createComposite(this);
		cRight.setLayout(new GridLayout());
		cRight.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ec = new ExpandableComposite[IcpcCode.components.length];
		
		for (int i = 0; i < ec.length; i++) {
			String c = IcpcCode.components[i];
			ec[i] = tk.createExpandableComposite(cLeft, ExpandableComposite.TWISTIE);
			ec[i].setText(c);
			UserSettings2.setExpandedState(ec[i], UC2_HEADING + c.substring(0, 1));
			Composite inlay = new Composite(ec[i], SWT.NONE);
			inlay.setLayout(new FillLayout());
			CommonViewer cv = new CommonViewer();
			ViewerConfigurer vc =
				new ViewerConfigurer(new ComponentContentProvider(c), new DefaultLabelProvider(),
					new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_TABLE, SWT.SINGLE, cv));
			ec[i].setData(cv);
			cv.create(vc, inlay, SWT.NONE, this);
			cv.addDoubleClickListener(new CommonViewer.DoubleClickListener() {
				public void doubleClicked(PersistentObject obj, CommonViewer cv){
					ICodeSelectorTarget target =
						CodeSelectorHandler.getInstance().getCodeSelectorTarget();
					if (target != null) {
						target.codeSelected(obj);
					}
				}
			});
			cv.getViewerWidget().addSelectionChangedListener(new ISelectionChangedListener() {
				
				public void selectionChanged(SelectionChangedEvent event){
					IStructuredSelection sel = (IStructuredSelection) event.getSelection();
					if (!sel.isEmpty()) {
						IcpcCode code = (IcpcCode) sel.getFirstElement();
						tCrit.setText(code.get("criteria"));
						tIncl.setText(code.get("inclusion"));
						tExcl.setText(code.get("exclusion"));
						tNote.setText(code.get("note"));
						cRight.layout();
					}
					
				}
			});
			ec[i].addExpansionListener(new ExpansionAdapter() {
				@Override
				public void expansionStateChanging(final ExpansionEvent e){
					ExpandableComposite src = (ExpandableComposite) e.getSource();
					
					if (e.getState() == true) {
						CommonViewer cv = (CommonViewer) src.getData();
						cv.notify(CommonViewer.Message.update);
					}
					UserSettings2.saveExpandedState(UC2_HEADING + src.getText().substring(0, 1),
						e.getState());
				}
				
				public void expansionStateChanged(ExpansionEvent e){
					fLeft.reflow(true);
				}
				
			});
			ec[i].setClient(inlay);
		}
		
		Section sCrit = tk.createSection(cRight, Section.EXPANDED);
		sCrit.setText("Kriterien");
		tCrit = tk.createText(sCrit, "\n", SWT.BORDER | SWT.MULTI);
		sCrit.setClient(tCrit);
		Section sIncl = tk.createSection(cRight, Section.EXPANDED);
		sIncl.setText("Einschliesslich");
		tIncl = tk.createText(sIncl, "\n", SWT.BORDER | SWT.MULTI);
		sIncl.setClient(tIncl);
		Section sExcl = tk.createSection(cRight, Section.EXPANDED);
		sExcl.setText("Ausser");
		tExcl = tk.createText(sExcl, "", SWT.BORDER | SWT.MULTI);
		Section sNote = tk.createSection(cRight, Section.EXPANDED);
		tNote = tk.createText(cRight, "\n", SWT.BORDER | SWT.MULTI);
		sNote.setText("Anmerkung");
		
	}
	
	class ComponentContentProvider extends CommonContentProviderAdapter {
		private String component;
		
		public ComponentContentProvider(String component){
			this.component = component;
		}
		
		public Object[] getElements(Object inputElement){
			return IcpcCode.loadAllFromComponent(chapter, component, false).toArray();
		}
		
	}
	
	public void setComponent(String mode){
		for (int i = 0; i < ec.length; i++) {
			ec[i].setEnabled(true);
		}
		if ("RFE".equals(mode)) {
			// all components enabled
			
		} else if ("DG".equals(mode)) {
			// only 1 and 7 enabled
			for (int i = 1; i < 6; i++) {
				ec[i].setEnabled(false);
				ec[i].setExpanded(false);
			}
		} else if ("PROC".equals(mode)) {
			// 2,3,5,6 enabled
			ec[0].setEnabled(false);
			ec[0].setExpanded(false);
			ec[3].setEnabled(false);
			ec[3].setExpanded(false);
			ec[6].setEnabled(false);
			ec[6].setExpanded(false);
		}
		fLeft.reflow(true);
	}
}
