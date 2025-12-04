package ch.elexis.tarmedprefs;

import java.util.Comparator;
import java.util.List;

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

public class TardocSpecialistComposite extends Composite {

	private IMandator mandator;

	private Label leftLabel;
	private Label rightLabel;

	@Inject
	private ICodingService codingService;
	
	public TardocSpecialistComposite(Composite parent, int style) {
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

		GridData gdLeft = new GridData(SWT.BEGINNING, SWT.TOP, false, false);
		leftLabel = new Label(listComposite, SWT.WRAP);
		leftLabel.setLayoutData(gdLeft);

		GridData gdRight = new GridData(SWT.FILL, SWT.TOP, true, false);
		rightLabel = new Label(listComposite, SWT.WRAP);
		rightLabel.setLayoutData(gdRight);
	}

	@SuppressWarnings("unchecked")
	public void openSelectionDialog() {
		if (mandator == null || codingService == null) {
			return;
		}
		List<ICoding> codes = codingService.getAvailableCodes("tardoc_dignitaet");
		GenericSelectionDialog gsd = new GenericSelectionDialog(getShell(), codes,
				Messages.TardocSpecialistComposite_selectDignitiesTitle, StringUtils.EMPTY);
		gsd.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ICoding) {
					return ((ICoding) element).getCode() + " - " + ((ICoding) element).getDisplay();
				}
				return super.getText(element);
			}
		});
		gsd.setSelection((List<Object>) (List<?>) ArzttarifeUtil.getMandantTardocSepcialist(mandator));
		if (gsd.open() == Dialog.OK) {
			IStructuredSelection selection = gsd.getSelection();
			ArzttarifeUtil.setMandantTardocSepcialist(mandator, selection.toList());
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
		List<ICoding> specialist = ArzttarifeUtil.getMandantTardocSepcialist(mandator);
		specialist.sort(Comparator.comparingInt(c -> {
			try {
				return Integer.parseInt(c.getCode().replaceAll("\\D", StringUtils.EMPTY));
			} catch (NumberFormatException e) {

				return Integer.MAX_VALUE;
			}
		}));
		StringBuilder left = new StringBuilder();
		StringBuilder right = new StringBuilder();
		int size = specialist.size();
		int mid = (size + 1) / 2;
		for (int i = 0; i < size; i++) {
			ICoding c = specialist.get(i);
			StringBuilder target = (i < mid) ? left : right;
			if (target.length() > 0) {
				target.append(System.lineSeparator());
			}
			target.append("- ").append(c.getCode()).append(" - ").append(c.getDisplay());
		}
		leftLabel.setText(left.toString());
		rightLabel.setText(right.toString());
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