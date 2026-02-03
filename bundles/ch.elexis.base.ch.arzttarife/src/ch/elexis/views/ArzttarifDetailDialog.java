/*******************************************************************************
 * Copyright (c) 2008, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/

package ch.elexis.views;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.arzttarife.ambulatory.IAmbulatoryAllowance;
import ch.elexis.base.ch.arzttarife.tardoc.ITardocLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.base.ch.arzttarife.util.ArzttarifeUtil;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.verrechnet.Constants;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.Money;

public class ArzttarifDetailDialog extends Dialog {
	private IBilled billed;
	private Combo cSide;
	private ComboViewer cBezug;

	public ArzttarifDetailDialog(Shell shell, IBilled tl) {
		super(shell);
		billed = tl;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		IBillable billable = billed.getBillable();
		Composite ret = (Composite) super.createDialogArea(parent);
		ret.setLayout(new GridLayout(8, false));

		Label lTitle = new Label(ret, SWT.WRAP);
		lTitle.setText(billable.getText());
		lTitle.setLayoutData(SWTHelper.getFillGridData(8, true, 1, true));

		if (isArzttarif(billable)) {
			double primaryScale = billed.getPrimaryScaleFactor();
			double secondaryScale = billed.getSecondaryScaleFactor();
			double tpAL = ArzttarifeUtil.getAL(billed) / 100.0;
			double tpTL = ArzttarifeUtil.getTL(billed) / 100.0;
			double tpw = billed.getFactor();
			Money mAL = new Money(tpAL * tpw * primaryScale * secondaryScale);
			Money mTL = new Money(tpTL * tpw * primaryScale * secondaryScale);
			double tpAll = Math.round((tpAL + tpTL) * 100.0) / 100.0;
			Money mAll = new Money(tpAll * tpw * primaryScale * secondaryScale);

			new Label(ret, SWT.NONE).setText("TP AL");
			new Label(ret, SWT.NONE).setText(Double.toString(tpAL));
			new Label(ret, SWT.NONE).setText(" x ");
			new Label(ret, SWT.NONE).setText("TP-Wert");
			new Label(ret, SWT.NONE).setText(Double.toString(tpw));
			new Label(ret, SWT.NONE).setText(" = ");
			new Label(ret, SWT.NONE).setText("CHF AL");
			new Label(ret, SWT.NONE).setText(mAL.getAmountAsString());

			new Label(ret, SWT.NONE).setText("TP TL");
			new Label(ret, SWT.NONE).setText(Double.toString(tpTL));
			new Label(ret, SWT.NONE).setText(" x ");
			new Label(ret, SWT.NONE).setText("TP-Wert");
			new Label(ret, SWT.NONE).setText(Double.toString(tpw));
			new Label(ret, SWT.NONE).setText(" = ");
			new Label(ret, SWT.NONE).setText("CHF TL");
			new Label(ret, SWT.NONE).setText(mTL.getAmountAsString());

			Label sep = new Label(ret, SWT.SEPARATOR | SWT.HORIZONTAL);
			sep.setLayoutData(SWTHelper.getFillGridData(8, true, 1, false));

			new Label(ret, SWT.NONE).setText("TP ");
			new Label(ret, SWT.NONE).setText(Double.toString(tpAll));
			new Label(ret, SWT.NONE).setText(" x ");
			new Label(ret, SWT.NONE).setText("TP-Wert");
			new Label(ret, SWT.NONE).setText(Double.toString(tpw));
			new Label(ret, SWT.NONE).setText(" = ");
			new Label(ret, SWT.NONE).setText("CHF ");
			new Label(ret, SWT.NONE).setText(mAll.getAmountAsString());

			Label sep2 = new Label(ret, SWT.SEPARATOR | SWT.HORIZONTAL);
			sep2.setLayoutData(SWTHelper.getFillGridData(8, true, 1, false));

			String mins = Integer.toString(getMinutesReflective(billable));
			new Label(ret, SWT.NONE).setText("Zeit:");
			new Label(ret, SWT.NONE).setText(mins + " min.");

			new Label(ret, SWT.NONE).setText("Seite");
			cSide = new Combo(ret, SWT.SINGLE);
			cSide.setItems(new String[] { "egal", "links", "rechts" });

			String side = (String) billed.getExtInfo(Constants.FLD_EXT_SIDE);
			if (side == null) {
				cSide.select(0);
			} else if (side.equalsIgnoreCase("l")) {
				cSide.select(1);
			} else {
				cSide.select(2);
			}
			if (getServiceTypReflective(billable).equals("Z") || getServiceTypReflective(billable).equals("R")
					|| getServiceTypReflective(billable).equals("B")) {
				new Label(ret, SWT.NONE);
				new Label(ret, SWT.NONE);
				new Label(ret, SWT.NONE).setText("Bezug");

				cBezug = new ComboViewer(ret, SWT.BORDER);
				cBezug.setContentProvider(ArrayContentProvider.getInstance());
				cBezug.setLabelProvider(new LabelProvider());
				List<BezugComboItem> input = new ArrayList<>();
				input.add(BezugComboItem.noBezug());
				for (IBilled kVerr : billed.getEncounter().getBilled()) {
					if (!kVerr.getCode().equals(billable.getCode())) {
						input.add(BezugComboItem.of(kVerr.getCode()));
					}
				}
				cBezug.setInput(input);
				String bezug = (String) billed.getExtInfo("Bezug");
				if (bezug != null) {
					if (!input.contains(BezugComboItem.of(bezug))) {
						input.add(BezugComboItem.of(bezug));
						cBezug.setInput(input);
					}
					cBezug.setSelection(new StructuredSelection(BezugComboItem.of(bezug)), true);
				} else {
					cBezug.setSelection(new StructuredSelection(BezugComboItem.noBezug()), true);
				}
				cBezug.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						StructuredSelection selection = (StructuredSelection) cBezug.getSelection();
						if (selection != null && !selection.isEmpty()) {
							BezugComboItem selected = (BezugComboItem) selection.getFirstElement();
							if (selected.isNoBezug) {
								billed.setExtInfo("Bezug", StringUtils.EMPTY);
							} else {
								billed.setExtInfo("Bezug", selected.getCode());
							}
						}
					}
				});
			}
		} else if (isPauschale(billable)) {
			double tpAll = ((double) billed.getPoints()) / 100;
			double tpw = billed.getFactor();
			Money mAll = billed.getTotal();

			new Label(ret, SWT.NONE).setText("TP ");
			new Label(ret, SWT.NONE).setText(Double.toString(tpAll));
			new Label(ret, SWT.NONE).setText(" x ");
			new Label(ret, SWT.NONE).setText("TP-Wert");
			new Label(ret, SWT.NONE).setText(Double.toString(tpw));
			new Label(ret, SWT.NONE).setText(" = ");
			new Label(ret, SWT.NONE).setText("CHF ");
			new Label(ret, SWT.NONE).setText(mAll.getAmountAsString());
		}
		ret.pack();
		return ret;
	}

	private String getServiceTypReflective(IBillable billable) {
		try {
			Method getterMethod = billable.getClass().getMethod("getServiceTyp", (Class[]) null);
			Object typ = getterMethod.invoke(billable, (Object[]) null);
			if (typ instanceof String) {
				return (String) typ;
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			LoggerFactory.getLogger(getClass()).warn("Could not get service typ of [" + billable + "]", e.getMessage());
		}
		return null;
	}

	private Integer getMinutesReflective(IBillable billable) {
		try {
			Method getterMethod = billable.getClass().getMethod("getMinutes", (Class[]) null);
			Object minutes = getterMethod.invoke(billable, (Object[]) null);
			if (minutes instanceof Integer) {
				return (Integer) minutes;
			}
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			LoggerFactory.getLogger(getClass()).warn("Could not get minutes of [" + billable + "]", e.getMessage());
		}
		return Integer.valueOf(0);
	}

	private static class BezugComboItem {
		private String code;
		private boolean isNoBezug;

		public static BezugComboItem of(String code) {
			BezugComboItem ret = new BezugComboItem();
			ret.setCode(code);
			return ret;
		}

		public static BezugComboItem noBezug() {
			BezugComboItem ret = new BezugComboItem();
			ret.setCode("kein Bezug");
			ret.isNoBezug = true;
			return ret;
		}

		public boolean isNoBezug() {
			return isNoBezug;
		}

		private void setCode(String code) {
			this.code = code;
		}

		public String getCode() {
			return this.code;
		}

		@Override
		public String toString() {
			return getCode();
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((code == null) ? 0 : code.hashCode());
			result = prime * result + (isNoBezug ? 1231 : 1237);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			BezugComboItem other = (BezugComboItem) obj;
			if (code == null) {
				if (other.code != null)
					return false;
			} else if (!code.equals(other.code))
				return false;
			if (isNoBezug != other.isNoBezug)
				return false;
			return true;
		}
	}

	@Override
	public void create() {
		super.create();
		getShell().setText(billed.getBillable().getCodeSystemName() + "-Details: " + billed.getCode());
	}

	@Override
	protected void okPressed() {
		int idx = cSide.getSelectionIndex();
		if (idx < 1) {
			billed.setExtInfo(Constants.FLD_EXT_SIDE, null);
		} else if (idx == 1) {
			billed.setExtInfo(Constants.FLD_EXT_SIDE, Constants.SIDE_L);
		} else {
			billed.setExtInfo(Constants.FLD_EXT_SIDE, Constants.SIDE_R);
		}
		CoreModelServiceHolder.get().save(billed);
		super.okPressed();
	}

	public static boolean isPauschale(IBillable billable) {
		return billable instanceof IAmbulatoryAllowance;
	}

	public static boolean isArzttarif(IBillable billable) {
		return billable instanceof ITardocLeistung || billable instanceof ITarmedLeistung;
	}
}
