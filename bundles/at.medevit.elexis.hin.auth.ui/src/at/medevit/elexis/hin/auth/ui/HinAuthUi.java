package at.medevit.elexis.hin.auth.ui;

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

import at.medevit.elexis.hin.auth.core.IHinAuthUi;

@Component
public class HinAuthUi implements IHinAuthUi {

	private String inputValue;

	private Object supplierValue;

	@Override
	public void openBrowser(String url) {
		Program.launch(url);
	}

	@Override
	public Optional<String> openInputDialog(String title, String message) {
		inputValue = null;
		Display.getDefault().syncExec(() -> {
			InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), title, message, null, null);
			if (dialog.open() == Dialog.OK) {
				inputValue = dialog.getValue();
			}
		});
		return Optional.ofNullable(inputValue);
	}

	@Override
	public Object getWithCancelableProgress(String name, Supplier<?> supplier) {
		supplierValue = null;
		Display.getDefault().syncExec(()-> {
			ProgressMonitorDialog progressDialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
			try {
				RetrySupplierRunnableWithProgress runnable = new RetrySupplierRunnableWithProgress(name, supplier);
				progressDialog.run(true, true, runnable);
				supplierValue = runnable.getValue();
			} catch (Exception e) {
				// TODO: handle exception
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
			value = supplier.get();
			while (value == null && !monitor.isCanceled()) {
				Thread.sleep(2000);
				value = supplier.get();
			}
			monitor.done();
		}

		public Object getValue() {
			return value;
		}
	}
}
