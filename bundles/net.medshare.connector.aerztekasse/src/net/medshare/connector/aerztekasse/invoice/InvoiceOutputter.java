/*******************************************************************************
 *
 * The authorship of this code and the accompanying materials is held by
 * medshare GmbH, Switzerland. All rights reserved.
 * http://medshare.net
 *
 * This code and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0
 *
 * Year of publication: 2012
 *
 *******************************************************************************/
package net.medshare.connector.aerztekasse.invoice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;
import org.slf4j.LoggerFactory;

import ch.elexis.TarmedRechnung.XMLExporter;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.model.InvoiceState;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Rechnung;
import ch.elexis.data.RnStatus;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Result;
import net.medshare.connector.aerztekasse.Messages;
import net.medshare.connector.aerztekasse.data.AerztekasseSettings;

public class InvoiceOutputter extends XMLExporter {

	// public only because of net.medshare.connector.aerztekasse_test
	public AerztekasseSettings settings;
	public boolean transferState = false;

	private String responseState;
	private String responseError;
	private String outputDir;
	private String transmittedInvoices;
	private String failedInvoices;
	private int transmittedInvoicesCount = 0;
	private int failedInvoicesCount = 0;

	/**
	 * Output und Übertragun für eine Liste von Rechnungen an die Ärztekasse. Die
	 * Rechnungen werden jede einzeln mit doExport als XML gespeichert, gezippt und
	 * an die Ärztekasse übermittelt. Bei erfolgreicher übermittlung wird der Status
	 * der Rechnung auf bezahlt gesetzt.
	 *
	 * @param type desired mode (original, copy, storno)
	 * @param rnn  a Collection of Rechnung - Objects to output
	 */
	@Override
	public Result<Rechnung> doOutput(final IRnOutputter.TYPE type, final Collection<Rechnung> rnn, Properties props) {
		final Result<Rechnung> ret = new Result<Rechnung>();
		transmittedInvoices = StringUtils.EMPTY;
		failedInvoices = StringUtils.EMPTY;
		IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
		if (outputDir == null) {
			SWTHelper.SimpleDialog dlg = new SWTHelper.SimpleDialog(new SWTHelper.IControlProvider() {
				@Override
				public Control getControl(Composite parent) {
					return createSettingsControl(parent);
				}

				@Override
				public void beforeClosing() {
					// Nothing
				}
			});
			if (dlg.open() != Dialog.OK) {
				return ret;
			}
		}
		try {
			progressService.runInUI(PlatformUI.getWorkbench().getProgressService(), new IRunnableWithProgress() {
				@Override
				public void run(final IProgressMonitor monitor) {
					boolean abort = false;
					monitor.beginTask(Messages.InvoiceOutputter_DoExport, rnn.size() * 3);
					File f = new File(outputDir);
					f.mkdirs();

					// XML und ArrayList mit Rechnungsnamen aller Rechnungen erstellen
					ArrayList<String> fileNamesXml = new ArrayList<String>(rnn.size());
					for (Rechnung rn : rnn) {
						String fileNameXml = rn.getNr();
						String filePathXml = outputDir + fileNameXml + ".xml"; //$NON-NLS-1$
						if (type == TYPE.STORNO) {
							fileNameXml += "_storno.xml"; //$NON-NLS-1$
						} else {
							fileNameXml += ".xml"; //$NON-NLS-1$
						}
						fileNamesXml.add(fileNameXml);
						if (doExport(rn, filePathXml, type, false) == null) {
							ret.add(Result.SEVERITY.ERROR, 1, Messages.InvoiceOutputter_ErrorInInvoice + rn.getNr(), rn,
									true);
							abort = true;
							break;
						}
						monitor.worked(1);
						if (monitor.isCanceled()) {
							break;
						}
					}

					// XMLs Zippen wenn do Export aller Rechnung ok war
					// Name of the ZIP file
					String filePathZip = outputDir + "invoices.zip"; //$NON-NLS-1$
					if (!abort) {
						try {
							// Delete the ZIP file if it already exists
							deleteFile(filePathZip);

							// Create the ZIP file
							ZipOutputStream out = new ZipOutputStream(new FileOutputStream(filePathZip));

							// Fill ZIP file with XMLs
							for (String fileNameXml : fileNamesXml) {
								String filePathXml = outputDir + fileNameXml;

								// Create a buffer for reading the files
								byte[] buf = new byte[1024];

								// Compress the file
								FileInputStream in = new FileInputStream(filePathXml);

								// Add ZIP entry to output stream.
								out.putNextEntry(new ZipEntry(fileNameXml));

								// Transfer bytes from the file to the ZIP file
								int len;
								while ((len = in.read(buf)) > 0) {
									out.write(buf, 0, len);
								}
								// Complete the entry
								out.closeEntry();
								in.close();

								monitor.worked(1);
								if (monitor.isCanceled()) {
									break;
								}
							}
							// Complete the ZIP file
							out.finish();
							out.flush();
							out.close();

						} catch (IOException e) {
							ExHandler.handle(e);
							abort = true;
						}
					}

					// ZIP File Übermitteln
					// Übermittlung an Ärztekasse und Parsen der Antwort
					String filePathAnswer = outputDir + "answer.html"; //$NON-NLS-1$
					if (!abort) {
						deleteFile(filePathAnswer);
						if (doHttpPost(filePathZip, filePathAnswer)) {
							// Wenn übermittlung OK, Status für Rechnungen setzen
							if (transferState) {
								for (Rechnung rn : rnn) {
									if (type == TYPE.ORIG) {
										rn.setStatus(RnStatus.BEZAHLT);
									}
									transmittedInvoicesCount++;
									transmittedInvoices += Messages.InvoiceOutputter_SuccessInvoiceNr + rn.getNr()
											+ " : " //$NON-NLS-1$
											+ Messages.InvoiceOutputter_NewState + " : " //$NON-NLS-1$
											+ RnStatus.getStatusText(rn.getStatus());

									monitor.worked(1);
									if (monitor.isCanceled()) {
										break;
									}
								}
							} else {
								// Übermittlung an Ärztekasse fehlgeschlagen
								for (Rechnung rn : rnn) {
									rn.reject(RnStatus.REJECTCODE.REJECTED_BY_PEER,
											Messages.InvoiceOutputter_TransmissionFailed + StringUtils.SPACE
													+ responseState + "/" //$NON-NLS-1$
													+ responseError);
									failedInvoicesCount++;
									failedInvoices += Messages.InvoiceOutputter_FailureInvoiceNr + rn.getNr() + " : " //$NON-NLS-1$
											+ StringUtils.SPACE + responseState + "/" + responseError; //$NON-NLS-1$

									monitor.worked(1);
									if (monitor.isCanceled()) {
										break;
									}
								}
							}
						} else {
							// Http POST fehlgeschlagen, abbruch
							Iterator<Rechnung> ir = rnn.iterator();
							Rechnung r = ir.next();
							ret.add(Result.SEVERITY.ERROR, 1, Messages.InvoiceOutputter_ErrorHttpPost, r, true);
							r.setStatus(InvoiceState.DEFECTIVE);

						}

					}
					for (String fileNameXml : fileNamesXml) {
						deleteFileOnExit(outputDir + fileNameXml);
					}
					deleteFileOnExit(filePathZip);
					deleteFileOnExit(filePathAnswer);

					monitor.done();
				}
			}, null);
		} catch (Exception ex) {
			ExHandler.handle(ex);
			ret.add(Result.SEVERITY.ERROR, 2, ex.getMessage(), null, true);
		}
		if (!ret.isOK()) {
			String errorStr = StringUtils.EMPTY;
			for (Result<Rechnung>.msg errorMsg : ret.getMessages()) {
				errorStr += errorMsg.getText() + StringUtils.LF;
			}
			SWTHelper.alert(Messages.InvoiceOutputter_ErrorInvoice + ret.get().getNr(), errorStr);
		}

		if (failedInvoicesCount > 0) {
			if ((transmittedInvoicesCount + failedInvoicesCount) < 10) {
				SWTHelper.showError(Messages.InvoiceOutputter_TransmittedInvoicesTitle,
						Messages.InvoiceOutputter_TransmittedInvoices + transmittedInvoices + failedInvoices);
			} else {
				SWTHelper.showError(Messages.InvoiceOutputter_TransmittedInvoicesTitle, MessageFormat.format(
						Messages.InvoiceOutputter_TransmisionAKFailure, failedInvoicesCount, transmittedInvoicesCount));
			}
		} else {
			if (transmittedInvoicesCount < 10) {
				SWTHelper.showInfo(Messages.InvoiceOutputter_TransmittedInvoicesTitle,
						Messages.InvoiceOutputter_TransmittedInvoices + transmittedInvoices);
			} else {
				SWTHelper.showInfo(Messages.InvoiceOutputter_TransmittedInvoicesTitle,
						MessageFormat.format(Messages.InvoiceOutputter_TransmisionAKSuccess, transmittedInvoicesCount));
			}
		}
		return ret;
	}

