package at.medevit.elexis.agenda.ui.composite;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import at.medevit.elexis.agenda.ui.composite.IAgendaComposite.AgendaSpanSize;
import ch.elexis.agenda.data.Termin;
import ch.elexis.core.data.activator.CoreHub;

public class SideBarComposite extends Composite {
	
	private IAgendaComposite agendaComposite;
	
	private List<String> selectedResources = new ArrayList<>();
	
	private ComboViewer spanSizeCombo;
	
	private Label indication;
	
	public SideBarComposite(Composite parent, int style){
		super(parent, style);
		setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		setBackgroundMode(SWT.INHERIT_FORCE);
		
		setLayout(new GridLayout(1, true));
		
		indication = new Label(this, SWT.NONE);
		indication.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false));
		
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
		
		addListener(SWT.MouseEnter, new Listener() {
			@Override
			public void handleEvent(Event event){
				showContent();
			}
		});
		
		addListener(SWT.MouseExit, new Listener() {
			@Override
			public void handleEvent(Event event){
				// filter exit events triggered by moving over children
				Rectangle bounds = getBounds();
				if (!bounds.contains(new Point(event.x, event.y))) {
					hideContent();
				}
			}
		});
		
		hideContent();
	}
	
	private void hideContent(){
		Control[] controls = getChildren();
		for (Control control : controls) {
			if (control == indication) {
				indication.setText(">");
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
			if (control == indication) {
				indication.setText("<");
				continue;
			}
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
			// update button selection
			for (Control child : getChildren()) {
				if (child instanceof Button) {
					if (selectedResources.contains(((Button) child).getText())) {
						((Button) child).setSelection(true);
					} else {
						((Button) child).setSelection(false);
					}
				}
			}
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
}
