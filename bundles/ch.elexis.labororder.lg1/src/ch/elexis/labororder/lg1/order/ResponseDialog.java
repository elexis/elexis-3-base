package ch.elexis.labororder.lg1.order;

import java.net.URI;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.chromium.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.slf4j.LoggerFactory;

public class ResponseDialog extends Dialog {
	
	private String responseText;
	private Label title;
	private Browser browser;
	
	public ResponseDialog(String text, Shell shell){
		super(shell);
		this.responseText = text;
	}
	
	@Override
	protected Control createDialogArea(Composite container){
		Composite parent = (Composite) super.createDialogArea(container);
		
		title = new Label(parent, SWT.NONE);
		title.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		if (StringUtils.isNotBlank(responseText)) {
			title.setText("Es ist folgendes Problem aufgetreten");
			browser = new Browser(parent, SWT.BORDER);
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.widthHint = 600;
			gd.heightHint = 600;
			browser.setLayoutData(gd);
			browser.setText(responseText);
		}
		return parent;
	}
	
	@Override
	protected Control createButtonBar(Composite parent){
		Composite composite = new Composite(parent, SWT.NONE);
		GridData data =
			new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_CENTER);
		data.exclude = true;
		composite.setLayoutData(data);
		return composite;
	}
	
	public static void openMedapp(String location){
		Display.getDefault().asyncExec(() -> {
			try {
				IWorkbenchBrowserSupport browserSupport =
					PlatformUI.getWorkbench().getBrowserSupport();
				IWebBrowser externalBrowser = browserSupport.getExternalBrowser();
				externalBrowser.openURL(new URI(location).toURL());
			} catch (Exception ex) {
				LoggerFactory.getLogger(ResponseDialog.class).error("Error open medapp url", ex);
			}
		});
	}
}
