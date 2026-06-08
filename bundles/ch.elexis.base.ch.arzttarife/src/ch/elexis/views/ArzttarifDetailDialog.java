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
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
	private IBillable billable;
	private Combo cSide;
	private ComboViewer cBezug;
	private Button bFranchiseFree;

	private BezugComboItem selectedBezug;

	public ArzttarifDetailDialog(Shell shell, IBilled tl) {
		super(shell);
		billed = tl;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		billable = billed.getBillable();
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

			if (requiresSide(billed.getBillable())) {
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
				cSide.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));
			}

			if (getServiceTypReflective(billable).equals("Z") || getServiceTypReflective(billable).equals("R")
					|| getServiceTypReflective(billable).equals("B")) {
				new Label(ret, SWT.NONE).setText("Bezug");

				cBezug = new ComboViewer(ret, SWT.BORDER);
				cBezug.setContentProvider(ArrayContentProvider.getInstance());
				cBezug.setLabelProvider(new LabelProvider());
				List<BezugComboItem> input = new ArrayList<>();
				input.add(BezugComboItem.noBezug());
				for (IBilled kVerr : billed.getEncounter().getBilled()) {
					if (!kVerr.getCode().equals(billable.getCode())) {
						BezugComboItem item = BezugComboItem.of(kVerr);
						input.add(item);
					}
				}
				cBezug.setInput(input);
				String bezug = (String) billed.getExtInfo(Constants.FLD_EXT_REALTION);
				String id = (String) billed.getExtInfo(Constants.FLD_EXT_REALTION_ID);
				if (bezug != null) {
					if (id != null) {
						IBilled relatedBilled = CoreModelServiceHolder.get().load(id, IBilled.class).orElse(null);
						if (relatedBilled != null) {
							selectedBezug = BezugComboItem.of(relatedBilled);
						}
					} else {
						selectedBezug = input.stream().filter(b -> b.getCode().equals(bezug)).findFirst().orElse(null);
					}
				} else {
					selectedBezug = BezugComboItem.noBezug();

				}
				cBezug.setSelection(new StructuredSelection(selectedBezug), true);
				cBezug.addSelectionChangedListener(new ISelectionChangedListener() {
					@Override
					public void selectionChanged(SelectionChangedEvent event) {
						StructuredSelection selection = (StructuredSelection) cBezug.getSelection();
						if (selection != null && !selection.isEmpty()) {
							selectedBezug = (BezugComboItem) selection.getFirstElement();
							if (requiresSide(billed.getBillable())) {
								IBilled relatedBilled = CoreModelServiceHolder.get()
										.load(selectedBezug.getId(), IBilled.class).orElse(null);
								if (relatedBilled != null) {
									String relatedSide = (String) relatedBilled.getExtInfo(Constants.FLD_EXT_SIDE);
									if (relatedSide == null) {
										cSide.select(0);
									} else if (relatedSide.equalsIgnoreCase("l")) {
										cSide.select(1);
									} else {
										cSide.select(2);
									}
								}
							}
						}
					}
				});
			}

			Label lFranchiseFree = new Label(ret, SWT.NONE);
			lFranchiseFree.setText("Leistung franchise befreit");
			lFranchiseFree.setLayoutData(SWTHelper.getFillGridData(3, true, 1, false));
			bFranchiseFree = new Button(ret, SWT.CHECK);
			bFranchiseFree
					.setSelection(StringUtils.isNotBlank((String) billed.getExtInfo(Constants.FLD_EXT_FRANCHISEFREE)));
			bFranchiseFree.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
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

	private boolean requiresSide(IBillable billable) {
		if (billable != null) {
			try {
				Method getterMethod = billable.getClass().getMethod("requiresSide", (Class[]) null);
				Object typ = getterMethod.invoke(billable, (Object[]) null);
				if (typ instanceof Boolean) {
					return (Boolean) typ;
				}
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				LoggerFactory.getLogger(getClass()).warn("Could not get service typ of [" + billable + "]",
						e.getMessage());
			}
		}

		return false;
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
		private String id;
		private String code;
		private String side;
		private boolean isNoBezug;

		public static BezugComboItem of(IBilled billed) {
			BezugComboItem ret = new BezugComboItem();
			ret.setId(billed.getId());
			ret.setCode(billed.getCode());
			ret.setSide((String) billed.getExtInfo(Constants.FLD_EXT_SIDE));
			return ret;
		}

		public String getId() {
			return id;
		}

		private void setId(String id) {
			this.id = id;
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

		private void setSide(String side) {
			this.side = StringUtils.upperCase(side);

		}

		public String getCode() {
			return this.code;
		}

		@Override
		public String toString() {
			return getCode() + (StringUtils.isNotBlank(side) ? StringUtils.SPACE + side : StringUtils.EMPTY);
		}

		@Override
		public int hashCode() {
			return Objects.hash(code, id, isNoBezug);
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
			return Objects.equals(code, other.code) && Objects.equals(id, other.id) && isNoBezug == other.isNoBezug;
		}
	}

	@Override
	public void create() {
		super.create();
		getShell().setText(billed.getBillable().getCodeSystemName() + "-Details: " + billed.getCode());
	}

	@Override
	protected void okPressed() {
		if (isArzttarif(billable)) {
			int idx = cSide.getSelectionIndex();
			if (idx < 1) {
				billed.setExtInfo(Constants.FLD_EXT_SIDE, null);
			} else if (idx == 1) {
				billed.setExtInfo(Constants.FLD_EXT_SIDE, Constants.SIDE_L);
			} else {
				billed.setExtInfo(Constants.FLD_EXT_SIDE, Constants.SIDE_R);
			}
			if (selectedBezug.isNoBezug) {
				billed.setExtInfo(Constants.FLD_EXT_REALTION, StringUtils.EMPTY);
			} else {
				billed.setExtInfo(Constants.FLD_EXT_REALTION, selectedBezug.getCode());
				billed.setExtInfo(Constants.FLD_EXT_REALTION_ID, selectedBezug.getId());
			}

			if (bFranchiseFree.getSelection()) {
				billed.setExtInfo(Constants.FLD_EXT_FRANCHISEFREE, Boolean.TRUE.toString());
			} else {
				billed.setExtInfo(Constants.FLD_EXT_FRANCHISEFREE, null);
			}
			CoreModelServiceHolder.get().save(billed);
		}
		super.okPressed();
	}

	public static boolean isPauschale(IBillable billable) {
		return billable instanceof IAmbulatoryAllowance;
	}

	public static boolean isArzttarif(IBillable billable) {
		return billable instanceof ITardocLeistung || billable instanceof ITarmedLeistung;
	}
}
