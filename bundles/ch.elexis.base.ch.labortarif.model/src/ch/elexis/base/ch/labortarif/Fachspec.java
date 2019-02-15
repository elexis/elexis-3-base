package ch.elexis.base.ch.labortarif;

import ch.elexis.core.importer.div.importers.ExcelWrapper;
import ch.rgw.tools.StringTool;

public class Fachspec {
	
	public static Fachspec[] loadFachspecs(int langdef){
		ExcelWrapper excelWrapper = new ExcelWrapper();
		excelWrapper.setFieldTypes(new Class[] {
			Integer.class, String.class, Integer.class, Integer.class
		});
		if (excelWrapper.load(Fachspec.class.getResourceAsStream("/rsc/arztpraxen.xls"), langdef)) {
			int first = excelWrapper.getFirstRow();
			int last = excelWrapper.getLastRow();
			Fachspec[] fspecs = new Fachspec[last - first + 1];
			for (int i = first; i <= last; i++) {
				fspecs[i] = new Fachspec(excelWrapper.getRow(i).toArray(new String[0]));
			}
			return fspecs;
		}
		return null;
	}
	
	public int code, from, until;
	public String name;
	
	Fachspec(String[] line){
		this(Integer.parseInt(StringTool.getSafe(line, 0)), StringTool.getSafe(line, 1),
			Integer.parseInt(StringTool.getSafe(line, 2)),
			Integer.parseInt(StringTool.getSafe(line, 3)));
	}
	
	Fachspec(int code, String name, int from, int until){
		this.code = code;
		this.from = from;
		this.until = until;
		this.name = name;
	}
	
	/**
	 * Find the spec a given row belongs to
	 * 
	 * @param specs
	 *            a list of all specs
	 * @param row
	 *            the row to match
	 * @return the spec number or -1 if no spec
	 */
	public static int getFachspec(Fachspec[] specs, int row){
		for (Fachspec spec : specs) {
			if (spec.from <= row && spec.until >= row) {
				return spec.code;
			}
		}
		return -1;
	}
}
