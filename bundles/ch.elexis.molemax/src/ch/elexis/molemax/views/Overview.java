/*******************************************************************************
 * Copyright (c) 2007-2014 G. Weirich, A. Brögli and A. Häffner.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    rgw - initial API and implementation
 *    rgw - 2014: Changes for Elexis 2.x
 ******************************************************************************/
package ch.elexis.molemax.views;

import java.io.File;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.events.RefreshingPartListener;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.data.Anwender;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.molemax.Messages;
import ch.elexis.molemax.data.Tracker;
import ch.rgw.tools.TimeTool;
import jakarta.inject.Inject;

/**
 * This is the view that shows 4x3 images<br>
 * <table>
 * <tr>
 * <td>head left</td>
 * <td>head front</td>
 * <td>head right</td>
 * <td>head back</td>
 * </tr>
 * <tr>
 * <td>waist left</td>
 * <td>waist front</td>
 * <td>waist right</td>
 * <td>waist back</td>
 * </tr>
 * <tr>
 * <td>leg left</td>
 * <td>leg front</td>
 * <td>leg right</td>
 * <td>lef back</td>
 * </tr>
 * </table>
 * Images are saved in a directory named ny the date and subdirectories
 * named<br>
 * 0-4 (head)<br>
 * 5-8 (waist<br>
 * 9-11 (legs)<br>
 * There is always one image called base.jpg that is the overview image of the
 * given region. Other images in the same subdirectory are detail images that
 * are named x-y-w-h-seq.jpg from their origin
 *
 * @author Gerry
 *
 */
public class Overview extends ViewPart implements IRefreshable {
	public static final String ID = "molemax.overview";
	Form form;
	FormToolkit tk;
	protected Tracker[][] trackers;
	private StackLayout stack;
	private Composite inlay;
	AllSlotsDisplay dispAll;
	RowDisplay dispRow;
	TimeMachineDisplay tmd;
	Patient pat;
	String date;
	Composite outer;
	private IAction selectDateAction, restoreAction /* , newDateAction */;

	public Overview() {
		tk = UiDesk.getToolkit();
	}

	private RefreshingPartListener udpateOnVisible = new RefreshingPartListener(this);

	@Optional
	@Inject
	void activePatient(IPatient patient) {
		CoreUiUtil.runAsyncIfActive(() -> {
			setPatient((Patient) NoPoUtil.loadAsPersistentObject(patient), null);
		}, form);
	}

	@Override
	public void createPartControl(final Composite parent) {
		form = tk.createForm(parent);
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		Composite body = form.getBody();
		body.setLayout(new FillLayout());
		inlay = new Composite(body, SWT.BORDER);
		// inlay.setForeground(Desk.theColorRegistry.get(UiDesk.COL_LIGHTBLUE));
		stack = new StackLayout();
		inlay.setLayout(stack);
		dispAll = new AllSlotsDisplay(this, inlay);
		dispRow = new RowDisplay(this, inlay);
		tmd = new TimeMachineDisplay(this, inlay);
		trackers = new Tracker[12][];
		outer = parent;
		date = new TimeTool().toString(TimeTool.DATE_COMPACT);
		makeActions();
		ViewMenus menu = new ViewMenus(getViewSite());
		menu.createMenu(restoreAction);
		menu.createToolbar(selectDateAction);
		setTopControl(dispAll);

		getSite().getPage().addPartListener(udpateOnVisible);
	}

	@Override
	public void dispose() {
		getSite().getPage().removePartListener(udpateOnVisible);
		for (int i = 0; i < 12; i++) {
			if (trackers[i] != null)
				Tracker.dispose(trackers[i]);
		}
		dispAll.dispose();
		dispRow.dispose();
		super.dispose();
	}

	void setTopControl(final Composite top) {
		stack.topControl = top;
		ScrollBar sc = dispRow.right.getHorizontalBar();
		int sub = 12; // cheap workaround
		if (sc != null) {
			Point pt = sc.getSize();
			sub += pt.y;
		}
		inlay.setSize(outer.getClientArea().width, outer.getClientArea().height - sub);
		inlay.layout();
	}

