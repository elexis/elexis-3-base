package at.medevit.elexis.agenda.ui.handler;


import java.util.Optional;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.agenda.ui.dialog.HistoryDialog;
import at.medevit.elexis.agenda.ui.model.Event;
import at.medevit.elexis.agenda.ui.rcprap.StateHistoryFormatterUtil;
import ch.elexis.core.model.IAppointment;
import ch.elexis.core.model.IPeriod;
import ch.elexis.core.ui.e4.locks.AcquireLockBlockingUi;
import ch.elexis.core.ui.e4.locks.ILockHandler;

public class ShowHistoryHandler {

	@Inject
	private ESelectionService selectionService;

	@Execute
	public Object execute() {
		Optional<IPeriod> period = getSelectedPeriod();

		period.ifPresent(p -> {
			AcquireLockBlockingUi.aquireAndRun(p, new ILockHandler() {
				@Override
				public void lockFailed() {

				}

				@Override
				public void lockAcquired() {
					IAppointment appointment = (IAppointment) p;
					String stateHistory = appointment.getStateHistoryFormatted("dd.MM.yyyy HH:mm:ss").replaceAll("\n",
							"<br />");
					stateHistory = StateHistoryFormatterUtil.replaceIdsWithLabels(stateHistory);
					String formattedStateHistory = StateHistoryFormatterUtil.formatStateHistoryFull(stateHistory);
					String description = appointment.getReason().replaceAll("\n", "<br />") + "<br /><br />"
							+ formattedStateHistory;
					HistoryDialog historyDialog = new HistoryDialog(new Shell(), description, appointment);
					historyDialog.open();
				}
			});
		});

		return null;
	}

	private Optional<IPeriod> getSelectedPeriod() {
		try {
			ISelection activeSelection = (ISelection) selectionService.getSelection();
			if (activeSelection instanceof StructuredSelection && !((StructuredSelection) activeSelection).isEmpty()) {
				Object element = ((StructuredSelection) activeSelection).getFirstElement();
				if (element instanceof IPeriod) {
					return Optional.of((IPeriod) element);
				}
			}
		} catch (Exception e) {
			LoggerFactory.getLogger(getClass()).error("Fehler beim Abrufen des ausgewählten Termins", e);
		}
		return Optional.empty();
	}
}
