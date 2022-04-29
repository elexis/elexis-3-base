package at.medevit.elexis.ehc.ui.docbox.wizard;

import org.apache.commons.lang3.StringUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.openhealthtools.mdht.uml.cda.util.CDAUtil;

import at.medevit.elexis.ehc.docbox.service.DocboxService;
import at.medevit.elexis.ehc.ui.preference.PreferencePage;
import ch.elexis.core.services.holder.ConfigServiceHolder;

public class ExportPrescriptionWizardPage2 extends WizardPage {
	private Text xmlText;

	protected ExportPrescriptionWizardPage2(String pageName) {
		super(pageName);
		setTitle(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		xmlText = new Text(composite, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		xmlText.setLayoutData(new GridData(GridData.FILL_BOTH));

		setControl(composite);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			try {
				CDAUtil.save(ExportPrescriptionWizard.getDocument().getDocRoot().getClinicalDocument(), output);
			} catch (Exception e) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
						"Das Rezept konnte nicht erstellt werden. " + e.getMessage());
				return;
			}
			xmlText.setText(output.toString());
		}
	}

	@Override
	public boolean isPageComplete() {
		return !xmlText.getText().isEmpty();
	}

	private void writePdf(ByteArrayOutputStream pdf) throws FileNotFoundException, IOException {
		String outputDir = ConfigServiceHolder.getUser(PreferencePage.EHC_OUTPUTDIR,
				PreferencePage.getDefaultOutputDir());
		File pdfFile = new File(outputDir + File.separator + getRezeptFileName() + ".pdf");
		try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
			fos.write(pdf.toByteArray());
			fos.flush();
		}
	}

	public boolean finish() {
		try {
			String outputDir = ConfigServiceHolder.getUser(PreferencePage.EHC_OUTPUTDIR,
					PreferencePage.getDefaultOutputDir());
			ExportPrescriptionWizard.getDocument()
					.saveToFile(outputDir + File.separator + getRezeptFileName() + ".xml");
			ByteArrayOutputStream pdf = DocboxService.getPrescriptionPdf(ExportPrescriptionWizard.getDocument());
			writePdf(pdf);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public String getRezeptFileName() {
		String ret = ExportPrescriptionWizard.getRezept().getLabel();
		return ret.replaceAll(StringUtils.SPACE, "_");
	}
}
