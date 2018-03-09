package at.medevit.elexis.impfplan.ui.billing;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import at.medevit.elexis.impfplan.model.DiseaseDefinitionModel;
import at.medevit.elexis.impfplan.model.po.Vaccination;
import at.medevit.elexis.impfplan.ui.dialogs.ApplicationInputDialog;
import at.medevit.elexis.impfplan.ui.handlers.ApplyVaccinationHandler;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.core.data.interfaces.IVerrechnetAdjuster;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.Artikel;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.elexis.data.Verrechnet;
import ch.rgw.tools.Money;

public class VaccinationVerrechnetAdjuster implements IVerrechnetAdjuster {
	
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	
	@Override
	public void adjust(final Verrechnet verrechnet){
		executor.submit(new Runnable() {
			@Override
			public void run(){
				IVerrechenbar verrechenbar = verrechnet.getVerrechenbar();
				if (verrechenbar instanceof Artikel) {
					String atc_code = ((Artikel) verrechenbar).getATC_code();
					if (atc_code != null && atc_code.length() > 4) {
						if (atc_code.toUpperCase()
							.startsWith(DiseaseDefinitionModel.VACCINATION_ATC_GROUP_TRAILER)) {
							Konsultation kons = verrechnet.getKons();
							if (kons != null) {
								Fall fall = kons.getFall();
								if (fall != null) {
									Patient patient = fall.getPatient();
									if (patient != null) {
										performVaccination(patient.getId(), (Artikel) verrechenbar);
									}
								}
							}
							verrechnet.setDetail(Verrechnet.VATSCALE, Double.toString(0.0));
						}
					}
				}
			}
		});
	}
	
	private void performVaccination(String patientId, Artikel article){
		UiDesk.asyncExec(new Runnable() {
			@Override
			public void run(){
				Date d = new Date();
				if (ApplyVaccinationHandler.inProgress()) {
					d = ApplyVaccinationHandler.getKonsDate();
				}
				
				Mandant m = (Mandant) ElexisEventDispatcher.getSelected(Mandant.class);
				ApplicationInputDialog aid =
					new ApplicationInputDialog(UiDesk.getTopShell(), article);
				aid.open();
				String lotNo = aid.getLotNo();
				String side = aid.getSide();
				
				Vaccination vacc = new Vaccination(patientId, article, d, lotNo, m.storeToString());
				
				if (side != null && !side.isEmpty()) {
					vacc.setSide(side);
				}
			}
		});
	}
	
	@Override
	public void adjustGetNettoPreis(Verrechnet verrechnet, Money price){
		// TODO Auto-generated method stub
		
	}
	
}
