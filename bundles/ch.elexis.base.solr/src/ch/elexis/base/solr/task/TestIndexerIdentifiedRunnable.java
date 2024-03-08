package ch.elexis.base.solr.task;

import java.io.Serializable;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;

import ch.elexis.core.model.tasks.SingleIdentifiableTaskResult;
import ch.elexis.core.model.tasks.TaskException;

public class TestIndexerIdentifiedRunnable extends AbstractIndexerIdentifiedRunnable {

	public SingleIdentifiableTaskResult run(Map<String, Serializable> runContext) {
		// Implementierung für Testzwecke, könnte leer sein oder einfache Logik
		// enthalten
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLocalizedDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Serializable> run(Map<String, Serializable> runContext, IProgressMonitor progressMonitor,
			Logger logger) throws TaskException {
		// TODO Auto-generated method stub
		return null;
	}
}
