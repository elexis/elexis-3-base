package ch.elexis.base.ch.ebanking;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import ch.elexis.base.ch.ebanking.esr.Messages;
import ch.elexis.base.ch.ebanking.model.IEsrRecord;
import ch.elexis.core.model.IInvoice;
import ch.elexis.core.model.esr.ESRRejectCode;
import ch.elexis.core.ui.UiDesk;

public class ESRLabelProvider extends LabelProvider implements ITableLabelProvider, ITableColorProvider {

	DecimalFormat df = new DecimalFormat("###0.00"); //$NON-NLS-1$

	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy"); // $NON-NLS-1$

	public String getColumnText(Object element, int columnIndex) {
		String text = StringUtils.EMPTY;

		IEsrRecord rec = (IEsrRecord) element;

		switch (columnIndex) {
		case ESRView.DATUM_INDEX:
			text = formatter.format(rec.getDate());
			break;
		case ESRView.RN_NUMMER_INDEX:
			IInvoice rn = rec.getInvoice();
			if (rn != null) {
				text = rn.getNumber();
			}
			break;
		case ESRView.BETRAG_INDEX:
			text = rec.getAmount().getAmountAsString();
			break;
		case ESRView.EINGELESEN_INDEX:
			text = formatter.format(rec.getImportDate());
			break;
		case ESRView.VERRECHNET_INDEX:
			text = formatter.format(rec.getProcessingDate());
			break;
		case ESRView.GUTGESCHRIEBEN_INDEX:
			text = formatter.format(rec.getValutaDate());
			break;
		case ESRView.PATIENT_INDEX:
			text = rec.getPatient() != null ? rec.getPatient().getLabel() : StringUtils.EMPTY;
			break;
		case ESRView.BUCHUNG_INDEX:
			if (!rec.hasBookedDate()) {
				text = Messages.ESRView2_notbooked;
			} else {
				text = formatter.format(rec.getBookedDate());
			}
			break;
		case ESRView.DATEI_INDEX:
			text = rec.getFile();
			break;
		}

		return text;
	}

	public Color getForeground(Object element, int columnIndex) {
		return null;
	}

	public Color getBackground(Object element, int columnIndex) {
		IEsrRecord rec = (IEsrRecord) element;

		if (rec.getRejectCode() == ESRRejectCode.OK) {
			if (!rec.hasBookedDate()) {
				return UiDesk.getColor(UiDesk.COL_GREY);
			}
			return UiDesk.getColor(UiDesk.COL_WHITE);
		} else {
			return UiDesk.getColor(UiDesk.COL_RED);
		}
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

}
