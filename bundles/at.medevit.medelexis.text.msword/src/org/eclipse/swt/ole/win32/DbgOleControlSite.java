package org.eclipse.swt.ole.win32;

import java.io.File;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.statushandlers.StatusManager;

import ch.elexis.core.data.status.ElexisStatus;

public class DbgOleControlSite extends OleControlSite {

	public DbgOleControlSite(Composite parent, int style, String progId) {
		super(parent, style, progId);
	}

	public DbgOleControlSite(Composite parent, int style, String progId, File file) {
		super(parent, style, progId, file);
	}

	@Override
	protected void releaseObjectInterfaces() {
		try {
			super.releaseObjectInterfaces();
		} catch (NullPointerException npe) {
			StatusManager.getManager()
					.handle(new ElexisStatus(ElexisStatus.ERROR, "at.medevit.medelexis.text.msword",
							ElexisStatus.CODE_NOFEEDBACK, npe.getMessage(), npe, ElexisStatus.LOG_FATALS),
							StatusManager.LOG);
		}
	}
}
