package ch.elexis.base.ch.arzttarife.psycho.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.base.ch.arzttarife.model.service.CoreModelServiceHolder;
import ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.jpa.model.adapter.AbstractIdDeleteModelAdapter;
import ch.elexis.core.model.IBillableOptifier;
import ch.elexis.core.model.IBillableVerifier;
import ch.elexis.core.model.IBilled;
import ch.elexis.core.model.IBillingSystemFactor;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.model.billable.AbstractOptifier;
import ch.elexis.core.model.billable.DefaultVerifier;
import ch.elexis.core.services.holder.BillingServiceHolder;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.core.types.VatInfo;
import ch.rgw.tools.Result;

public class PsychoLeistung extends AbstractIdDeleteModelAdapter<ch.elexis.core.jpa.entities.PsychoLeistung>
		implements Identifiable, IPsychoLeistung {

	public static final String STS_CLASS = "ch.elexis.data.PsychoLeistung";

	private static IBillableOptifier<PsychoLeistung> optifier;
	private IBillableVerifier verifier;

	public PsychoLeistung(ch.elexis.core.jpa.entities.PsychoLeistung entity) {
		super(entity);
		verifier = new DefaultVerifier();
	}

	@Override
	public synchronized IBillableOptifier<PsychoLeistung> getOptifier() {
		if (optifier == null) {
			optifier = new AbstractOptifier<PsychoLeistung>(CoreModelServiceHolder.get(),
					ContextServiceHolder.get()) {

				@Override
				public Result<IBilled> remove(IBilled billed, IEncounter encounter) {
					Result<IBilled> ret = super.remove(billed, encounter);
					if (!isPercent((PsychoLeistung) billed.getBillable())) {
						updatePercent(billed);
					}
					return ret;
				}

				@Override
				protected void setAmount(IBilled billed, double amount) {
					super.setAmount(billed, amount);
					updatePercent(billed);
				}

				@Override
				protected void setPrice(PsychoLeistung billable, IBilled billed) {
					Optional<IBillingSystemFactor> billingFactor = BillingServiceHolder.get().getBillingSystemFactor(
							billed.getEncounter().getCoverage().getBillingSystem().getName(),
							billed.getEncounter().getDate());
					if (billingFactor.isPresent()) {
						billed.setFactor(billingFactor.get().getFactor());
					} else {
						billed.setFactor(1.0);
					}
					int points = 0;
					if (billable.getTP() != null) {
						if (!isPercent(billable)) {
							try {
								points = getPoints(billable);
							} catch (NumberFormatException ne) {
								// ignore ...
							}
						}
					}
					billed.setPoints(points);
				}

				private int getPoints(PsychoLeistung billable) {
					return Integer.valueOf(billable.getTP()) * 100;
				}

				private void updatePercent(IBilled billed) {
					List<IBilled> allPsycho = getAllPsycho(billed);
					for (IBilled psychoBilled : allPsycho) {
						if (isPercent((PsychoLeistung) psychoBilled.getBillable())) {
							setPercent(psychoBilled, allPsycho);
						}
					}
				}

				/**
				 * Get all {@link IBilled} of the encounter, including the provided
				 * {@link IBilled} instead of an possible old version from the encounter.
				 * 
				 * @param billed
				 * @return
				 */
				private List<IBilled> getAllPsycho(IBilled billed) {
					List<IBilled> ret = new ArrayList<>();
					List<IBilled> encounterPsycho = new ArrayList<>(getPsycho(billed.getEncounter().getBilled()));
					for (IBilled encounterBilled : encounterPsycho) {
						if (!encounterBilled.getId().equals(billed.getId())) {
							ret.add(encounterBilled);
						}
					}
					if (!billed.isDeleted()) {
						ret.add(billed);
					}
					return ret;
				}

				private boolean isPercent(PsychoLeistung billable) {
					return billable.getTP().startsWith("%");
				}

				private void setPercent(IBilled billed, List<IBilled> allPsycho) {
					int sumTP = 0;
					for (IBilled psychoBilled : allPsycho) {
						if (!isPercent((PsychoLeistung) psychoBilled.getBillable())) {
							sumTP += getPoints((PsychoLeistung) psychoBilled.getBillable()) * psychoBilled.getAmount();
						}
					}
					int percent = getPercent((PsychoLeistung) billed.getBillable());
					billed.setPoints(sumTP);
					billed.setPrimaryScale(percent);
					System.out.println("Set percent [" + ((PsychoLeistung) billed.getBillable()).getCode() + "] tp ["
							+ sumTP + "] scale [" + percent + "]");
					CoreModelServiceHolder.get().save(billed);
					ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, billed);
				}
				
				private List<IBilled> getPsycho(List<IBilled> billed) {
					return billed.stream().filter(b -> b.getBillable() instanceof PsychoLeistung).collect(Collectors.toList());
				}
				
				private int getPercent(PsychoLeistung billedPsycho) {
					return Integer.parseInt(billedPsycho.getTP().substring(1)); 
				}
			};
		}
		return optifier;
	}

	@Override
	public IBillableVerifier getVerifier() {
		return verifier;
	}

	@Override
	public VatInfo getVatInfo() {
		return VatInfo.VAT_CH_ISTREATMENT;
	}

	@Override
	public String getCodeSystemCode() {
		return "581";
	}

	@Override
	public String getCodeSystemName() {
		return ch.elexis.core.jpa.entities.PsychoLeistung.CODESYSTEM_NAME;
	}

	@Override
	public String getCode() {
		return getEntity().getCode();
	}

	@Override
	public void setCode(String value) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getText() {
		return getEntity().getText();
	}

	@Override
	public void setText(String value) {
		getEntityMarkDirty().setCodeText(value);

	}

	@Override
	public LocalDate getValidFrom() {
		return getEntity().getValidFrom();
	}

	@Override
	public void setValidFrom(LocalDate value) {
		getEntityMarkDirty().setValidFrom(value);
	}

	@Override
	public LocalDate getValidTo() {
		return getEntity().getValidUntil();
	}

	@Override
	public void setValidTo(LocalDate value) {
		getEntityMarkDirty().setValidUntil(value);
	}

	@Override
	public String getTP() {
		return getEntity().getTp();
	}

	@Override
	public void setTP(String value) {
		getEntityMarkDirty().setTp(value);
	}

	@Override
	public String getDescription() {
		return getEntity().getDescription();
	}

	@Override
	public void setDescription(String value) {
		getEntityMarkDirty().setDescription(value);
	}

	@Override
	public String getLabel() {
		return getCode() + StringUtils.SPACE + getText();
	}

	@Override
	public boolean addXid(String domain, String id, boolean updateIfExists) {
		return XidServiceHolder.get().addXid(this, domain, id, updateIfExists);
	}

	@Override
	public IXid getXid(String domain) {
		return XidServiceHolder.get().getXid(this, domain);
	}

	@Override
	public String getLimitations() {
		return getEntity().getLimitations();
	}

	@Override
	public void setLimitations(String value) {
		getEntityMarkDirty().setLimitations(value);
	}

	@Override
	public String getExclusions() {
		return getEntity().getExclusions();
	}

	@Override
	public void setExclusions(String value) {
		getEntityMarkDirty().setExclusions(value);
	}
}
