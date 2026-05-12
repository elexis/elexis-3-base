package ch.elexis.base.ch.arzttarife.psycho.model.importer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.LoggerFactory;

import ch.elexis.base.ch.arzttarife.psycho.model.PsychoLimitation;
import ch.elexis.core.interfaces.AbstractReferenceDataImporter;
import ch.elexis.core.interfaces.IReferenceDataImporter;
import ch.elexis.core.model.IBlob;
import ch.elexis.core.services.IModelService;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;

@Component(property = IReferenceDataImporter.REFERENCEDATAID
		+ "=psycholimitation", service = IReferenceDataImporter.class)
public class PsychoLimitationReferenceDataImporter extends AbstractReferenceDataImporter {

	private static final String REFERENCEDATA_PSYCHO_LIMITATION_VERSION = "referencedata/psycho/limitation/version";

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)")
	private IModelService coreModelService;

	@Override
	public int getCurrentVersion() {
		return ConfigServiceHolder.get().get(REFERENCEDATA_PSYCHO_LIMITATION_VERSION, 0);
	}

	@Override
	public IStatus performImport(IProgressMonitor ipm, InputStream input, Integer newVersion) {
		// perform import with update consumer
		IStatus ret = Status.OK_STATUS;
		try {
			Optional<IBlob> blob = CoreModelServiceHolder.get().load(PsychoLimitation.LIMITATION_ID,
				IBlob.class);
			if (blob.isEmpty()) {
				blob = Optional.of(CoreModelServiceHolder.get().create(IBlob.class));
				blob.get().setId(PsychoLimitation.LIMITATION_ID);
			}
			blob.get().setStringContent(IOUtils.toString(input, "UTF-8"));
			CoreModelServiceHolder.get().save(blob.get());
		} catch (IOException e) {
			LoggerFactory.getLogger(getClass()).error("Exception importing limitation", e);
			ret = Status.CANCEL_STATUS;
		}
		if (ret.isOK()) {
			PsychoLimitation.update();
			if (newVersion != null) {
				ConfigServiceHolder.get().set(REFERENCEDATA_PSYCHO_LIMITATION_VERSION, newVersion);
			}
		}
		return ret;
	}
}
