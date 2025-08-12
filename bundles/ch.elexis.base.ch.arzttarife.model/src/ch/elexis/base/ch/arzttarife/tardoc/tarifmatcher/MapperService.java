package ch.elexis.base.ch.arzttarife.tardoc.tarifmatcher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.slf4j.LoggerFactory;

import ch.elexis.core.utils.CoreUtil;
import ch.oaat_otma.PatientCase;
import ch.oaat_otma.grouper.parser.ParseError;
import ch.oaat_otma.mapper.Mapper;
import ch.oaat_otma.mapper.MapperResult;
import ch.oaat_otma.mapper.ServiceCatalog;
import ch.oaat_otma.mapper.TardocCatalog;

@Component(service = MapperService.class)
public class MapperService {

	private static final String TARDOC_FILENAME = "tardoc_TARDOC_1.4b_de.json";
	private static final String LKAAT_FILENAME = "lkaat_1.0b.json";
	
	private Mapper mapper;

	@Activate
	public void activate() {
		TardocCatalog tardocCatalog = null;
		ServiceCatalog serviceCatalog = null;

		File rootDir = CoreUtil.getWritableUserDir();
		File tarifmatcherdir = new File(rootDir, "tarifmatcher");
		if (!tarifmatcherdir.exists()) {
			tarifmatcherdir.mkdir();
		}
		File tardocFile = new File(tarifmatcherdir, TARDOC_FILENAME);
		try (OutputStream out = new FileOutputStream(new File(tarifmatcherdir, TARDOC_FILENAME))) {
			IOUtils.copy(MapperService.class.getResourceAsStream("/rsc/mapper/" + TARDOC_FILENAME),
					out);
			tardocCatalog = TardocCatalog.readCatalog(tardocFile);
		} catch (IOException | ParseError e) {
			LoggerFactory.getLogger(getClass()).error("Error initializing Mapper", e);
		}
		File lkaatFile = new File(tarifmatcherdir, LKAAT_FILENAME);
		try (OutputStream out = new FileOutputStream(new File(tarifmatcherdir, LKAAT_FILENAME))) {
			IOUtils.copy(MapperService.class.getResourceAsStream("/rsc/mapper/" + LKAAT_FILENAME), out);
			serviceCatalog = ServiceCatalog.readCatalog(lkaatFile);
		} catch (IOException | ParseError e) {
			LoggerFactory.getLogger(getClass()).error("Error initializing Mapper", e);
		}
		mapper = new Mapper(serviceCatalog, tardocCatalog);
	}

	public MapperResult getResult(PatientCase patientCase) {
		return mapper.map(patientCase);
	}
}
