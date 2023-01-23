/*******************************************************************************
 * Copyright (c) 2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Sponsoring:
 * 	 mediX Notfallpaxis, diepraxen Stauffacher AG, Zürich
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.agenda.ui;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;

import ch.elexis.actions.Activator;
import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.agenda.series.SerienTermin;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.locks.ILockHandler;
import ch.elexis.core.ui.util.PersistentObjectDropTarget;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.elexis.dialogs.TerminDialog;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class ProportionalSheet extends Composite implements IAgendaLayout {
	static final int LEFT_OFFSET_DEFAULT = 20;
	static final int PADDING_DEFAULT = 5;

	private int left_offset, padding;
	private AgendaParallel view;
	private MenuManager contextMenuManager;
	private List<TerminLabel> tlabels;
	private double ppm;
	private int sheetHeight;
	private String[] resources;
	private int textWidth;
	private double sheetWidth;
	private double widthPerColumn;
	private boolean ctrlKeyDown = false;

	private TimeTool setTerminTo(int x, int y) {
		String resource = ""; //$NON-NLS-1$
		for (int i = 0; i < resources.length; i++) {
			double lower = left_offset + i * (widthPerColumn + padding);
			double upper = lower + widthPerColumn;
			if (isBetween(x, lower, upper)) {
				resource = resources[i];
				break;
			}
		}
		String startOfDayTimeInMinutes = ConfigServiceHolder.get()
				.get(PreferenceConstants.AG_DAY_PRESENTATION_STARTS_AT, "0000", false);
		int dayStartHour = Integer.parseInt(startOfDayTimeInMinutes.substring(0, 2));

		int minute = (int) Math.round(y / ppm);
		TimeTool tt = new TimeTool(Activator.getDefault().getActDate());
		int hour = minute / 60;
		minute = minute - (60 * hour);
		int raster = 5;
		minute = ((minute + (raster >> 1)) / raster) * raster;
		tt.set(TimeTool.AM_PM, TimeTool.AM);
		tt.set(TimeTool.HOUR, (dayStartHour + hour));
		tt.set(TimeTool.MINUTE, minute);
		if (resource.length() > 0) {
			Activator.getDefault().setActResource(resource);
		}
		return tt;
	}

	public ProportionalSheet(Composite parent, AgendaParallel v) {
		super(parent, SWT.NO_BACKGROUND);
		view = v;
		addControlListener(new ControlAdapter() {
			@Override
			public void controlResized(ControlEvent e) {
				layout();
				recalc();
			}
		});
		addPaintListener(new TimePainter());
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {
				TimeTool tt = setTerminTo(e.x, e.y);
				TerminDialog dlg = new TerminDialog(null);
				dlg.create();
				dlg.setTime(tt);
				if (dlg.open() == Dialog.OK) {
					refresh();
				}
			}

		});

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.CTRL) {
					ctrlKeyDown = true;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.CTRL) {
					ctrlKeyDown = false;
				}
			}
		});

		// setBackground(Desk.getColor(Desk.COL_GREEN));
		left_offset = LEFT_OFFSET_DEFAULT;
		padding = PADDING_DEFAULT;
		new PersistentObjectDropTarget(this, new PersistentObjectDropTarget.IReceiver() {

			public boolean accept(PersistentObject o) {
				return true;
			}

			public void dropped(PersistentObject o, DropTargetEvent e) {
				Point pt = Display.getCurrent().map(null, ProportionalSheet.this, e.x, e.y);
				TimeTool tt = setTerminTo(pt.x, pt.y);
				if (o instanceof Termin) {
					Termin t = (Termin) o;
					if (Termin.overlaps(Activator.getDefault().getActResource(), tt, t.getDauer(), t.getId())) {
						SWTHelper.showInfo("Termin Kollision", "Termine überschneiden sich");
					} else {
						if (ctrlKeyDown) { // copy
							ctrlKeyDown = false;
							Termin tCopy = (Termin) t.clone();
							if (t.isRecurringDate() && t.getKontakt() == null) {
								// take kontakt from root termin
								tCopy.setKontakt(new SerienTermin(t).getRootTermin().getKontakt());
							}
							AcquireLockBlockingUi.aquireAndRun(tCopy, new ILockHandler() {

								@Override
								public void lockFailed() {
									tCopy.delete();
								}

								@Override
								public void lockAcquired() {
									tCopy.setStartTime(tt);
									tCopy.setBereich(Activator.getDefault().getActResource());
								}
							});
						} else { // move
							AcquireLockBlockingUi.aquireAndRun(t, new ILockHandler() {

								@Override
								public void lockFailed() {
									// do nothing
								}

								@Override
								public void lockAcquired() {
									t.setStartTime(tt);
									t.setBereich(Activator.getDefault().getActResource());
								}
							});
						}
					}
					refresh();
				}
			}
		});
	}

	private boolean isBetween(int x, double lower, double upper) {
		int y = (int) Math.round(lower);
		int z = (int) Math.round(upper);
		if ((x >= y) && (x <= z)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean setFocus() {
		ctrlKeyDown = false;
		return super.setFocus();
	}

	public MenuManager getContextMenuManager() {
		return contextMenuManager;
	}

	public void clear() {
		while (tlabels != null && tlabels.size() > 0) {
			tlabels.remove(0).dispose();
		}
		recalc();

	}

	synchronized void refresh() {
		String[] resnames = view.getDisplayedResources();
		Query<Termin> qbe = new Query<Termin>(Termin.class, Termin.TABLENAME, false,
				new String[] { Termin.FLD_LINKGROUP, Termin.FLD_DAUER, Termin.FLD_BEGINN, Termin.FLD_TAG,
						Termin.FLD_GRUND, Termin.FLD_PATIENT, Termin.FLD_DELETED, Termin.FLD_TERMINSTATUS,
						Termin.FLD_TERMINTYP, Termin.FLD_BEREICH, Termin.FLD_STATUSHIST });
		String day = Activator.getDefault().getActDate().toString(TimeTool.DATE_COMPACT);
		qbe.add("Tag", "=", day);
		qbe.startGroup();

		for (String n : resnames) {
			qbe.add("BeiWem", "=", n);
			qbe.or();
		}
		qbe.endGroup();
		List<Termin> apps = qbe.execute();
		// clear old TerminLabel list
		if (tlabels != null) {
			for (TerminLabel terminLabel : tlabels) {
				terminLabel.dispose();
			}
			tlabels.clear();
		} else {
			tlabels = new LinkedList<TerminLabel>();
		}
		if (!isDisposed()) {
			// populate new TerminLabel list
			for (Termin termin : apps) {
				String m = termin.getBereich();
				int idx = StringTool.getIndex(resnames, m);
				if (idx != -1) {
					TerminLabel terminLabel = new TerminLabel(this);
					terminLabel.set(termin, idx);
					tlabels.add(terminLabel);
				}
			}
			TerminLabel.checkAllCollisions(tlabels);
			recalc();
		}
	}

	void recalc() {
		if (tlabels != null) {
			ppm = AgendaParallel.getPixelPerMinute();

			String startOfDayTimeInMinutes = ConfigServiceHolder.get()
					.get(PreferenceConstants.AG_DAY_PRESENTATION_STARTS_AT, "0000", false);
			int sodtHours = Integer.parseInt(startOfDayTimeInMinutes.substring(0, 2));
			int sodtMinutes = Integer.parseInt(startOfDayTimeInMinutes.substring(2));
			int sodtM = (sodtHours * 60);
			sodtM += sodtMinutes;

			String endOfDayTimeInMinutes = ConfigServiceHolder.get()
					.get(PreferenceConstants.AG_DAY_PRESENTATION_ENDS_AT, "2359", false);
			int eodtHours = Integer.parseInt(endOfDayTimeInMinutes.substring(0, 2));
			int eodtMinutes = Integer.parseInt(endOfDayTimeInMinutes.substring(2));
			int eodtM = (eodtHours * 60);
			eodtM += eodtMinutes;

			sheetHeight = (int) Math.round(ppm * (eodtM - sodtM));
			ScrolledComposite sc = (ScrolledComposite) getParent();
			Point mySize = getSize();

			if (mySize.x > 0.0) {
				if (mySize.y != sheetHeight) {
					setSize(mySize.x, sheetHeight);
					sc.setMinSize(getSize());
				}
				ScrollBar bar = sc.getVerticalBar();
				int barWidth = 14;
				if (bar != null) {
					barWidth = bar.getSize().x;
				}
				resources = view.getDisplayedResources();
				int count = resources.length;
				Point textSize = SWTHelper.getStringBounds(this, "88:88"); //$NON-NLS-1$
				textWidth = textSize.x;
				left_offset = textWidth + 2;
				sheetWidth = mySize.x - 2 * left_offset - barWidth;
				widthPerColumn = sheetWidth / count;
				ColumnHeader header = view.getHeader();
				header.recalc(widthPerColumn, left_offset, padding, textSize.y);

				for (TerminLabel l : tlabels) {
					l.refresh();
				}
				sc.layout();
			}
		}
	}

	public double getPixelPerMinute() {
		return ppm;
	}

	public double getWidthPerColumn() {
		return widthPerColumn;
	}

	public int getLeftOffset() {
		return left_offset;
	}

	public int getPadding() {
		return padding;
	}

	class TimePainter implements PaintListener {

		public void paintControl(PaintEvent e) {
			GC gc = e.gc;
			gc.fillRectangle(e.x, e.y, e.width, e.height);
			int y = 0;
			TimeTool runner = new TimeTool();
			String dayStartsAt = ConfigServiceHolder.get().get(PreferenceConstants.AG_DAY_PRESENTATION_STARTS_AT,
					"0000", false);
			runner.set(dayStartsAt); // $NON-NLS-1$

			String dayEndsAt = ConfigServiceHolder.get().get(PreferenceConstants.AG_DAY_PRESENTATION_ENDS_AT, "2359",
					false);
			TimeTool limit = new TimeTool(dayEndsAt); // $NON-NLS-1$
			Point textSize = gc.textExtent("88:88"); //$NON-NLS-1$
			int textwidth = textSize.x;

			int quarter = (int) Math.round(15.0 * AgendaParallel.getPixelPerMinute());
			int w = ProportionalSheet.this.getSize().x - 5;
			int left = 0;
			int right = w - textwidth;
			while (runner.isBefore(limit)) {
				gc.drawLine(left, y, w, y); // volle Linie
				String time = runner.toString(TimeTool.TIME_SMALL);
				gc.drawText(time, 0, y + 1);
				gc.drawText(time, right, y + 1);
				y += quarter;
				gc.drawLine(textwidth - 3, y, textwidth, y);
				gc.drawLine(right, y, right + 3, y);
				y += quarter;
				gc.drawLine(textwidth - 6, y, textwidth, y);
				gc.drawLine(right, y, right + 6, y);
				y += quarter;
				gc.drawLine(textwidth - 3, y, textwidth, y);
				gc.drawLine(right, y, right + 3, y);
				y += quarter;
				runner.addHours(1);
			}
			addCurrentTimeline(gc, dayStartsAt);
		}
	}

	/**
	 * adds a red horizontal line representing the current time
	 *
	 * @param gc
	 * @param dayStartsAt
	 */
	private void addCurrentTimeline(GC gc, String dayStartsAt) {
		// calculate start of day time in minutes
		int sodtHours = Integer.parseInt(dayStartsAt.substring(0, 2));
		int sodtMinutes = Integer.parseInt(dayStartsAt.substring(2));
		int sodtM = (sodtHours * 60);
		sodtM += sodtMinutes;

		// calc current time line
		int w = ProportionalSheet.this.getSize().x - 5;
		int y = 0;
		Calendar c = Calendar.getInstance();
		int minuteOfDay = c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE);
		if (minuteOfDay < sodtM) {
			y = (int) getPixelPerMinute();
		} else {
			int startMinute = minuteOfDay - sodtM;
			y = (int) Math.round(startMinute * getPixelPerMinute());
		}
		gc.setForeground(UiDesk.getColor(UiDesk.COL_RED));
		gc.drawLine(getLeftOffset() - 5, y, w, y); // create a horizontal red line (about full width)
		gc.setForeground(UiDesk.getColor(UiDesk.COL_BLACK));
	}

	public Composite getComposite() {
		return this;
	}
}
