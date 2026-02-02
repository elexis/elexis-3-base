package at.medevit.elexis.aerztekasse.ui.dialog;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.wb.swt.ResourceManager;

import at.medevit.elexis.aerztekasse.ui.preferences.AerztekassePreferences;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;

public class MissingSettingsDialog extends TitleAreaDialog {
	public MissingSettingsDialog(Shell parentShell) {
		super(parentShell);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout(1, false));
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		Label lblSetNow = new Label(ret, SWT.NONE);
		lblSetNow.setText(ch.elexis.core.l10n.Messages.MissingSettingsDlg_DefineNow);

		Hyperlink openPrefsLink = new Hyperlink(ret, SWT.NONE);
		openPrefsLink.setText(ch.elexis.core.l10n.Messages.MissingSettingsDlg_Link);
		openPrefsLink.setForeground(UiDesk.getColorRegistry().get(UiDesk.COL_BLUE));
		openPrefsLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				PreferenceDialog preferenceDialog = PreferencesUtil.createPreferenceDialogOn(getParentShell(),
						AerztekassePreferences.ID, null, null);
				preferenceDialog.open();
			}
		});
		return ret;
	}

	@Override
	public void create() {
		super.create();
		setTitle(ch.elexis.core.l10n.Messages.MissingSettingsDlg_Title);
		setMessage(ch.elexis.core.l10n.Messages.MissingSettingsDlg_Message);
		setTitleImage(ResourceManager.getPluginImage("at.medevit.elexis.aerztekasse.ui", "rsc/aerztekasse_logo.png"));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
	}

}
