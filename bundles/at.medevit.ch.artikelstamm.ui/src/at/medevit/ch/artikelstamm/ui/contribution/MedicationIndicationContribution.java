package at.medevit.ch.artikelstamm.ui.contribution;

import java.util.Collections;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.ui.internal.IndicationCodeSelectionDialog;
import at.medevit.ch.artikelstamm.ui.internal.IndicationCodeUtil;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.prescription.Constants;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.views.contribution.IViewContribution;

public class MedicationIndicationContribution implements IViewContribution {

	private IPrescription detailObject;

	private Link indicationLink;

	private Composite contributionComposite;

	@Override
	public void setUnlocked(boolean unlocked) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getLocalizedTitle() {
		return "Indikationscode";
	}

	@Override
	public Composite initComposite(Composite parent) {
		contributionComposite = new Composite(parent, SWT.NONE);
		contributionComposite.setLayout(new GridLayout());

		indicationLink = new Link(contributionComposite, 0);
		indicationLink.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		// Event handling when users click on links.
		indicationLink.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (detailObject != null && detailObject.getArticle() instanceof IArtikelstammItem) {
					IArtikelstammItem item = (IArtikelstammItem) detailObject.getArticle();
					if (item.isPm()) {
						Optional<String> indicationCodeHistory = IndicationCodeUtil.getLastIndicationCode(item,
								detailObject.getPatient(), Collections.emptyList());

						Display.getDefault().syncExec(() -> {
							IndicationCodeSelectionDialog dialog = new IndicationCodeSelectionDialog(item,
									Display.getDefault().getActiveShell());

							indicationCodeHistory.ifPresent(code -> {
								dialog.setSelectedCode(code);
							});
							if (dialog.open() == Window.OK) {
								if (dialog.getSelectedCode() instanceof String selectedCode) {
									detailObject.setExtInfo(Constants.FLD_EXT_INDICATIONCODE, selectedCode);
									CoreModelServiceHolder.get().save(detailObject);
									// refresh
									setDetailObject(detailObject, null);
								}
							}
						});
					}
				}
			}
		});
		return contributionComposite;
	}

	@Override
	public void setDetailObject(Object detailObject, Object additionalData) {
		if (detailObject instanceof IPrescription) {
			this.detailObject = (IPrescription) detailObject;
			if (detailObject != null && this.detailObject.getArticle() instanceof IArtikelstammItem) {
				IArtikelstammItem item = (IArtikelstammItem) this.detailObject.getArticle();
				if (item.isPm()) {
					show();
					contributionComposite.setEnabled(true);
					String indicationCode = (String) this.detailObject.getExtInfo(Constants.FLD_EXT_INDICATIONCODE);
					if (StringUtils.isNotBlank(indicationCode)) {
						indicationLink.setText("<a>" + getLocalizedTitle() + " " + indicationCode + "</a>");
						return;
					}
					indicationLink.setText("<a>" + getLocalizedTitle() + "</a>");
					return;
				}
			}
		} else {
			this.detailObject = null;
		}
		indicationLink.setText(StringUtils.EMPTY);
		contributionComposite.setEnabled(false);
		hide();
	}

	private void hide() {
		if (contributionComposite.getLayoutData() instanceof GridData) {
			contributionComposite.setVisible(false);
			GridData gd = (GridData) contributionComposite.getLayoutData();
			gd.exclude = true;
			contributionComposite.getParent().requestLayout();
		}
	}

	private void show() {
		if (contributionComposite.getLayoutData() instanceof GridData) {
			contributionComposite.setVisible(true);
			GridData gd = (GridData) contributionComposite.getLayoutData();
			gd.exclude = false;
			contributionComposite.getParent().requestLayout();
		}
	}
}
