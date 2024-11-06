package com.hilotec.elexis.messwerte.v2.views;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.core.di.extensions.Service;
import org.slf4j.LoggerFactory;

import com.hilotec.elexis.messwerte.v2.data.Messung;
import com.hilotec.elexis.messwerte.v2.data.MessungKonfiguration;
import com.hilotec.elexis.messwerte.v2.data.Messwert;

import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.findings.IObservation;
import ch.elexis.core.findings.IObservation.ObservationType;
import ch.elexis.core.findings.ObservationComponent;
import ch.elexis.core.findings.UriType;
import ch.elexis.core.findings.codes.CodingSystem;
import ch.elexis.core.findings.templates.model.FindingsTemplate;
import ch.elexis.core.findings.templates.model.FindingsTemplates;
import ch.elexis.core.findings.templates.service.IFindingsTemplateService;
import ch.elexis.core.findings.util.ModelUtil;
import ch.elexis.core.findings.util.commands.UpdateFindingTextCommand;
import ch.elexis.core.findings.util.model.TransientCoding;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.core.utils.CoreUtil;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.rgw.tools.TimeTool;

public class ObservationMigrator {

	@Inject
	@Service(filterExpression = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.findings.model)")
	private IModelService findingsModelService;

	@Inject
	private IFindingsTemplateService findigsTemplateService;

	private Map<String, FindingsTemplate> templatesMap;

	private Properties mappingProperties;

	public ObservationMigrator() {
		CoreUiUtil.injectServices(this);
		templatesMap = new HashMap<>();
		
		// check if default templates exist, import if not
		FindingsTemplates defaultTemplates = findigsTemplateService.getFindingsTemplates("Standard Vorlagen");
		if(defaultTemplates.getFindingsTemplates().isEmpty()) {
			try {
				findigsTemplateService.importTemplateFromFile(getDefaultTemplatesFilePath());
			} catch (IOException e) {
				LoggerFactory.getLogger(getClass()).error("Error importing default templates", e);
			}
		}
		mappingProperties = new Properties();
		try {
			mappingProperties.load(getClass().getResourceAsStream("/rsc/migration/mapping.properties"));
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Could not load mapping properties.", e);
		}
	}

	public void loadProperties(String filename) {
		try (FileInputStream fis = new FileInputStream(filename)) {
			mappingProperties.load(fis);
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Could not load mapping properties.", e);
		}
	}

	private String getDefaultTemplatesFilePath() {
		File tmpFile = new File(CoreUtil.getTempDir(), "befundvorlage_default.xml");
		tmpFile.deleteOnExit();
		try (FileOutputStream fout = new FileOutputStream(tmpFile)) {
			IOUtils.copy(getClass().getResourceAsStream("/rsc/migration/befundvorlage_default.xml"), fout);
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Could not export default templates.", e);
			return null;
		}
		return tmpFile.getAbsolutePath();
	}

	public void migrate(IProgressMonitor monitor) {
		MessungKonfiguration.getInstance().readFromXML();
		List<Patient> allPatients = new Query<Patient>(Patient.class).execute();
		monitor.beginTask("Hilotec Messwerte Migration", allPatients.size());
		for (Patient patient : allPatients) {
			List<Messung> allMessungen = Messung.getPatientMessungen(patient, null);
			for (Messung messung : allMessungen) {
				TimeTool messungDatum = new TimeTool(messung.getDatum());
				List<IObservation> createdObservations = new ArrayList<IObservation>();
				for (Messwert messwert : messung.getMesswerte()) {
					if (hasValue(messwert)) {
						Optional<FindingsTemplate> template = getTemplateMapping(messwert.getName());
						if (template.isPresent()) {
							String mappedCode = mappingProperties.getProperty(messwert.getName());
							try {
								IObservation observation = null;
								if (isMappedCodeComponent(mappedCode)) {
									observation = getCreatedObservation(createdObservations,
											getMappedCodeNoComponent(mappedCode));
									if (observation == null) {
										observation = (IObservation) findigsTemplateService
												.createFinding(patient.toIPatient(), template.get());
										createdObservations.add(observation);
									}
									String componentCode = getMappedCodeComponent(mappedCode);
									if (StringUtils.isNotBlank(componentCode)) {
										List<ObservationComponent> components = observation.getComponents();
										for (ObservationComponent observationComponent : components) {
											if (ModelUtil.isCodeInList(CodingSystem.ELEXIS_LOCAL_CODESYSTEM.getSystem(),
													componentCode, observationComponent.getCoding())) {
												setValue(observationComponent, messwert.getWert());
												observation.updateComponent(observationComponent);
												break;
											}
										}
									}
								} else {
									observation = (IObservation) findigsTemplateService
											.createFinding(patient.toIPatient(), template.get());
									setValue(observation, messwert.getWert());
								}

								observation.setEffectiveTime(messungDatum.toLocalDateTime());
								observation.setOriginUri(UriType.DB.toString(messwert.storeToString()));
								messwert.delete();
								try {
									new UpdateFindingTextCommand(observation).execute();
								} catch (ElexisException e) {
									LoggerFactory.getLogger(getClass()).warn("Updating finding text failed"); //$NON-NLS-1$
								}
							} catch (ElexisException e) {
								LoggerFactory.getLogger(getClass()).error("Error creating finding for messwert '{}'", //$NON-NLS-1$
										messwert.getName(), e);
							}
						}
					}
				}
			}
			monitor.worked(1);
		}
		monitor.done();
	}

