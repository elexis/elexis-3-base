package ch.elexis.buchhaltung.kassenbuch;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.tools.StringTool;

public class EditPaymentModesDialog extends TitleAreaDialog {

	public EditPaymentModesDialog(Shell shell) {
		super(shell);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Text ret = new Text(parent, SWT.MULTI | SWT.BORDER);
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		ret.setText(StringTool.join(KassenbuchEintrag.getPaymentModes(), StringUtils.LF));
		return ret;
	}

	@Override
	public void create() {
		super.create();
		setTitle("Zahlungsarten");
		setMessage("Geben Sie eine Zahlungsart pro Zeile ein");
		getShell().setText("Elexis-Kassenbuch");
	}

	@Override
	protected void okPressed() {
		String nPaymentModes = ((Text) getDialogArea()).getText().replaceAll(StringUtils.LF,
				KassenbuchEintrag.GLOBAL_CFG_SEPARATOR);
		ConfigServiceHolder.setGlobal(KassenbuchEintrag.PAYMENT_MODES,
				nPaymentModes.replaceAll(StringUtils.CR, StringUtils.EMPTY));
		super.okPressed();
	}
}
