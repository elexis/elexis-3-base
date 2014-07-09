package com.hilotec.elexis.opendocument2omnivore;

import java.io.File;
import java.io.RandomAccessFile;

import ch.elexis.omnivore.data.DocHandle;

import com.hilotec.elexis.opendocument.export.IDocExporter;

public class Exporter implements IDocExporter {

	@Override
	public void exportDocument(String path) {
		RandomAccessFile f;
		byte[] b;
		try {
			f = new RandomAccessFile(path, "r");
			b = new byte[(int)f.length()];
			f.read(b);
			f.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		DocHandle.assimilate(path);
		new File(path).delete();
	}

}
