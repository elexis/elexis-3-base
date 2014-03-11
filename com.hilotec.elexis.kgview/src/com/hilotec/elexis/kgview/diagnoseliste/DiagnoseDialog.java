package com.hilotec.elexis.kgview.diagnoseliste;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.icpc.IcpcCode;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

import com.tiff.common.ui.datepicker.DatePickerCombo;

public class DiagnoseDialog extends TitleAreaDialog {
	private DiagnoselisteItem di;
	private DatePickerCombo date;
	private Text text;
	private boolean showDate;
	private boolean showICPC;
	
	public DiagnoseDialog(Shell parentShell, DiagnoselisteItem di, boolean showDate,
		boolean showICPC){
		super(parentShell);
		this.di = di;
		this.showDate = showDate;
		this.showICPC = showICPC;
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		setTitle("Eintrag bearbeiten");
		
		Composite comp = new Composite(parent, 0);
		comp.setLayout(new GridLayout(2, false));
		comp.setLayoutData(SWTHelper.getFillGridData());
		
		Label lblText = new Label(comp, 0);
		lblText.setText("Text");
		text = SWTHelper.createText(comp, 5, SWT.MULTI);
		text.setText(di.getText());
		text.setLayoutData(SWTHelper.getFillGridData());
		
		if (showDate) {
			Label lblDate = new Label(comp, 0);
			lblDate.setText("Datum");
			date = new DatePickerCombo(comp, 0);
			TimeTool tt = new TimeTool(di.getDatum());
			date.setDate(tt.getTime());
		}
		
		if (showICPC) {
			Label lblICPC = new Label(comp, 0);
			lblICPC.setText("ICPC");
			
			Label icpc = new Label(comp, 0);
			String code = di.getICPC();
			if (!StringTool.isNothing(code)) {
				IcpcCode c = IcpcCode.load(code);
				icpc.setText(c.getLabel());
			}
		}
		
		return comp;
	}
	
	@Override
	protected void okPressed(){
		di.setText(text.getText());
		if (showDate) {
			di.setDatum(new TimeTool(date.getDate().getTime()).toString(TimeTool.DATE_GER));
		}
		close();
	}
}
