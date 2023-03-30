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

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ch.elexis.base.ch.arzttarife.complementary.IComplementaryLeistung;
import ch.elexis.base.ch.arzttarife.service.ArzttarifeModelServiceHolder;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.views.IDetailDisplay;

public class ComplementaryDetailDisplay implements IDetailDisplay {
	private ScrolledForm form;
	private FormToolkit toolkit = UiDesk.getToolkit();
	private Section infoSection;

	// selected
	private IComplementaryLeistung complementary;

	private Text codeChapter;
	private Text codeCode;
	private Text codeText;
	private Text codeDescription;
	private Text codeFixedValue;

	private ComplementarySubDetail subDetail;

	@Inject
	public void selection(
			@Optional @Named("ch.elexis.views.codeselector.complementary.selection") IComplementaryLeistung complementary) {
		if (complementary != null && !form.isDisposed()) {
			display(complementary);
		}
	}

	public Composite createDisplay(Composite parent, IViewSite site) {
		form = toolkit.createScrolledForm(parent);
		TableWrapLayout twl = new TableWrapLayout();
		form.getBody().setLayout(twl);
		form.setText("Keine Leistung ausgewählt.");

		// General Information
		infoSection = toolkit.createSection(form.getBody(),
				Section.COMPACT | Section.EXPANDED | Section.TWISTIE | Section.TITLE_BAR);
		TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
		twd.grabHorizontal = true;
		infoSection.setLayoutData(twd);
		infoSection.addExpansionListener(new SectionExpansionHandler());
		infoSection.setText("Details");

		Composite info = toolkit.createComposite(infoSection);
		twl = new TableWrapLayout();
		info.setLayout(twl);

		Label lbl = toolkit.createLabel(info, "Kapitel");
		// get a bold version of the standard font
		FontData[] bfd = lbl.getFont().getFontData();
		bfd[0].setStyle(SWT.BOLD);
		Font boldFont = new Font(Display.getCurrent(), bfd[0]);
		lbl.setFont(boldFont);

		codeChapter = toolkit.createText(info, StringUtils.EMPTY);
		codeChapter.setEditable(false);
		codeChapter.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		lbl = toolkit.createLabel(info, "Code");
		lbl.setFont(boldFont);

		codeCode = toolkit.createText(info, StringUtils.EMPTY);
		codeCode.setEditable(false);
		twd = new TableWrapData(TableWrapData.FILL_GRAB);
		codeCode.setLayoutData(twd);

		lbl = toolkit.createLabel(info, "Text");
		lbl.setFont(boldFont);

		codeText = toolkit.createText(info, StringUtils.EMPTY);
		codeText.setEditable(false);
		twd = new TableWrapData(TableWrapData.FILL_GRAB);
		codeText.setLayoutData(twd);

		lbl = toolkit.createLabel(info, "Beschreibung");
		lbl.setFont(boldFont);

		codeDescription = toolkit.createText(info, StringUtils.EMPTY, SWT.MULTI);
		codeDescription.setEditable(false);
		twd = new TableWrapData(TableWrapData.FILL_GRAB);
		codeDescription.setLayoutData(twd);

		lbl = toolkit.createLabel(info, "Pauschal Preis");
		lbl.setFont(boldFont);

		codeFixedValue = toolkit.createText(info, StringUtils.EMPTY, SWT.MULTI);
		twd = new TableWrapData(TableWrapData.FILL_GRAB);
		codeFixedValue.setLayoutData(twd);
		codeFixedValue.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				String text = codeFixedValue.getText();
				if (complementary != null) {
					if (text.isEmpty()) {
						complementary.setFixedValue(-1);
						return;
					}
					try {
						int value = (int) (Float.parseFloat(text) * 100);
						complementary.setFixedValue(value);
					} catch (NumberFormatException ex) {
						// ignore and keep last valid value
					}
				}
			}
		});
		ControlDecoration deco = new ControlDecoration(codeFixedValue, SWT.LEFT | SWT.TOP);
		deco.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION)
				.getImage());
		deco.setDescriptionText(
				"Hier kann ein pauschal Preis angegeben werden. Dieser wird dann anstelle des Stundensatz als Preis verwendet.");

		subDetail = new ComplementarySubDetail(info, SWT.NONE);
		twd = new TableWrapData(TableWrapData.FILL_GRAB);
		subDetail.setLayoutData(twd);
		subDetail.hide();

		infoSection.setClient(info);

		return form.getBody();
	}

	public Class<?> getElementClass() {
		return IComplementaryLeistung.class;
	}

	public void display(Object obj) {
		if (obj instanceof IComplementaryLeistung) {
			complementary = (IComplementaryLeistung) obj;
			if (isSub(complementary)) {
				complementary = getParent(complementary);
			}
			if (complementary.getCode().equals("1302")) {
				subDetail.show(complementary);
			} else {
				subDetail.hide();
			}

			form.setText(complementary.getLabel());

			codeChapter.setText(complementary.getChapter());
			codeCode.setText(complementary.getCode());
			codeText.setText(complementary.getText());
			codeDescription.setText(complementary.getDescription());
			if (complementary.isFixedValueSet()) {
				codeFixedValue.setText(Double.toString(complementary.getFixedValue() / 100.0));
			} else {
				codeFixedValue.setText(StringUtils.EMPTY);
			}
		} else {
			complementary = null;
			form.setText("Keine Leistung ausgewählt.");

			codeChapter.setText(StringUtils.EMPTY);
			codeCode.setText(StringUtils.EMPTY);
			codeText.setText(StringUtils.EMPTY);
			codeDescription.setText(StringUtils.EMPTY);
			codeFixedValue.setText(StringUtils.EMPTY);
			subDetail.hide();
		}
		infoSection.layout();
		form.reflow(true);
	}

	private IComplementaryLeistung getParent(IComplementaryLeistung complementary) {
		String start = complementary.getId().substring(0, complementary.getId().indexOf("sub"));
		String end = complementary.getId().substring(complementary.getId().indexOf("-"));
		return ArzttarifeModelServiceHolder.get().load(start + end, IComplementaryLeistung.class).orElse(null);
	}

	private boolean isSub(IComplementaryLeistung complementary) {
		return complementary.getId().contains("sub");
	}

	public String getTitle() {
		return "Komplementärmedizin";
	}

	private final class SectionExpansionHandler extends ExpansionAdapter {
		@Override
		public void expansionStateChanged(ExpansionEvent e) {
			form.reflow(true);
		}
	}
}
