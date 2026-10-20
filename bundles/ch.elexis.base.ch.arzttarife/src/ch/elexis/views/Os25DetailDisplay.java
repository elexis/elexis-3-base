package ch.elexis.views;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ch.elexis.base.ch.arzttarife.os25.IOs25Leistung;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.LabeledInputField;
import ch.elexis.core.ui.util.LabeledInputField.InputData;
import ch.elexis.core.ui.views.IDetailDisplay;
import jakarta.inject.Inject;
import jakarta.inject.Named;

public class Os25DetailDisplay implements IDetailDisplay {

	ScrolledForm form;
	FormToolkit tk = UiDesk.getToolkit();
	LabeledInputField.AutoForm tblLab;
	private FormText details;

	InputData[] data = new InputData[] { new InputData("Position", "code", InputData.Typ.STRING, null),
			new InputData("Taxpunkte", "TP", InputData.Typ.STRING, null),
			new InputData("Stufe", "stufe", InputData.Typ.STRING, null),
			new InputData("Honorarempfaenger", "honorarEmpfaenger", InputData.Typ.STRING, null),
			new InputData("Fachgebiet/Kapitel", "chapter", InputData.Typ.STRING, null),
			new InputData("Unterkapitel", "subChapter", InputData.Typ.STRING, null),
			new InputData("Leistungstyp", "leistungsTyp", InputData.Typ.STRING, null),
			new InputData("Gueltig von", "validFrom", InputData.Typ.STRING, null),
			new InputData("Gueltig bis", "validTo", InputData.Typ.STRING, null) };

	@Inject
	public void selection(@Optional @Named(Os25CodeSelectorFactory.SELECTION_NAME) IOs25Leistung os25) {
		if (os25 != null && form != null && !form.isDisposed()) {
			display(os25);
		}
	}

	@Override
	public Composite createDisplay(Composite parent, IViewSite site) {
		form = tk.createScrolledForm(parent);
		TableWrapLayout twl = new TableWrapLayout();
		form.getBody().setLayout(twl);

		tblLab = new LabeledInputField.AutoForm(form.getBody(), data);
		TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
		twd.grabHorizontal = true;
		tblLab.setLayoutData(twd);

		tk.createLabel(form.getBody(), "Details");
		details = tk.createFormText(form.getBody(), false);

		// user can not edit tarif
		Arrays.asList(data).forEach(id -> id.setEditable(false));
		return form.getBody();
	}

	@Override
	public void display(Object obj) {
		IOs25Leistung ps25 = (IOs25Leistung) obj;
		form.setText(ps25.getLabel());
		tblLab.reload(ps25);
		details.setText(toFormText(ps25), true, false);
		form.reflow(true);
	}

	private String toFormText(IOs25Leistung ps25) {
		StringBuilder ret = new StringBuilder("<form>");
		append(ret, "Leistung", ps25.getLeistung());
		append(ret, "Spezifikation", ps25.getSpezifikation());
		append(ret, "Anwendungsregeln", ps25.getAnwendungsRegeln());
		append(ret, "Moegliche Kombination", ps25.getMoeglicheKombination());
		ret.append("</form>");
		return ret.toString();
	}

	private void append(StringBuilder ret, String label, String value) {
		if (StringUtils.isNotBlank(value)) {
			ret.append("<p><b>").append(escape(label)).append("</b><br/>")
					.append(escape(value).replace(StringUtils.LF, "<br/>")).append("</p>");
		}
	}

	private String escape(String value) {
		return StringUtils.defaultString(value).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

	@Override
	public Class<?> getElementClass() {
		return IOs25Leistung.class;
	}

	@Override
	public String getTitle() {
		return "OS25";
	}
}