	@Override
	public String getDescription() {
		return Messages.InvoiceOutputter_TransmisionAK;
	}

	@Override
	public Control createSettingsControl(Object parent) {
		final Composite compParent = (Composite) parent;
		outputDir = System.getProperty("java.io.tmpdir") + "InvoiceOutput" + File.separator; //$NON-NLS-1$ //$NON-NLS-2$
		Composite ret = new Composite(compParent, SWT.NONE);
		ret.setLayout(new GridLayout(2, false));
		Label l = new Label(ret, SWT.NONE);
		l.setText(Messages.InvoiceOutputter_InvoiceOutputDir);
		l.setLayoutData(SWTHelper.getFillGridData(2, true, 1, false));
		final Text text = new Text(ret, SWT.READ_ONLY | SWT.BORDER);
		text.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		Button b = new Button(ret, SWT.PUSH);
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				DirectoryDialog dd = new DirectoryDialog(compParent.getShell(), SWT.OPEN);
				dd.setFilterPath(outputDir);
				String tmpOutputDir = dd.open();
				if (tmpOutputDir != null) {
					outputDir = tmpOutputDir.replace("\\", "/") + "/"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					text.setText(outputDir);
				}
			}
		});
		b.setText(Messages.InvoiceOutputter_ChangeDir);
		text.setText(outputDir);
		return ret;
	}

	/**
	 * Übermittelt das übergebene Zip File an die Ärztekasse. Bei erfolgreicher
	 * Übermittlung wird die Antwort geparst und gespeichert und es wird true
	 * zurückgegeben, sonst false.
	 *
	 * @param inFilePath  Pfad der Zip Datei
	 * @param outFilePath Pfad der HTML Respoonse Datei
	 * @param ret         Resultat um allfällige Fehlermeldungen hinzuzufügen
	 * @param rn          Rechnung welcher übermittelt wird
	 * @return true bei erfolgreichem Post, sonst false
	 */
	public boolean doHttpPost(String inFilePath, String outFilepath) {
		// public only because of net.medshare.connector.aerztekasse_test
		boolean returnValue = false;
		try {
			transferState = false;
			responseState = StringUtils.EMPTY;
			responseError = StringUtils.EMPTY;

			if (settings == null) {
				settings = new AerztekasseSettings((CoreHub.actMandant));
			}

			ClientHttpRequest post = new ClientHttpRequest(settings.getUrl());
			post.setParameter("user", settings.getUsername()); //$NON-NLS-1$
			post.setParameter("pwd", settings.getPassword()); //$NON-NLS-1$
			post.setParameter("chkDoubleOK", "false"); //$NON-NLS-1$ //$NON-NLS-2$
			post.setParameter("docs", "false"); //$NON-NLS-1$ //$NON-NLS-2$
			post.setParameter("lang", "A"); //$NON-NLS-1$ //$NON-NLS-2$
			post.setParameter("txtDocument", new File(inFilePath)); //$NON-NLS-1$
			post.setParameter("btnTransfert", "true"); //$NON-NLS-1$ //$NON-NLS-2$

			FileOutputStream fout = new FileOutputStream(outFilepath);
			OutputStreamWriter writer = new OutputStreamWriter(fout, "UTF-8"); //$NON-NLS-1$

			BufferedReader input = new BufferedReader(new InputStreamReader(post.post(), "UTF-8")); //$NON-NLS-1$

			String line;
			while ((line = input.readLine()) != null) {
				writer.append(line);
				writer.append(System.getProperty("line.separator")); //$NON-NLS-1$
				writer.flush();
				parseHttpPostResponse(line);
			}
			writer.close();
			fout.flush();
			fout.close();

			returnValue = true;
		} catch (Exception e) {
			LoggerFactory.getLogger(InvoiceOutputter.class).error("invoice output failure", e);
		}
		return returnValue;
	}

	/**
	 * Parser für die Http POST Antwort von der Ärztekasse. Wenn erfolgreoch, wird
	 * transferState auf true gesetzt. Allfällige Fehlermeldungen werden
	 * gespeichert, Status in responseState und Fehler in responseError.
	 *
	 * @param line
	 */
	private void parseHttpPostResponse(String line) {
		int startIndex = line.indexOf("ctl02__TextStatus"); //$NON-NLS-1$
		if (startIndex > 0) {
			responseState = getResponse(line, startIndex);
			if (responseState.contains("ReadyToControl")) { //$NON-NLS-1$
				transferState = true;
			} else {
				transferState = false;
			}
		}
		startIndex = line.indexOf("ctl02__TextError"); //$NON-NLS-1$
		if (startIndex > 0) {
			responseError = getResponse(line, startIndex);
		}
	}

	/**
	 * Gibt die entsprechende Meldung des gefunden Tilte, Status oder Errors zurück.
	 * ist keine korrekte Meldung vorhanden, wird null zurückgegeben.
	 *
	 * @param line
	 * @param startIndex
	 * @return Meldung
	 */
	private String getResponse(String line, int startIndex) {
		startIndex = line.indexOf(">", startIndex); //$NON-NLS-1$
		if (startIndex > 0) {
			int stopIndex = line.indexOf("<", startIndex); //$NON-NLS-1$
			if (stopIndex > startIndex + 2) {
				return line.substring(startIndex + 3, stopIndex);
			}
		}
		return null;
	}

	/**
	 * Löscht das angegebene File
	 *
	 * @param filename
	 */
	public static boolean deleteFile(String filename) {
		File f = new File(filename);
		boolean deleted = f.delete();
		return deleted;
	}

	/**
	 * Löscht das angegebene File beim Beenden von Elexis
	 *
	 * @param filename
	 */
	private void deleteFileOnExit(String filename) {
		File f = new File(filename);
		f.deleteOnExit();
	}
}
