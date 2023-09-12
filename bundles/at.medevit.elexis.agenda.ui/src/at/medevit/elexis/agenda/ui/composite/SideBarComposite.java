package at.medevit.elexis.agenda.ui.composite;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

import at.medevit.elexis.agenda.ui.composite.IAgendaComposite.AgendaSpanSize;
import at.medevit.elexis.agenda.ui.dialog.RecurringAppointmentDialog;
import at.medevit.elexis.agenda.ui.function.LoadEventTimeSpan;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IDayMessage;
import ch.elexis.core.model.IPeriod;
import ch.elexis.core.model.agenda.Area;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.e4.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.e4.locks.ILockHandler;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.utils.CoreUtil;

public class SideBarComposite extends Composite {

	private IAgendaComposite agendaComposite;

	private List<String> selectedResources = new ArrayList<>();

	private ComboViewer spanSizeCombo;

	private ToolBarManager menuManager;

	private Button scrollToNowCheck;

	private TableViewer moveTable;
	private List<IPeriod> movePeriods;

	private MoveInformation currentMoveInformation;

	private Composite areaComposite;

	@Inject
	private IEventBroker eventBroker;

	private DateTime calendar;

	private DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd"); //$NON-NLS-1$
	private Text dayMessage;

	public SideBarComposite(Composite parent, int style) {
		this(parent, false, style);
	}

