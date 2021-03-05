package ch.elexis.global_inbox.ui.parts;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;

import ch.elexis.core.jdt.Nullable;
import ch.elexis.core.time.TimeUtil;

public class MultiDateSelector extends Composite {
	
	private Date value;
	
	private List<SelectionListener> listeners;
	
	private Composite optionsComposite;
	private CDateTime dateTime;
	
	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public MultiDateSelector(Composite parent, int style){
		super(parent, style);
		listeners = new ArrayList<SelectionListener>();
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.verticalSpacing = 3;
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
		
		dateTime =
			new CDateTime(this, CDT.DATE_SHORT | CDT.DROP_DOWN | SWT.BORDER | CDT.TAB_FIELDS);
		GridData gd_dateTime = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_dateTime.widthHint = 100;
		dateTime.setLayoutData(gd_dateTime);
		dateTime.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				value = dateTime.getSelection();
				listeners.forEach(l -> l.widgetSelected(e));
			};
		});
		
		optionsComposite = new Composite(this, SWT.EMBEDDED);
		RowLayout rl_optionsComposite = new RowLayout(SWT.HORIZONTAL);
		rl_optionsComposite.spacing = 3;
		rl_optionsComposite.marginTop = 0;
		rl_optionsComposite.marginBottom = 0;
		rl_optionsComposite.marginRight = 0;
		optionsComposite.setLayout(rl_optionsComposite);
		GridData gd_optionsComposite = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gd_optionsComposite.heightHint = 20;
		optionsComposite.setLayoutData(gd_optionsComposite);
	}
	
	@Override
	protected void checkSubclass(){
		// Disable the check that prevents subclassing of SWT components
	}
	
	public void addSelectionListener(SelectionListener selectionListener){
		listeners.add(selectionListener);
	}
	
	public Date getSelection(){
		return value;
	}
	
	/**
	 * 
	 * @param selectionOptions
	 * @param defaultDate
	 * @return the determined default to for preselection
	 */
	public Date setSelectionOptionsAndDefault(List<LocalDate> selectionOptions, @Nullable Date defaultDate){
		
		dateTime.setSelection(null);
		
		Control[] children = optionsComposite.getChildren();
		for (Control control : children) {
			control.dispose();
		}
		
		Collections.sort(selectionOptions, Collections.reverseOrder());
		for (LocalDate option : selectionOptions) {
			Link dateLink = new Link(optionsComposite, SWT.FLAT);
			dateLink.setText("<a>" + TimeUtil.formatSafe(option, TimeUtil.DATE_GER_SHORT) + "</a>");
			dateLink.setData(TimeUtil.toDate(option));
			dateLink.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e){
					value = (Date) ((Link) e.getSource()).getData();
					dateTime.setSelection(value);
					listeners.forEach(l -> l.widgetSelected(e));
				};
			});
		}
		
		optionsComposite.layout(true);
		
		if (defaultDate != null) {
			dateTime.setSelection(defaultDate);
		} else {
			if (selectionOptions.size() > 0) {
				dateTime.setSelection(TimeUtil.toDate(selectionOptions.get(0)));
			}
		}
		
		return dateTime.getSelection();
	}
	
}
