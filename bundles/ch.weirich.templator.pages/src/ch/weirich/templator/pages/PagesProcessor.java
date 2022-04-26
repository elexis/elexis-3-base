package ch.weirich.templator.pages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;
import ch.medelexis.templator.model.IProcessor;
import ch.medelexis.templator.model.ProcessingSchema;
import ch.medelexis.templator.model.SchemaFilterOutputStream;
import ch.medelexis.templator.model.StorageController;
import ch.medelexis.templator.ui.OOOProcessorPrefs;
import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;

public class PagesProcessor implements IProcessor {
	private ProcessingSchema proc;

	public PagesProcessor() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return "Apple(tm) iWork(tm) Pages(tm)";
	}

	@Override
	public boolean doOutput(ProcessingSchema schema) {
		proc = schema;
		File tmpl = schema.getTemplateFile();
		if (!tmpl.exists()) {
			SWTHelper.alert("Template missing",
					MessageFormat.format("Konnte Vorlagedatei {0} nicht öffnen", tmpl.getAbsolutePath()));
			return false;
		}
		try {
			ZipInputStream zis = new ZipInputStream(new FileInputStream(tmpl));
			Patient actPatient = ElexisEventDispatcher.getSelectedPatient();
			StorageController sc = StorageController.getInstance();
			File output = null;
			output = sc.createFile(actPatient, tmpl.getName());
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(output));
			ZipEntry ze;
			while ((ze = zis.getNextEntry()) != null) {
				ZipEntry zo = new ZipEntry(ze.getName());
				zos.putNextEntry(zo);
				zo.setMethod(ZipOutputStream.DEFLATED);
				if (ze.getName().equals("index.xml")) {
					SchemaFilterOutputStream sfo = new SchemaFilterOutputStream(proc, zos, this);
					FileTool.copyStreams(zis, sfo);
				} else {
					FileTool.copyStreams(zis, zos);
				}
			}

			zos.finish();
			zis.close();
			zos.close();
			String cmd = CoreHub.localCfg.get(Preferences.PREFERENCE_BRANCH + "cmd", "open");
			String param = CoreHub.localCfg.get(OOOProcessorPrefs.PREFERENCE_BRANCH + "param", "%");
			int i = param.indexOf('%');
			if (i != -1) {
				param = param.substring(0, i) + output.getAbsolutePath() + param.substring(i + 1);
			}
			Process process = Runtime.getRuntime().exec(new String[] { cmd, param });
			return process.waitFor() == 0;
		} catch (Exception e) {
			ExHandler.handle(e);
			SWTHelper.alert("Pages Processor", "Problem mit dem Erstellen des Dokuments " + e.getMessage());
		}
		return false;
	}

	@Override
	public String convert(String input) {
		String replacement = input.replaceAll("\\t", "<sf:tab/>");
		replacement = replacement.replaceAll("\\n", "<sf:br/>");
		return replacement;
	}
}
