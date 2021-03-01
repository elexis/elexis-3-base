package ch.elexis.base.ch.labortarif.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.labortarif.ILaborLeistung;
import ch.elexis.base.ch.labortarif.LaborTarifConstants;
import ch.elexis.core.model.IBillable;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.ICodeElement;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.billable.AbstractOptifier;
import ch.elexis.core.services.ICodeElementService.CodeElementTyp;
import ch.elexis.core.services.ICodeElementService.ContextKeys;
import ch.elexis.core.services.ICodeElementServiceContribution;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.holder.BillingServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.rgw.tools.Result;
import ch.rgw.tools.Result.SEVERITY;

public class LaborLeistungOptifier extends AbstractOptifier<ILaborLeistung> {
	
	public LaborLeistungOptifier(IModelService modelService){
		super(modelService);
	}
	
	private boolean isOptify() {
		Optional<IMandator> activeMandator = ContextServiceHolder.get().getActiveMandator();
		if(activeMandator.isPresent()) {
			return ConfigServiceHolder.get().get(activeMandator.get(),
				ch.elexis.core.constants.Preferences.LEISTUNGSCODES_OPTIFY, true);
		} else {
			LoggerFactory.getLogger(getClass()).warn("No active Mandator, default is to optify.");
			return true;
		}
	}
	
	@Override
	public Result<IBilled> add(ILaborLeistung billable, IEncounter encounter, double amount,
		boolean save){
		Result<IBilled> ret = super.add(billable, encounter, amount, save);
		if (isOptify()) {
			List<IBilled> list = encounter.getBilled();
			
			boolean haveKons = false;
			IBilled v470710 = null;
			IBilled v470720 = null;
			int z4707 = 0;
			int z470710 = 0;
			int z470720 = 0;
			
			for (IBilled billed : list) {
				IBillable existing = billed.getBillable();
				if (existing == null) {
					return new Result<IBilled>(
						SEVERITY.ERROR, 1, "Could not resolve billable for billed ["
							+ billed.getId() + "] in encounter [" + encounter.getId() + "]",
						null, true);
				} else if (existing instanceof ILaborLeistung) {
					String existingCode = existing.getCode();
					if (existingCode.equals("4707.00")) { // Pauschale //$NON-NLS-1$
						if (z4707 < 1) {
							z4707 = 1;
						} else {
							return new Result<IBilled>(SEVERITY.WARNING, 1,
								"4707.00 only once per cons", billed, false); //$NON-NLS-1$
						}
					} else if (existingCode.equals("4707.10")) { // Fachbereich C //$NON-NLS-1$
						v470710 = billed;
					} else if (existingCode.equals("4707.20")) { // Fachbereich //$NON-NLS-1$
						// nicht-C
						v470720 = billed;
					} else if (existingCode.equals("4703.00") || existingCode.equals("4701.00") //$NON-NLS-1$//$NON-NLS-2$
						|| existingCode.equals("4704.00") || existingCode.equals("4706.00")) { //$NON-NLS-1$ //$NON-NLS-2$
						continue;
					} else {
						ILaborLeistung existingLaborLeistung = (ILaborLeistung) existing;
						if (!isSchnellAnalyse(existingLaborLeistung)) {
							if (existingLaborLeistung.getSpeciality().indexOf("C") > -1) { //$NON-NLS-1$
								z470710 += billed.getAmount();
							} else {
								z470720 += billed.getAmount();
							}
						}
					}
				} else if (existing.getCode().equals("00.0010") //$NON-NLS-1$
					|| existing.getCode().equals("00.0060")) { // Kons erste 5 Minuten  //$NON-NLS-1$
					haveKons = true;
				}
			}
			// reduce amendments to max. 24 TP
			while (((4 + 2 * z470710 + z470720) > 26) && z470710 > 0) {
				z470710--;
			}
			while (((4 + 2 * z470710 + z470720) > 24) && z470720 > 0) {
				z470720--;
			}
			
			if (z470710 == 0 || haveKons == false) {
				if (v470710 != null) {
					encounter.removeBilled(v470710);
				}
			} else {
				if (v470710 == null) {
					v470710 = createBilled(encounter, "4707.10"); //$NON-NLS-1$
				}
				v470710.setAmount(z470710);
			}
			
			if (z470720 == 0 || haveKons == false) {
				if (v470720 != null) {
					encounter.removeBilled(v470720);
				}
			} else {
				if (v470720 == null) {
					v470720 = createBilled(encounter, "4707.20"); //$NON-NLS-1$
				}
				v470720.setAmount(z470720);
			}
			
			if (v470710 != null && save) {
				CoreModelServiceHolder.get().save(v470710);
			}
			if (v470720 != null && save) {
				CoreModelServiceHolder.get().save(v470720);
			}
		}
		return ret;
	}
	
