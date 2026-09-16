package ch.elexis.hl7.message.ui.preference;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.e4.jface.preference.URIFieldEditorComposite;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;

public class MessagePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	private TableViewer receiverViewer;

	private BooleanFieldEditor bStoreGlobal;

	private URIFieldEditorComposite outputDirEditor;

	private Composite receiverArea;

	public MessagePreferencePage() {
		super(GRID);
		setPreferenceStore(new ConfigServicePreferenceStore(Scope.LOCAL));
	}

	@Override
	public void init(IWorkbench workbench) {

	}

	@Override
	protected void createFieldEditors() {
		bStoreGlobal = new BooleanFieldEditor(PreferenceUtil.PREF_FILESYSTEM_OUTPUTDIR_GLOBAL,
				ch.elexis.core.l10n.Messages.PreferencesServer_storeFSGlobal, getFieldEditorParent()) {
			@Override
			protected void fireValueChanged(String property, Object oldValue, Object newValue) {
				super.fireValueChanged(property, oldValue, newValue);
				if (FieldEditor.VALUE.equals(property)) {
					boolean global = Boolean.TRUE.equals(newValue);
					ConfigServiceHolder.setGlobal(PreferenceUtil.PREF_FILESYSTEM_OUTPUTDIR_GLOBAL, global);
					updateOutputDirStore(global);
				}
			}
		};
		addField(bStoreGlobal);

		outputDirEditor = new URIFieldEditorComposite(PreferenceUtil.PREF_FILESYSTEM_OUTPUTDIR, "Ausgabe Verzeichnis",
				getFieldEditorParent(), SWT.NONE);
		outputDirEditor.setEmptyStringAllowed(true);
		Composite area = new Composite(getFieldEditorParent(), SWT.NONE);
		receiverArea = area;
		area.setLayoutData(new GridData(GridData.FILL_BOTH));
		area.setLayout(new GridLayout(1, false));

		Label lbl = new Label(area, SWT.NONE);
		lbl.setText("Empfänger Konfiguration");
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		ToolBarManager toolbar = new ToolBarManager();
		toolbar.add(new AddReceiverAction());
		toolbar.add(new RemoveReceiverAction());
		ToolBar toolbarControl = toolbar.createControl(area);
		toolbarControl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));

		receiverViewer = new TableViewer(area, SWT.BORDER);
		receiverViewer.setContentProvider(ArrayContentProvider.getInstance());
		receiverViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof Receiver) {
					return ((Receiver) element).getApplication() + " - " + ((Receiver) element).getFacility(); //$NON-NLS-1$
				}
				return super.getText(element);
			}
		});
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.heightHint = 100;
		receiverViewer.getTable().setLayoutData(gd);
		receiverViewer.setInput(PreferenceUtil.getReceivers());
	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		bStoreGlobal.setPreferenceStore(new ConfigServicePreferenceStore(Scope.GLOBAL));
		bStoreGlobal.load();
		updateOutputDirStore(PreferenceUtil.isStoreGlobal());
		return control;
	}

	private void updateOutputDirStore(boolean global) {
		if (outputDirEditor == null || outputDirEditor.isDisposed()) {
			return;
		}
		outputDirEditor.setPreferenceStore(new ConfigServicePreferenceStore(global ? Scope.GLOBAL : Scope.LOCAL));
	}

	@Override
	protected void adjustGridLayout() {
		super.adjustGridLayout();
		if (!(getFieldEditorParent().getLayout() instanceof GridLayout)) {
			return;
		}
		int numColumns = ((GridLayout) getFieldEditorParent().getLayout()).numColumns;
		spanAllColumns(outputDirEditor, numColumns);
		spanAllColumns(receiverArea, numColumns);
	}

	private void spanAllColumns(Composite composite, int numColumns) {
		if (composite != null && !composite.isDisposed() && composite.getLayoutData() instanceof GridData) {
			((GridData) composite.getLayoutData()).horizontalSpan = numColumns;
		}
	}

	private class AddReceiverAction extends Action {
		@Override
		public ImageDescriptor getImageDescriptor() {
			return Images.IMG_NEW.getImageDescriptor();
		}

		@Override
		public void run() {
			Receiver receiver = new Receiver();
			ReceiverEditDialog dialog = new ReceiverEditDialog(getShell());
			dialog.setReceiver(receiver);
			if (dialog.open() == Window.OK) {
				PreferenceUtil.addReceiver(receiver);
				receiverViewer.setInput(PreferenceUtil.getReceivers());
			}
		}
	}

	private class RemoveReceiverAction extends Action {
		@Override
		public ImageDescriptor getImageDescriptor() {
			return Images.IMG_DELETE.getImageDescriptor();
		}

		@Override
		public void run() {
			IStructuredSelection selection = receiverViewer.getStructuredSelection();
			if (selection != null && !selection.isEmpty()) {
				Receiver selected = (Receiver) selection.getFirstElement();
				PreferenceUtil.removeReceiver(selected);
				receiverViewer.setInput(PreferenceUtil.getReceivers());
			}
		}
	}
}
