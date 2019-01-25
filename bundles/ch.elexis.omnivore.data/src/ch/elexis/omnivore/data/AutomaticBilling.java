package ch.elexis.omnivore.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.core.data.interfaces.events.MessageEvent;
import ch.elexis.core.data.interfaces.events.MessageEvent.MessageType;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.ui.views.Messages;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Leistungsblock;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.omnivore.PreferenceConstants;
import ch.rgw.tools.Result;
import ch.rgw.tools.TimeTool;

public class AutomaticBilling {
	
	public static boolean isEnabled(){
		String blockId = CoreHub.localCfg.get(PreferenceConstants.AUTO_BILLING_BLOCK, "");
		return CoreHub.localCfg.get(PreferenceConstants.AUTO_BILLING, false)
			&& !blockId.isEmpty();
	}
	
	private static Executor executor = Executors.newSingleThreadExecutor();
	
	private Patient patient;
	private DocHandle docHandle;
	
	public AutomaticBilling(DocHandle docHandle){
		this.patient = docHandle.getPatient();
		this.docHandle = docHandle;
	}
	
	public void bill(){
		if (isEnabled() && docHandle != null) {
			// do actual billing in a separate thread
			executor.execute(new Runnable() {
				@Override
				public void run(){
					try {
						Konsultation encounter = getEncounter();
						if (encounter != null) {
							Leistungsblock block = Leistungsblock.load(
								CoreHub.localCfg.get(PreferenceConstants.AUTO_BILLING_BLOCK, ""));
							if (encounter != null && encounter.isEditable(false)) {
								addBlockToEncounter(block, encounter);
							} else {
								LoggerFactory.getLogger(getClass()).warn(String.format(
									"Could not add block [%s] for document of patient [%s] because no valid kons found.",
									block.getName(), patient.getLabel()));
							}
						}
					} catch (Exception e) {
						ElexisEventDispatcher.getInstance().fireMessageEvent(new MessageEvent(
							MessageType.ERROR, "Error",
							"Es ist ein Fehler bei der automatischen Verrechnung aufgetreten."));
						LoggerFactory.getLogger(getClass()).error("Error billing block", e);
					}
				}
			});
		}
	}
	
	private void addBlockToEncounter(Leistungsblock block, Konsultation encounter){
		List<ICodeElement> elements = block.getElements(encounter);
		for (ICodeElement element : elements) {
			if (element instanceof PersistentObject) {
				Result<IVerrechenbar> result = encounter.addLeistung((IVerrechenbar) element);
				if (!result.isOK()) {
					ElexisEventDispatcher.getInstance()
						.fireMessageEvent(new MessageEvent(MessageType.WARN,
							Messages.VerrechnungsDisplay_imvalidBilling,
							patient.getLabel() + "\nDokument import Verrechnung von ["
								+ ((IVerrechenbar) element).getCode() + "]\n\n"
								+ result.toString()));
				}
			}
		}
	}
	
	private Konsultation getEncounter(){
		Konsultation encounter = patient.getLastKonsultation();
		if (encounter == null || !encounter.isEditable(false)) {
			encounter = createEncounter();
		}
		return encounter;
	}
	
	private Konsultation createEncounter(){
		Konsultation lastEncounter = patient.getLastKonsultation();
		Fall fall = null;
		if(lastEncounter != null) {
			fall = lastEncounter.getFall();
		}
		if (fall == null || !fall.isOpen()) {
			List<Fall> openFall = getOpenFall();
			if (openFall.isEmpty()) {
				fall = patient.neuerFall(Fall.getDefaultCaseLabel(), Fall.getDefaultCaseReason(),
					Fall.getDefaultCaseLaw());
			} else {
				fall = openFall.get(0);
			}
		}
		if (fall != null) {
			return fall.neueKonsultation();
		}
		return null;
	}
	
	private List<Fall> getOpenFall(){
		ArrayList<Fall> ret = new ArrayList<Fall>();
		Fall[] faelle = patient.getFaelle();
		for (Fall f : faelle) {
			if (f.isOpen()) {
				ret.add(f);
			}
		}
		ret.sort(new Comparator<Fall>() {
			TimeTool o1Begin = new TimeTool();
			TimeTool o2Begin = new TimeTool();
			@Override
			public int compare(Fall o1, Fall o2){
				if (o1.getBeginnDatum() != null && o2.getBeginnDatum() != null) {
					o1Begin.set(o1.getBeginnDatum());
					o2Begin.set(o2.getBeginnDatum());
					return o1Begin.compareTo(o2Begin);
				}
				return Long.compare(o1.getLastUpdate(), o2.getLastUpdate());
			}
		});
		return ret;
	}
}