	public SideBarComposite(Composite parent, boolean includeMove, int style) {
		super(parent, style);
		CoreUiUtil.injectServicesWithContext(this);
		setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		setBackgroundMode(SWT.INHERIT_FORCE);

		setLayout(new GridLayout(1, true));

		menuManager = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL);
		menuManager.add(new Action(">", Action.AS_PUSH_BUTTON) { //$NON-NLS-1$
			@Override
			public void run() {
				if (">".equals(super.getText())) { //$NON-NLS-1$
					showContent();
					super.setText("<"); //$NON-NLS-1$
				} else {
					hideContent();
					super.setText(">"); //$NON-NLS-1$
				}
				super.run();
			}
		});
		menuManager.createControl(this).setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));

		calendar = new DateTime(this, SWT.CALENDAR | SWT.CALENDAR_WEEKNUMBERS);
		calendar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.stateMask > 0 || CoreUtil.isMac()) {
					agendaComposite.setSelectedDate(
							LocalDate.of(calendar.getYear(), calendar.getMonth() + 1, calendar.getDay()));
				}
				updateDayMessage();
			}
		});

		Label label = new Label(this, SWT.NONE);
		String cfgName = "agenda-boldFont"; //$NON-NLS-1$
		FontRegistry fr = JFaceResources.getFontRegistry();
		if (!fr.hasValueFor(cfgName)) {
			FontDescriptor boldDescriptor = FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD);
			fr.put(cfgName, boldDescriptor.getFontData());
		}
		Font boldFont = fr.get(cfgName);
		label.setFont(boldFont);
		label.setText("Bereiche");
		ScrolledComposite areaScrolledComposite = new ScrolledComposite(this, SWT.V_SCROLL);
		areaScrolledComposite.setLayout(new FillLayout());
		areaScrolledComposite.setExpandVertical(true);
		areaScrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		areaScrolledComposite.addListener(SWT.Resize, event -> {
			areaScrolledComposite.setMinSize(areaComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		});

		areaComposite = new Composite(areaScrolledComposite, SWT.NONE);
		areaComposite.setLayout(new GridLayout());
		List<Area> areas = AppointmentServiceHolder.get().getAreas();
		for (Area area : areas) {
			Button btn = new Button(areaComposite, SWT.CHECK);
			btn.setText(area.getName());
			btn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (e.getSource() instanceof Button) {
						if (((Button) e.getSource()).getSelection()) {
							selectedResources.add(((Button) e.getSource()).getText());
						} else {
							selectedResources.remove(((Button) e.getSource()).getText());
						}
						saveSelectedResources();
					}
				}
			});
		}
		areaComposite.pack();
		areaScrolledComposite.setContent(areaComposite);

		label = new Label(this, SWT.NONE);
		label.setFont(boldFont);
		label.setText("Tagesnachricht");
		dayMessage = SWTHelper.createText(this, 4, SWT.V_SCROLL);
		// set text field's maximum width to the width of the calendar
		GridData gd = (GridData) dayMessage.getLayoutData();
		gd.widthHint = calendar.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;

		dayMessage.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				String tx = dayMessage.getText();
				LocalDate date = LocalDate.of(calendar.getYear(), calendar.getMonth() + 1, calendar.getDay());
				Optional<IDayMessage> message = CoreModelServiceHolder.get().load(date.format(yyyyMMdd),
						IDayMessage.class);
				if (message.isPresent()) {
					message.get().setMessage(tx);
					CoreModelServiceHolder.get().save(message.get());
				} else if (StringUtils.isNotBlank(tx)) {
					IDayMessage newMessage = CoreModelServiceHolder.get().create(IDayMessage.class);
					newMessage.setDate(date);
					newMessage.setMessage(tx);
					CoreModelServiceHolder.get().save(newMessage);
				}
			}
		});

		label = new Label(this, SWT.NONE);
		label.setFont(boldFont);
		label.setText(Messages.AgendaUI_SideBar_steps);
		spanSizeCombo = new ComboViewer(this, SWT.BORDER);
		spanSizeCombo.setContentProvider(ArrayContentProvider.getInstance());
		spanSizeCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((AgendaSpanSize) element).getLabel();
			}
		});
		spanSizeCombo.setInput(AgendaSpanSize.values());
		spanSizeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (selection instanceof StructuredSelection) {
					if (!selection.isEmpty()) {
						agendaComposite.setSelectedSpanSize(
								(AgendaSpanSize) ((StructuredSelection) selection).getFirstElement());
						saveConfigurationString("selectedSpanSize", //$NON-NLS-1$
								((AgendaSpanSize) ((StructuredSelection) selection).getFirstElement()).name());
					}
				}
			}
		});

		label = new Label(this, SWT.NONE);
		label.setFont(boldFont);
		label.setText(Messages.AgendaUI_SideBar_auto_scroll_to_now);
		scrollToNowCheck = new Button(this, SWT.CHECK);
		scrollToNowCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				agendaComposite.setScrollToNow(scrollToNowCheck.getSelection());
				saveConfigurationString("scrollToNow", //$NON-NLS-1$
						Boolean.toString(scrollToNowCheck.getSelection()));				
				super.widgetSelected(e);
			}
		});

		Label separator = new Label(this, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button btn = new Button(this, SWT.NONE);
		btn.setText(Messages.AgendaUI_SideBar_create_new_series);
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.getSource() instanceof Button) {
					RecurringAppointmentDialog dlg = new RecurringAppointmentDialog(
							AppointmentServiceHolder.get().createAppointmentSeries());
					dlg.open();
				}
			}
		});

		if (includeMove) {
			label = new Label(this, SWT.NONE);
			label.setFont(boldFont);
			label.setText(Messages.AgendaUI_SideBar_move_date);
			label.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true));
			moveTable = new TableViewer(this, SWT.MULTI);
			moveTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
			moveTable.setContentProvider(ArrayContentProvider.getInstance());
			moveTable.setLabelProvider(new LabelProvider() {
				@Override
				public String getText(Object element) {
					if (element instanceof IPeriod) {
						return ((IPeriod) element).getLabel();
					}
					return super.getText(element);
				}
			});
			MenuManager menuManager = new MenuManager();
			menuManager.add(new Action() {
				@Override
				public String getText() {
					return Messages.AgendaUI_SideBar_abort_move_date;
				}

				@Override
				public ImageDescriptor getImageDescriptor() {
					return Images.IMG_DELETE.getImageDescriptor();
				}

				@Override
				public void run() {
					IStructuredSelection selection = moveTable.getStructuredSelection();
					if (selection != null && !selection.isEmpty()) {
						for (Object selected : selection.toList()) {
							if (selected instanceof IPeriod) {
								SideBarComposite.this.removeMovePeriod((IPeriod) selected);
							}
						}
					}
				}
			});
			Menu contextMenu = menuManager.createContextMenu(moveTable.getTable());
			moveTable.getTable().setMenu(contextMenu);

			gd = new GridData(SWT.LEFT, SWT.BOTTOM, false, false);
			gd.widthHint = 150;
			moveTable.getTable().setLayoutData(gd);
			movePeriods = new ArrayList<>();
		}

		hideContent();
	}

	private void updateDayMessage() {
		LocalDate date = LocalDate.of(calendar.getYear(), calendar.getMonth() + 1, calendar.getDay());
		Optional<IDayMessage> message = CoreModelServiceHolder.get().load(date.format(yyyyMMdd), IDayMessage.class);
		if (message.isPresent()) {
			dayMessage.setText(message.get().getMessage());
		} else {
			dayMessage.setText(StringUtils.EMPTY);
		}
	}

	@Inject
	@org.eclipse.e4.core.di.annotations.Optional
	public void loadDate(@UIEventTopic(ElexisEventTopics.BASE + "agenda/loadtimespan") LoadEventTimeSpan timespan) {
		if (timespan != null && calendar != null && !calendar.isDisposed()) {
			if (!LocalDate.of(calendar.getYear(), calendar.getMonth() + 1, calendar.getDay())
					.equals(timespan.getFrom())) {
				if (agendaComposite instanceof WeekComposite && timespan.isWeek()) {
					calendar.setDate(timespan.getFrom().getYear(), timespan.getFrom().getMonthValue() - 1,
							timespan.getFrom().getDayOfMonth());
				} else if (agendaComposite instanceof ParallelComposite && timespan.isDay()) {
					calendar.setDate(timespan.getFrom().getYear(), timespan.getFrom().getMonthValue() - 1,
							timespan.getFrom().getDayOfMonth());
				}
				updateDayMessage();
			}
		}
	}

	private void hideContent() {
		Control[] controls = getChildren();
		for (Control control : controls) {
			if (control instanceof ToolBar) {
				// never hide the toolbar

				continue;
			}
			GridData gridData = (GridData) control.getLayoutData();
			if (gridData == null) {
				gridData = new GridData();
				control.setLayoutData(gridData);
			}
			control.setVisible(false);
			gridData.exclude = true;
		}
		getParent().layout();
	}

	private void showContent() {
		Control[] controls = getChildren();
		for (Control control : controls) {
			GridData gridData = (GridData) control.getLayoutData();
			if (gridData == null) {
				gridData = new GridData();
				control.setLayoutData(gridData);
			}
			control.setVisible(true);
			gridData.exclude = false;
		}
		getParent().layout();
		updateDayMessage();
	}

	public void setAgendaComposite(IAgendaComposite agendaComposite) {
		this.agendaComposite = agendaComposite;
		loadSelectedResources();
		agendaComposite.setSelectedResources(selectedResources);
		String selectedSpanSize = loadConfigurationString("selectedSpanSize"); //$NON-NLS-1$
		if (!selectedSpanSize.isEmpty()) {
			spanSizeCombo.setSelection(new StructuredSelection(AgendaSpanSize.valueOf(selectedSpanSize)));
		}
		String value = loadConfigurationString("scrollToNow"); //$NON-NLS-1$
		if (value != null && value.equalsIgnoreCase("true")) { //$NON-NLS-1$
			scrollToNowCheck.setSelection(true);
			agendaComposite.setScrollToNow(true);
		}
	}

	private void saveSelectedResources() {
		agendaComposite.setSelectedResources(selectedResources);
		StringBuilder sb = new StringBuilder();
		for (String resource : selectedResources) {
			if (sb.length() > 0) {
				sb.append("|"); //$NON-NLS-1$
			}
			sb.append(resource);
		}
		System.out.println(sb.toString());
		saveConfigurationString("selectedResources", sb.toString()); //$NON-NLS-1$
	}

	private void loadSelectedResources() {
		String loadedResources = loadConfigurationString("selectedResources"); //$NON-NLS-1$
		String[] parts = loadedResources.split("\\|"); //$NON-NLS-1$
		if (parts.length > 0 && !parts[0].isEmpty()) {
			selectedResources.clear();
			selectedResources.addAll(Arrays.asList(parts));
			List<String> selections = new ArrayList<>();
			// update button selection

			for (Control child : areaComposite.getChildren()) {
				if (child instanceof Button) {
					if (selectedResources.contains(((Button) child).getText())) {
						((Button) child).setSelection(true);
						selections.add(((Button) child).getText());
					} else {
						((Button) child).setSelection(false);
					}
				}
			}
			selectedResources = selections;
		}
	}

	private void saveConfigurationString(String configKey, String value) {
		ConfigServiceHolder.get().setActiveUserContact(
				"at.medevit.elexis.agenda.ui/" + agendaComposite.getConfigId() + "/" + configKey, //$NON-NLS-1$ //$NON-NLS-2$
				value);	
		}

	private String loadConfigurationString(String configKey) {
		return ConfigServiceHolder.get().getActiveUserContact(
				"at.medevit.elexis.agenda.ui/" + agendaComposite.getConfigId() + "/" + configKey, StringUtils.EMPTY); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public void addMovePeriod(IPeriod period) {
		if (moveTable != null && !moveTable.getTable().isDisposed()) {
			if (!movePeriods.contains(period)) {
				movePeriods.add(period);
			}
			moveTable.setInput(movePeriods);
		}
	}

	public void removeMovePeriod(IPeriod period) {
		if (moveTable != null && !moveTable.getTable().isDisposed()) {
			movePeriods.remove(period);
			moveTable.setInput(movePeriods);
		}
	}

	public Optional<MoveInformation> getMoveInformation() {
		if (currentMoveInformation != null) {
			currentMoveInformation.setMoveablePeriods(movePeriods);
		}
		return Optional.ofNullable(currentMoveInformation);
	}

	public void setMoveInformation(LocalDateTime date, String resource) {
		currentMoveInformation = new MoveInformation(this, date, resource);
	}

	public static class MoveInformation {
		private SideBarComposite sideBar;
		private LocalDateTime dateTime;
		private String resource;

		private List<IPeriod> moveablePeriods;

		public MoveInformation(SideBarComposite sideBar, LocalDateTime dateTime, String resource) {
			this.sideBar = sideBar;
			this.dateTime = dateTime;
			this.resource = resource;
		}

		public void setMoveablePeriods(List<IPeriod> periods) {
			this.moveablePeriods = new ArrayList<>(periods);
		}

		public List<IPeriod> getMoveablePeriods() {
			return moveablePeriods;
		}

		public void movePeriod(IPeriod iPeriod) {
			AcquireLockBlockingUi.aquireAndRun(iPeriod, new ILockHandler() {
				@Override
				public void lockAcquired() {
					iPeriod.setStartTime(dateTime);
					if (iPeriod instanceof IAppointment) {
						((IAppointment) iPeriod).setSchedule(resource);
					}
					CoreModelServiceHolder.get().save(iPeriod);
					Display.getDefault().timerExec(250, new Runnable() {
						@Override
						public void run() {
							if (sideBar != null && !sideBar.isDisposed()) {
								sideBar.removeMovePeriod(iPeriod);
								sideBar.eventBroker.post(ElexisEventTopics.EVENT_RELOAD, IAppointment.class);
							}
						}
					});
				}

				@Override
				public void lockFailed() {
					// TODO Auto-generated method stub

				}
			});
		}
	}
}
