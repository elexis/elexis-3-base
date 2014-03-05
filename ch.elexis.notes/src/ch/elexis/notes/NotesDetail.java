/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/
package ch.elexis.notes;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.text.ETFTextPlugin;
import ch.elexis.core.ui.text.ITextPlugin.ICallback;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.ExHandler;

/**
 * Dislplay details (text., links, keywords) of a note
 * 
 * @author gerry
 * 
 */
public class NotesDetail extends Composite {
	private ETFTextPlugin etf;
	List lRefs;
	Text tKeywords;
	ScrolledForm fNote, fRefs;
	FormToolkit tk = UiDesk.getToolkit();
	private IAction newRefAction, delRefAction;
	Note actNote;
	
	NotesDetail(Composite parent){
		super(parent, SWT.NONE);
		etf = new ETFTextPlugin();
		
		setLayout(new GridLayout());
		SashForm sash = new SashForm(this, SWT.VERTICAL);
		sash.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		fNote = tk.createScrolledForm(sash);
		fNote.getBody().setLayout(new GridLayout());
		etf.createContainer(fNote.getBody(), new SaveCallback()).setLayoutData(
			SWTHelper.getFillGridData(1, true, 1, true));
		etf.setSaveOnFocusLost(true);
		tKeywords = tk.createText(fNote.getBody(), ""); //$NON-NLS-1$
		tKeywords.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		tKeywords.addFocusListener(new FocusAdapter() {
			
			@Override
			public void focusLost(FocusEvent e){
				if (actNote != null) {
					actNote.setKeywords(tKeywords.getText());
				}
				super.focusLost(e);
			}
			
		});
		fRefs = tk.createScrolledForm(sash);
		fRefs.getBody().setLayout(new GridLayout());
		lRefs = new List(fRefs.getBody(), SWT.SINGLE);
		lRefs.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e){
				String[] sel = lRefs.getSelection();
				if (sel.length > 0) {
					execute(sel[0]);
				}
			}
			
		});
		lRefs.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		fRefs.setText(Messages.NotesDetail_xrefs);
		makeActions();
		fRefs.getToolBarManager().add(newRefAction);
		fRefs.getToolBarManager().add(delRefAction);
		fRefs.updateToolBar();
		tk.adapt(lRefs, true, true);
		sash.setWeights(new int[] {
			80, 20
		});
	}
	
	public void setNote(Note note){
		actNote = note;
		fNote.setText(note.get("Title")); //$NON-NLS-1$
		etf.loadFromByteArray(note.getContent(), false);
		// etf.insertText("",note.get("Contents"),SWT.LEFT);
		tKeywords.setText(note.getKeywords());
		lRefs.removeAll();
		for (String s : note.getRefs()) {
			lRefs.add(s);
		}
	}
	
	/**
	 * Run a program to view an external file
	 * 
	 * @param filename
	 */
	public void execute(String filename){
		try {
			int r = filename.lastIndexOf('.');
			String ext = ""; //$NON-NLS-1$
			if (r != -1) {
				ext = filename.substring(r + 1);
			}
			Program proggie = Program.findProgram(ext);
			if (proggie != null) {
				proggie.execute(filename);
			} else {
				if (Program.launch(filename) == false) {
					Runtime.getRuntime().exec(filename);
				}
				
			}
			
		} catch (Exception ex) {
			ExHandler.handle(ex);
			SWTHelper.showError(Messages.NotesDetail_couldNotLaunch, ex.getMessage());
		}
	}
	
	private void makeActions(){
		newRefAction = new Action(Messages.NotesDetail_newActionCaption) {
			{
				setToolTipText(Messages.NotesDetail_newActionToolTip);
				setImageDescriptor(Images.IMG_NEW.getImageDescriptor());
			}
			
			public void run(){
				if (new AddLinkDialog(getShell(), actNote).open() == Dialog.OK) {
					setNote(actNote);
				}
			}
		};
		delRefAction = new Action(Messages.NotesDetail_deleteActionCaption) {
			{
				setToolTipText(Messages.NotesDetail_deleteActionToolTip);
				setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
			}
			
			public void run(){
				String actRef = lRefs.getSelection()[0];
				if (SWTHelper.askYesNo(Messages.NotesDetail_deleteConfirmCaption,
					Messages.NotesDetail_deleteConfirmMessage)) {
					actNote.removeRef(actRef);
					setNote(actNote);
				}
			}
		};
	}
	
	class SaveCallback implements ICallback {
		
		public void save(){
			byte[] cnt = etf.storeToByteArray();
			actNote.setContent(cnt);
			
		}
		
		public boolean saveAs(){
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}
