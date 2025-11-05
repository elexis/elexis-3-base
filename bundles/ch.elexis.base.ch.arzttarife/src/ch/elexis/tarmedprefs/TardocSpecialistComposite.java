package ch.elexis.tarmedprefs;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ch.elexis.base.ch.arzttarife.util.ArzttarifeUtil;
import ch.elexis.core.findings.ICoding;
import ch.elexis.core.findings.codes.ICodingService;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.e4.dialog.GenericSelectionDialog;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import jakarta.inject.Inject;

public class TardocSpecialistComposite extends Composite {

	private IMandator mandator;

	private Label label;
	private Button openSelection;

	@Inject
	private ICodingService codingService;
	
	public TardocSpecialistComposite(Composite parent, int style) {
		super(parent, style);
		CoreUiUtil.injectServices(this);

		setLayout(new GridLayout(2, false));

		label = new Label(this, SWT.NONE);
		label.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		openSelection = new Button(this, SWT.PUSH);
		openSelection.setText("...");
		openSelection.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<ICoding> codes = codingService.getAvailableCodes("tardoc_dignitaet");
			
				GenericSelectionDialog gsd = new GenericSelectionDialog(getShell(), codes,
						"TARDOC Dignitäten w\u00E4hlen", "");
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
		});
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
		label.setText(
				specialist.stream().map(c -> c.getCode() + " - " + c.getDisplay()).collect(Collectors.joining(",")));
		layout();
	}
}
