package ch.elexis.mednet.webapi.ui;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.mednet.webapi.core.IMednetAuthUi;

@Component
public class MednetAuthUi implements IMednetAuthUi {

	private static final Logger logger = LoggerFactory.getLogger(MednetAuthUi.class);
	private String inputValue;
	private Object supplierValue;

	@Override
	public void openBrowser(String url) {
		Program.launch(url);
		logger.info("Browser opened with URL: {}", url); //$NON-NLS-1$
	}

	@Override
	public Optional<String> openInputDialog(String title, String message) {
		inputValue = null;
		Display.getDefault().syncExec(() -> {
			InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), title, message, null, null);
			if (dialog.open() == Dialog.OK) {
				inputValue = dialog.getValue();
				logger.info("User completed InputDialog with value: {}", inputValue); //$NON-NLS-1$
			}
		});
		return Optional.ofNullable(inputValue);
	}

	@Override
	public Object getWithCancelableProgress(String name, Supplier<?> supplier) {
		supplierValue = null;
		Display.getDefault().syncExec(() -> {
			ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
			try {
				RetrySupplierRunnableWithProgress runnable = new RetrySupplierRunnableWithProgress(name, supplier);
				progressDialog.run(true, true, runnable);
				supplierValue = runnable.getValue();
				logger.info("Supplier completed with value: {}", supplierValue); //$NON-NLS-1$
			} catch (InvocationTargetException | InterruptedException e) {
				logger.error("Error while executing supplier: {}", e.getMessage(), e); //$NON-NLS-1$
			}
		});
		return supplierValue;
	}

	private class RetrySupplierRunnableWithProgress implements IRunnableWithProgress {

		private Supplier<?> supplier;
		private String name;
		private Object value;

		public RetrySupplierRunnableWithProgress(String name, Supplier<?> supplier) {
			this.name = name;
			this.supplier = supplier;
		}

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			monitor.beginTask(name, IProgressMonitor.UNKNOWN);
			while (value == null && !monitor.isCanceled()) {
				value = supplier.get();
				if (value != null) {
					break;
				}
				Thread.sleep(2000);
			}
			monitor.done();
		}

		public Object getValue() {
			return value;
		}
	}
}
