/*******************************************************************************
 * Copyright (c) 2015, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;

/**
 * Create one external file for editing. There will be no COM/OLE communication,
 * but direct editing with MS Word.
 *
 * @author thomashu
 *
 */
public class ExternalFile {
	private static File externalTempDir;
	private File tempFile;

	FileType fileType;

	public enum FileType {
		UNKNOWN(""), DOCX("_rels/.rels"), SXW("application/vnd.sun.xml.writer"), ODT( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				"application/vnd.oasis.opendocument.text"), //$NON-NLS-1$
		ERROR(""); //$NON-NLS-1$

		private final String mime;

		FileType(String mime) {
			this.mime = mime;
		}

		public String getMime() {
			return mime;
		}
	};

	public ExternalFile() {
		try {
			// create a directory in the temporary folder of the user
			if (externalTempDir == null) {
				externalTempDir = createTempDirectory();
			}
			String tempFileName = externalTempDir.getAbsolutePath() + File.separator + "wtp_" //$NON-NLS-1$
					+ Long.toString(System.nanoTime()) + ".docx"; //$NON-NLS-1$
			// create a file for keeping the current version of the document
			tempFile = new File(tempFileName);
			tempFile.createNewFile();

			// add listener for workbench shutdown and dispose CommunicationFile
			PlatformUI.getWorkbench().addWorkbenchListener(new DisposeWorkbenchListener(this));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private static class DisposeWorkbenchListener implements IWorkbenchListener {
		private ExternalFile file;

		public DisposeWorkbenchListener(ExternalFile file) {
			this.file = file;
		}

		@Override
		public boolean preShutdown(IWorkbench workbench, boolean forced) {
			return true;
		}

		@Override
		public void postShutdown(IWorkbench workbench) {
			file.dispose();
			file = null;
		}
	}

	private File createTempDirectory() throws IOException {
		File tmp = File.createTempFile("wtp_", "_tmp"); //$NON-NLS-1$ //$NON-NLS-2$

		if (!(tmp.delete())) {
			throw new IOException("Could not delete temp file: " + tmp.getAbsolutePath()); //$NON-NLS-1$
		}

		if (!(tmp.mkdir())) {
			throw new IOException("Could not create temp directory: " + tmp.getAbsolutePath()); //$NON-NLS-1$
		}

		return tmp;
	}

	public synchronized byte[] getContentAsByteArray() {
		synchronized (tempFile) {
			FileInputStream fis = null;
			byte[] content = new byte[(int) tempFile.length()];
			try {
				fis = new FileInputStream(tempFile);
				fis.read(content);
				fis.close();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			} finally {
				try {
					if (fis != null)
						fis.close();
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
			return content;
		}
	}

	public synchronized void write(byte[] bArray) {
		synchronized (tempFile) {
			FileOutputStream tmpOutput = null;
			try {
				tmpOutput = new FileOutputStream(tempFile);
				tmpOutput.write(bArray);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			} finally {
				try {
					if (tmpOutput != null)
						tmpOutput.close();
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
		}
	}

	public synchronized void write(InputStream is) {
		synchronized (tempFile) {
			FileOutputStream tmpOutput = null;
			try {
				tmpOutput = new FileOutputStream(tempFile);
				byte[] buffer = new byte[2048];
				int bytesRead = 0;
				while ((bytesRead = is.read(buffer)) != -1) {
					tmpOutput.write(buffer, 0, bytesRead);
				}
			} catch (IOException e) {
				throw new IllegalStateException(e);
			} finally {
				try {
					if (tmpOutput != null)
						tmpOutput.close();
					if (is != null)
						is.close();
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
		}
	}

	public synchronized File getFile() {
		return tempFile;
	}

	public synchronized boolean isError() {
		return fileType == FileType.ERROR;
	}

	public synchronized File getDirectory() {
		return externalTempDir;
	}

	public synchronized void dispose() {
		tempFile.delete();
	}

	public void open() {
		if (!Program.launch(tempFile.getAbsolutePath())) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Konnnte Word nicht starten.");
		}
	}
}
