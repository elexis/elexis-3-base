/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OleWrapper for accessing Word Document members.
 *
 * @author thomashu
 *
 */
public class OleWordDocument extends OleWrapper {
	private Logger logger = LoggerFactory.getLogger(OleWordDocument.class);

	private OleWordSite site;

	public OleWordDocument(OleWordSite site, OleAutomation oleAuto, Display display, OleWrapperManager manager) {
		super(oleAuto, display, manager);
		this.site = site;
	}

	/**
	 * Get the Content (Returns a Range object that represents the main document
	 * story.) from the Document object.
	 *
	 * @param wordDocument
	 * @return
	 */
	public OleWordRange getContent(OleWrapperManager manager) {
		OleAutomation oleAuto = runGetOleAutomationProperty("Content"); //$NON-NLS-1$
		return new OleWordRange(oleAuto, display, manager);
	}

	public OleWordRange getRange(int start, int end, OleWrapperManager manager) {
		Variant[] boundriesArgs = new Variant[2];
		boundriesArgs[0] = new Variant(start);
		boundriesArgs[1] = new Variant(end);

		Variant oleVar = runInvoke("Range", boundriesArgs); //$NON-NLS-1$
		OleAutomation oleAuto = OleUtil.getOleAutomationFromVariant(oleVar);
		oleVar.dispose();
		boundriesArgs[0].dispose();
		boundriesArgs[1].dispose();
		return new OleWordRange(oleAuto, display, manager);
	}

	public OleWordShapes getShapes(OleWrapperManager manager) {
		OleAutomation oleAuto = runGetOleAutomationProperty("Shapes"); //$NON-NLS-1$
		return new OleWordShapes(oleAuto, display, manager);
	}

	public String getName() {
		Variant value = runGetVariantProperty("Name"); //$NON-NLS-1$
		String ret = value.getString();
		value.dispose();
		return ret;
	}

	public boolean isValid() {
		try {
			Variant value = runGetVariantProperty("Name"); //$NON-NLS-1$
			if (value == null) {
				return false;
			}
			value.dispose();
		} catch (IllegalStateException ex) {
			return false;
		}
		return true;
	}

	public OleWordPageSetup getPageSetup(OleWrapperManager manager) {
		OleAutomation oleAuto = runGetOleAutomationProperty("PageSetup"); //$NON-NLS-1$
		return new OleWordPageSetup(oleAuto, display, manager);
	}

	public OleWordSections getSections(OleWrapperManager manager) {
		OleAutomation oleAuto = runGetOleAutomationProperty("Sections"); //$NON-NLS-1$
		return new OleWordSections(oleAuto, display, manager);
	}

	public void printOut(boolean background) {
		Variant[] arguments = new Variant[1];
		arguments[0] = new Variant(background);
		runInvoke("PrintOut", arguments); //$NON-NLS-1$
		arguments[0].dispose();
	}

	public void activate() {
		runInvoke("Activate"); //$NON-NLS-1$
	}

	public void close() {
		runInvoke("Close"); //$NON-NLS-1$
	}

	public CommunicationFile getCommunicationFile() {
		CommunicationFile ret = site.getCommunicationFile();
		ret.write(this);
		return ret;
	}

	public void save(File file) {
		// create separate file for save as (else write error due to unknown reason)
		File saFile = null;
		try {
			for (int i = 0; i < 100; i++) {
				String sa2FileName = file.getAbsolutePath() + "_sa2" + i; //$NON-NLS-1$
				saFile = new File(sa2FileName);
				if (!saFile.exists()) {
					saFile.createNewFile();
					break;
				}
			}
			if (saFile != null) {
				Variant[] arguments = new Variant[2];
				arguments[0] = new Variant(saFile.getAbsolutePath());
				arguments[1] = new Variant(OleWordConstants.wdFormatDocumentDefault);

				if (OleWordApplication.WORD_VERSION == 2010)
					runInvoke("SaveAs2", arguments); //$NON-NLS-1$
				else
					runInvoke("SaveAs", arguments); //$NON-NLS-1$

				arguments[0].dispose();
				arguments[1].dispose();

				if (saFile.length() == 0) {
					logger.warn("Save created empty file");
				}

				// copy the saved file and remove the temp file
				ZipUtil.copyFile(saFile, file);
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} finally {
			if (saFile != null) {
				saFile.delete();
			}
		}
	}

	public DocxWordDocument getDocxWordDocument() {
		CommunicationFile file = getCommunicationFile();
		DocxWordDocument docxWordDocument = new DocxWordDocument(file.getFile());
		return docxWordDocument;
	}

	public void dispose() {
		GlobalOleWordWrapperManager.add(oleObj);
		GlobalOleWordWrapperManager.disposeAll();
	}

	protected static HashMap<String, Integer> memberIdMap = new HashMap<String, Integer>();

	@Override
	protected synchronized int getIdForMember(String member) {
		Integer id = memberIdMap.get(member);
		if (id == null) {
			id = OleUtil.getMemberId(oleObj, member);
			memberIdMap.put(member, id);
		}
		return id;
	}
}
