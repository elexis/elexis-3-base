package com.hilotec.elexis.kgview;

import org.eclipse.e4.core.di.annotations.Optional;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.util.NoPoUtil;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.ui.events.RefreshingPartListener;
import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.data.Patient;
import ch.rgw.tools.StringTool;
import jakarta.inject.Inject;

public abstract class PatientTextFView extends SimpleTextFView implements IRefreshable {
	protected final String dbfield;

	private Patient actPatient;

	private RefreshingPartListener udpateOnVisible = new RefreshingPartListener(this);

	protected PatientTextFView(String field) {
		dbfield = field;
	}

	@Override
	protected void initialize() {
		Patient p = ElexisEventDispatcher.getSelectedPatient();
		if (p != null) {
			patientChanged(p);
		}

		getSite().getPage().addPartListener(udpateOnVisible);
	}

	@Override
	protected void fieldChanged() {
		Patient p = ElexisEventDispatcher.getSelectedPatient();
		if (p != null)
			p.set(dbfield, getText());
	}

	private void patientChanged(Patient p) {
		setEnabled(true);
		setText(StringTool.unNull(p.get(dbfield)));
	}

	@Inject
	void activePatient(@Optional IPatient patient) {
		CoreUiUtil.runAsyncIfActive(() -> {
			Patient p = (Patient) NoPoUtil.loadAsPersistentObject(patient);
			if (actPatient != null) {
				// save on deselect or patient change
				if (p == null || !actPatient.getId().equals(p.getId())) {
					actPatient.set(dbfield, getText());
				}
			}
			if (p != null) {
				patientChanged(p);
			} else {
				setEnabled(false);
			}
			actPatient = p;
		}, area);
	}

	@Override
	public void dispose() {
		getSite().getPage().removePartListener(udpateOnVisible);
		super.dispose();
	}

	@Override
	public void refresh() {
		activePatient(ContextServiceHolder.get().getActivePatient().orElse(null));
	}
}
