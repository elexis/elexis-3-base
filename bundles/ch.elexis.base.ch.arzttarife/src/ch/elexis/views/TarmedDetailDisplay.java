/*******************************************************************************
 * Copyright (c) 2005-2008, G. Weirich and Elexis
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

import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ch.elexis.arzttarife_schweiz.Messages;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedKumulation;
import ch.elexis.base.ch.arzttarife.tarmed.ITarmedLeistung;
import ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationArt;
import ch.elexis.base.ch.arzttarife.tarmed.TarmedKumulationTyp;
import ch.elexis.base.ch.arzttarife.util.TarmedDefinitionenUtil;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.LabeledInputField;
import ch.elexis.core.ui.views.IDetailDisplay;
import ch.rgw.tools.TimeTool;

public class TarmedDetailDisplay implements IDetailDisplay {

	public static final TimeTool INFINITE = new TimeTool("19991231");

	private FormToolkit tk;
	private ScrolledForm form;
	private String[] fields = { Messages.TarmedDetailDisplay_DigniQuant, Messages.TarmedDetailDisplay_DigniQual,
			Messages.TarmedDetailDisplay_Sparte, Messages.TarmedDetailDisplay_RiskClass,
			Messages.TarmedDetailDisplay_TPDoc, Messages.TarmedDetailDisplay_TPTec, Messages.TarmedDetailDisplay_TPAss,
			Messages.TarmedDetailDisplay_NumbereAss, Messages.TarmedDetailDisplay_TimeAct,
			Messages.TarmedDetailDisplay_TimeBeforeAfter, Messages.TarmedDetailDisplay_TimeWrite,
			Messages.TarmedDetailDisplay_TimeChange, Messages.TarmedDetailDisplay_TimeRoom,
			Messages.TarmedDetailDisplay_Relation, Messages.TarmedDetailDisplay_NameInternal };
	private String[] retrieve = { "DigniQuanti", "DigniQuali", "Sparte", "Anaesthesie", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			"TP_AL", "TP_TL", "TP_ASSI", "ANZ_ASSI", "LSTGIMES_MIN", "VBNB_MIN", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			"BEFUND_MIN", "WECHSEL_MIN", "RAUM_MIN", "Bezug", "Nickname" //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$
	};

	private Text[] inputs = new Text[fields.length];
	private FormText medinter, techinter, exclusion, inclusion, limits, hirarchy;
	private FormText validity;
	private ITarmedLeistung actCode;

	public TarmedDetailDisplay() {

	}

	@Inject
	public void selection(@Optional @Named("ch.elexis.views.codeselector.tarmed.selection") ITarmedLeistung tarmed) {
		if (tarmed != null && !form.isDisposed()) {
			display(tarmed);
		}
	}

	public Composite createDisplay(Composite parent, IViewSite notUsed) {
		tk = UiDesk.getToolkit();
		form = tk.createScrolledForm(parent);
		TableWrapLayout twl = new TableWrapLayout();
		// twl.numColumns=4;
		// twl.makeColumnsEqualWidth=true;
		form.getBody().setLayout(twl /* new GridLayout(6,true) */);
		LabeledInputField.Tableau cFields = new LabeledInputField.Tableau(form.getBody());
		for (int i = 0; i < fields.length; i++) {
			inputs[i] = (Text) cFields.addComponent(fields[i]).getControl();
		}
		final int last = fields.length - 1;
		inputs[last].addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (actCode != null) {
					actCode.setNickname(inputs[last].getText());
				}
			}

		});
		TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
		twd.grabHorizontal = true;
		cFields.setLayoutData(twd);

		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_MedInter);
		medinter = tk.createFormText(form.getBody(), false);
		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_TecInter);
		techinter = tk.createFormText(form.getBody(), false);
		// Composite cLimits=tk.createComposite(form.getBody());
		// cLimits.setLayout(new ColumnLayout());
		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_DontCombine);
		exclusion = tk.createFormText(form.getBody(), false);
		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_DoCombine);
		inclusion = tk.createFormText(form.getBody(), false);
		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_PossibleAdd);
		hirarchy = tk.createFormText(form.getBody(), false);
		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_Limits);
		limits = tk.createFormText(form.getBody(), false);
		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_Validity);
		validity = tk.createFormText(form.getBody(), false);
		return form.getBody();
	}

	public Composite createDisplayFromDeteils(Composite parent, IViewSite notUsed) {
		tk = UiDesk.getToolkit();
		form = tk.createScrolledForm(parent);
		TableWrapLayout twl = new TableWrapLayout();
		form.getBody().setLayout(twl /* new GridLayout(6,true) */);
		TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
		twd.grabHorizontal = true;
		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_MedInter);
		medinter = tk.createFormText(form.getBody(), false);
		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_TecInter);
		techinter = tk.createFormText(form.getBody(), false);
		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_DontCombine);
		exclusion = tk.createFormText(form.getBody(), false);
		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_DoCombine);
		inclusion = tk.createFormText(form.getBody(), false);
		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_PossibleAdd);
		hirarchy = tk.createFormText(form.getBody(), false);
		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_Limits);
		limits = tk.createFormText(form.getBody(), false);
		tk.createLabel(form.getBody(), Messages.TarmedDetailDisplay_Validity);
		validity = tk.createFormText(form.getBody(), false);
		return form.getBody();
	}

	public Class getElementClass() {
		return ITarmedLeistung.class;
	}

	public void display(Object obj) {
		if (obj instanceof ITarmedLeistung) {
			actCode = (ITarmedLeistung) obj;
			form.setText(actCode.getLabel());
			if (inputs[0] != null) {
				inputs[0].setText(actCode.getDigniQuanti());
				inputs[1].setText(actCode.getDigniQuali());
				inputs[2].setText(actCode.getSparte());
				inputs[3].setText(TarmedDefinitionenUtil
						.getTextForRisikoKlasse((String) actCode.getExtension().getExtInfo("ANAESTHESIE"))); //$NON-NLS-1$
				for (int i = 4; i < fields.length - 1; i++) {
					String val = (String) actCode.getExtension().getExtInfo(retrieve[i]);
					if (val == null) {
						val = StringUtils.EMPTY;
					}
					inputs[i].setText(val);
				}
				inputs[fields.length - 1].setText(actCode.getNickname()); // $NON-NLS-1$
			}
			medinter.setText(actCode.getExtension().getMedInterpretation(), false, false);
			techinter.setText(actCode.getExtension().getTechInterpretation(), false, false);
			List<ITarmedKumulation> kumulations = actCode.getKumulations(TarmedKumulationArt.SERVICE);
			exclusion.setText(getKumulationsString(kumulations, actCode.getCode(), TarmedKumulationTyp.EXCLUSION),
					false, false);
			inclusion.setText(getKumulationsString(kumulations, actCode.getCode(), TarmedKumulationTyp.INCLUSION),
					false, false); // $NON-NLS-1$
			List<String> hirarchyCodes = actCode.getHierarchy(actCode.getValidFrom());
			hirarchy.setText(String.join(", ", hirarchyCodes), false, false);

			String limit = (String) actCode.getExtension().getExtInfo("limits"); //$NON-NLS-1$
			if (limit != null) {
				StringBuilder sb = new StringBuilder();
				sb.append("<form>"); //$NON-NLS-1$
				String[] ll = limit.split("#"); //$NON-NLS-1$
				for (String line : ll) {
					String[] f = line.split(","); //$NON-NLS-1$
					if (f.length == 6) {
						sb.append("<li>"); //$NON-NLS-1$
						if (f[0].equals("<=")) { //$NON-NLS-1$
							sb.append(Messages.TarmedDetailDisplay_max).append(StringUtils.SPACE);
						} else {
							sb.append(f[0]).append(StringUtils.SPACE);
						}
						sb.append(f[1]).append(Messages.TarmedDetailDisplay_times);
						if (f[3].equals("P")) { //$NON-NLS-1$
							sb.append(Messages.TarmedDetailDisplay_per);
						} else {
							sb.append(Messages.TarmedDetailDisplay_after);
						}
						sb.append(f[2]).append(StringUtils.SPACE);
						sb.append(TarmedDefinitionenUtil.getTextForZR_Einheit(f[4]));
						sb.append("</li>"); //$NON-NLS-1$
					}
				}
				sb.append("</form>"); //$NON-NLS-1$
				limits.setText(sb.toString(), true, false);
			} else {
				limits.setText(StringUtils.EMPTY, false, false);
			}

			// validity
			String text;
			TimeTool tGueltigVon = new TimeTool(actCode.getValidFrom());
			TimeTool tGueltigBis = new TimeTool(actCode.getValidTo());
			if (tGueltigVon != null && tGueltigBis != null) {
				String from = tGueltigVon.toString(TimeTool.DATE_GER);
				String to;
				if (tGueltigBis.isSameDay(INFINITE)) {
					to = StringUtils.EMPTY;
				} else {
					to = tGueltigBis.toString(TimeTool.DATE_GER);
				}
				text = from + "-" + to; //$NON-NLS-1$
			} else {
				text = StringUtils.EMPTY;
			}
			validity.setText(text, false, false);

			form.reflow(true);
		}

	}

	private String getKumulationsString(List<ITarmedKumulation> list, String code, TarmedKumulationTyp typ) {
		StringBuilder sb = new StringBuilder();
		if (list != null) {
			List<ITarmedKumulation> slaveServices = list.stream()
					.filter(k -> k.getTyp() == typ && k.getSlaveArt() == TarmedKumulationArt.SERVICE
							&& k.getSlaveCode().equals(code) && k.getMasterArt() == TarmedKumulationArt.SERVICE)
					.collect(Collectors.toList());
			List<ITarmedKumulation> masterServices = list.stream()
					.filter(k -> k.getTyp() == typ && k.getMasterArt() == TarmedKumulationArt.SERVICE
							&& k.getMasterCode().equals(code) && k.getSlaveArt() == TarmedKumulationArt.SERVICE)
					.collect(Collectors.toList());

			if (!slaveServices.isEmpty() || !masterServices.isEmpty()) {
				sb.append("Leistungen: ");
				StringJoiner sj = new StringJoiner(", ");
				for (ITarmedKumulation tarmedKumulation : slaveServices) {
					// dont add same exclusion multiple times
					if (!sj.toString().contains(tarmedKumulation.getMasterCode())) {
						sj.add(tarmedKumulation.getMasterCode());
					}
				}
				for (ITarmedKumulation tarmedKumulation : masterServices) {
					// dont add same exclusion multiple times
					if (!sj.toString().contains(tarmedKumulation.getSlaveCode())) {
						sj.add(tarmedKumulation.getSlaveCode());
					}
				}
				sb.append(sj.toString());
			}
			List<ITarmedKumulation> slaveGroups = list.stream()
					.filter(k -> k.getTyp() == typ && k.getSlaveArt() == TarmedKumulationArt.GROUP)
					.collect(Collectors.toList());
			List<ITarmedKumulation> masterGroups = list.stream()
					.filter(k -> k.getTyp() == typ && k.getMasterArt() == TarmedKumulationArt.GROUP)
					.collect(Collectors.toList());

			if (!slaveGroups.isEmpty() || !masterGroups.isEmpty()) {
				sb.append(StringUtils.SPACE);
				sb.append("Gruppen: ");
				StringJoiner sj = new StringJoiner(", ");
				for (ITarmedKumulation tarmedKumulation : slaveGroups) {
					// dont add same exclusion multiple times
					if (!sj.toString().contains(tarmedKumulation.getSlaveCode())) {
						sj.add(tarmedKumulation.getSlaveCode());
					}
				}
				for (ITarmedKumulation tarmedKumulation : masterGroups) {
					// dont add same exclusion multiple times
					if (!sj.toString().contains(tarmedKumulation.getMasterCode())) {
						sj.add(tarmedKumulation.getMasterCode());
					}
				}
				sb.append(sj.toString());
			}
		}
		return sb.toString();
	}

	public String getTitle() {
		return "Tarmed"; //$NON-NLS-1$
	}

}
