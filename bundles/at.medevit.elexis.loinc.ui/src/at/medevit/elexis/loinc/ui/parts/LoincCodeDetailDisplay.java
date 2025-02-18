package at.medevit.elexis.loinc.ui.parts;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
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

import at.medevit.elexis.loinc.model.LoincCode;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.views.IDetailDisplay;
import jakarta.inject.Inject;
import jakarta.inject.Named;

public class LoincCodeDetailDisplay implements IDetailDisplay {

	private FormToolkit toolkit;
	private ScrolledForm form;

	protected LoincCode actCode;

	private Section infoSection;
	private Text codeCode;
	private Text codeShortDesc;
	private Text codeUnit;
	private Text codeText;
	private Text codeClazz;

	public LoincCodeDetailDisplay() {
	}

	public Class getElementClass() {
		return LoincCode.class;
	}

	public String getTitle() {
		return "LOINC"; //$NON-NLS-1$
	}

	@Override
	public Composite createDisplay(Composite parent, IViewSite site) {
		toolkit = UiDesk.getToolkit();
		form = toolkit.createScrolledForm(parent);
		TableWrapLayout twl = new TableWrapLayout();
		form.getBody().setLayout(twl);
		form.setText("Kein LOINC Code ausgewählt.");

		form.getToolBarManager().add(new RemoveAction()); // NEW LINE
		form.getToolBarManager().update(true);

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

		Label lbl = toolkit.createLabel(info, "Code");
		// get a bold version of the standard font
		FontData[] bfd = lbl.getFont().getFontData();
		bfd[0].setStyle(SWT.BOLD);
		Font boldFont = new Font(Display.getCurrent(), bfd[0]);
		lbl.setFont(boldFont);

		codeCode = toolkit.createText(info, StringUtils.EMPTY);
		codeCode.setEditable(false);
		codeCode.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		lbl = toolkit.createLabel(info, "Kurz Beschreibung");
		lbl.setFont(boldFont);

		codeShortDesc = toolkit.createText(info, StringUtils.EMPTY);
		codeShortDesc.setEditable(false);
		twd = new TableWrapData(TableWrapData.FILL_GRAB);
		codeShortDesc.setLayoutData(twd);

		lbl = toolkit.createLabel(info, "Einheit");
		lbl.setFont(boldFont);

		codeUnit = toolkit.createText(info, StringUtils.EMPTY);
		codeUnit.setEditable(false);
		twd = new TableWrapData(TableWrapData.FILL_GRAB);
		codeUnit.setLayoutData(twd);

		lbl = toolkit.createLabel(info, "Text");
		lbl.setFont(boldFont);

		codeText = toolkit.createText(info, StringUtils.EMPTY, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		codeText.setEditable(false);
		twd = new TableWrapData(TableWrapData.FILL_GRAB);
		twd.heightHint = 100;
		codeText.setLayoutData(twd);

		lbl = toolkit.createLabel(info, "Klassifikation");
		lbl.setFont(boldFont);

		codeClazz = toolkit.createText(info, StringUtils.EMPTY);
		codeClazz.setEditable(false);
		twd = new TableWrapData(TableWrapData.FILL_GRAB);
		codeClazz.setLayoutData(twd);

		infoSection.setClient(info);

		return form.getBody();
	}

	@Inject
	public void selection(@Optional @Named("at.medevit.elexis.loinc.ui.parts.selection") LoincCode code) {
		if (form != null && !form.isDisposed()) {
			display(code);
		}
	}

	@Override
	public void display(Object obj) {
		if (obj instanceof LoincCode) {
			actCode = (LoincCode) obj;
			form.setText(actCode.getLabel());

			codeCode.setText(actCode.getCode());
			codeShortDesc.setText(actCode.get(LoincCode.FLD_SHORTNAME));
			codeUnit.setText(actCode.get(LoincCode.FLD_UNIT));
			codeText.setText(actCode.getText());
			codeClazz.setText(actCode.get(LoincCode.FLD_CLASS));
		} else {
			actCode = null;
			form.setText("Kein LOINC Code ausgewählt.");

			codeCode.setText(StringUtils.EMPTY);
			codeShortDesc.setText(StringUtils.EMPTY);
			codeUnit.setText(StringUtils.EMPTY);
			codeText.setText(StringUtils.EMPTY);
			codeClazz.setText(StringUtils.EMPTY);
		}
		infoSection.layout();
		form.reflow(true);
	}

	private final class SectionExpansionHandler extends ExpansionAdapter {
		@Override
		public void expansionStateChanged(ExpansionEvent e) {
			form.reflow(true);
		}
	}

	protected class RemoveAction extends Action {
		@Override
		public ImageDescriptor getImageDescriptor() {
			return Images.IMG_DELETE.getImageDescriptor();
		}

		@Override
		public void run() {
			if (actCode != null) {
				actCode.delete();
				display(null);
				ElexisEventDispatcher.reload(LoincCode.class);
			}
		}
	}
}
