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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.text.model.Samdas;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.events.RefreshingPartListener;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.text.ExternalLink;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.IRefreshable;
import ch.rgw.compress.CompEx;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Result;

/**
 * The eclipse View for the notes. Contains a master-Part (NotesList) and a
 * details-part (NotesDetail)
 *
 * @author gerry
 *
 */
public class NotesView extends ViewPart implements IRefreshable {
	static final String ID = "ch.elexis.notes.view"; //$NON-NLS-1$
	ScrolledForm fMaster;
	NotesList master;
	NotesDetail detail;
	boolean hasScanner = false;
	private IAction newCategoryAction, newNoteAction, delNoteAction, scanAction;
	FormToolkit tk = UiDesk.getToolkit();

	private RefreshingPartListener udpateOnVisible = new RefreshingPartListener(this);

	@Override
	public void createPartControl(Composite parent) {

		SashForm sash = new SashForm(parent, SWT.HORIZONTAL);
		fMaster = tk.createScrolledForm(sash);
		fMaster.getBody().setLayout(new GridLayout());
		master = new NotesList(fMaster.getBody());
		master.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		detail = new NotesDetail(sash);
		// detail.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		makeActions();
		fMaster.setText(Messages.NotesView_categories);
		if (hasScanner) {
			fMaster.getToolBarManager().add(scanAction);
			fMaster.getToolBarManager().add(new Separator());
		}
		fMaster.getToolBarManager().add(newCategoryAction);
		fMaster.getToolBarManager().add(newNoteAction);
		fMaster.getToolBarManager().add(delNoteAction);
		fMaster.getToolBarManager().add(new Separator());
		newNoteAction.setEnabled(false);
		detail.setEnabled(false);
		fMaster.updateToolBar();
		sash.setWeights(new int[] { 3, 7 });
		// fDetail.updateToolBar();
		// fDetail.reflow(true);
		getSite().getPage().addPartListener(udpateOnVisible);
	}

	public void dispose() {
		getSite().getPage().removePartListener(udpateOnVisible);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	private void makeActions() {
		newCategoryAction = new Action(Messages.NotesView_newCategory) {
			{
				setToolTipText(Messages.NotesView_createMainCategoryTootltip);
				setImageDescriptor(Images.IMG_NEW.getImageDescriptor());
			}

			public void run() {
				InputDialog id = new InputDialog(getViewSite().getShell(),
						Messages.NotesView_createMainCategoryDlgTitle, Messages.NotesView_createMainCategoryDlgMessage,
						StringUtils.EMPTY, null);
				if (id.open() == Dialog.OK) {
					/* Note note= */new Note(null, id.getValue(), StringUtils.EMPTY);
					master.tv.refresh();
				}
			}
		};
		newNoteAction = new Action(Messages.NotesView_newNoteCaption) {
			{
				setToolTipText(Messages.NotesView_newNoteTooltip);
				setImageDescriptor(Images.IMG_ADDITEM.getImageDescriptor());
			}

			public void run() {
				Note act = (Note) ElexisEventDispatcher.getSelected(Note.class);
				if (act != null) {
					InputDialog id = new InputDialog(getViewSite().getShell(), Messages.NotesView_newNoteDlgTitle,
							Messages.NotesView_newNoteDlgMessage, StringUtils.EMPTY, null);
					if (id.open() == Dialog.OK) {
						/* Note note= */new Note(act, id.getValue(), StringUtils.EMPTY);
						master.tv.refresh();
					}
				}
			}

		};
		delNoteAction = new Action(Messages.NotesView_deleteActionCaption) {
			{
				setToolTipText(Messages.NotesView_deleteActionTooltip);
				setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
			}

			public void run() {
				Note act = (Note) ElexisEventDispatcher.getSelected(Note.class);
				if (act != null) {
					if (SWTHelper.askYesNo(Messages.NotesView_deleteConfirmDlgTitle,
							Messages.NotesView_deleteConfirmDlgMessage)) {
						act.delete();
						master.tv.refresh();
					}
				}
			}

		};
		// Check if there is a scanner service available and if so, create a
		// "Scan" button
		if (Extensions.isServiceAvailable(GlobalServiceDescriptors.SCAN_TO_PDF)) {
			hasScanner = true;
			scanAction = new Action(Messages.NotesView_scabCaption) {
				{
					setToolTipText(Messages.NotesView_scanTooltip);
					ImageDescriptor imgScanner = AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.notes", //$NON-NLS-1$
							"icons" + File.separator + "scanner.ico"); //$NON-NLS-1$ //$NON-NLS-2$
					setImageDescriptor(imgScanner);
				}

				public void run() {
					try {
						Object scanner = Extensions.findBestService(GlobalServiceDescriptors.SCAN_TO_PDF);
						if (scanner != null) {
							Result<byte[]> res = (Result<byte[]>) Extensions.executeService(scanner, "acquire", //$NON-NLS-1$
									new Class[0], new Object[0]);
							if (res.isOK()) {
								Note act = (Note) ElexisEventDispatcher.getSelected(Note.class);
								byte[] pdf = res.get();
								InputDialog id = new InputDialog(getViewSite().getShell(),
										Messages.NotesView_importDocuDlgTitle, Messages.NotesView_importDocDlgMessage,
										StringUtils.EMPTY, null);
								if (id.open() == Dialog.OK) {
									String name = id.getValue();
									String basedir = CoreHub.localCfg.get(Preferences.CFGTREE, null);
									if (basedir == null) {
										SWTHelper.alert(Messages.NotesView_badBaseDirectoryTitle,
												Messages.NotesView_badBaseDirectoryMessage);
										return;
									}
									File file = new File(basedir, name.replaceAll("\\s", "_") //$NON-NLS-1$ //$NON-NLS-2$
											+ ".pdf"); //$NON-NLS-1$
									if (!file.createNewFile()) {
										SWTHelper.alert(Messages.NotesView_importErrorTitle,
												"Kann Datei " + file.getAbsolutePath() + " nicht schreiben");
										return;
									}
									FileOutputStream fout = new FileOutputStream(file);
									BufferedOutputStream bout = new BufferedOutputStream(fout);
									bout.write(pdf);
									bout.close();
									Samdas samdas = new Samdas(name);
									Samdas.Record record = samdas.getRecord();
									Samdas.XRef xref = new Samdas.XRef(ExternalLink.ID, file.getAbsolutePath(), 0,
											name.length());
									record.add(xref);
									XMLOutputter xo = new XMLOutputter(Format.getRawFormat());
									String cnt = xo.outputString(samdas.getDocument());
									byte[] nb = CompEx.Compress(cnt.getBytes("utf-8"), CompEx.ZIP); //$NON-NLS-1$
									/* Note note= */new Note(act, name, nb, "text/xml"); //$NON-NLS-1$
									master.tv.refresh();
								}

							}
						}

					} catch (Exception ex) {
						ExHandler.handle(ex);
						SWTHelper.showError(Messages.NotesView_importErrorDlgTitle, ex.getMessage());
					}
				}
			};
		}

	}

	@Override
	public void refresh() {
		activeNote(ContextServiceHolder.get().getTyped(Note.class).orElse(null));
	}

	@Inject
	void activeNote(@Optional Note note) {
		CoreUiUtil.runAsyncIfActive(() -> {
			if (note != null) {
				detail.setEnabled(true);
				detail.setNote(note);
				if (newNoteAction != null) {
					newNoteAction.setEnabled(true);
				}
			} else {
				if (newNoteAction != null) {
					newNoteAction.setEnabled(false);
				}
			}
		}, fMaster);
	}
}
