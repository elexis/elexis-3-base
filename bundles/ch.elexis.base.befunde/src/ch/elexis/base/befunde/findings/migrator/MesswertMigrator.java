package ch.elexis.base.befunde.findings.migrator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.base.befunde.findings.migrator.messwert.MesswertFieldMapping;
import ch.elexis.base.befunde.findings.migrator.strategy.IMigrationStrategy;
import ch.elexis.base.befunde.findings.migrator.strategy.MesswertMigrationStrategyFactory;
import ch.elexis.befunde.Messwert;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.findings.ICoding;
import ch.elexis.core.findings.IFinding;
import ch.elexis.core.findings.IFindingsService;
import ch.elexis.core.findings.IObservation;
import ch.elexis.core.findings.UriType;
import ch.elexis.core.findings.migration.IMigratorContribution;
import ch.elexis.core.findings.templates.service.IFindingsTemplateService;
import ch.elexis.core.findings.util.commands.UpdateFindingTextCommand;
import ch.elexis.data.DBConnection;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.TimeTool;

@Component
public class MesswertMigrator implements IMigratorContribution {

	private static Logger logger = LoggerFactory.getLogger(MesswertMigrator.class);

	private IFindingsTemplateService templateService;

	private IFindingsService findingsService;

	@Reference(unbind = "-")
	public void setFindingsTemplateService(IFindingsTemplateService templateService) {
		this.templateService = templateService;
	}

	@Reference(unbind = "-")
	public void setFindingsService(IFindingsService findingsService) {
		this.findingsService = findingsService;
	}

	@Activate
	public void activate() {
		if (initialized()) {
			MesswertMigrationStrategyFactory.clearCodeToTemplateCache();
			MesswertMigrationStrategyFactory.setFindingsTemplateService(templateService);
			logger.debug("Initialized, activation successful"); //$NON-NLS-1$
		} else {
			logger.error("Not initialized, activation failed"); //$NON-NLS-1$
		}
	}

	/**
	 * Test if the migrator initialization was successful.
	 *
	 * @return
	 */
	public boolean initialized() {
		return templateService != null && findingsService != null;
	}

	/**
	 * Try to migrate all Messwert values of the patient to {@link IObservation}
	 * instances. If all values are migrated the Messwert is marked. If the
	 * migration of one value fails, none of the values is migrated to
	 * {@link IObservation}.
	 *
	 * @param patientId
	 */
	public void migratePatientMesswerte(String patientId) {
		Map<String, MesswertFieldMapping> mappingsMap = buildMappingsMap(MesswertFieldMapping.getMappings());
		for (Messwert messwert : getMesswerte(patientId)) {
			migrateMesswert(messwert, mappingsMap);
		}
	}

	private Map<String, MesswertFieldMapping> buildMappingsMap(List<MesswertFieldMapping> list) {
		HashMap<String, MesswertFieldMapping> ret = new HashMap<>();
		for (MesswertFieldMapping messwertFieldMapping : list) {
			ret.put(messwertFieldMapping.getLocalBefund() + messwertFieldMapping.getLocalBefundField(),
					messwertFieldMapping);
		}
		return ret;
	}

	private void migrateMesswert(Messwert messwert, Map<String, MesswertFieldMapping> mappingsMap) {
		String name = messwert.get(Messwert.FLD_NAME);
		TimeTool timeTool = new TimeTool();
		List<IObservation> observations = new ArrayList<>();
		boolean migrationError = false;
		if (isNotMigrated(messwert)) {
			@SuppressWarnings("unchecked")
			Map<Object, Object> values = messwert.getMap(Messwert.FLD_BEFUNDE);
			for (Object key : values.keySet()) {
				MesswertFieldMapping mapping = mappingsMap.get(name + ((String) key));
				if (mapping != null) {
					Optional<IObservation> observation = migrateMesswert(messwert, mapping, observations);
					if (!observation.isPresent()) {
						migrationError = true;
						break;
					} else {
						timeTool.set(messwert.getDate());
						observation.get().setEffectiveTime(timeTool.toLocalDateTime());
						observation.get().setOriginUri(UriType.DB.toString(messwert.storeToString()));
						observations.add(observation.get());
						try {
							new UpdateFindingTextCommand(observation.get()).execute();
						} catch (ElexisException e) {
							logger.warn("Updating finding text [" + name + ((String) key) + "] failed"); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				} else {
					logger.warn("No mapping for [" + name + ((String) key) + "], not migrated"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			if (migrationError) {
				// delete all created Observations og this Messwert
				deleteObservations(observations);
			}
		}
	}

	private void deleteObservations(List<IObservation> observations) {
		if (!observations.isEmpty()) {
			DBConnection connection = PersistentObject.getDefaultConnection();
			Stm stm = connection.getStatement();
			try {
				for (IObservation observation : observations) {
					stm.exec("DELETE FROM CH_ELEXIS_CORE_FINDINGS_OBSERVATION WHERE ID='" + observation.getId() + "';"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			} finally {
				connection.releaseStatement(stm);
			}
		}
	}

	private Optional<IObservation> migrateMesswert(Messwert messwert, MesswertFieldMapping mapping,
			List<IObservation> createdObservations) {
		String result = messwert.getResult(mapping.getLocalBefundField());
		if (result != null && !result.isEmpty()) {
			IMigrationStrategy strategy = MesswertMigrationStrategyFactory.get(mapping, messwert, createdObservations);
			return strategy.migrate();
		}
		return Optional.empty();
	}

	private List<Messwert> getMesswerte(String patientId) {
		Query<Messwert> query = new Query<Messwert>(Messwert.class);
		query.add(Messwert.FLD_PATIENT_ID, Query.EQUALS, patientId);
		return query.execute();
	}

	private boolean isNotMigrated(Messwert messwert) {
		return lookupMigratedObservations(UriType.DB.toString(messwert.storeToString())).isEmpty();
	}

	private List<IObservation> lookupMigratedObservations(String originuri) {
		ArrayList<IObservation> ret = new ArrayList<>();
		Stm stm = PersistentObject.getDefaultConnection().getStatement();
		if (stm != null) {
			try {
				ResultSet result = stm.query(
						"SELECT ID FROM CH_ELEXIS_CORE_FINDINGS_OBSERVATION WHERE originuri = '" + originuri + "';"); //$NON-NLS-1$ //$NON-NLS-2$
				while ((result != null) && result.next()) {
					String id = result.getString(1);
					findingsService.findById(id, IObservation.class, true).ifPresent(o -> ret.add(o));
				}
			} catch (SQLException e) {
				LoggerFactory.getLogger(getClass()).error("Error on migrated lookup", e); //$NON-NLS-1$
			} finally {
				PersistentObject.getDefaultConnection().releaseStatement(stm);
			}
		}
		return ret;
	}

	@Override
	public boolean canHandlePatientsFindings(Class<? extends IFinding> filter, ICoding coding) {
		return filter.isAssignableFrom(IObservation.class);
	}

	@Override
	public void migratePatientsFindings(String patientId, Class<? extends IFinding> filter, ICoding coding) {
		if (filter.isAssignableFrom(IObservation.class)) {
			migratePatientMesswerte(patientId);
		}
	}
}
