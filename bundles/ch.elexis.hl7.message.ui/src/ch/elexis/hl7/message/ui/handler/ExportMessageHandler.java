package ch.elexis.hl7.message.ui.handler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.slf4j.LoggerFactory;

import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.ui.dialogs.ChoiceDialog;
import ch.elexis.hl7.message.core.IHL7MessageService;
import ch.elexis.hl7.message.ui.preference.PreferenceUtil;
import ch.elexis.hl7.message.ui.preference.Receiver;

public class ExportMessageHandler extends AbstractHandler implements IHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException{
		String messageTyp = event.getParameter("ch.elexis.hl7.message.ui.exportmessage.typ");
		if (messageTyp != null && !messageTyp.isEmpty()) {
			if (exportMessage(messageTyp)) {
				return null;
			}
		} else {
			MessageDialog.openInformation(Display.getDefault().getActiveShell(),
				"Unbekannter Message Typ", "Der Message Typ [" + messageTyp
					+ "] ist nicht bekannt. Es wurde keine Message exportiert.");
		}
		return null;
	}
	
	private boolean exportMessage(String messageTyp){
		try {
			Receiver receiver = null;
			List<Receiver> receivers = PreferenceUtil.getReceivers();
			if (receivers.isEmpty()) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Empfänger",
					"Es sind keine Empfänger konfiguriert.");
				return false;
			} else if (receivers.size() > 1) {
				String[] choices = new String[receivers.size()];
				for(int i = 0; i < receivers.size(); i++) {
					choices[i] = receivers.get(i).getApplication() + " - " + receivers.get(i).getFacility();
				}
				ChoiceDialog choiceDialog = new ChoiceDialog(Display.getDefault().getActiveShell(),
					"Empfänger Auswahl",
					"Mehrere Empfänger konfiguriert, welcher soll verwendet werden?.", choices);
				if (choiceDialog.open() == Window.OK) {
					int index = choiceDialog.getResult();
					receiver = receivers.get(index);
				} else {
					return false;
				}
			} else {
				receiver = receivers.get(0);
			}
			Map<String, Object> context = MessageUtil.getContext();
			context.put(IHL7MessageService.CONTEXT_RECEIVINGAPPLICATION, receiver.getApplication());
			context.put(IHL7MessageService.CONTEXT_RECEIVINGFACILITY, receiver.getFacility());

			String message =
				MessageServiceHolder.getService().getMessage(messageTyp, context);
			MessageUtil.export(messageTyp, message);
		} catch (ElexisException e) {
			LoggerFactory.getLogger(getClass()).error("Error generating message", e);
			MessageDialog.openError(Display.getDefault().getActiveShell(),
				"Fehler", "Es ist ein Fehler bei der Generierung der Message vom Typ [" + messageTyp
					+ "] aufgetreten. Es wurde keine Message exportiert.");
			return false;
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error writing file", e);
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
				"Es ist ein Fehler beim Schreiben der Message vom Typ [" + messageTyp
					+ "] aufgetreten.");
			return false;
		}
		return true;
	}
}
