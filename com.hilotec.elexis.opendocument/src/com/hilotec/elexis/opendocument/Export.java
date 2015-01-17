package com.hilotec.elexis.opendocument;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.hilotec.elexis.opendocument.export.IDocExporter;

import ch.elexis.core.data.util.Extensions;

public class Export {
	public static final String EXT = "com.hilotec.elexis.opendocument.docexport";
	public static class Exporter {
		private String label;
		private IDocExporter exporter;

		public Exporter(String label, IDocExporter exp) {
			this.label = label;
			this.exporter = exp;
		}

		public String getLabel() {
			return this.label;
		}

		public void export(String path) {
			exporter.exportDocument(path);
		}
	}

	public static Exporter[] getExporters() {
		List<Exporter> res = new ArrayList<Exporter>();
		for (IConfigurationElement ce : Extensions.getExtensions(EXT)) {
			try {
				IDocExporter de = (IDocExporter) ce.createExecutableExtension("class");
				res.add(new Exporter(ce.getAttribute("title"), de));
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return res.toArray(new Exporter[0]);
	}
}
