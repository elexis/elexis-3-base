package ch.elexis.base.befunde.findings.migrator.strategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.base.befunde.findings.migrator.messwert.MesswertFieldMapping;
import ch.elexis.befunde.Messwert;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.findings.IObservation;
import ch.elexis.core.findings.IObservation.ObservationType;
import ch.elexis.core.findings.ObservationComponent;
import ch.elexis.core.findings.codes.CodingSystem;
import ch.elexis.core.findings.util.ModelUtil;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

public class ComponentMigration extends AbstractMigrationStrategy implements IMigrationStrategy {

	private static Logger logger = LoggerFactory.getLogger(ComponentMigration.class);

	private MesswertFieldMapping mapping;
	private Messwert messwert;
	private List<IObservation> createdObservations;

	private String componentGrpCode;
	private String componentCode;

	public ComponentMigration(MesswertFieldMapping mapping, Messwert messwert, List<IObservation> createdObservations) {
		this.mapping = mapping;
		this.messwert = messwert;
		this.createdObservations = createdObservations;

		// determine if component code
		String code = mapping.getFindingsCode();
		String[] parts = code.split("\\."); //$NON-NLS-1$
		if (parts.length == 2) {
			componentGrpCode = parts[0];
			componentCode = parts[1];
		} else if (parts.length == 1) {
			componentGrpCode = parts[0];
			componentCode = null;
		}
	}

	@Override
	public Optional<IObservation> migrate() {
		IObservation observation = getOrCreateObservation();
		if (observation != null) {
			boolean valueSet = false;
			// work on a component
			if (componentCode != null) {
				List<ObservationComponent> components = observation.getComponents();
				for (ObservationComponent observationComponent : components) {
					if (ModelUtil.isCodeInList(CodingSystem.ELEXIS_LOCAL_CODESYSTEM.getSystem(), componentCode,
							observationComponent.getCoding())) {
						valueSet = setValue(observationComponent);
						observation.updateComponent(observationComponent);
						break;
					}
				}
			} else { // work on the group
				valueSet = setValues(observation);
			}
			if (valueSet) {
				return Optional.of(observation);
			}
		}
		return Optional.empty();
	}

	private boolean setValues(IObservation observation) {
		List<ObservationComponent> components = observation.getComponents();
		ObservationType type = getComponentsType(components);
		if (type != null) {
			if (type == ObservationType.NUMERIC) {
				int valuesSet = 0;
				String result = messwert.getResult(mapping.getLocalBefundField());
				List<BigDecimal> values = NumericMigration.getValues(result);
				for (int i = 0; i < components.size(); i++) {
					if (i < values.size()) {
						ObservationComponent component = components.get(i);
						ObservationType componentType = component.getTypeFromExtension(ObservationType.class);
						if (componentType == ObservationType.NUMERIC) {
							component.setNumericValue(values.get(i));
							observation.updateComponent(component);
							valuesSet++;
						}
					}
				}
				if (valuesSet == components.size()) {
					String comment = NumericMigration.getComment(result);
					if (comment != null && !comment.isEmpty()) {
						// include whole text as comment if values size does not match
						if (values.size() > valuesSet) {
							comment = result;
						}
						observation.setComment(comment);
					}
					return true;
				} else {
					logger.error("Could only set " + valuesSet + " of " + values.size() + " values of Messwert [" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
							+ messwert.getId() + "]"); //$NON-NLS-1$
					return false;
				}
			} else if (type == ObservationType.TEXT) {
				String value = TextMigration.getValue(messwert.getResult(mapping.getLocalBefundField()));
				// set in the first available text component
				for (ObservationComponent observationComponent : components) {
					ObservationType componentType = observationComponent.getTypeFromExtension(ObservationType.class);
					if (componentType == ObservationType.TEXT) {
						observationComponent.setStringValue(value);
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Get the most likely {@link ObservationType} of the components. Currently
	 * first encountered {@link ObservationType}.
	 *
	 * @param components
	 * @return
	 */
	private ObservationType getComponentsType(List<ObservationComponent> components) {
		for (ObservationComponent observationComponent : components) {
			ObservationType type = observationComponent.getTypeFromExtension(ObservationType.class);
			if (type != null) {
				return type;
			}
		}
		return null;
	}

	private boolean setValue(ObservationComponent observationComponent) {
		ObservationType type = observationComponent.getTypeFromExtension(ObservationType.class);
		if (type == ObservationType.NUMERIC) {
			BigDecimal value = NumericMigration.getValue(messwert.getResult(mapping.getLocalBefundField()));
			observationComponent.setNumericValue(value);
			return true;
		} else if (type == ObservationType.TEXT) {
			String value = TextMigration.getValue(messwert.getResult(mapping.getLocalBefundField()));
			observationComponent.setStringValue(value);
			return true;
		}
		return false;
	}

	private IObservation getOrCreateObservation() {
		// lookup already created group observation
		for (IObservation iObservation : createdObservations) {
			if (ModelUtil.isCodeInList(CodingSystem.ELEXIS_LOCAL_CODESYSTEM.getSystem(), componentGrpCode,
					iObservation.getCoding())) {
				return iObservation;
			}
		}
		try {
			return (IObservation) templateService.createFinding(
					CoreModelServiceHolder.get().load(messwert.get(Messwert.FLD_PATIENT_ID), IPatient.class).get(),
					template);
		} catch (ElexisException e) {
			logger.error("Error creating observation", e); //$NON-NLS-1$
		}
		return null;
	}
}