	/**
	 * set a new Patient or date - Find all images for this patient and the given
	 * date
	 *
	 * @param p   the patient
	 * @param dat the date. if date is null, take the latest available sequenze.
	 */
	public void setPatient(final Patient p, String dat) {
		if (p == null) {
			form.setText(Messages.Overview_noPatient);
			return;
		}
		if (dat == null) {
			dat = Tracker.getLastSequenceDate(p);
		}
		// save time if this patient and date are already selected
		if (p.equals(pat) && dat.equals(date)) {
			return;
		}
		for (int i = 0; i < 12; i++) {
			if (trackers[i] != null)
				Tracker.dispose(trackers[i]);
		}
		pat = p;
		date = dat;
		for (int i = 0; i < 12; i++) {
			Tracker base = Tracker.loadBase(p, date, i);
			trackers[i] = Tracker.getImageStack(base);
		}
		dispAll.reload();
		form.setText(p.getLabel()); // +", ab "+dat);
		setTopControl(dispAll);
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public void refresh() {
		activePatient(ContextServiceHolder.get().getActivePatient().orElse(null));
	}

	public void clearEvent(final Class<? extends PersistentObject> template) {
		if (template.equals(Patient.class)) {
			setPatient(null, null);
		}

	}

	public void selectionEvent(final PersistentObject obj) {
		if (obj instanceof Anwender) {
			dispAll.setUser();
		}
		if (obj instanceof Patient) {
			setPatient((Patient) obj, null);
		}

	}

	private void makeActions() {
		selectDateAction = new Action(Messages.Overview_baseDate) {
			{
				setImageDescriptor(Overview.getImageDescriptor("icons/notiz.png"));
				setToolTipText(Messages.Overview_selectSequence);
			}

			@Override
			public void run() {
				BaseSelectorDialog bsd = new BaseSelectorDialog(getViewSite().getShell(), pat);
				if (bsd.open() == Dialog.OK) {
					setPatient(pat, bsd.ret);
				}
			}

		};
		restoreAction = new Action(Messages.Overview_restore) {
			{
				setImageDescriptor(Overview.getImageDescriptor("icons/rescue.gif"));
				setToolTipText(Messages.Overview_restoresequence);
			}

			@Override
			public void run() {
				DirectoryDialog dlg = new DirectoryDialog(UiDesk.getTopShell());
				dlg.setMessage("Geben Sie das Basisverzeichnis der zu importierenden Sequenz ein");
				dlg.setText("Bildsequenz rekonstruieren");
				String dirname = dlg.open();
				if (dirname != null) {
					File dir = new File(dirname);
					if (dir.getName().matches("20[0-9][0-9][01][0-9][0-3][0-9]")) {
						TimeTool ttDate = new TimeTool(dir.getName());
						setPatient(pat, ttDate.toString(TimeTool.DATE_GER));

						File[] subdirs = dir.listFiles();
						for (File sub : subdirs) {
							if (sub.getName().matches("[0-9]{1,2}")) {
								File[] imgs = sub.listFiles();
								boolean baseSet = false;
								for (File img : imgs) {
									if (img.getName().matches("base\\..+")) {
										dispAll.addImageFromSequence(Integer.parseInt(sub.getName()), img);
										baseSet = true;
									}
								}
								if (baseSet) {
									for (File img : imgs) {
										if (img.getName().matches("base\\..+")) {
											continue;
										}
										dispAll.addImageFromSequence(Integer.parseInt(sub.getName()), img);
									}
								}

							}
						}
					} else {
						SWTHelper.showError("Import nicht möglich", "Der Verzeichnisname ist nicht yyyymmdd");
					}
				}
			}
		};
		/*
		 * newDateAction=new Action("Neu..."){ {
		 * setImageDescriptor(Desk.theImageRegistry .getDescriptor(Desk.IMG_NEW));
		 * setToolTipText("Eine neue Basissequenz erstellen"); }
		 *
		 * @Override public void run() {
		 *
		 * super.run(); } };
		 */
	}

	public static ImageDescriptor getImageDescriptor(final String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.molemax", path); //$NON-NLS-1$
	}

}
