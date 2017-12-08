package ch.elexis.base.ch.ebanking;

import java.text.DecimalFormat;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import ch.elexis.base.ch.ebanking.esr.ESRRecord;
import ch.elexis.base.ch.ebanking.esr.Messages;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Rechnung;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class ESRLabelProvider extends LabelProvider implements ITableLabelProvider,
		ITableColorProvider {
	
	DecimalFormat df = new DecimalFormat("###0.00"); //$NON-NLS-1$
	
	public String getColumnText(Object element, int columnIndex){
		String text = ""; //$NON-NLS-1$
		
		ESRRecord rec = (ESRRecord) element;
		
		switch (columnIndex) {
		case ESRView.DATUM_INDEX:
			text = rec.get(ESRRecord.FLD_DATE); //$NON-NLS-1$
			break;
		case ESRView.RN_NUMMER_INDEX:
			Rechnung rn = rec.getRechnung();
			if (rn != null) {
				text = rn.getNr();
			}
			break;
		case ESRView.BETRAG_INDEX:
			text = rec.getBetrag().getAmountAsString();
			break;
		case ESRView.EINGELESEN_INDEX:
			text = rec.getEinlesedatatum();
			break;
		case ESRView.VERRECHNET_INDEX:
			text = rec.getVerarbeitungsdatum();
			break;
		case ESRView.GUTGESCHRIEBEN_INDEX:
			text = rec.getValuta();
			break;
		case ESRView.PATIENT_INDEX:
			text = rec.getPatient().getLabel();
			break;
		case ESRView.BUCHUNG_INDEX:
			String dat = rec.getGebucht();
			if (StringTool.isNothing(dat)) {
				text = Messages.ESRView2_notbooked;
			} else {
				text = new TimeTool(dat).toString(TimeTool.DATE_GER);
			}
			break;
		case ESRView.DATEI_INDEX:
			text = rec.getFile();
			break;
		}
		
		return text;
	}
	
	public Color getForeground(Object element, int columnIndex){
		return null;
	}
	
	public Color getBackground(Object element, int columnIndex){
		ESRRecord rec = (ESRRecord) element;
		
		if (rec.getRejectCode().equals(ESRRecord.REJECT.OK)) {
			if (StringTool.isNothing(rec.getGebucht())) {
				return UiDesk.getColor(UiDesk.COL_GREY);
			}
			return UiDesk.getColor(UiDesk.COL_WHITE);
		} else {
			return UiDesk.getColor(UiDesk.COL_RED);
		}
	}
	
	@Override
	public Image getColumnImage(Object element, int columnIndex){
		return null;
	}
	
}
