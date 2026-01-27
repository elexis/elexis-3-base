package ch.elexis.tarmedprefs;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import ch.elexis.base.ch.arzttarife.util.ArzttarifeUtil;
import ch.elexis.core.findings.ICoding;
import ch.elexis.core.findings.codes.ICodingService;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.e4.dialog.GenericSelectionDialog;
import ch.elexis.core.ui.e4.util.CoreUiUtil;

public class SectionCodeComposite extends Composite {

	private IMandator mandator;

	private Label label;

	@Inject
	private ICodingService codingService;
	
	public SectionCodeComposite(Composite parent, int style) {
		super(parent, style);
		CoreUiUtil.injectServices(this);

		setLayout(new GridLayout(1, false));

		Composite listComposite = new Composite(this, SWT.NONE);
		GridData gdList = new GridData(SWT.FILL, SWT.TOP, true, false);
		listComposite.setLayoutData(gdList);

		GridLayout listLayout = new GridLayout(2, false);
		listLayout.marginWidth = 0;
		listLayout.horizontalSpacing = 10;
		listComposite.setLayout(listLayout);

		GridData gdLeft = new GridData(SWT.FILL, SWT.TOP, false, false);
		label = new Label(listComposite, SWT.WRAP);
		label.setLayoutData(gdLeft);
	}

	public void openSelectionDialog() {
		if (mandator == null || codingService == null) {
			return;
		}
		List<ICoding> codes = codingService.getAvailableCodes("forumdatenaustausch_sectioncode");
		GenericSelectionDialog gsd = new GenericSelectionDialog(getShell(), codes,
				"Fachbereich auswählen", StringUtils.EMPTY);
		gsd.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ICoding) {
					return ((ICoding) element).getCode() + " - " + ((ICoding) element).getDisplay();
				}
				return super.getText(element);
			}
		});
		ArzttarifeUtil.getMandantSectionCode(mandator).ifPresent(c -> {
			gsd.setSelection(Collections.singletonList(c));
		});
		if (gsd.open() == Dialog.OK) {
			IStructuredSelection selection = gsd.getSelection();
			ArzttarifeUtil.setMandantSectionCode(mandator, (ICoding) selection.getFirstElement());
			CoreModelServiceHolder.get().save(mandator);
			updateUi();
		}
	}

	@Override
	public void dispose() {
		CoreUiUtil.uninjectServices(this);
		super.dispose();
	}

	public void setMandator(IMandator mandator) {
		this.mandator = mandator;
		updateUi();
	}

	private void updateUi() {
		Optional<ICoding> sectionCode = ArzttarifeUtil.getMandantSectionCode(mandator);
		if (sectionCode.isPresent()) {
			label.setText(sectionCode.get().getCode() + " - " + sectionCode.get().getDisplay());
		} else {
			label.setText(StringUtils.EMPTY);
		}
		layout(true, true);
		if (!isDisposed()) {
			getDisplay().asyncExec(() -> {
				if (!isDisposed()) {
					relayoutScrolledParent();
				}
			});
		}
	}

	private void relayoutScrolledParent() {
	    Composite p = this;
	    while (p != null && !p.isDisposed()) {
	        if (p instanceof ScrolledComposite) {
	            ScrolledComposite sc = (ScrolledComposite) p;
				Control ctrl = sc.getContent();
				if (ctrl != null && !ctrl.isDisposed()) {
					if (ctrl instanceof Composite) {
						Composite content = (Composite) ctrl;
						content.layout(true, true);
						sc.setMinSize(content.computeSize(SWT.DEFAULT, SWT.DEFAULT));
					}
	            }
	            break;
	        }
	        p = p.getParent();
	    }
	}
}