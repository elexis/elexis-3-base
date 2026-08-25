package at.medevit.elexis.inbox.ui.preferences;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import at.medevit.elexis.inbox.model.IInboxElementService;
import at.medevit.elexis.inbox.model.IInboxElementService.Mandator;
import at.medevit.elexis.inbox.ui.part.provider.IInboxElementUiProvider;
import at.medevit.elexis.inbox.ui.part.provider.InboxElementUiExtension;
import ch.elexis.core.services.holder.ConfigServiceHolder;

public class ElementsUiPreferenceComposite extends Composite {

	private Button infoLastEncounterBtn;
	private Button infoFamilyDoctorBtn;

	private IInboxElementUiProvider provider;
	private String providerId;

	public ElementsUiPreferenceComposite(IInboxElementUiProvider iInboxElementUiProvider, Composite parent, int style) {
		super(parent, style);
		
		InboxElementUiExtension elementsUi = new InboxElementUiExtension();
		this.provider = iInboxElementUiProvider;
		this.providerId = elementsUi.getId(iInboxElementUiProvider);

		createContents();
	}

	private void createContents() {
		setLayout(new GridLayout(3, false));

		Label label = new Label(this, SWT.NONE);
		GridData gd = new GridData(SWT.LEFT, SWT.CENTER, true, true);
		gd.widthHint = 150;
		label.setLayoutData(gd);
		label.setText(provider.getObjectLabel());

		infoFamilyDoctorBtn = new Button(this, SWT.CHECK);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, true);
		infoFamilyDoctorBtn.setLayoutData(gd);
		infoFamilyDoctorBtn.setText("Info an Stammarzt (Fallback zu Mandant der letzten Konsulation)");
		infoFamilyDoctorBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				infoLastEncounterBtn.setSelection(false);
			}
		});
		infoFamilyDoctorBtn.setSelection(getCurrentValue() == Mandator.FAMILY);

		infoLastEncounterBtn = new Button(this, SWT.CHECK);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, true);
		infoLastEncounterBtn.setLayoutData(gd);
		infoLastEncounterBtn.setText("Info an Mandant der letzten Konsulation");
		infoLastEncounterBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				infoFamilyDoctorBtn.setSelection(false);
			}
		});
		infoLastEncounterBtn.setSelection(getCurrentValue() == Mandator.ENCOUNTER);

	}

	public IInboxElementService.Mandator getCurrentValue() {
		return IInboxElementService.Mandator.valueOf(
				ConfigServiceHolder.get().get(String.format(IInboxElementService.PREFERENCE_INBOX_MANDATOR, providerId),
						IInboxElementService.Mandator.ENCOUNTER.name()));

	}

	public void apply() {
		if (infoFamilyDoctorBtn.getSelection()) {
			ConfigServiceHolder.get().set(String.format(IInboxElementService.PREFERENCE_INBOX_MANDATOR, providerId),
					IInboxElementService.Mandator.FAMILY.name());
		} else {
			ConfigServiceHolder.get().set(String.format(IInboxElementService.PREFERENCE_INBOX_MANDATOR, providerId),
					IInboxElementService.Mandator.ENCOUNTER.name());
		}
	}
}
