package ch.elexis.views;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;

import ch.elexis.core.ui.dialogs.base.InputDialog;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.data.ComplementaryLeistung;
import ch.elexis.data.Query;

public class ComplementarySubDetail extends Composite {

	private ComplementaryLeistung complementary;

	private ToolBar toolbar;

	private TableViewer subTable;

	public ComplementarySubDetail(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout());
		createContent();
	}

	private void createContent() {
		ToolBarManager manager = new ToolBarManager();
		manager.add(new Action() {
			@Override
			public String getToolTipText() {
				return "Neuer alternativer Leistungstext";
			}

			@Override
			public ImageDescriptor getImageDescriptor() {
				return Images.IMG_NEW.getImageDescriptor();
			}

			@Override
			public void run() {
				SubDetailDialog dialog = new SubDetailDialog(getShell(), "Neuer alternativer Leistungstext mit Wert",
						"Bitte den neuen alternativen Leistungstext und Wert eingeben.");
				if (dialog.open() == InputDialog.OK) {
					String newAlternativeText = dialog.getText();
					Integer newAlternativeValue = dialog.getValue();
					ComplementaryLeistung newComplementary = new ComplementaryLeistung(getNextSubId(),
							complementary.get(ComplementaryLeistung.FLD_CHAPTER),
							complementary.getCode(), newAlternativeText, "",
							complementary.get(ComplementaryLeistung.FLD_VALID_FROM),
							complementary.get(ComplementaryLeistung.FLD_VALID_TO));
					newComplementary.setFixedValue(newAlternativeValue);
					updateContent();
				}
			}
		});

		manager.add(new Action() {
			@Override
			public String getToolTipText() {
				return "Alternativer Leistungstext entfernen";
			}

			@Override
			public ImageDescriptor getImageDescriptor() {
				return Images.IMG_DELETE.getImageDescriptor();
			}

			@Override
			public void run() {
				ISelection selection = subTable.getSelection();
				if (selection != null && !selection.isEmpty()) {
					ComplementaryLeistung element = (ComplementaryLeistung) ((StructuredSelection) selection)
							.getFirstElement();
					element.delete();
					updateContent();
				}
			}
		});
		toolbar = manager.createControl(this);
		toolbar.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));

		subTable = new TableViewer(this, SWT.BORDER);
		subTable.getTable().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		subTable.setContentProvider(ArrayContentProvider.getInstance());
		subTable.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof ComplementaryLeistung) {
					return ((ComplementaryLeistung) element).getText() + " ("
							+ ((ComplementaryLeistung) element).getFixedValue() + ")";
				}
				return super.getText(element);
			}
		});
	}

	private void updateContent() {
		Query<ComplementaryLeistung> query = new Query<ComplementaryLeistung>(ComplementaryLeistung.class);
		query.add(ComplementaryLeistung.FLD_CODE, Query.EQUALS, complementary.getCode());
		query.add("id", Query.LIKE, complementary.getCode() + "sub%");
		subTable.setInput(query.execute());

		subTable.refresh();
	}

	private String getNextSubId() {
		Query<ComplementaryLeistung> query = new Query<ComplementaryLeistung>(ComplementaryLeistung.class);
		query.clear(true);
		query.add(ComplementaryLeistung.FLD_CODE, Query.EQUALS, complementary.getCode());
		query.add("id", Query.LIKE,
				complementary.getCode() + "sub%-" + complementary.get(ComplementaryLeistung.FLD_VALID_FROM));
		List<ComplementaryLeistung> existing = query.execute();
		Map<Integer, String> existingIndexMap = new HashMap<Integer, String>();
		for (ComplementaryLeistung complementaryLeistung : existing) {
			String id = complementaryLeistung.getId();
			String subId = id.substring(id.indexOf("sub") + 3, id.indexOf("-"));
			existingIndexMap.put(Integer.valueOf(subId), id);
		}
		Integer freeIndex = 1;
		while (existingIndexMap.containsKey(freeIndex)) {
			freeIndex++;
		}
		return complementary.getCode() + "sub" + freeIndex + "-"
				+ complementary.get(ComplementaryLeistung.FLD_VALID_FROM);
	}

	public void hide() {
		this.complementary = null;
		for (Control child : getChildren()) {
			if (child.getLayoutData() instanceof GridData) {
				((GridData) child.getLayoutData()).exclude = true;
			}
			child.setVisible(false);
		}
		getParent().layout(true, true);
	}

	public void show(ComplementaryLeistung complementary) {
		this.complementary = complementary;
		updateContent();
		for (Control child : getChildren()) {
			if (child.getLayoutData() instanceof GridData) {
				((GridData) child.getLayoutData()).exclude = false;
			}
			child.setVisible(true);
		}
		getParent().layout(true, true);
	}

	private class SubDetailDialog extends Dialog {

		private String title;

		private String message;

		private String textString;

		private Text text;

		private Integer valueInt;

		private Text value;

		protected SubDetailDialog(Shell parentShell, String dialogTitle, String dialogMessage) {
			super(parentShell);
			this.title = dialogTitle;
			message = dialogMessage;
		}

		public Integer getValue() {
			return valueInt;
		}

		public String getText() {
			return textString;
		}

		@Override
		protected void okPressed() {
			try {
				valueInt = Integer.valueOf(value.getText());
			} catch (NumberFormatException e) {
				valueInt = -1;
			}
			if (StringUtils.isNotBlank(text.getText())) {
				textString = text.getText();
				super.okPressed();
			}
		}

		@Override
		protected void configureShell(Shell shell) {
			super.configureShell(shell);
			if (title != null) {
				shell.setText(title);
			}
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			// create composite
			Composite composite = (Composite) super.createDialogArea(parent);
			// create message
			if (message != null) {
				Label label = new Label(composite, SWT.WRAP);
				label.setText(message);
				GridData data = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL
						| GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER);
				data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
				label.setLayoutData(data);
				label.setFont(parent.getFont());
			}
			text = new Text(composite, SWT.SINGLE | SWT.BORDER);
			text.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
			text.setMessage("Leistungstext");

			value = new Text(composite, SWT.SINGLE | SWT.BORDER);
			value.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
			value.setMessage("Wert");

			applyDialogFont(composite);
			return composite;
		}
	}
}
