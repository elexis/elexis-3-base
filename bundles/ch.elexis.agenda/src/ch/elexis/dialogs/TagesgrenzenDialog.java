/*******************************************************************************
 * Copyright (c) 2006-2007, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation, adapted from JavaAgenda
 *
 *******************************************************************************/
package ch.elexis.dialogs;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.ModelPackage;
import ch.elexis.core.model.builder.IAppointmentBuilder;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.core.services.holder.AppointmentServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.types.AppointmentState;
import ch.elexis.core.types.AppointmentType;
import ch.elexis.core.ui.util.SWTHelper;

public class TagesgrenzenDialog extends TitleAreaDialog {
	LocalDate day;
	Text text;
	String beiwem;
	List<IAppointment> lRes;

	public TagesgrenzenDialog(Shell parent, String tag, String bereich) {
		super(parent);
		day = LocalDate.parse(tag, DateTimeFormatter.ofPattern("yyyyMMdd"));
		beiwem = bereich;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setLayout(new GridLayout());
		text = SWTHelper.createText(ret, 6, SWT.BORDER);

		IQuery<IAppointment> query = CoreModelServiceHolder.get().getQuery(IAppointment.class);
		query.and(ModelPackage.Literals.IAPPOINTMENT__SCHEDULE, COMPARATOR.EQUALS, beiwem);
		query.and("tag", COMPARATOR.EQUALS, day);
		String typReserved = AppointmentServiceHolder.get().getType(AppointmentType.BOOKED);
		query.and(ModelPackage.Literals.IAPPOINTMENT__TYPE, COMPARATOR.EQUALS, typReserved);
		query.orderByLeftPadded("beginn", ORDER.ASC);
		lRes = query.execute();

		StringBuilder sb = new StringBuilder();
		for (IAppointment t : lRes) {
			sb.append(DateTimeFormatter.ofPattern("HH:mm").format(t.getStartTime())).append("-")
					.append(t.getEndTime() != null ? DateTimeFormatter.ofPattern("HH:mm").format(t.getEndTime())
							: StringUtils.EMPTY)
					.append(StringUtils.LF);
		}
		text.setText(sb.toString());
		return ret;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Tagesgrenzen");
		setMessage(
				"Bitte geben Sie nicht planbare Zeiträume in der Form hh:mm-hh:mm jeweils in einer eigenen Zeile ein");
		getShell().setText("Agenda");
	}

	@Override
	protected void okPressed() {
		for (IAppointment t : lRes) {
			CoreModelServiceHolder.get().delete(t);
		}
		String[] sl = text.getText().split("\\s*[\\n*\\r*,]\\n?\\r?\\s*");
		for (String s : sl) {
			String[] lim = s.split("-");
			LocalTime startTime = LocalTime.parse(lim[0], DateTimeFormatter.ofPattern("HH:mm"));
			LocalTime endTime = LocalTime.parse(lim[1], DateTimeFormatter.ofPattern("HH:mm"));
			String typReserved = AppointmentServiceHolder.get().getType(AppointmentType.BOOKED);
			String emptyState = AppointmentServiceHolder.get().getState(AppointmentState.EMPTY);
			new IAppointmentBuilder(CoreModelServiceHolder.get(), beiwem, day.atTime(startTime), day.atTime(endTime),
					typReserved, emptyState).buildAndSave();
		}
		super.okPressed();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
}
