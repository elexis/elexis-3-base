package ch.elexis.omnivore.ui.util;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.elexis.omnivore.model.util.CategoryUtil;
import ch.elexis.omnivore.ui.Messages;

public class CategorySelectDialog extends Dialog {
	private Combo combo;
	private Text textInput;
	private List<String> categories;
	private String selectedCategory;
	private String title;
	private String message;

	public CategorySelectDialog(Shell parentShell, String title, String message, List<String> categories) {
		super(parentShell);
		this.title = title;
		this.message = message;
		this.categories = categories;

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		Label label = new Label(container, SWT.NONE);
		label.setText(message);
		textInput = new Text(container, SWT.BORDER);
		textInput.setMessage(Messages.DocumentMetaDataDialog_deleteCategoryComboConfirmText);
		combo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.add(Messages.DocumentMetaDataDialog_deleteCategoryComboConfirm);
		for (String category : categories) {
			combo.add(category);
		}
		combo.select(0);

		return container;
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(title);
	}

	@Override
	protected void okPressed() {
		String userInput = textInput.getText().trim();
		if (!userInput.isEmpty()) {
			if (!categories.contains(userInput)) {
				CategoryUtil.addCategory(userInput);
			}
			selectedCategory = userInput;
		} else {

			selectedCategory = combo.getText();
		}
		super.okPressed();
	}

	public String getSelectedCategory() {
		return selectedCategory;
	}
}