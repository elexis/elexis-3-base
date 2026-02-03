package at.medevit.elexis.aerztekasse.ui.preferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import at.medevit.elexis.aerztekasse.core.IAerztekasseService;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.core.ui.preferences.inputs.ComboFieldEditor;
import ch.elexis.core.ui.preferences.inputs.PasswordFieldEditor;

public class AerztekassePreferences extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public static final String CFG_OUTPUTDIR = "aerztekasse/outputdir";

	public static final String ID = "at.medevit.elexis.aerztekasse.ui.preferences";

	private String[] deploymentLevels;

	public AerztekassePreferences() {
		super(GRID);
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.GLOBAL));

		// only provide deployment level selection if running snapshot mode
		boolean snapshotMode = System.getProperty(IAerztekasseService.AERZTEKASSE_SNAPSHOT_MODE) != null;
		if (snapshotMode) {
			deploymentLevels = new String[] { "PREPROD", "PROD" };
		}
	}

	@Override
	protected void createFieldEditors() {
		// archiv directory
		addField(new DirectoryFieldEditor(IAerztekasseService.cfgArchiveDir,
				ch.elexis.core.l10n.Messages.Core_ArchiveDir, getFieldEditorParent()));

		// receive directory
		addField(new DirectoryFieldEditor(IAerztekasseService.cfgErrorDir,
				ch.elexis.core.l10n.Messages.Core_Error_Directory, getFieldEditorParent()));

		// username
		addField(new StringFieldEditor(IAerztekasseService.cfgUsername, ch.elexis.core.l10n.Messages.Core_Username,
				getFieldEditorParent()));
		// password
		addField(new PasswordFieldEditor(IAerztekasseService.cfgPassword,
				ch.elexis.core.l10n.Messages.Core_Password, getFieldEditorParent()));

		Label separator = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
		separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
				((GridLayout) getFieldEditorParent().getLayout()).numColumns + 1, 1));

		Set<IContact> billerSet = getBillerSet();
		ArrayList<IContact> sortedBillerList = new ArrayList<IContact>(billerSet);
		Collections.sort(sortedBillerList, new Comparator<IContact>() {
			@Override
			public int compare(IContact o1, IContact o2) {
				IContact r1 = o1;
				IContact r2 = o2;
				return getLabel(r1).compareToIgnoreCase(getLabel(r2));
			}

		});

		for (IContact biller : sortedBillerList) {
			// contractor
			addField(new StringFieldEditor(IAerztekasseService.cfgAccount + "/" + biller.getId(),
					ch.elexis.core.l10n.Messages.Prefs_Contractor + " " + getLabel(biller), getFieldEditorParent()));
		}

		// add deployment level selection if running in snapshot mode
		if (deploymentLevels != null) {
			separator = new Label(getFieldEditorParent(), SWT.SEPARATOR | SWT.HORIZONTAL);
			separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false,
					((GridLayout) getFieldEditorParent().getLayout()).numColumns + 1, 1));

			ComboFieldEditor cmbDeployLevel = new ComboFieldEditor(IAerztekasseService.cfgDeploymentLevel,
					ch.elexis.core.l10n.Messages.Prefs_DeplymentLevel, deploymentLevels, getFieldEditorParent());
			addField(cmbDeployLevel);
		}
	}

	private String getLabel(IContact contact) {
		StringBuilder sb = new StringBuilder();
		sb.append(contact.getDescription1()).append(StringUtils.SPACE)
				.append(StringUtils.defaultString(contact.getDescription2()));
		if (!StringUtils.isBlank(contact.getDescription3())) {
			sb.append("(").append(contact.getDescription3()).append(")"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return sb.toString();
	}

	private Set<IContact> getBillerSet() {
		HashSet<IContact> ret = new HashSet<>();
		CoreModelServiceHolder.get().getQuery(IMandator.class).execute().stream().filter(m -> m.isActive())
				.map(m -> m.getBiller()).forEach(c -> ret.add(c));
		return ret;
	}
}
