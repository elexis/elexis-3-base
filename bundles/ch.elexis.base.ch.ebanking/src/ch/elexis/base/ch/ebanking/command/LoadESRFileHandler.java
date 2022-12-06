package ch.elexis.base.ch.ebanking.command;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;

import ch.elexis.base.ch.ebanking.ESRView;
import ch.elexis.base.ch.ebanking.esr.ESRFile;
import ch.elexis.base.ch.ebanking.esr.ESRRecord;
import ch.elexis.base.ch.ebanking.esr.Messages;
import ch.elexis.base.ch.ebanking.model.IEsrRecord;
import ch.elexis.base.ch.ebanking.print.ESRJournalLetter;
import ch.elexis.base.ch.ebanking.print.ESRLetter;
import ch.elexis.core.data.util.ResultAdapter;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.utils.CoreUtil;
import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.Result;

public class LoadESRFileHandler extends AbstractHandler implements IElementUpdater {

	public static final String COMMAND_ID = "ch.elexis.ebanking_ch.command.loadESRFile"; //$NON-NLS-1$

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		FileDialog fld = new FileDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell(), SWT.OPEN | SWT.MULTI);
		fld.setText(Messages.ESRView_selectESR);
		fld.setFilterExtensions(new String[] { "*.xml;*.zip;*.v11" });
		if (fld.open() != null) {
			String filepath = fld.getFilterPath();
			String[] filenames = fld.getFileNames();
			for (String filename : filenames) {
				if (filename != null) {
					final File file = new File(filepath + File.separator + filename);
					if (file.isFile() && file.exists() && file.canRead()) {
						try {
							PlatformUI.getWorkbench().getProgressService().busyCursorWhile(new IRunnableWithProgress() {

								public void run(IProgressMonitor monitor)
										throws InvocationTargetException, InterruptedException {
									File tmpDir = null;
									List<File> esrFiles = new ArrayList<>();
									try {
										String extension = FilenameUtils.getExtension(file.getName());
										if ("zip".equalsIgnoreCase(extension)) {
											tmpDir = new File(CoreUtil.getTempDir(),
													"esr_zip_" + System.currentTimeMillis());
											if (tmpDir.mkdirs()) {
												try {
													FileTool.unzip(file, tmpDir);
													esrFiles.addAll(
															Arrays.asList(tmpDir.listFiles(new FilenameFilter() {
																@Override
																public boolean accept(File dir, String name) {
																	if (file.isFile()) {
																		String extension = FilenameUtils
																				.getExtension(name);
																		return "xml".equalsIgnoreCase(extension);
																	}
																	return false;
																}
															})));
												} catch (IOException e) {
													ExHandler.handle(e);
												}
											}
										} else if ("xml".equalsIgnoreCase(extension)
												|| "v11".equalsIgnoreCase(extension)) {
											esrFiles.add(file);
										}
										for (File esrFile : esrFiles) {
											monitor.beginTask(Messages.ESRView_reading_ESR + " " + esrFile.getName(), //$NON-NLS-1$
													(int) (esrFile.length() / 25));
											ESRFile esrf = new ESRFile();
											Result<List<ESRRecord>> result = esrf.read(esrFile, monitor);
											if (result.isOK()) {
												ESRRecordsRunnable recordsRunnable = new ESRRecordsRunnable(monitor,
														result.get());
												recordsRunnable.run();
												openESRJournalPdf(esrFile, result.get());
												if (monitor.isCanceled()) {
													break;
												}
											} else {
												ResultAdapter.displayResult(result, Messages.ESRView_errorESR);
											}
										}
									} finally {
										updateEsrView(event);
										if (tmpDir != null) {
											for (File file : tmpDir.listFiles()) {
												file.delete();
											}
											tmpDir.delete();
										}
									}
								}
							});
						} catch (InvocationTargetException e) {
							ExHandler.handle(e);
							SWTHelper.showError(Messages.ESRView_errorESR2, Messages.ESRView_errrorESR2,
									Messages.ESRView_couldnotread + e.getMessage() + e.getCause().getMessage());
						} catch (InterruptedException e) {
							ExHandler.handle(e);
							SWTHelper.showError("ESR interrupted", Messages.ESRView_interrupted, e //$NON-NLS-1$
									.getMessage());
						}
					} else {
						SWTHelper.showInfo(Messages.ESRView_errrorESR2, Messages.ESRView_couldnotread + filename);
					}
				}
			}
		}
		return null;
	}

	private void openESRJournalPdf(File esrFile, List<ESRRecord> readRecords) {
		List<IEsrRecord> records = readRecords.stream().map(r -> r.toIEsrRecord()).collect(Collectors.toList());
		ESRLetter.print(new ESRJournalLetter(esrFile, records));
	}

	@Override
	public void updateElement(UIElement element, Map parameters) {
		element.setIcon(Images.IMG_IMPORT.getImageDescriptor());
		element.setTooltip(Messages.ESRView_read_ESR_explain);
	}

	private void updateEsrView(ExecutionEvent event) {
		UiDesk.asyncExec(new Runnable() {

			@Override
			public void run() {
				IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
				if (window != null) {
					IWorkbenchPage page = window.getActivePage();
					if (page != null) {
						ESRView view = (ESRView) page.findView(ESRView.ID);
						if (view != null) {
							view.updateView();
						}
					}
				}

			}
		});
	}

}
