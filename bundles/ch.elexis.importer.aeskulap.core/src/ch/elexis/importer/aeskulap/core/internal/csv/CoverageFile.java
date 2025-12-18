package ch.elexis.importer.aeskulap.core.internal.csv;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.SubMonitor;
import org.slf4j.LoggerFactory;

import ch.elexis.core.constants.XidConstants;
import ch.elexis.core.model.IBillingSystem;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IOrganization;
import ch.elexis.core.model.builder.ICoverageBuilder;
import ch.elexis.core.services.holder.BillingSystemServiceHolder;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.data.Fall;
import ch.elexis.data.Patient;
import ch.elexis.importer.aeskulap.core.IAeskulapImportFile;
import ch.elexis.importer.aeskulap.core.IAeskulapImporter;
import ch.rgw.tools.TimeTool;

public class CoverageFile extends AbstractCsvImportFile<Fall> implements IAeskulapImportFile {

	private File file;

	private CoverageGarant coverageGarant;

	public CoverageFile(File file) {
		super(file);
		this.file = file;
		File[] garantFile = file.getParentFile().listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().equals("garant.csv");
			}
		});
		if (garantFile.length > 0) {
			this.coverageGarant = new CoverageGarant(garantFile[0]);
		} else {
			throw new IllegalStateException("Could not find garant.csv in [" + file.getParentFile() + "]");
		}
	}

	@Override
	public File getFile() {
		return file;
	}

	public static boolean canHandleFile(File file) {
		return FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("csv")
				&& FilenameUtils.getBaseName(file.getName()).equalsIgnoreCase("Pat_Garanten");
	}

	@Override
	public boolean isHeaderLine(String[] line) {
		return line.length > 1 && line[1] != null && line[1].equalsIgnoreCase("GAR_KURZBEZ");
	}

	@Override
	public Fall create(String[] line) {
		Patient patient = (Patient) getWithXid(IAeskulapImporter.XID_IMPORT_PATIENT, line[0]);
		if (patient == null) {
			LoggerFactory.getLogger(getClass()).error("Could not find patient_no (Patient) [" + line[0]
						+ "]");
			return null;
		}
		ICoverage coverage = new ICoverageBuilder(CoreModelServiceHolder.get(), ConfigServiceHolder.get(),
				BillingSystemServiceHolder.get(), patient.toIPatient()).build();
		Optional<IOrganization> guarantor = getGuarantor(line[1]);
		if (guarantor.isPresent()) {
			coverage.setGuarantor(guarantor.get());
			coverage.setCostBearer(guarantor.get());
		}
		CoreModelServiceHolder.get().save(coverage);
		return Fall.load(coverage.getId());
	}

	private Optional<IBillingSystem> getBillingSystem(String string) {
		if (StringUtils.isEmpty(string) || string.toLowerCase().startsWith("g")) {
			return BillingSystemServiceHolder.get().getBillingSystem("KVG");
		} else if (StringUtils.isNotEmpty(string) && string.toLowerCase().startsWith("u")) {
			return BillingSystemServiceHolder.get().getBillingSystem("UVG");
		} else if (StringUtils.isNotEmpty(string) && string.toLowerCase().startsWith("z")) {
			return BillingSystemServiceHolder.get().getBillingSystem("VVG");
		}
		return Optional.empty();
	}

	private Optional<IOrganization> getGuarantor(String garKurzBez) {
		String[] guarantLine = coverageGarant.getGuarantLine(garKurzBez);
		if (guarantLine != null) {
			// EDI_SUB_NO
			String ean = guarantLine[17];
			if (StringUtils.isNotBlank(ean)) {
				List<IOrganization> found = XidServiceHolder.get().findObjects(XidConstants.EAN, ean,
						IOrganization.class);
				if (found.size() > 0) {
					return Optional.of(found.get(0));
				}
			}
			// BSV
			String bsv = guarantLine[20];
			if (StringUtils.isNotBlank(bsv)) {
				List<IOrganization> found = XidServiceHolder.get().findObjects(XidConstants.DOMAIN_BSVNUM, bsv,
						IOrganization.class);
				if (found.size() > 0) {
					return Optional.of(found.get(0));
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Code from CovercardUpdateUtil.java
	 * 
	 * @param system
	 */
	private void updateBillingSystem(IBillingSystem system) {
		if (system != null) {
			String requirements = BillingSystemServiceHolder.get().getRequirements(system);
			if (!requirements.contains("Versicherungsnummer:T")) {
				requirements = StringUtils.isBlank(requirements) ? "Versicherungsnummer:T"
						: requirements + ";Versicherungsnummer:T";
				BillingSystemServiceHolder.get().addOrModifyBillingSystem(system.getName(), null, requirements,
						system.getLaw());
			}
			String optionals = StringUtils.defaultString(BillingSystemServiceHolder.get().getOptionals(system));
			if (!optionals.contains("VEKANr:T")) {
				optionals = StringUtils.isBlank(optionals) ? "VEKANr:T"
						: appendWithoutDuplicateSeparator(optionals, "VEKANr:T");
				BillingSystemServiceHolder.get().addOrModifyBillingSystem(system.getName(), null,
						BillingSystemServiceHolder.get().getRequirements(system), system.getLaw());
			}
		}
	}

	private String appendWithoutDuplicateSeparator(String current, String append) {
		if (current.endsWith(";")) {
			return current + append;
		}
		return current + ";" + append;
	}

	@Override
	public void setProperties(Fall fall, String[] line) {
		if (fall != null) {
			ICoverage coverage = CoreModelServiceHolder.get().load(fall.getId(), ICoverage.class)
					.orElseThrow(() -> new IllegalStateException("Could not convert coverage [" + fall.getId() + "]"));
			TimeTool insertedTime = new TimeTool(line[5]);

			getBillingSystem(line[4]).ifPresent(bs -> coverage.setBillingSystem(bs));
			coverage.setInsuranceNumber(line[3]);
			coverage.setDateFrom(insertedTime.toLocalDate());

			// add veka / covercard nr 80xxxxxxxx
			if (StringUtils.isNotBlank(line[9]) && line[9].startsWith("80")) {
				updateBillingSystem(coverage.getBillingSystem());
				coverage.setExtInfo("VEKANr", StringUtils.defaultIfBlank(line[9], ""));
				coverage.setExtInfo("VEKAValid", StringUtils.defaultIfBlank(line[10], ""));
			}

			// PAT_GAR_NO
			fall.addXid(getXidDomain(), line[2], true);
			CoreModelServiceHolder.get().save(coverage);
		}
	}

	@Override
	public boolean doImport(Map<Type, IAeskulapImportFile> transientFiles, boolean overwrite, SubMonitor monitor) {
		monitor.beginTask("Aeskuplap Fälle Import", getLineCount());
		try {
			String[] line = null;
			while ((line = getNextLine()) != null) {
				// PAT_GAR_NO
				Fall fall = getExisting(line[2]);
				if (fall == null) {
					fall = create(line);
				} else if (!overwrite) {
					// skip if overwrite is not set
					continue;
				}
				setProperties(fall, line);
				monitor.worked(1);
			}
			return true;
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Error importing file", e);
		} finally {
			close();
			monitor.done();
		}
		return false;
	}

	@Override
	public Type getType() {
		return Type.COVERAGE;
	}

	@Override
	public String getXidDomain() {
		return IAeskulapImporter.XID_IMPORT_COVERAGE;
	}
}
