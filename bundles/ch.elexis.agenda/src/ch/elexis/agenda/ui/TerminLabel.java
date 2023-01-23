/*******************************************************************************
 * Copyright (c) 2009-2012, G. Weirich and Elexis
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
 *    M. Descher - dynamic adaptation of font size
 *******************************************************************************/

package ch.elexis.agenda.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import ch.elexis.actions.Activator;
import ch.elexis.actions.AgendaActions;
import ch.elexis.agenda.Messages;
import ch.elexis.agenda.acl.ACLContributor;
import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.agenda.series.SerienTermin;
import ch.elexis.agenda.series.ui.SerienTerminDialog;
import ch.elexis.agenda.util.Plannables;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.locks.ILockHandler;
import ch.elexis.core.ui.util.PersistentObjectDragSource;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.PersistentObject;
import ch.elexis.dialogs.TerminDialog;

public class TerminLabel extends Composite {
	private Label lbl;
	private Termin t;
	private SerienTermin serienTermin;
	private int column;
	private Composite state;
	private int originalFontHeightPixel, originalFontHeightPoint;
	private FontData lblFontData;
	IAgendaLayout ial;
	Activator agenda = Activator.getDefault();
	private IAction terminKuerzenAction, terminVerlaengernAction, terminAendernAction;
	private GC lblGc;
	private List<TerminLabel> overLapped;

	/**
	 * Static map holding all fonts used by all TerminLabel instances.
	 */
	private static HashMap<Integer, Font> fontMap = new HashMap<Integer, Font>();

	public TerminLabel(IAgendaLayout al) {
		super(al.getComposite(), SWT.BORDER);
		ial = al;
		makeActions();
		GridLayout gl = new GridLayout(2, false);
		gl.marginHeight = 1;
		gl.marginWidth = 1;
		setLayout(gl);
		lbl = new Label(this, SWT.WRAP);
		lblGc = new GC(lbl);
		lblFontData = lbl.getFont().getFontData()[0];
		originalFontHeightPoint = lblFontData.getHeight();
		originalFontHeightPixel = lblGc.getFontMetrics().getHeight();
		lblGc.dispose();

		state = new Composite(this, SWT.NONE);
		state.setLayoutData(new GridData());
		lbl.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				agenda.setActDate(getTermin().getDay());
				agenda.setActResource(getTermin().getBereich());
				AcquireLockBlockingUi.aquireAndRun(getTermin(), new ILockHandler() {

					@Override
					public void lockFailed() {
						// do nothing
					}

					@Override
					public void lockAcquired() {
						if (getTermin().isRecurringDate()) {
							new SerienTerminDialog(UiDesk.getTopShell(), new SerienTermin(getTermin())).open();
						} else {
							new TerminDialog(getTermin()).open();
						}
					}
				});
			}

			@Override
			public void mouseUp(MouseEvent e) {
				agenda.dispatchTermin(getTermin());
				super.mouseUp(e);
			}

