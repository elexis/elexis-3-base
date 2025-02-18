package ch.elexis.laborimport.medics.v2.dbcheck;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.LoggerFactory;

import ch.elexis.core.importer.div.importers.ExcelWrapper;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.dbcheck.external.ExternalMaintenance;
import ch.elexis.data.LabItem;
import ch.elexis.data.Query;
import ch.elexis.labor.medics.v2.labimport.PatientLabor;

public class UpdateLabItemCode extends ExternalMaintenance {

	public UpdateLabItemCode() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String executeMaintenance(IProgressMonitor pm, String DBVersion) {
		List<LabItem> items = getLabItems();
		pm.beginTask(getMaintenanceDescription() + " Lade Methodenstamm", IProgressMonitor.UNKNOWN);
		Map<String, String> nameToCodeMap = getNameToCodeMap();
		pm.beginTask(getMaintenanceDescription() + " (" + items.size() + " Medics Parameter) (" + nameToCodeMap.size()
				+ " Medics Methodenstamm)", items.size());
		int updated = 0;
		int already = 0;
		int notfound = 0;
		for (LabItem item : items) {
			String code = nameToCodeMap.get(item.getName());
			if (code != null) {
				if (code.equals(item.getKuerzel())) {
					already++;
				} else {
					item.setKuerzel(code);
					updated++;
				}
			} else {
				LoggerFactory.getLogger(getClass()).warn("No code found for medics item " + item.getLabel());
				notfound++;
			}
		}
		ConfigServiceHolder.setGlobal("ch.elexis.laborimport.medics.v2.dbcheck.UpdateLabItemCode",
				"done_" + LocalDateTime.now());
		return "In " + items.size() + " Medics Parametern wurden " + updated + " angepasst, " + already
				+ " waren bereits richtig und " + notfound + " konnte in " + nameToCodeMap.size()
				+ " Methoden nicht gefunden werden.";
	}

	private Map<String, String> getNameToCodeMap() {
		Map<String, String> ret = new HashMap<>();
		ExcelWrapper exw = new ExcelWrapper();
		if (exw.load(getClass().getResourceAsStream("/rsc/codes/methodenstamm_20200131.xlsx"), 0)) {
			exw.setFieldTypes(new Class[] { String.class /* methode */, String.class /* langname */
			});
			int lastRow = exw.getLastRow();
			int firstRow = exw.getFirstRow() + 1; // header offset
			for (int i = firstRow; i <= lastRow; i++) {
				List<String> row = exw.getRow(i);
				ret.put(row.get(1), row.get(0));
			}
		}
		return ret;
	}

	private List<LabItem> getLabItems() {
		Query<LabItem> qli = new Query<LabItem>(LabItem.class);
		qli.add(LabItem.GROUP, Query.EQUALS, PatientLabor.LABOR_NAME);
		return qli.execute();
	}

	@Override
	public String getMaintenanceDescription() {
		return "Kürzel der Medics Labor Parameter aus Methodenstamm setzen.";
	}

	public static boolean wasExecuted() {
		return ConfigServiceHolder.getGlobal("ch.elexis.laborimport.medics.v2.dbcheck.UpdateLabItemCode", null) != null;
	}

	public static void execute() {
		Display display = Display.getDefault();
		display.syncExec(() -> {
			Shell activeshell = display.getActiveShell();
			ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(activeshell);
			try {
				progressDialog.run(true, false, new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						UpdateLabItemCode update = new UpdateLabItemCode();
						String result = update.executeMaintenance(monitor, StringUtils.EMPTY);
						LoggerFactory.getLogger(UpdateLabItemCode.class).info("LabItems update result:" + result);
					}
				});
			} catch (InvocationTargetException | InterruptedException e) {
				MessageDialog.openError(activeshell, "Medics Importer", "Fehler beim Update der Labor Parameter");
				LoggerFactory.getLogger(UpdateLabItemCode.class).error("Error on LabItems update", e);
			}
		});
	}
}
