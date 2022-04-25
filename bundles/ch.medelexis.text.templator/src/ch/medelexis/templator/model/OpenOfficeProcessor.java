/**
 * Copyright (c) 2010-2012, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 */

package ch.medelexis.templator.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.util.SWTHelper;
import ch.medelexis.templator.ui.OOOProcessorPrefs;
import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;

/**
 * This is a processor that takes OpenOffice Documents as templates
 *
 * @author gerry
 *
 */
public class OpenOfficeProcessor implements IProcessor {
	private static final Logger log = LoggerFactory.getLogger(OpenOfficeProcessor.class);
	ProcessingSchema proc;

	public String getName() {
		return "OpenOffice-Processor";
	}

	@Override
	public boolean doOutput(ProcessingSchema schema) {
		proc = schema;
		File tmpl = schema.getTemplateFile();
		if (!tmpl.exists()) {
			log.warn("Template " + schema + " missing");
			SWTHelper.alert("Template missing",
					MessageFormat.format("Konnte Vorlagedatei {0} nicht öffnen", tmpl.getAbsolutePath()));
			return false;
		}
		try {
			StorageController sc = StorageController.getInstance();
			ZipInputStream zis = new ZipInputStream(new FileInputStream(tmpl));
			File output = sc.createFile(ElexisEventDispatcher.getSelectedPatient(), tmpl.getName());
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(output));
			ZipEntry ze;
			while ((ze = zis.getNextEntry()) != null) {
				zos.putNextEntry(ze);
				if (ze.getName().equals("content.xml") || ze.getName().equals("styles.xml")) {
					SchemaFilterOutputStream sfo = new SchemaFilterOutputStream(proc, zos, this);
					FileTool.copyStreams(zis, sfo);
					// sfo.close();
				} else {
					FileTool.copyStreams(zis, zos);
				}
			}
			zos.close();
			zis.close();
			String cmd = CoreHub.localCfg.get(OOOProcessorPrefs.PREFERENCE_BRANCH + "cmd", "swriter.exe");
			String param = CoreHub.localCfg.get(OOOProcessorPrefs.PREFERENCE_BRANCH + "param", "%");
			int i = param.indexOf('%');
			if (i != -1) {
				param = param.substring(0, i) + output.getAbsolutePath() + param.substring(i + 1);
			}
			String[] params_ = param.split(" +");
			String[] params = new String[params_.length + 1];
			params[0] = cmd;
			for (i = 0; i < params_.length; i++) {
				params[i + 1] = params_[i];
			}
			/* Process process = */ Runtime.getRuntime().exec(params);
			return true;
		} catch (Exception e) {
			ExHandler.handle(e);
			SWTHelper.alert("OpenOffice Processor", "Problem mit dem Erstellen des Dokuments " + e.getMessage());
		}
		return false;
	}

	@Override
	public String convert(String input) {
		String ret = input.replaceAll("\\t", "<text:tab/>");
		ret = ret.replaceAll("\\n", "<text:line-break/>");
		return ret;
	}
}
