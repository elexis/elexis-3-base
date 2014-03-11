package com.hilotec.elexis.kgview;

import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Patient;
import ch.rgw.tools.StringTool;

public abstract class PatientTextFView extends SimpleTextFView implements ElexisEventListener {
	protected final String dbfield;
	
	protected PatientTextFView(String field){
		dbfield = field;
	}
	
	@Override
	protected void initialize(){
		Patient p = ElexisEventDispatcher.getSelectedPatient();
		if (p != null) {
			patientChanged(p);
		}
		
		ElexisEventDispatcher.getInstance().addListeners(this);
	}
	
	@Override
	protected void fieldChanged(){
		Patient p = ElexisEventDispatcher.getSelectedPatient();
		p.set(dbfield, getText());
	}
	
	private void patientChanged(Patient p){
		setEnabled(true);
		setText(StringTool.unNull(p.get(dbfield)));
	}
	
	public void catchElexisEvent(final ElexisEvent ev){
		UiDesk.syncExec(new Runnable() {
			public void run(){
				Patient p = (Patient) ev.getObject();
				if (ev.getType() == ElexisEvent.EVENT_DESELECTED) {
					p.set(dbfield, getText());
					setEnabled(false);
				} else if (ev.getType() == ElexisEvent.EVENT_SELECTED) {
					patientChanged(p);
				}
			}
		});
	}
	
	private final ElexisEvent eetmpl = new ElexisEvent(null, Patient.class,
		ElexisEvent.EVENT_SELECTED | ElexisEvent.EVENT_DESELECTED);
	
	public ElexisEvent getElexisEventFilter(){
		return eetmpl;
	}
	
	@Override
	public void dispose(){
		ElexisEventDispatcher.getInstance().removeListeners(this);
		super.dispose();
	}
}
