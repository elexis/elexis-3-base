package at.medevit.elexis.agenda.ui.composite;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;

import at.medevit.elexis.agenda.ui.composite.IAgendaComposite.AgendaSpanSize;
import ch.elexis.agenda.data.Termin;
import ch.elexis.agenda.series.ui.SerienTerminDialog;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.model.IPeriod;
import ch.elexis.core.ui.icons.Images;
import ch.rgw.tools.TimeTool;

public class SideBarComposite extends Composite {
	
	private IAgendaComposite agendaComposite;
	
	private List<String> selectedResources = new ArrayList<>();
	
	private ComboViewer spanSizeCombo;
	
	private ToolBarManager menuManager;
	
	private Button scrollToNowCheck;
	
	private TableViewer moveTable;
	private List<IPeriod> movePeriods;
	
	private MoveInformation currentMoveInformation;
	
	public SideBarComposite(Composite parent, int style){
		this(parent, false, style);
	}
	
	public SideBarComposite(Composite parent, boolean includeMove, int style){
		super(parent, style);
		setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		setBackgroundMode(SWT.INHERIT_FORCE);
		
		setLayout(new GridLayout(1, true));
		
		menuManager = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL);
		menuManager.add(new Action(">", Action.AS_PUSH_BUTTON) {
			@Override
			public void run(){
				if (">".equals(super.getText())) {
					showContent();
					super.setText("<");
				} else {
					hideContent();
					super.setText(">");
				}
				super.run();
			}
		});
		menuManager.createControl(this)
			.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));

		DateTime calendar = new DateTime(this, SWT.CALENDAR);
		calendar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				agendaComposite.setSelectedDate(
					LocalDate.of(calendar.getYear(), calendar.getMonth() + 1, calendar.getDay()));
			}
		});
		
		Label label = new Label(this, SWT.NONE);
		FontDescriptor boldDescriptor =
			FontDescriptor.createFrom(label.getFont()).setStyle(SWT.BOLD);
		Font boldFont = boldDescriptor.createFont(label.getDisplay());
		label.setFont(boldFont);
		label.setText("Bereiche");
		for (String bereich : Termin.TerminBereiche) {
			Button btn = new Button(this, SWT.CHECK);
			btn.setText(bereich);
			btn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e){
					if (e.getSource() instanceof Button) {
						if (((Button) e.getSource()).getSelection()) {
							selectedResources.add(((Button) e.getSource()).getText());
						} else {
							selectedResources.remove(((Button) e.getSource()).getText());
						}
						agendaComposite.setSelectedResources(selectedResources);
						saveSelectedResources();
					}
				}
			});
		}
		
		label = new Label(this, SWT.NONE);
		label.setFont(boldFont);
		label.setText("Zeitschritte");
		spanSizeCombo = new ComboViewer(this, SWT.BORDER);
		spanSizeCombo.setContentProvider(ArrayContentProvider.getInstance());
		spanSizeCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element){
				return ((AgendaSpanSize) element).getLabel();
			}
		});
		spanSizeCombo.setInput(AgendaSpanSize.values());
		spanSizeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event){
				ISelection selection = event.getSelection();
				if (selection instanceof StructuredSelection) {
					if (!selection.isEmpty()) {
						agendaComposite.setSelectedSpanSize(
							(AgendaSpanSize) ((StructuredSelection) selection).getFirstElement());
						saveConfigurationString("selectedSpanSize",
							((AgendaSpanSize) ((StructuredSelection) selection).getFirstElement())
								.name());
					}
				}
			}
		});
		
		label = new Label(this, SWT.NONE);
		label.setFont(boldFont);
		label.setText("Auto. zu jetzt scrollen");
		scrollToNowCheck = new Button(this, SWT.CHECK);
		scrollToNowCheck.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				agendaComposite.setScrollToNow(scrollToNowCheck.getSelection());
				saveConfigurationString("scrollToNow",
					Boolean.toString(scrollToNowCheck.getSelection()));
				super.widgetSelected(e);
			}
		});
		
		Label separator = new Label(this, SWT.HORIZONTAL | SWT.SEPARATOR);
		separator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button btn = new Button(this, SWT.NONE);
		btn.setText("Neue Serie anlegen");
		btn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				if (e.getSource() instanceof Button) {
					SerienTerminDialog dlg =
						new SerienTerminDialog(getShell(), null);
					dlg.open();
				}
			}
		});
		
		if (includeMove) {
			label = new Label(this, SWT.NONE);
			label.setFont(boldFont);
			label.setText("Termin verschieben");
			label.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true));
			moveTable = new TableViewer(this, SWT.MULTI);
			moveTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, false));
			moveTable.setContentProvider(ArrayContentProvider.getInstance());
			moveTable.setLabelProvider(new LabelProvider() {
				@Override
				public String getText(Object element){
					if (element instanceof IPeriod) {
						return ((IPeriod) element).getLabel();
					}
					return super.getText(element);
				}
			});
			MenuManager menuManager = new MenuManager();
			menuManager.add(new Action() {
				@Override
				public String getText(){
					return "verschieben abbrechen";
				}
				
				@Override
				public ImageDescriptor getImageDescriptor(){
					return Images.IMG_DELETE.getImageDescriptor();
				}
				
				@Override
				public void run(){
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
			
			GridData gd = new GridData(SWT.LEFT, SWT.BOTTOM, false, false);
			gd.widthHint = 150;
			moveTable.getTable().setLayoutData(gd);
			movePeriods = new ArrayList<>();
		}
		
		hideContent();
	}
	
	private void hideContent(){
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
	
	private void showContent(){
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
	}
	
	public void setAgendaComposite(IAgendaComposite agendaComposite){
		this.agendaComposite = agendaComposite;
		loadSelectedResources();
		agendaComposite.setSelectedResources(selectedResources);
		String selectedSpanSize = loadConfigurationString("selectedSpanSize");
		if (!selectedSpanSize.isEmpty()) {
			spanSizeCombo
				.setSelection(new StructuredSelection(AgendaSpanSize.valueOf(selectedSpanSize)));
		}
		String value = loadConfigurationString("scrollToNow");
		if (value != null && value.equalsIgnoreCase("true")) {
			scrollToNowCheck.setSelection(true);
			agendaComposite.setScrollToNow(true);
		}
	}
	
	private void saveSelectedResources(){
		agendaComposite.setSelectedResources(selectedResources);
		StringBuilder sb = new StringBuilder();
		for (String resource : selectedResources) {
			if (sb.length() > 0) {
				sb.append("|");
			}
			sb.append(resource);
		}
		saveConfigurationString("selectedResources", sb.toString());
	}
	
	private void loadSelectedResources(){
		String loadedResources = loadConfigurationString("selectedResources");
		String[] parts = loadedResources.split("\\|");
		if (parts.length > 0 && !parts[0].isEmpty()) {
			selectedResources.clear();
			selectedResources.addAll(Arrays.asList(parts));
			List<String> selections = new ArrayList<>();
			// update button selection
			for (Control child : getChildren()) {
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
	
	private void saveConfigurationString(String configKey, String value){
		CoreHub.localCfg.set(
			"at.medevit.elexis.agenda.ui/" + agendaComposite.getConfigId() + "/" + configKey,
			value);
	}
	
	private String loadConfigurationString(String configKey){
		return CoreHub.localCfg.get(
			"at.medevit.elexis.agenda.ui/" + agendaComposite.getConfigId() + "/" + configKey, "");
	}
	
	public void addMovePeriod(IPeriod period){
		if (moveTable != null && !moveTable.getTable().isDisposed()) {
			if (!movePeriods.contains(period)) {
				movePeriods.add(period);
			}
			moveTable.setInput(movePeriods);
		}
	}
	
	public void removeMovePeriod(IPeriod period){
		if (moveTable != null && !moveTable.getTable().isDisposed()) {
			movePeriods.remove(period);
			moveTable.setInput(movePeriods);
		}
	}
	
	public Optional<MoveInformation> getMoveInformation(){
		if (currentMoveInformation != null) {
			currentMoveInformation.setMoveablePeriods(movePeriods);
		}
		return Optional.ofNullable(currentMoveInformation);
	}
	
	public void setMoveInformation(LocalDateTime date, String resource){
		currentMoveInformation = new MoveInformation(this, date, resource);
	}
	
	public static class MoveInformation {
		private SideBarComposite sideBar;
		private LocalDateTime dateTime;
		private String resource;
		
		private List<IPeriod> moveablePeriods;
		
		public MoveInformation(SideBarComposite sideBar, LocalDateTime dateTime, String resource){
			this.sideBar = sideBar;
			this.dateTime = dateTime;
			this.resource = resource;
		}
		
		public void setMoveablePeriods(List<IPeriod> periods){
			this.moveablePeriods = new ArrayList<>(periods);
		}
		
		public List<IPeriod> getMoveablePeriods(){
			return moveablePeriods;
		}
		
		public void movePeriod(IPeriod iPeriod){
			iPeriod.setStartTime(new TimeTool(dateTime));
			if (iPeriod instanceof Termin) {
				((Termin) iPeriod).setBereich(resource);
			}
			moveablePeriods.remove(iPeriod);
			Display.getDefault().timerExec(250, new Runnable() {
				@Override
				public void run(){
					if (sideBar != null && !sideBar.isDisposed()) {
						sideBar.removeMovePeriod(iPeriod);
						sideBar.agendaComposite.refetchEvents();
					}
				}
			});
		}
	}
}
