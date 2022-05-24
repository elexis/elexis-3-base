package ch.elexis.barcode.scanner.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.State;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.barcode.scanner.BarcodeScannerMessage;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.serial.Connection;
import ch.elexis.core.serial.Connection.ComPortListener;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;

public class InputHandler extends ToggleHandler implements ComPortListener {
	private static Logger logger = LoggerFactory.getLogger(InputHandler.class);
	List<Connection> connections = new ArrayList<>();
	ExecutorService executorService = Executors.newSingleThreadExecutor();

	@Override
	protected void executeToggle(ExecutionEvent event, boolean newState) {

		if (newState == true) {
			Set<String> usedComPorts = new HashSet<>();
			setBaseEnabled(false);
			executorService.execute(new Runnable() {

				@Override
				public void run() {
					for (int i = 0; i < PreferencePage.NUMBER_OF_SCANNERS; i++) {
						String postfix = i > 0 ? String.valueOf(i) : StringUtils.EMPTY;
						String comPort = CoreHub.localCfg.get(PreferencePage.BarcodeScanner_COMPORT + postfix,
								StringUtils.EMPTY);
						String comSettings = CoreHub.localCfg.get(PreferencePage.BarcodeScanner_SETTINGS + postfix,
								"9600,8,n,1");
						boolean waitForNewline = CoreHub.localCfg
								.get(PreferencePage.BarcodeScanner_WAITFORNEWLINE + postfix, false);
						if (!comPort.isEmpty()) {
							if (usedComPorts.add(comPort)) {
								openConnection(i, postfix, comPort, comSettings, waitForNewline);
							} else {
								logger.debug("barcode scanner " + (i + 1) + " com port already in use: " + comPort);

							}
						}
					}
					if (connections.isEmpty()) {
						toggleButtonOff();
						SWTHelper.showInfo("Barcode Scanner",
								"Die Verbindung zum Barcode Scanner konnte nicht aufgebaut werden.\nProbieren Sie es erneut oder überprüfen Sie bitte die Einstellungen des Barcode Scanners.");
					}
					setBaseEnabled(true);
				}
			});

		} else {
			closeAllConnections();
			toggleButtonOff();
			setBaseEnabled(true);
		}

	}

	private void openConnection(int i, String postfix, String comPort, String comSettings, boolean waitForNewline) {
		Connection barcodeScannerConn = new Connection("Barcode Scanner@" + comPort, comPort, comSettings, this);
		if (waitForNewline) {
			barcodeScannerConn.withEndOfChunk(new byte[] { 0x0A }).excludeDelimiters(true);
		}
		if (barcodeScannerConn.connect()) {
			logger.debug("barcode scanner " + (i + 1) + " connected to port: " + comPort);
			connections.add(barcodeScannerConn);
		} else {
			SWTHelper.showError("Fehler mit Port",
					"Konnte Verbindung zu Barcode Scanner " + (i + 1) + " auf Port " + comPort + " nicht öffnen.");
		}
	}

	private void closeAllConnections() {
		int i = 0;
		for (Connection con : connections) {
			if (con.isOpen()) {
				con.close(2000);
				i++;
			}
		}
		connections.clear();
		final int barcodeScannerSize = i;
		executorService.execute(new Runnable() {
			public void run() {
				try {
					Thread.sleep(barcodeScannerSize * 2500);
					logger.debug("closed barcode scanners size: " + barcodeScannerSize);

				} catch (InterruptedException e) {
					/* ignore */
				}
			}
		});

	}

	@Override
	public void gotChunk(Connection conn, String chunk) {
		logger.debug(conn.getName() + ": gotChunk(): " + chunk);
		ElexisEventDispatcher.getInstance()
				.fire(new ElexisEvent(new BarcodeScannerMessage(conn.getName(), conn.getMyPort(), chunk),
						BarcodeScannerMessage.class, ElexisEvent.EVENT_UPDATE, ElexisEvent.PRIORITY_NORMAL));
	}

	private void toggleButtonOff() {
		ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		Command toggleCommand = commandService.getCommand(COMMAND_ID);

		State state = toggleCommand.getState("STYLE");
		boolean currentState = (Boolean) state.getValue();
		if (currentState) {
			// turn it off
			state.setValue(!currentState);
			UiDesk.getDisplay().syncExec(new Runnable() {

				public void run() {
					commandService.refreshElements(toggleCommand.getId(), null);
				}
			});
		}
	}

	@Override
	public void closed() {
		logger.info("Closed"); //$NON-NLS-1$
	}
}