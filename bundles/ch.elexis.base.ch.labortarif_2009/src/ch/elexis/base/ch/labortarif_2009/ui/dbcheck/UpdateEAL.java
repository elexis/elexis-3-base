package ch.elexis.base.ch.labortarif_2009.ui.dbcheck;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.ui.dbcheck.external.ExternalMaintenance;
import ch.elexis.labortarif2009.data.EALBlocksCodeUpdater;
import ch.elexis.labortarif2009.data.EALLabItemCodeUpdater;
import ch.elexis.labortarif2009.data.EALVerrechnet2015Updater;

public class UpdateEAL extends ExternalMaintenance {
	
	private static Logger logger = LoggerFactory.getLogger(UpdateEAL.class);
	
	@Override
	public String executeMaintenance(IProgressMonitor pm, String DBVersion){
		UpdateEALDialog dialog =
			new UpdateEALDialog(Display.getDefault().getActiveShell(), "EAL Update");
		dialog.setBlockOnOpen(true);
		StringBuilder result = new StringBuilder();
		if (dialog.open() == UpdateEALDialog.OK) {
			// update verrechnet
			if (dialog.isUpdateVerrechnet2015()) {
				EALVerrechnet2015Updater verrechnet2015Updater = new EALVerrechnet2015Updater();
				verrechnet2015Updater.fix2015Chapters();
				result.append(verrechnet2015Updater.update2015Verrechnet());
				result.append("\n\n");
			}
			// update blocks
			EALBlocksCodeUpdater blockUpdater = new EALBlocksCodeUpdater();
			if (dialog.isUpdateEALAnalysenBlocks()) {
				result.append(blockUpdater.updateBlockCodesAnalysen());
				result.append("\n\n");
			}
			if (!dialog.isUpdateEALAnalysenBlocks() && dialog.isUpdateEALBlocks()) {
				result.append(blockUpdater.updateBlockCodes());
				result.append("\n\n");
			}
			// update items
			if (dialog.isUpdateEALLabItem()) {
				EALLabItemCodeUpdater itemUpdater = new EALLabItemCodeUpdater();
				result.append(itemUpdater.updateLabItemCodeAnalysen());
			}
		}
		return result.toString();
	}
	
	@Override
	public String getMaintenanceDescription(){
		return "EAL Leistungen anpassen";
	}
}
