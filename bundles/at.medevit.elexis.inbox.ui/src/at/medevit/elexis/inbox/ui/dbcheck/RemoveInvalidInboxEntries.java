package at.medevit.elexis.inbox.ui.dbcheck;

import java.time.LocalDate;
import java.time.ZoneId;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.inbox.model.IInboxElement;
import at.medevit.elexis.inbox.ui.InboxServiceHolder;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQueryCursor;
import ch.elexis.core.ui.dbcheck.external.ExternalMaintenance;

public class RemoveInvalidInboxEntries extends ExternalMaintenance {

	private boolean removeOldEntries;

	@Override
	public String executeMaintenance(IProgressMonitor pm, String DBVersion) {
		int removedOld = 0;
		int removedInvalid = 0;
		getRemoveOldEntries();
		if (removeOldEntries) {
			IQuery<IInboxElement> inboxQuery = InboxServiceHolder.getModelService().getQuery(IInboxElement.class, true);
			inboxQuery.and("lastupdate", COMPARATOR.LESS,
					LocalDate.now().minusDays(7).atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000);
			try (IQueryCursor<IInboxElement> entries = inboxQuery.executeAsCursor()) {
				LoggerFactory.getLogger(getClass()).warn("Removing " + entries.size() + " inbox elements");
				pm.beginTask("Bitte warten, alte Inbox Einträge werden gelöscht", entries.size());
				while (entries.hasNext()) {
					IInboxElement element = entries.next();
					InboxServiceHolder.getModelService().remove(element);
					removedOld++;
					pm.worked(1);
				}
			}
		}
		IQuery<IInboxElement> inboxQuery = InboxServiceHolder.getModelService().getQuery(IInboxElement.class, true);
		try (IQueryCursor<IInboxElement> entries = inboxQuery.executeAsCursor()) {
			LoggerFactory.getLogger(getClass()).warn("Checking " + entries.size() + " inbox elements");
			pm.beginTask("Bitte warten, Inbox Einträge werden überprüft", entries.size());
			while (entries.hasNext()) {
				IInboxElement element = entries.next();
				if (element.getObject() == null) {
					InboxServiceHolder.getModelService().remove(element);
					removedInvalid++;
					pm.worked(1);
				}
			}
		}
		return "Es wurden [" + removedOld + "] alte Inbox Einträge gelöscht, und [" + removedInvalid
				+ "] nicht mehr valide Inbox Einträge gelöscht";
	}

	private void getRemoveOldEntries() {
		Display.getDefault().syncExec(() -> {
			removeOldEntries = MessageDialog.openQuestion(Display.getDefault().getActiveShell(),
					"Alte Einträge löschen",
					"Sollen Inbox Einträge die älter als 7 Tage sind aus der DB entfernt werden?");
		});
	}

	@Override
	public String getMaintenanceDescription() {
		return "Nicht mehr aktive und alte Inbox Einträge entfernen.";
	}

}
