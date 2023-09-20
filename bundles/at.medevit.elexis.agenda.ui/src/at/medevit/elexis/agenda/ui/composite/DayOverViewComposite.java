package at.medevit.elexis.agenda.ui.composite;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Spinner;

import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.services.IAppointmentService;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;

public class DayOverViewComposite extends Canvas implements PaintListener {

	public enum CollisionErrorLevel {
		ERROR, WARNING
	}

	@Inject
	private IAppointmentService appointmentService;

	@Inject
	private IConfigService configService;

	private CDateTime txtTimeFrom;
	private CDateTime txtTimeTo;
	private Spinner txtDuration;

	private int ts;
	private int te;
	private int tagStart = ts * 60; // 7 Uhr
	private int tagEnd = te * 60;
	private int[] rasterValues = new int[] { 5, 10, 15, 30 };
	private int rasterIndex;
	private double minutes;
	private double pixelPerMinute;

	private Slider slider;

	private boolean bModified;
	private String msg;

	private Point d;
	private int sep;

	private java.util.List<IAppointment> list;
	private IAppointment appointment;

	private Group parent;

	private CollisionErrorLevel collisionErrorLevel = CollisionErrorLevel.ERROR;

	public DayOverViewComposite(final Group parent, IAppointment appointment, CDateTime txtTimeFrom,
			CDateTime txtTimeTo, Spinner txtDuration) {
		super(parent, SWT.NONE);
		CoreUiUtil.injectServicesWithContext(this);
		this.parent = parent;

		this.ts = getRasterStartTime();
		this.te = getRasterEndTime();
		this.rasterIndex = getRasterIndex();

		this.appointment = appointment;
		this.txtTimeFrom = txtTimeFrom;
		this.txtDuration = txtDuration;
		this.txtTimeTo = txtTimeTo;

		addPaintListener(this);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(final MouseEvent e) {
				if (e.y > sep + 2) {
					rasterIndex = (rasterIndex >= rasterValues.length) ? 0 : rasterIndex + 1;
					setRasterIndex(rasterIndex);
					redraw();
				} else {
					Point loc = slider.getLocation();
					slider.setLocation(e.x, loc.y);
					slider.updateTimes();
				}
			}

		});

		slider = new Slider(this);
	}

	public void setAppointment(IAppointment appointment) {
		this.appointment = appointment;
	}

	/**
	 * Tagesbalken neu kalkulieren (Startzeit, Endzeit, pixel pro Minute etc.)
	 *
	 */
	void recalc() {
		loadCurrentAppointments();

		tagStart = ts * 60;
		tagEnd = te * 60;
		int i = 0;
		if (!list.isEmpty()) {
			IAppointment pi = list.get(i);
			// Tagesanzeige ca. eine halbe Stunde vor dem ersten reservierten
			// Zeitraum anfangen
			if (isTerminTypeIsReserviert(pi)) {
				tagStart = pi.getDurationMinutes() < 31 ? 0 : pi.getDurationMinutes() - 30;
			} else {
				tagStart = 0;
			}
			i = list.size() - 1;
			pi = list.get(i);

			int end = getStartMinute(pi) + pi.getDurationMinutes();
			if (end < 1408) {
				tagEnd = 1439;
			}

			if (tagStart != 0) {
				tagStart = tagStart / 60 * 60;
			}
			if (tagEnd < 1439) {
				tagEnd = (tagEnd + 30) / 60 * 60;
			}
			minutes = tagEnd - tagStart;
			d = this.getSize();
			pixelPerMinute = d.x / minutes;
			sep = d.y / 2;
		}
	}

	@Override
	public Point computeSize(final int wHint, final int hHint, final boolean changed) {
		return new Point(getParent().getSize().x, 60);
	}

	/**
	 * Tagesbalken zeichnen
	 */
	public void paintControl(final PaintEvent pe) {
		recalc();
		GC g = pe.gc;
		Color def = g.getBackground();
		// Balken zeichnen
		g.setBackground(getColor(SWT.COLOR_GREEN));
		Rectangle r = new Rectangle(0, 0, d.x, sep - 2);
		g.fillRectangle(r);

		// Termine darauf zeichnen
		for (IAppointment p : list) {
			paintAppointment(g, p, r, tagStart, tagEnd);
		}

		// Lineal zeichnen
		g.setBackground(def);
		g.setFont(getSmallFont());

		g.drawLine(0, sep, d.x, sep);
		if (rasterIndex >= rasterValues.length) {
			rasterIndex = 0;
			setRasterIndex(rasterIndex);
		}
		double chunkwidth = rasterValues[rasterIndex] * pixelPerMinute;
		int chunksPerHour = 60 / rasterValues[rasterIndex];
		int ch = chunksPerHour - 1;
		int hr = tagStart / 60;
		if (chunkwidth < 0.1) {
			return;
		}
		for (double x = 0; x <= d.x; x += chunkwidth) {
			int lx = (int) Math.round(x);
			if (++ch == chunksPerHour) {
				g.drawLine(lx, sep - 1, lx, sep + 6);
				g.drawString(Integer.toString(hr++), lx, sep + 6);
				ch = 0;
			} else {
				g.drawLine(lx, sep, lx, sep + 4);
			}
		}

		slider.redraw();
	}

	private void paintAppointment(GC gc, IAppointment p, Rectangle r, int start, int end) {
		double minutes = end - start;
		double pixelPerMinute = (double) r.width / minutes;
		int x = (int) Math.round((getStartMinute(p) - start) * pixelPerMinute);
		int w = (int) Math.round(p.getDurationMinutes() * pixelPerMinute);
		gc.setBackground(getTypColor(p));
		gc.fillRectangle(x, r.y, w, r.height);
	}

	private class Slider extends Composite implements MouseListener, MouseMoveListener {
		boolean isDragging;

		Slider(final Composite parent) {
			super(parent, SWT.BORDER);
			setBackground(getColor(SWT.COLOR_RED)); // $NON-NLS-1$
			addMouseListener(this);
			addMouseMoveListener(this);
		}

		void set() {
			int v = getTimeInMinutes();
			int d = txtDuration.getSelection();
			Rectangle r = getParent().getBounds();
			int x = (int) Math.round((v - tagStart) * pixelPerMinute);
			int w = (int) Math.round(d * pixelPerMinute);
			setBounds(x, 0, w, r.height / 2);
			setTimeTo(v + d);
			bModified = true;
			updateCollision();
		}

		public void mouseDoubleClick(final MouseEvent e) {
		}

		public void mouseDown(final MouseEvent e) {
			isDragging = true;
		}

		public void mouseUp(final MouseEvent e) {
			if (isDragging) {
				isDragging = false;
				updateTimes();
			}
		}

		public void mouseMove(final MouseEvent e) {
			if (isDragging) {
				Point loc = getLocation();
				int x = loc.x + e.x;
				setLocation(x, loc.y);
			}

		}

		public void updateTimes() {
			Point loc = getLocation();
			Rectangle rec = getParent().getBounds();
			double minutes = tagEnd - tagStart;
			double minutesPerPixel = minutes / rec.width;
			int minute = (int) Math.round(loc.x * minutesPerPixel) + tagStart;
			int raster = rasterValues[rasterIndex];
			minute = ((minute + (raster >> 1)) / raster) * raster;
			setTimeFrom(minute);
			set();
		}

	}

	public void set() {
		if (slider != null) {
			recalc();
			slider.set();
			updateCollision();
			redraw();
			this.getParent().layout();
		}

	}

	public void refresh() {
		if (slider != null) {
			recalc();
			updateCollision();
			redraw();
			this.getParent().layout();
		}
	}

	public void setCollisionErrorLevel(CollisionErrorLevel level) {
		this.collisionErrorLevel = level;
	}

	private boolean isTerminTypeIsReserviert(IAppointment pi) {
		return pi.getType().equals(configService.getAsList("agenda/TerminTypen").get(1)); //$NON-NLS-1$
	}

	private boolean isTerminTypeIsFree(IAppointment pi) {
		return pi.getType().equals(configService.getAsList("agenda/TerminTypen").get(0)); //$NON-NLS-1$
	}

	private int getStartMinute(IAppointment pi) {
		long minutesIntoTheDay = ChronoUnit.MINUTES.between(pi.getStartTime().toLocalDate().atStartOfDay(),
				pi.getStartTime());
		return (int) minutesIntoTheDay;
	}

	private int getTimeInMinutes() {
		LocalDateTime localDateTime = txtTimeFrom.getSelection().toInstant().atZone(ZoneId.systemDefault())
				.toLocalDateTime();
		long minutesIntoTheDay = ChronoUnit.MINUTES.between(localDateTime.toLocalDate().atStartOfDay(), localDateTime);
		return (int) minutesIntoTheDay;
	}

	private void setTimeTo(int i) {
		LocalDateTime localDateTime = txtTimeTo.getSelection().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
				.atStartOfDay();
		appointment.setEndTime(localDateTime.plusMinutes(i));
		txtTimeTo.setSelection(Date.from(appointment.getEndTime().atZone(ZoneId.systemDefault()).toInstant()));
	}

	private void setTimeFrom(int i) {
		LocalDateTime localDateTime = txtTimeFrom.getSelection().toInstant().atZone(ZoneId.systemDefault())
				.toLocalDate().atStartOfDay();
		appointment.setStartTime(localDateTime.plusMinutes(i));
		txtTimeFrom.setSelection(Date.from(appointment.getStartTime().atZone(ZoneId.systemDefault()).toInstant()));
	}

	private void updateCollision() {
		updateMessage(isColliding());
	}

	private void loadCurrentAppointments() {
		// @REF Plannables#loadTermine
		if (appointment.getSchedule() == null) {
			list = new ArrayList<>();
		}

		IQuery<IAppointment> query = CoreModelServiceHolder.get().getQuery(IAppointment.class,
				!configService.getActiveUserContact("agenda/zeige_geloeschte", "0").equals("0")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		query.and("tag", COMPARATOR.EQUALS, appointment.getStartTime().toLocalDate()); //$NON-NLS-1$
		query.and(ModelPackage.Literals.IAPPOINTMENT__SCHEDULE, COMPARATOR.EQUALS, appointment.getSchedule());
		list = query.execute();
		if (list.isEmpty()) {
			appointmentService.assertBlockTimes(appointment.getStartTime().toLocalDate(), appointment.getSchedule());
			list = query.execute();
		}
		list = list.stream().sorted(Comparator.comparing(a -> a.getStartTime())).collect(Collectors.toList());
	}

	private boolean isColliding() {
		for (IAppointment iAppointment : list) {
			if (!iAppointment.getId().equals(appointment.getId())) {
				if (isOverlapping(appointment.getStartTime(), appointment.getEndTime(), iAppointment.getStartTime(),
						iAppointment.getEndTime())) {
					System.out.println(
							String.format("Collide %s with %s", appointment.getLabel(), iAppointment.getLabel())); //$NON-NLS-1$
					return true;
				}
			}
		}
		return false;
	}

	private boolean isOverlapping(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
		if (start1 != null && start2 != null && end1 != null && end2 != null) {
			return start1.isBefore(end2) && start2.isBefore(end1);
		}
		return false;
	}

	private int getRasterStartTime() {
		try {
			// @REF ConfigServiceHolder.getUser("agenda/dayView/Start", 7);
			return Integer.parseInt(configService.getActiveUserContact("agenda/dayView/Start", "7")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NumberFormatException e) {
			return rasterIndex;
		}
	}

	private int getRasterEndTime() {
		try {
			// @REF ConfigServiceHolder.getUser("agenda/dayView/End", 19);
			return Integer.parseInt(configService.getActiveUserContact("agenda/dayView/End", "19")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NumberFormatException e) {
			return rasterIndex;
		}
	}

	private int getRasterIndex() {
		try {
			// @REF ConfigServiceHolder.getUser("agenda/dayView/raster", 3)
			return Integer.parseInt(configService.getActiveUserContact("agenda/dayView/raster", "3")); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (NumberFormatException e) {
			return rasterIndex;
		}
	}

	private void setRasterIndex(int rasterIndex) {
		// @REF ConfigServiceHolder.setUser("agenda/dayView/raster", rasterIndex);
		// //$NON-NLS-1$
		ContextServiceHolder.get().getActiveUserContact()
				.ifPresent(c -> configService.set(c, "agenda/dayView/raster", String.valueOf(rasterIndex))); //$NON-NLS-1$
	}

	private void updateMessage(final boolean collision) {
		msg = Messages.AgendaUI_DayOverView_create_or_change;

		slider.setBackground(getColor(SWT.COLOR_GRAY)); // $NON-NLS-1$ //TODO LIGHTGREY

		if (collision) {
			slider.setBackground(getColor(SWT.COLOR_DARK_GRAY)); // $NON-NLS-1$
			msg += Messages.AgendaUI_DayOverView_date_collision;
		}

		getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (collisionErrorLevel == CollisionErrorLevel.ERROR) {
					setMessage(msg, collision ? IMessageProvider.ERROR : IMessageProvider.NONE);
				} else if (collisionErrorLevel == CollisionErrorLevel.WARNING) {
					setMessage(msg, collision ? IMessageProvider.WARNING : IMessageProvider.NONE);
				}
			}
		});
	}

	private void setMessage(String msg, int i) {
		if (i == IMessageProvider.NONE) {
			parent.setForeground(parent.getParent().getForeground());
			parent.setText(msg);

		} else if (i == IMessageProvider.ERROR) {
			parent.setText(msg);
			parent.setForeground(getColor(SWT.COLOR_RED));
		} else if (i == IMessageProvider.WARNING) {
			parent.setText(msg);
			parent.setForeground(getColor(SWT.COLOR_DARK_YELLOW));
		}
	}

	public boolean isValid() {
		return !CollisionErrorLevel.ERROR.equals(collisionErrorLevel);
	}

	/** Die einem Plannable-Typ zugeordnete Farbe holen */
	private Color getTypColor(IAppointment p) {
		String coldesc = appointmentService.getContactConfiguredTypeColor(null, p.getType());
		ColorRegistry cr = JFaceResources.getColorRegistry();
		String col = StringTool.pad(StringTool.LEFT, '0', coldesc, 6);

		if (!cr.hasValueFor(col)) {
			RGB rgb;
			try {
				rgb = new RGB(Integer.parseInt(col.substring(0, 2), 16), Integer.parseInt(col.substring(2, 4), 16),
						Integer.parseInt(col.substring(4, 6), 16));
			} catch (NumberFormatException nex) {
				ExHandler.handle(nex);
				rgb = new RGB(100, 100, 100);
			}
			cr.put(col, rgb);
		}
		return cr.get(col);
	}

	private Color getColor(int swtColor) {
		Display display = Display.getCurrent();
		return display.getSystemColor(swtColor);
	}

	private Font getSmallFont() {
		// @REF UiDesk.getFont(Preferences.USR_SMALLFONT))
		String cfgName = "anwender/smallfont"; //$NON-NLS-1$
		FontRegistry fr = JFaceResources.getFontRegistry();
		if (!fr.hasValueFor(cfgName)) {
			FontData[] fd = PreferenceConverter
					.basicGetFontData(configService.getActiveUserContact(cfgName, StringUtils.EMPTY));
			fr.put(cfgName, fd);
		}
		return fr.get(cfgName);
	}
}
