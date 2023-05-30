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

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.molemax.Messages;
import ch.elexis.molemax.data.MolemaxACL;
import ch.elexis.molemax.data.Tracker;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.TimeTool;

public class ImageSlot extends Composite implements DropTargetListener {
	// slots
	public static final int LEFT_1 = 0;
	public static final int FRONT_1 = 1;
	public static final int RIGHT_1 = 2;
	public static final int BACK_1 = 3;

	public static final int LEFT_2 = 4;
	public static final int FRONT_2 = 5;
	public static final int RIGHT_2 = 6;
	public static final int BACK_2 = 7;

	public static final int LEFT_3 = 8;
	public static final int FRONT_3 = 9;
	public static final int RIGHT_3 = 10;
	public static final int BACK_3 = 11;

	public static final String CAPTION_NOOP = Messages.ImageSlot_notPermitted;
	public static final String TEXT_NOOP = Messages.ImageSlot_insufficientRights;
	public static final String INSUFF_RIGHTS = Messages.ImageSlot_insufficientRights2;

	private Tracker[] myTracker;

	final int mySlot;
	private final Overview home;
	private final MenuItem mDelete;

	public ImageSlot(final Overview home, final Composite parent, final int slotNr) {
		super(parent, SWT.BORDER);
		DropTarget dt = new DropTarget(this, DND.DROP_COPY);
		dt.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		dt.addDropListener(this);
		setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		myTracker = home.trackers[slotNr];
		mySlot = slotNr;
		this.home = home;
		addPaintListener(new PaintListener() {
			public void paintControl(final PaintEvent e) {
				GC gc = e.gc;
				double scale = 1.0;
				if ((myTracker.length > 0) && (myTracker[0] != null)) {
					Point pt = getSize();
					if (AccessControlServiceHolder.get().request(MolemaxACL.SEE_IMAGES)) {
						Image img = myTracker[0].createImage();
						if (img != null) {
							ImageData idata = img.getImageData();
							if (idata != null) {
								scale = (double) pt.x / (double) idata.width;
								gc.drawImage(img, 0, 0, idata.width, idata.height, 0, 0,
										(int) Math.round(idata.width * scale), (int) Math.round(idata.height * scale));
							}
						}
					} else {
						SWTHelper.writeCentered(gc, INSUFF_RIGHTS, new Rectangle(0, 0, pt.x, pt.y));
					}

				}
			}

		});
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseUp(final MouseEvent e) {
				if (e.button == 1) {
					ImageSlot ims = (ImageSlot) e.getSource();
					home.dispRow.rightContents.setslot(ims.mySlot);
					home.setTopControl(home.dispRow);
					home.dispRow.setRow(ims.mySlot % 4);
				}
			}

		});
		Menu menu = new Menu(this);
		mDelete = new MenuItem(menu, SWT.NONE);
		mDelete.setText(Messages.ImageSlot_delete);
		mDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				int num = myTracker.length;
				String question = Messages.ImageSlot_reallydelete;
				if (num > 1) {
					question = Messages.ImageSlot_these + Integer.toString(num) + Messages.ImageSlot_imagesdelete;
				}
				if (SWTHelper.askYesNo(Messages.ImageSlot_imageDel, question)) {
					Tracker.delete(myTracker);
					redraw();
				}

			}

		});
		setMenu(menu);
		setUser();
	}

	public void setUser() {
		mDelete.setEnabled(AccessControlServiceHolder.get().request(MolemaxACL.CHANGE_IMAGES));
	}

	public void dragEnter(final DropTargetEvent event) {
		event.detail = DND.DROP_COPY;
	}

	public void dragLeave(final DropTargetEvent event) {
		// TODO Auto-generated method stub

	}

	public void dragOperationChanged(final DropTargetEvent event) {
		// TODO Auto-generated method stub

	}

	public void dragOver(final DropTargetEvent event) {
		// TODO Auto-generated method stub

	}

	public void drop(final DropTargetEvent event) {
		if (AccessControlServiceHolder.get().request(MolemaxACL.CHANGE_IMAGES)) {
			String[] files = (String[]) event.data;
			TimeTool today = new TimeTool();
			TimeTool seq = new TimeTool(home.date);
			long diff = today.diff(seq, 60000 * 60 * 24);
			if (diff > 1) {
				if (SWTHelper.askYesNo(Messages.ImageSlot_newsequence,
						Messages.ImageSlot_chosensequenceis + diff + Messages.ImageSlot_daysold)) {
					home.setPatient(home.pat, today.toString(TimeTool.DATE_GER));
				}
			}
			for (String file : files) {
				if (myTracker.length == 0) {
					myTracker = new Tracker[1];
					myTracker[0] = new Tracker(home.pat, home.date, mySlot, new File(file));
				} else {
					if (SWTHelper.askYesNo(Messages.ImageSlot_replace, Messages.ImageSlot_deleteall)) {
						Tracker.delete(myTracker);
						myTracker = new Tracker[1];
						myTracker[0] = new Tracker(home.pat, home.date, mySlot, new File(file));
					}
				}

			}
			redraw();
		} else {
			SWTHelper.alert(CAPTION_NOOP, TEXT_NOOP);
		}
	}

	public void dropAccept(final DropTargetEvent event) {
		// TODO Auto-generated method stub

	}

	public void setImage(final File file) {
		if (AccessControlServiceHolder.get().request(MolemaxACL.CHANGE_IMAGES)) {
			if (file.getName().startsWith("base")) {
				if (myTracker.length != 0) {
					if (SWTHelper.askYesNo(Messages.ImageSlot_replace, Messages.ImageSlot_deleteall)) {
						Tracker.delete(myTracker);
					} else {
						return;
					}
				}
				myTracker = new Tracker[1];
				myTracker[0] = new Tracker(home.pat, home.date, mySlot, file);
				// myTracker[0].setFile(file);
			} else {
				if (myTracker.length == 0) {
					SWTHelper.showError("Fehlerhafte Struktur",
							"Dieses Verzeichnis " + file.getParentFile().getAbsolutePath() + " enthält kein Basisbild");
				} else {
					String[] koord = file.getName().split("-");
					Rectangle rec = new Rectangle(Integer.parseInt(koord[0]), Integer.parseInt(koord[1]),
							Integer.parseInt(koord[2]), Integer.parseInt(koord[3]));
					Tracker t = new Tracker(home.pat, myTracker[0], home.date, mySlot, rec);
					Tracker[] tOld = home.trackers[mySlot];
					Tracker[] tNew = new Tracker[tOld.length + 1];
					for (int i = 0; i < tOld.length; i++) {
						tNew[i] = tOld[i];
					}
					tNew[tOld.length] = t;
					home.trackers[mySlot] = tNew;
					t.setFile(file);
				}
			}
		}
	}

}
