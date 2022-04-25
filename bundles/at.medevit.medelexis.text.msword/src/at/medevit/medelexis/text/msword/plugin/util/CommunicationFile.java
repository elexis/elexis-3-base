/*******************************************************************************
 * Copyright (c) 2012, MEDEVIT OG and MEDELEXIS AG
 * All rights reserved.
 ******************************************************************************/
package at.medevit.medelexis.text.msword.plugin.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;

/**
 * Create one temp file for communication between different parts (OLE/COM and
 * TextPlugin) of this TextPlugin implementation.
 *
 * @author thomashu
 *
 */
public class CommunicationFile {
	private File tempDir;
	private File tempFile;
	private File tempConversionFile;

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

	public CommunicationFile() {
		try {
			// create a directory in the temporary folder of the user
			tempDir = createTempDirectory();
			String tempFileName = tempDir.getAbsolutePath() + File.separator + "wtp_" //$NON-NLS-1$
					+ Long.toString(System.nanoTime()) + ".tmp"; //$NON-NLS-1$
			// create a file for keeping the current version of the document
			tempFile = new File(tempFileName);
			tempFile.createNewFile();

			tempFileName = tempDir.getAbsolutePath() + File.separator + "conv_" //$NON-NLS-1$
					+ Long.toString(System.nanoTime()) + ".tmp"; //$NON-NLS-1$
			// create a file for keeping the current version of the document
			tempConversionFile = new File(tempFileName);
			tempConversionFile.createNewFile();

			// add listener for workbench shutdown and dispose CommunicationFile
			PlatformUI.getWorkbench().addWorkbenchListener(new DisposeWorkbenchListener(this));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private static class DisposeWorkbenchListener implements IWorkbenchListener {
		private CommunicationFile file;

		public DisposeWorkbenchListener(CommunicationFile file) {
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

	public synchronized void write(OleWordDocument document) {
		synchronized (tempFile) {

			document.save(tempFile);
		}
	}

	public synchronized void write(DocxWordDocument document) {
		synchronized (tempFile) {
			FileOutputStream tmpOutput = null;
			try {
				tmpOutput = new FileOutputStream(tempFile);
				document.writeTo(tmpOutput);

				fileType = FileType.DOCX;
			} catch (IOException e) {
				throw new IllegalStateException(e);
			} finally {
				try {
					if (tmpOutput != null) {
						tmpOutput.close();
					}
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
		}
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

				fileType = guessFileType(bArray);

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
			DocumentConversion.convertToWordCompatibleType(this);
		}
	}

	public synchronized void write(InputStream is) {
		synchronized (tempFile) {
			FileOutputStream tmpOutput = null;
			try {
				tmpOutput = new FileOutputStream(tempFile);
				byte[] buffer = new byte[2048];
				int bytesRead = 0;
				boolean typeGuessed = false;
				while ((bytesRead = is.read(buffer)) != -1) {
					if (!typeGuessed) {
						fileType = guessFileType(buffer);
						typeGuessed = true;
					}

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
			DocumentConversion.convertToWordCompatibleType(this);
		}
	}

	public static FileType guessFileType(byte[] bytes) {
		String sample = new String(bytes);
		if (sample.contains(FileType.SXW.getMime()))
			return FileType.SXW;
		if (sample.contains(FileType.ODT.getMime()))
			return FileType.ODT;
		if (sample.contains(FileType.DOCX.getMime()))
			return FileType.DOCX;

		return FileType.UNKNOWN;
	}

	public synchronized File getFile() {
		return tempFile;
	}

	public synchronized boolean isError() {
		return fileType == FileType.ERROR;
	}

	public synchronized File getDirectory() {
		return tempDir;
	}

	public synchronized void dispose() {
		if (tempDir != null && tempDir.exists()) {
			ZipUtil.deleteRecursive(tempDir);
		}
		DocumentConversion.dispose();
	}

	public File getConversionFile() {
		return tempConversionFile;
	}
}
