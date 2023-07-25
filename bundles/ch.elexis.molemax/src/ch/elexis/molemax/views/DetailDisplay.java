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
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
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

import ch.elexis.core.ac.EvACE;
import ch.elexis.core.ac.Right;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.services.holder.AccessControlServiceHolder;
import ch.elexis.molemax.Messages;
import ch.elexis.molemax.data.Tracker;
import ch.elexis.core.ui.util.SWTHelper;

public class DetailDisplay extends Composite {
	private org.eclipse.swt.widgets.Tracker mouseTracker;
	private final DetailDisplay self;
	private final Overview home;
	private int actSlot;
	private final Menu menu;
	private final MenuItem mDelete;

	public DetailDisplay(final Composite parent, final Overview h) {
		super(parent, SWT.NONE);
		self = this;
		this.home = h;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(final MouseEvent e) {
				if (e.button == 1) {
					mouseTracker = new org.eclipse.swt.widgets.Tracker(self, SWT.RESIZE);
					Rectangle beg = new Rectangle(e.x, e.y, 5, 5);
					mouseTracker.setRectangles(new Rectangle[] { beg });
					mouseTracker.open();
					Rectangle[] rec = mouseTracker.getRectangles();
					if ((rec[0].width > 10) && (rec[0].height > 10)) {
						Tracker in = new Tracker(home.pat, home.trackers[actSlot][0], home.date, actSlot, rec[0]);
						Tracker[] tOld = home.trackers[actSlot];
						Tracker[] tNew = new Tracker[tOld.length + 1];
						for (int i = 0; i < tOld.length; i++) {
							tNew[i] = tOld[i];
						}
						tNew[tOld.length] = in;
						home.trackers[actSlot] = tNew;
						self.redraw();
					}
				} else {
					Tracker[] myTracker = home.trackers[actSlot];
					int t = Tracker.getTrackerAtPoint(myTracker, e.x, e.y);
					if (t > 0) {
						setMenu(menu);
						menu.setData(t);
					} else {
						setMenu(null);
					}
					super.mouseDown(e);
				}
			}

			@Override
			public void mouseDoubleClick(final MouseEvent e) {
				Tracker[] myTracker = home.trackers[actSlot];
				List<Tracker> list = Tracker.getTrackersAtPoint(myTracker, e.x, e.y);
				if (list.size() > 0) {
					home.tmd.setTracker(list);
					home.setTopControl(home.tmd);
				}
			}

		});

		addPaintListener(new PaintListener() {

			public void paintControl(final PaintEvent e) {
				GC gc = e.gc;
				if (AccessControlServiceHolder.get().evaluate(EvACE.of(Tracker.class, Right.VIEW))) {
					Tracker[] myTracker = home.trackers[actSlot];
					if (myTracker != null) {
						for (int i = 0; i < myTracker.length; i++) {
							if (myTracker[i] != null) {
								Image img = myTracker[i].createImage();
								Rectangle bounds = myTracker[i].getBounds();
								if (bounds != null) {
									if (img == null) {
										gc.drawRectangle(bounds);
									} else {
										if (i == 0) {
											gc.drawImage(img, bounds.x, bounds.y);
										} else {
											ImageData idata = img.getImageData();
											if (idata != null) {
												double scale = (double) bounds.width / idata.width;
												gc.drawImage(img, 0, 0, idata.width, idata.height, bounds.x, bounds.y,
														(int) Math.round(idata.width * scale),
														(int) Math.round(idata.height * scale));
											}
										}
									}
								}
							}
						}
					}
				} else {
					SWTHelper.writeCentered(gc, ImageSlot.INSUFF_RIGHTS, getBounds());
				}
			}

		});
		DropTarget dt = new DropTarget(this, DND.DROP_COPY);
		dt.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		dt.addDropListener(new DropTargetAdapter() {

			@Override
			public void drop(final DropTargetEvent event) {
				if (!AccessControlServiceHolder.get().evaluate(EvACE.of(Tracker.class, Right.UPDATE))) {
					SWTHelper.alert(ImageSlot.CAPTION_NOOP, ImageSlot.TEXT_NOOP);
					return;
				}
				Tracker[] myTracker = home.trackers[actSlot];
				Point pC = self.toControl(event.x, event.y);
				int t = Tracker.getTrackerAtPoint(myTracker, pC.x, pC.y);
				if (t > 0) {
					String[] files = (String[]) event.data;
					Tracker tracker = myTracker[t];
					Image img = tracker.createImage();
					if (img == null) {
						tracker.setFile(new File(files[0]));
					} else {
						Tracker in = new Tracker(home.pat, myTracker[0], null, actSlot, myTracker[t].getBounds());
						in.setFile(new File(files[0]));
						Tracker[] tOld = home.trackers[actSlot];
						Tracker[] tNew = new Tracker[tOld.length + 1];
						for (int i = 0; i < tOld.length; i++) {
							tNew[i] = tOld[i];
						}
						tNew[tOld.length] = in;
						home.trackers[actSlot] = tNew;
						self.redraw();
					}
					redraw();
				}
			}

			@Override
			public void dragEnter(final DropTargetEvent event) {
				event.detail = DND.DROP_COPY;
			}

			@Override
			public void dragOver(final DropTargetEvent event) {
				Tracker[] myTracker = home.trackers[actSlot];
				Point pC = self.toControl(event.x, event.y);
				int t = Tracker.getTrackerAtPoint(myTracker, pC.x, pC.y);
				if (t == 0) {
					event.detail = DND.DROP_NONE;
				} else {
					event.detail = DND.DROP_COPY;
				}
			}

		});

		menu = new Menu(this);
		mDelete = new MenuItem(menu, SWT.NONE);
		mDelete.setText(Messages.DetailDisplay_deleteFrame);
		mDelete.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (SWTHelper.askYesNo(Messages.DetailDisplay_deleteFrameAndImage,
						Messages.DetailDisplay_deleteReally)) {
					Integer t = (Integer) ((MenuItem) e.getSource()).getParent().getData();
					Tracker[] myTracker = home.trackers[actSlot];
					myTracker[t].delete();
					Tracker[] tNew = new Tracker[myTracker.length - 1];
					for (int i = 0, j = 0; i < myTracker.length; i++) {
						if (i == t) {
							continue;
						}
						tNew[j++] = myTracker[i];
					}
					home.trackers[actSlot] = tNew;
					self.redraw();
				}
			}

		});
		setUser();

	}

	public void setUser() {
		mDelete.setEnabled(AccessControlServiceHolder.get().evaluate(EvACE.of(Tracker.class, Right.UPDATE)));
	}

	void setslot(final int slot) {
		actSlot = slot;
		Tracker[] myTracker = home.trackers[actSlot];
		if (myTracker.length > 0) {
			Image img = myTracker[0].createImage();
			if (img != null) {
				ImageData imd = img.getImageData();
				setSize(imd.width + getBorderWidth(), imd.height);

				getParent().layout(true);

			}
			redraw();
		}
	}
}
