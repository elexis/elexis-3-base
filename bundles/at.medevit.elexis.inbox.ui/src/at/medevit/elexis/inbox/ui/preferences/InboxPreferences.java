package at.medevit.elexis.inbox.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import at.medevit.elexis.inbox.ui.part.provider.IInboxElementUiProvider;
import at.medevit.elexis.inbox.ui.part.provider.InboxElementUiExtension;

public class InboxPreferences extends PreferencePage implements IWorkbenchPreferencePage {

	private List<IInboxElementUiProvider> elementsUiProviders;

	private List<ElementsUiPreferenceComposite> elementsUiPreferenceComposites;

	public InboxPreferences() {
		super("Inbox");

		InboxElementUiExtension elementsUi = new InboxElementUiExtension();

		this.elementsUiProviders = elementsUi.getProviders();
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite area = new Composite(parent, SWT.NONE);
		area.setLayoutData(new GridData(GridData.FILL_BOTH));
		area.setLayout(new GridLayout(1, true));

		elementsUiPreferenceComposites = new ArrayList<>();
		for (IInboxElementUiProvider iInboxElementUiProvider : elementsUiProviders) {
			elementsUiPreferenceComposites
					.add(new ElementsUiPreferenceComposite(iInboxElementUiProvider, area, SWT.NONE));
		}

		
		return area;
	}

	@Override
	protected void performApply() {

		super.performApply();
	}

	@Override
	public boolean performOk() {
		elementsUiPreferenceComposites.forEach(c -> c.apply());
		return super.performOk();
	}

	@Override
	public void init(IWorkbench workbench) {
	}
}
