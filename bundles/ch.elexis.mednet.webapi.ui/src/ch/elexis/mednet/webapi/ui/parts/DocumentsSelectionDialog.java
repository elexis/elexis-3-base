package ch.elexis.mednet.webapi.ui.parts;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.core.model.IDocument;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.documents.composites.DocumentsSelectionComposite;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import ch.elexis.mednet.webapi.core.messages.Messages;
import ch.elexis.mednet.webapi.ui.handler.DocumentRemovalListener;

public class DocumentsSelectionDialog extends TitleAreaDialog {

	private DocumentsSelectionComposite attachmentsSelection;
	private List<IDocument> selectedDocuments = new ArrayList<>();
	private AttachmentsComposite attachments;
	private String attachmentsString;
	private String documentsString;
	private String toString = StringUtils.EMPTY;
	private Button epdCheckbox;
	private boolean isEpdSelected = false;

	public DocumentsSelectionDialog(Shell parentShell) {
		super(parentShell);
		setShellStyle(SWT.RESIZE);
		CoreUiUtil.injectServices(this);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		getShell().setMinimumSize(800, 500);

		Composite area = (Composite) super.createDialogArea(parent);
		area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		setTitle(Messages.DocumentsSelectionDialog_selectionTitle);

		Label lbl = new Label(container, SWT.HORIZONTAL);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		lbl = new Label(container, SWT.NONE);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		lbl.setText(Messages.DocumentsSelectionDialog_doubleClickToAttach);

		Text searchField = new Text(container, SWT.BORDER);
		searchField.setMessage(Messages.DocumentsSelectionDialog_search);
		searchField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		attachmentsSelection = new DocumentsSelectionComposite(container, SWT.NONE);
		attachmentsSelection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		attachmentsSelection.setPatient(ContextServiceHolder.get().getActivePatient().orElse(null));

		attachmentsSelection.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (selectedDocuments.size() < 3) {
					if (event.getSelection() instanceof IStructuredSelection && !event.getSelection().isEmpty()) {
						IDocument selectedDocument = (IDocument) ((IStructuredSelection) event.getSelection())
								.getFirstElement();
						attachments.addDocument(selectedDocument);
						selectedDocuments.add(selectedDocument);
					}
				} else {
					MessageDialog.openWarning(null, Messages.DocumentsSelectionDialog_limitReachedTitle,
							Messages.DocumentsSelectionDialog_limitReachedMessage);
				}
			}
		});

		searchField.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String searchText = searchField.getText();
				attachmentsSelection.setFilter(searchText);
			}
		});

		epdCheckbox = new Button(container, SWT.CHECK);
		epdCheckbox.setText(Messages.DocumentsSelectionDialog_epdCheckbox);
		epdCheckbox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		attachments = new AttachmentsComposite(container, SWT.NONE);
		attachments.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 2, 1));
		attachments.setAttachments(attachmentsString);
		attachments.setDocuments(documentsString);
		attachments.setPostfix(toString);
		attachments.setDocumentRemovalListener(new DocumentRemovalListener() {
			@Override
			public void documentRemoved(IDocument document) {
				selectedDocuments.removeIf(doc -> doc.getId().equals(document.getId()));
			}
		});

		return area;
	}

	@Override
	protected void okPressed() {
		isEpdSelected = epdCheckbox.getSelection();
		super.okPressed();
	}

	public List<IDocument> getSelectedDocuments() {
		return selectedDocuments;
	}

	public boolean isEpdCheckboxSelected() {
		return isEpdSelected;
	}
}