	private boolean hasValue(Messwert messwert) {
		if (StringUtils.isNotBlank(messwert.getWert())) {
			String value = messwert.getWert().trim();
			return !"0".equals(value);
		}
		return false;
	}

	private void setValue(IObservation observation, String value) {
		ObservationType type = observation.getObservationType();
		if (type == ObservationType.NUMERIC) {
			observation.setNumericValue(getNumericValue(value),
					observation.getNumericValueUnit().orElse(StringUtils.EMPTY));
		} else if (type == ObservationType.TEXT) {
			observation.setStringValue(value);
		}
	}

	private void setValue(ObservationComponent observationComponent, String value) {
		ObservationType type = observationComponent.getTypeFromExtension(ObservationType.class);
		if (type == ObservationType.NUMERIC) {
			observationComponent.setNumericValue(getNumericValue(value));
		} else if (type == ObservationType.TEXT) {
			observationComponent.setStringValue(value);
		}
	}

	private IObservation getCreatedObservation(List<IObservation> createdObservations, String componentGrpCode) {
		// lookup already created group observation
		for (IObservation iObservation : createdObservations) {
			if (ModelUtil.isCodeInList(CodingSystem.ELEXIS_LOCAL_CODESYSTEM.getSystem(), componentGrpCode,
					iObservation.getCoding())) {
				return iObservation;
			}
		}
		return null;
	}

	private Optional<FindingsTemplate> getTemplateMapping(String name) {
		String mappedCode = mappingProperties.getProperty(name);
		if (StringUtils.isNotBlank(mappedCode)) {
			mappedCode = getMappedCodeNoComponent(mappedCode);
			FindingsTemplate template = templatesMap.get(mappedCode);
			if (template == null) {
				template = findigsTemplateService.getFindingsTemplate(new TransientCoding(
						CodingSystem.ELEXIS_LOCAL_CODESYSTEM.getSystem(), mappedCode, StringUtils.EMPTY)).orElse(null);
				if (template != null) {
					templatesMap.put(mappedCode, template);
				}
			}
			return Optional.ofNullable(template);
		}
		return Optional.empty();
	}

	private boolean isMappedCodeComponent(String mappedCode) {
		return mappedCode.indexOf(".") > 0;
	}

	private String getMappedCodeComponent(String mappedCode) {
		return mappedCode.substring(mappedCode.indexOf(".") + 1);
	}

	private String getMappedCodeNoComponent(String mappedCode) {
		if (mappedCode.indexOf(".") > 0) {
			return mappedCode.substring(0, mappedCode.indexOf("."));
		}
		return mappedCode;
	}

	/**
	 * Get the first numeric value.
	 *
	 * @param result
	 * @return
	 */
	public BigDecimal getNumericValue(String result) {
		StringBuilder sb = new StringBuilder();
		for (char c : result.toCharArray()) {
			if (Character.isDigit(c) || c == '.' || c == ',') {
				sb.append(c);
			} else {
				break;
			}
		}
		if (sb.length() > 0) {
			String value = sb.toString().replaceAll(",", "."); //$NON-NLS-1$ //$NON-NLS-2$
			if (value.startsWith(".")) { //$NON-NLS-1$
				value = "0" + value; //$NON-NLS-1$
			}
			if (value.endsWith(".")) { //$NON-NLS-1$
				value = value + "0"; //$NON-NLS-1$
			}
			try {
				return new BigDecimal(value);
			} catch (NumberFormatException ne) {
				LoggerFactory.getLogger(getClass())
						.error("Could not parse numeric result [" + result + "] value [" + value + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
		return null;
	}
}
