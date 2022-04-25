package at.medevit.elexis.cobasmira.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;

import at.medevit.elexis.cobasmira.resulthandler.ErrorMessageHandler;
import at.medevit.elexis.cobasmira.resulthandler.ImportPatientResult;

public class CobasMiraLog {
	private static CobasMiraLog instance = null;

	private LinkedList<CobasMiraMessage> messageList = new LinkedList<CobasMiraMessage>();
	private PropertyChangeSupport changes = new PropertyChangeSupport(this);

	private CobasMiraLog() {
	}

	public static CobasMiraLog getInstance() {
		if (instance == null) {
			instance = new CobasMiraLog();
		}
		return instance;

	}

	public void addMessage(CobasMiraMessage add) {
		int importSuccess = 0;

		if (add.getBlockType() == CobasMiraMessage.BLOCK_TYPE_PATIENT_RESULTS) {
			importSuccess = ImportPatientResult.importPatientResult(add);
			add.setElexisStatus(importSuccess);
		} else if (add.getBlockType() == CobasMiraMessage.BLOCK_TYPE_ERROR_MESSAGE) {
			add.setElexisStatus(CobasMiraMessage.ELEXIS_RESULT_IGNORED);
			ErrorMessageHandler.handleError(add);
		} else if (add.getBlockType() == CobasMiraMessage.BLOCK_TYPE_RACK_INFORMATION) {
			add.setElexisStatus(CobasMiraMessage.ELEXIS_RESULT_IGNORED);
		}

		messageList.add(add);

		changes.firePropertyChange("messageList", null, messageList);
	}

	public LinkedList<CobasMiraMessage> getMessageList() {
		return messageList;
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

}
