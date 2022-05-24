package at.medevit.elexis.impfplan.ui.dialogs;

import java.util.Calendar;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.ResourceManager;

import at.medevit.elexis.impfplan.model.po.Vaccination;
import at.medevit.elexis.impfplan.ui.VaccinationEffectCheckboxTreeViewer;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.proposals.PersistentObjectContentProposal;
import ch.elexis.core.ui.proposals.PersistentObjectProposalProvider;
import ch.elexis.data.Mandant;
import ch.elexis.data.Patient;
import ch.rgw.tools.TimeTool;

public class EditVaccinationDialog extends TitleAreaDialog {
	private Vaccination vacc;

	private Text txtAdministrator;
	private String administratorString;
	private Text txtLotNo;
	private DateTime dateTimeDOA;
	private VaccinationEffectCheckboxTreeViewer vect;

	public EditVaccinationDialog(Shell parentShell, Vaccination vacc) {
		super(parentShell);
		this.vacc = vacc;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Angaben zur Impfung ändern");
		setTitleImage(
				ResourceManager.getPluginImage("at.medevit.elexis.impfplan.ui", "rsc/icons/vaccination_logo.png"));
		Patient sp = ElexisEventDispatcher.getSelectedPatient();
		setMessage((sp != null) ? sp.getLabel() : "missing patient name"); //$NON-NLS-1$

		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		Label lblAdministratingContact = new Label(container, SWT.NONE);
		lblAdministratingContact.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAdministratingContact.setText("Verabreicht von");

		txtAdministrator = new Text(container, SWT.BORDER);
		administratorString = vacc.get(Vaccination.FLD_ADMINISTRATOR);
		txtAdministrator.setText(vacc.getAdministratorLabel());
		txtAdministrator.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				administratorString = txtAdministrator.getText();
			}
		});
		txtAdministrator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		PersistentObjectProposalProvider<Mandant> mopp = new PersistentObjectProposalProvider<Mandant>(Mandant.class) {
			@Override
			public String getLabelForObject(Mandant a) {
				return a.getMandantLabel();
			}
		};

		ContentProposalAdapter mandatorProposalAdapter = new ContentProposalAdapter(txtAdministrator,
				new TextContentAdapter(), mopp, null, null);
		mandatorProposalAdapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		mandatorProposalAdapter.addContentProposalListener(new IContentProposalListener() {

			@Override
			public void proposalAccepted(IContentProposal proposal) {
				PersistentObjectContentProposal<Mandant> prop = (PersistentObjectContentProposal<Mandant>) proposal;
				administratorString = prop.getPersistentObject().storeToString();
			}
		});

		Label lblLotNo = new Label(container, SWT.NONE);
		lblLotNo.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLotNo.setText("Lot-Nr");

		txtLotNo = new Text(container, SWT.BORDER);
		txtLotNo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		txtLotNo.setText(vacc.getLotNo());

		Label lblVerabreichungsdatum = new Label(container, SWT.NONE);
		lblVerabreichungsdatum.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblVerabreichungsdatum.setAlignment(SWT.RIGHT);
		lblVerabreichungsdatum.setText("Verabreichungsdatum");

		dateTimeDOA = new DateTime(container, SWT.BORDER);
		TimeTool doa = vacc.getDateOfAdministration();
		dateTimeDOA.setDate(doa.get(Calendar.YEAR), doa.get(Calendar.MONTH), doa.get(Calendar.DAY_OF_MONTH));

		if (vacc.get(Vaccination.FLD_ARTIKEL_REF).length() == 0) {
			// nachtragsimpfung mit formal unbekanntem artikel
			Label lblImpfungGegen = new Label(container, SWT.NONE);
			lblImpfungGegen.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
			lblImpfungGegen.setAlignment(SWT.RIGHT);
			lblImpfungGegen.setText("Impfung gegen");

			vect = new VaccinationEffectCheckboxTreeViewer(container, SWT.BORDER,
					vacc.get(Vaccination.FLD_VACC_AGAINST));
			vect.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		}

		return area;
	}

	@Override
	protected void okPressed() {
		vacc.setAdministratorString(administratorString);
		vacc.setLotNo(txtLotNo.getText());

		Calendar instance = Calendar.getInstance();
		instance.set(Calendar.DAY_OF_MONTH, dateTimeDOA.getDay());
		instance.set(Calendar.MONTH, dateTimeDOA.getMonth());
		instance.set(Calendar.YEAR, dateTimeDOA.getYear());
		vacc.setDateOfAdministration(instance.getTime());

		if (vect != null) {
			vacc.setVaccAgainst(vect.getCheckedElementsAsCommaSeparatedString());
		}

		super.okPressed();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}
}
