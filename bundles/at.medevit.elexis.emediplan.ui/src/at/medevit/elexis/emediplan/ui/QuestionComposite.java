package at.medevit.elexis.emediplan.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class QuestionComposite extends Composite {
	private Button btn;
	private boolean defaulSelection = false;
	private boolean questionConfirmed = true;

	public QuestionComposite() {
		super(Display.getCurrent().getActiveShell(), SWT.NONE);
		setLayout(new GridLayout(1, false));
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		setVisible(false);

	}

	public void createQuestionText(String questionText) {
		if (questionText != null) {
			btn = new Button(this, SWT.CHECK);
			btn.setText(questionText);
			btn.setSelection(defaulSelection);
			if (defaulSelection) {
				btn.setEnabled(false);
			}
		}
	}

	public void setDefaulSelection(boolean defaulSelection) {
		this.defaulSelection = defaulSelection;
	}

	@Override
	public void update() {
		this.questionConfirmed = btn != null ? btn.getSelection() : defaulSelection;
		if (!this.isDisposed()) {
			super.update();
		}
	}

	public boolean isQuestionConfirmed() {
		return questionConfirmed;
	}
}