			@Override
			public void mouseDown(MouseEvent e) {
				ElexisEventDispatcher.fireSelectionEvent(getTermin().getKontakt());
			}
		});
		new PersistentObjectDragSource(lbl, new PersistentObjectDragSource.ISelectionRenderer() {
			public List<PersistentObject> getSelection() {
				ArrayList<PersistentObject> ret = new ArrayList<PersistentObject>();
				ret.add(getTermin());
				return ret;
			}
		}) {
			@Override
			public void dragFinished(DragSourceEvent event) {
				lbl.getParent().dispose();
				if (getTermin() != null && getTermin().equals(PersistentObjectDragSource.getDraggedObject())) {
					setDraggedObject(null);
				}
			}
		};
		new TerminLabelMenu();
	}

	@Override
	public void dispose() {
		if (getTermin() != null && getTermin().equals(PersistentObjectDragSource.getDraggedObject()) && isVisible()) {
			setVisible(false);
		} else {
			super.dispose();
		}

	}

	public void set(Termin tf, int col) {
		t = tf;
		if (getTermin().isRecurringDate()) {
			serienTermin = new SerienTermin(getTermin());
		} else {
			serienTermin = null;
		}

		this.column = col;
	}

	public TerminLabel(IAgendaLayout parent, Termin trm, int col) {
		this(parent);
		set(trm, col);
	}

	public int getColumn() {
		return column;
	}

	public Termin getTermin() {
		return t;
	}

	public void updateActions() {
		boolean canChangeAppointments = CoreHub.acl.request(ACLContributor.CHANGE_APPOINTMENTS);
		terminKuerzenAction.setEnabled(canChangeAppointments);
		terminVerlaengernAction.setEnabled(canChangeAppointments);
		terminAendernAction.setEnabled(canChangeAppointments);

	}

	public void refresh() {
		Termin appointment = getTermin();

		// termin of type recurring uses the root appointments title
		Termin rootAppointment = appointment;
		if (serienTermin != null) {
			rootAppointment = serienTermin.getRootTermin();
		}

		if (overLapped != null) {
			setBackground(getDisplay().getSystemColor(SWT.COLOR_RED));
		}
		Color back = Plannables.getTypColor(appointment);
		lbl.setBackground(back);
		// l.setBackground(Desk.getColor(Desk.COL_GREY20));
		lbl.setForeground(SWTHelper.getContrast(back));

		// l.setForeground(Plannables.getStatusColor(t));
		StringBuilder sb = new StringBuilder();
		sb.append(appointment.isRecurringDate() ? "Terminserie\n" : StringUtils.EMPTY);
		sb.append(appointment.getLabel()).append(StringUtils.LF).append(appointment.getGrund());
		sb.append("\n--------\n").append(appointment.getStatusHistoryDesc()); //$NON-NLS-1$

		String grund = rootAppointment.getGrund();
		if (grund != null && !grund.isEmpty())
			lbl.setText(rootAppointment.getTitle() + ", " + grund);
		else
			lbl.setText(rootAppointment.getTitle());

		lbl.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		lbl.setToolTipText(sb.toString());

		int lx = ial.getLeftOffset() + (int) Math.round(getColumn() * (ial.getWidthPerColumn() + ial.getPadding()));

		String startOfDayTimeInMinutes = ConfigServiceHolder.get()
				.get(PreferenceConstants.AG_DAY_PRESENTATION_STARTS_AT, "0000", false);
		int sodtHours = Integer.parseInt(startOfDayTimeInMinutes.substring(0, 2));
		int sodtMinutes = Integer.parseInt(startOfDayTimeInMinutes.substring(2));
		int sodtM = (sodtHours * 60);
		sodtM += sodtMinutes;

		String endOfDayTimeInMinutes = ConfigServiceHolder.getGlobal(PreferenceConstants.AG_DAY_PRESENTATION_ENDS_AT,
				"2359");
		int eodtHours = Integer.parseInt(endOfDayTimeInMinutes.substring(0, 2));
		int eodtMinutes = Integer.parseInt(endOfDayTimeInMinutes.substring(2));
		int eodtM = (eodtHours * 60);
		eodtM += eodtMinutes;

		if ((appointment.getBeginn() < sodtM) && (appointment.getBeginn() + appointment.getDauer() < sodtM)) {
			// skip this entry as begin and end are not visible
			setBounds(0, 0, 0, 0);
			return;
		}

		if ((appointment.getBeginn() > eodtM) && (appointment.getBeginn() + appointment.getDauer() > eodtM)) {
			// skip this entry as begin and end are not visible
			setBounds(0, 0, 0, 0);
			return;
		}

		int ly = 0;
		int lh = 0;

		if (appointment.getBeginn() < sodtM) {
			ly = (int) Math.round(appointment.getBeginn() * ial.getPixelPerMinute());
			int diff = ((appointment.getDauer() - sodtM) < 0) ? appointment.getDauer()
					: (appointment.getDauer() - sodtM);

			diff += ((appointment.getBeginn() + diff) > eodtM) ? (appointment.getBeginn() + diff) - eodtM : 0;

			lh = (int) Math.round(diff * ial.getPixelPerMinute());

		} else {
			int startMinute = appointment.getBeginn() - sodtM;
			ly = (int) Math.round(startMinute * ial.getPixelPerMinute());

			int ends = appointment.getBeginn() + appointment.getDauer();
			int heigthDiff = ends - eodtM;
			if (heigthDiff < 0)
				heigthDiff = 0;
			lh = (int) Math.round((appointment.getDauer() - heigthDiff) * ial.getPixelPerMinute());
		}
		int lw = (int) Math.round(ial.getWidthPerColumn());

		// dynamic font size adaption
		int newHeight = originalFontHeightPoint;
		if (originalFontHeightPixel >= lh) {
			int diffPixel = originalFontHeightPixel - lh;

			int diffPoint = diffPixel * 72 / Display.getCurrent().getDPI().y;

			newHeight = originalFontHeightPoint - (diffPoint + 1);
			if (newHeight <= 0)
				newHeight = originalFontHeightPoint;
			// non-nice constant 1 :( you got a better solution?
		}
		lblFontData.setHeight(newHeight);
		lbl.setFont(getLabelFont(lblFontData));

		setBounds(lx, ly, lw, lh);

		GridData gd = (GridData) state.getLayoutData();
		gd.minimumWidth = 10;
		gd.widthHint = 10;
		gd.heightHint = lh;
		state.setBackground(Plannables.getStatusColor(appointment));
		state.setToolTipText(appointment.getStatus());
		if (lbl.getMenu() == null) {
			lbl.setMenu(ial.getContextMenuManager().createContextMenu(lbl));
		}
		layout();
	}

	private Font getLabelFont(FontData fontData) {
		Font font = fontMap.get(fontData.getHeight());
		if (font == null) {
			font = new Font(UiDesk.getDisplay(), fontData);
			fontMap.put(fontData.getHeight(), font);
		}
		return font;
	}

	class TerminLabelMenu {
		TerminLabelMenu() {
			MenuManager contextMenuManager = new MenuManager();
			contextMenuManager.add(AgendaActions.getTerminStatusAction());
			contextMenuManager.add(terminKuerzenAction);
			contextMenuManager.add(terminVerlaengernAction);
			contextMenuManager.add(terminAendernAction);
			contextMenuManager.add(AgendaActions.getDelTerminAction());
			TerminLabel.this.lbl.setMenu(contextMenuManager.createContextMenu(TerminLabel.this.lbl));
		}

	};

	private void makeActions() {
		terminAendernAction = new Action(Messages.TagesView_changeTermin) {
			{
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
				setToolTipText(Messages.TagesView_changeThisTermin);
			}

			@Override
			public void run() {
				AcquireLockBlockingUi.aquireAndRun(getTermin(), new ILockHandler() {
					@Override
					public void lockFailed() {
						// do nothing
					}

					@Override
					public void lockAcquired() {
						agenda.setActResource(getTermin().getBereich());
						TerminDialog dlg = new TerminDialog(getTermin());
						dlg.open();
					}
				});

				refresh();

			}
		};
		terminKuerzenAction = new Action(Messages.TagesView_shortenTermin) {
			@Override
			public void run() {
				if (getTermin() != null) {
					AcquireLockBlockingUi.aquireAndRun(getTermin(), new ILockHandler() {
						@Override
						public void lockFailed() {
							// do nothing
						}

						@Override
						public void lockAcquired() {
							getTermin().setDurationInMinutes(getTermin().getDurationInMinutes() >> 1);
						}
					});
					ElexisEventDispatcher.update(getTermin());
				}
			}
		};
		terminVerlaengernAction = new Action(Messages.TagesView_enlargeTermin) {
			@Override
			public void run() {
				if (getTermin() != null) {
					AcquireLockBlockingUi.aquireAndRun(getTermin(), new ILockHandler() {
						@Override
						public void lockFailed() {
							// do nothing
						}

						@Override
						public void lockAcquired() {
							agenda.setActDate(getTermin().getDay());
							Termin n = Plannables.getFollowingTermin(agenda.getActResource(), agenda.getActDate(),
									getTermin());
							if (n != null) {
								getTermin().setEndTime(n.getStartTime());
							}
						}
					});
					refresh();
				}
			}
		};
	}

	public static void checkAllCollisions(List<TerminLabel> tlabels) {
		tlabels.parallelStream().forEach(terminLabel -> terminLabel.checkCollision(tlabels));
	}

	private void checkCollision(List<TerminLabel> tlabels) {
		String checkBereich = getTermin().getBereich();
		// use recurring termin for collision checking
		Termin termin = getTermin();
		for (TerminLabel otherLabel : tlabels) {
			Termin otherTermin = otherLabel.getTermin();
			if (otherLabel != this && otherTermin.getBereich().equals(checkBereich)) {
				if (Plannables.isOverlapped(termin, otherTermin)) {
					addOverlapped(otherLabel);
				}
			}
		}
	}

	private void addOverlapped(TerminLabel otherLabel) {
		if (overLapped == null) {
			overLapped = new ArrayList<TerminLabel>();
		}
		overLapped.add(otherLabel);
	}
}
