package at.medevit.elexis.inbox.core.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import at.medevit.elexis.inbox.core.ui.LabResultLabelProvider;
import at.medevit.elexis.inbox.core.ui.LabResultLabelProvider.LabelFields;
import ch.elexis.core.services.holder.ConfigServiceHolder;

public class InboxPreferences {
	private static final String INBOX = "inbox/"; //$NON-NLS-1$
	public static final String INBOX_LABRESULT_LBL_CHOOSEN = INBOX + "labresult/label/choosen"; //$NON-NLS-1$
	public static final String INBOX_LABRESULT_LBL_AVAILABLE = INBOX + "labresult/label/available"; //$NON-NLS-1$

	// Default choosen / available
	public static final String DEF_CHOOSEN = LabResultLabelProvider.LabelFields.LAB_VALUE_SHORT.toString() + "," //$NON-NLS-1$
			+ LabResultLabelProvider.LabelFields.LAB_VALUE_NAME.toString() + "," //$NON-NLS-1$
			+ LabResultLabelProvider.LabelFields.LAB_RESULT.toString();

	public static final String DEF_AVAILABLE = LabResultLabelProvider.LabelFields.REF_RANGE.toString() + "," //$NON-NLS-1$
			+ LabResultLabelProvider.LabelFields.ORIGIN.toString() + "," //$NON-NLS-1$
			+ LabResultLabelProvider.LabelFields.DATE.toString();

	private static List<LabelFields> choosenLabels;

	private static void loadChoosenLabel() {
		String[] labels = ConfigServiceHolder.getUser(INBOX_LABRESULT_LBL_CHOOSEN, DEF_CHOOSEN).split(","); //$NON-NLS-1$
		choosenLabels = new ArrayList<LabResultLabelProvider.LabelFields>();

		for (String label : labels) {
			LabelFields lblField = LabelFields.getEnum(label);
			if (lblField != null) {
				choosenLabels.add(lblField);
			}
		}
	}

	public static List<LabelFields> getChoosenLabel() {
		if (choosenLabels == null || choosenLabels.isEmpty()) {
			loadChoosenLabel();
		}
		return choosenLabels;
	}
}
