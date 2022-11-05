package at.medevit.elexis.loinc.ui.importer;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import at.medevit.elexis.loinc.model.LoincCode;
import at.medevit.elexis.loinc.ui.Activator;
import at.medevit.elexis.loinc.ui.LoincServiceComponent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.status.ElexisStatus;
import ch.elexis.core.ui.util.ImporterPage;
import ch.elexis.core.l10n.Messages;
import ch.elexis.core.ui.util.SWTHelper;

public class LoincCodeCsvImporter extends ImporterPage {

	public LoincCodeCsvImporter() {
		results = new String[2];
	}

	@Override
	public IStatus doImport(IProgressMonitor monitor) throws Exception {
		FileInputStream fis = null;

		if (results[0] != null && !results[0].isEmpty()) {
			File csv = new File(results[0]);

			try {
				fis = new FileInputStream(csv);
				LoincServiceComponent.getService().importFromCsv(fis, getFieldMapping());

			} catch (RuntimeException e) {
				return new ElexisStatus(ElexisStatus.ERROR, Activator.PLUGIN_ID, ElexisStatus.CODE_NOFEEDBACK,
						"Import failed", e);
			} finally {
				if (fis != null) {
					fis.close();
				}
			}

			ElexisEventDispatcher.reload(LoincCode.class);
		}
// if (results[1] != null && !results[1].isEmpty()) {
// File csv = new File(results[1]);
//
// try {
// fis = new FileInputStream(csv);
// LoincServiceComponent.getService().importMappingFromCsv(fis);
// } catch (RuntimeException e) {
// return new ElexisStatus(ElexisStatus.ERROR, Activator.PLUGIN_ID,
// ElexisStatus.CODE_NOFEEDBACK, "Mapping Import failed", e);
// } finally {
// if (fis != null) {
// fis.close();
// }
// }
// }
		return Status.OK_STATUS;
	}

	@Override
	public String getTitle() {
		return "LOINC Code CSV Importer"; //$NON-NLS-1$
	}

	@Override
	public String getDescription() {
		return "LOIN Code Import aus einer CSV Datei. Format CODE,LONGNAME,SHORTNAME,CLASS,UNIT";
	}

	@Override
	public Composite createPage(Composite parent) {
		Composite area = new Composite(parent, SWT.NONE);
		area.setLayout(new GridLayout(1, true));
		area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		LoincFileBasedImporter fis = new LoincFileBasedImporter(area, this, "Loinc Code CSV Datei:", 0);
		fis.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		return area;
	}

	private static Map<Integer, String> getFieldMapping() {
		HashMap<Integer, String> ret = new HashMap<Integer, String>();
		ret.put(0, LoincCode.FLD_CODE);
		ret.put(1, LoincCode.FLD_LONGNAME);
		ret.put(2, LoincCode.FLD_SHORTNAME);
		ret.put(3, LoincCode.FLD_CLASS);
		ret.put(4, LoincCode.FLD_UNIT);
		return ret;
	}

	private static class LoincFileBasedImporter extends Composite {

		public Text tFname;
		private String[] filterExts = { "*.csv" }; //$NON-NLS-1$
		private String[] filterNames = { Messages.Core_All_Files };

		public LoincFileBasedImporter(final Composite parent, final ImporterPage home, final String message,
				final int resultIdx) {
			super(parent, SWT.BORDER);
			setLayout(new GridLayout(1, false));
			final Label lFile = new Label(this, SWT.NONE);
			tFname = new Text(this, SWT.BORDER);
			home.results[resultIdx] = tFname.getText();
			lFile.setText(message); // $NON-NLS-1$
			lFile.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
			tFname.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
			Button bFile = new Button(this, SWT.PUSH);
			bFile.setText(Messages.ImporterPage_browse); // $NON-NLS-1$
			// bFile.setLayoutData(SWTHelper.getFillGridData(2,true,1,false));
			bFile.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					FileDialog fdl = new FileDialog(parent.getShell(), SWT.OPEN);
					fdl.setFilterExtensions(filterExts);
					fdl.setFilterNames(filterNames);
					String filename = fdl.open();
					if (filename != null) {
						tFname.setText(filename);
						home.results[resultIdx] = filename;
					}
				}
			});

		}
	}
}