	@Override
	public Result<IBilled> remove(IBilled removeBilled, IEncounter encounter){
		Result<IBilled> ret = super.remove(removeBilled, encounter);
		List<IBilled> list = encounter.getBilled();
		
		boolean haveKons = false;
		IBilled v470710 = null;
		IBilled v470720 = null;
		int z4707 = 0;
		int z470710 = 0;
		int z470720 = 0;
		
		for (IBilled billed : list) {
			IBillable existing = billed.getBillable();
			if (existing instanceof ILaborLeistung) {
				String existingCode = existing.getCode();
				if (existingCode.equals("4707.00")) { // Pauschale //$NON-NLS-1$
					if (z4707 < 1) {
						z4707 = 1;
					} else {
						return new Result<IBilled>(SEVERITY.WARNING, 1,
							"4707.00 only once per cons", billed, false); //$NON-NLS-1$
					}
				} else if (existingCode.equals("4707.10")) { // Fachbereich C //$NON-NLS-1$
					v470710 = billed;
				} else if (existingCode.equals("4707.20")) { // Fachbereich //$NON-NLS-1$
					// nicht-C
					v470720 = billed;
				} else if (existingCode.equals("4703.00") || existingCode.equals("4701.00") //$NON-NLS-1$//$NON-NLS-2$
					|| existingCode.equals("4704.00") || existingCode.equals("4706.00")) { //$NON-NLS-1$ //$NON-NLS-2$
					continue;
				} else {
					ILaborLeistung existingLaborLeistung = (ILaborLeistung) existing;
					if (!isSchnellAnalyse(existingLaborLeistung)) {
						if (existingLaborLeistung.getSpeciality().indexOf("C") > -1) { //$NON-NLS-1$
							z470710 += billed.getAmount();
						} else {
							z470720 += billed.getAmount();
						}
					}
				}
			} else if (existing.getCode().equals("00.0010") //$NON-NLS-1$
				|| existing.getCode().equals("00.0060")) { // Kons erste 5 Minuten  //$NON-NLS-1$
				haveKons = true;
			}
		}
		if (z470710 == 0 || haveKons == false) {
			if (v470710 != null) {
				encounter.removeBilled(v470710);
			}
		} else if (z470710 > 0 && v470710 != null && v470710.getAmount() != z470710) {
			v470710.setAmount(z470710);
			coreModelService.save(v470710);
		}
		if (z470720 == 0 || haveKons == false) {
			if (v470720 != null) {
				encounter.removeBilled(v470720);
			}
		} else if (z470720 > 0 && v470720 != null && v470720.getAmount() != z470720) {
			v470720.setAmount(z470720);
			coreModelService.save(v470720);
		}
		return ret;
	}
	
	@Override
	protected void setPrice(ILaborLeistung billable, IBilled billed){
		billed.setFactor(getFactor(billed.getEncounter().getDate()));
		billed.setPoints(billable.getPoints());
	}
	
	private IBilled createBilled(IEncounter encounter, String code){
		Optional<ICodeElementServiceContribution> laborContribution = CodeElementServiceHolder.get()
			.getContribution(CodeElementTyp.SERVICE, LaborTarifConstants.CODESYSTEM_NAME);
		if (laborContribution.isPresent()) {
			Optional<ICodeElement> codeElement = laborContribution.get().loadFromCode(code,
				Collections.singletonMap(ContextKeys.CONSULTATION, encounter));
			if (codeElement.isPresent()) {
				Result<IBilled> result =
					super.add((ILaborLeistung) codeElement.get(), encounter, 1.0);
				return result.get();
			} else {
				throw new IllegalStateException("No labor tarif code element [" + code + "] found");
			}
		} else {
			throw new IllegalStateException("No labor tarif code element contribution");
		}
	}
	
	private boolean isSchnellAnalyse(ILaborLeistung laborLeistung){
		String chapter = laborLeistung.getChapter().trim();
		if (chapter != null && !chapter.isEmpty()) {
			String[] chapters = chapter.split(",");
			for (String string : chapters) {
				if (string.trim().equals("5.1.2.2.1")) {
					return true;
				}
			}
		}
		return false;
	}
	
	private double getFactor(LocalDate date){
		Optional<IBillingSystemFactor> systemFactor = BillingServiceHolder.get()
			.getBillingSystemFactor(LaborTarifConstants.MULTIPLICATOR_NAME, date);
		if (systemFactor.isPresent()) {
			return systemFactor.get().getFactor();
		}
		return 1.0;
	}
}
