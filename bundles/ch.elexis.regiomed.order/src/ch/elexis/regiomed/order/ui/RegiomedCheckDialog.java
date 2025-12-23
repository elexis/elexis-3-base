package ch.elexis.regiomed.order.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.core.ui.icons.Images;
import ch.elexis.regiomed.order.messages.Messages;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse;
import ch.elexis.regiomed.order.model.RegiomedOrderResponse.ArticleResult;

public class RegiomedCheckDialog extends Dialog {

	private final RegiomedOrderResponse response;
	private final Set<String> removedIdentifiers = new HashSet<>();
	private int remainingErrors = 0;

	public RegiomedCheckDialog(Shell parentShell, RegiomedOrderResponse response) {
		super(parentShell);
		this.response = response;
		this.remainingErrors = response.articlesNOK;
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.RegiomedCheckDialog_Title);
		newShell.setImage(Images.IMG_LOGO.getImage());
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		area.setLayout(new GridLayout(1, false));

		Browser browser = new Browser(area, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		browser.addLocationListener(new LocationListener() {
			@Override
			public void changed(LocationEvent event) {
			}

			@Override
			public void changing(LocationEvent event) {
				if (event.location != null && event.location.startsWith("regiomed:remove:")) { //$NON-NLS-1$
					String[] parts = event.location.split(":", -1); //$NON-NLS-1$
					if (parts.length >= 4) {
						String pharma = parts[2];
						String ean = parts[3];
						if (StringUtils.isBlank(pharma))
							pharma = "0"; //$NON-NLS-1$
						if (StringUtils.isBlank(ean))
							ean = "0"; //$NON-NLS-1$
						String key = pharma + ":" + ean; //$NON-NLS-1$
						if (!removedIdentifiers.contains(key)) {
							removedIdentifiers.add(key);
							if (isErrorArticle(pharma, ean)) {
								remainingErrors--;
								updateOkButtonState();
							}
						}
					}
					event.doit = false;
				}
			}
		});

		String html = RegiomedCheckTemplate.generateHtml(response);
		browser.setText(html);

		return area;
	}

	public List<ArticleResult> getDeletedArticles() {
		List<ArticleResult> deletedItems = new ArrayList<>();

		if (response.articles == null)
			return deletedItems;

		// Wir müssen die Listen EXAKT so aufbauen wie im Template
		List<ArticleResult> okItems = response.articles.stream().filter(a -> a.success).collect(Collectors.toList());
		List<ArticleResult> nokItems = response.articles.stream().filter(a -> !a.success).collect(Collectors.toList());

		// 1. Prüfen, welche NOK-Zeilen gelöscht wurden
		for (int i = 0; i < nokItems.size(); i++) {
			String rowId = "nok_row_" + i;
			if (removedIdentifiers.contains(rowId)) {
				deletedItems.add(nokItems.get(i));
			}
		}

		// 2. Prüfen, welche OK-Zeilen gelöscht wurden (falls möglich)
		for (int i = 0; i < okItems.size(); i++) {
			String rowId = "ok_row_" + i;
			if (removedIdentifiers.contains(rowId)) {
				deletedItems.add(okItems.get(i));
			}
		}

		return deletedItems;
	}

	private boolean isErrorArticle(String pharmaStr, String eanStr) {
		if (response.articles == null)
			return false;
		for (ArticleResult item : response.articles) {
			String itemPharma = String.valueOf(item.pharmaCode);
			String itemEan = String.valueOf(item.eanID);
			if (StringUtils.isBlank(itemPharma) || "null".equals(itemPharma)) {
				itemPharma = "0";
			}
			if (StringUtils.isBlank(itemEan) || "null".equals(itemEan)) {
				itemEan = "0";
			}
			if (itemPharma.equals(pharmaStr) && itemEan.equals(eanStr)) {
				return !item.success;
			}
		}
		return false;
	}

	private void updateOkButtonState() {
		getShell().getDisplay().asyncExec(() -> {
			Button okBtn = getButton(IDialogConstants.OK_ID);
			if (okBtn != null && !okBtn.isDisposed()) {
				System.out.println("Test " + remainingErrors);
				okBtn.setEnabled(remainingErrors <= 0);
			}
		});
	}

	public Set<String> getRemovedIdentifiers() {
		return removedIdentifiers;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button okBtn = createButton(parent, IDialogConstants.OK_ID, Messages.RegiomedCheckDialog_OrderBinding, true);
		createButton(parent, IDialogConstants.CANCEL_ID, Messages.RegiomedCheckDialog_Cancel, false);

		if (remainingErrors > 0) {
			okBtn.setEnabled(false);
		}
	}

	@Override
	protected Point getInitialSize() {
		return new Point(950, 950);
	}
}