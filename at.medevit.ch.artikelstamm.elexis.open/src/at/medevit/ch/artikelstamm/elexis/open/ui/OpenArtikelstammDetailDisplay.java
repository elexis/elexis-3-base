package at.medevit.ch.artikelstamm.elexis.open.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;

import at.medevit.ch.artikelstamm.elexis.common.ui.DetailDisplay;
import at.medevit.ch.artikelstamm.ui.DetailComposite;

public class OpenArtikelstammDetailDisplay extends DetailDisplay {
	
	public void addAdditionalInformation(DetailComposite dc){
		addBlackBoxReasonInformation(dc);
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	private void addBlackBoxReasonInformation(DetailComposite dc){
		Group grpBlackBoxReason = new Group(dc, SWT.NONE);
		grpBlackBoxReason.setText("Disclaimer");
		grpBlackBoxReason.setLayout(new GridLayout(1, false));
		grpBlackBoxReason.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Label lblDisclaimer = new Label(grpBlackBoxReason, SWT.WRAP);
		lblDisclaimer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblDisclaimer.setFont(SWTResourceManager.getFont("Lucida Grande", 11, SWT.BOLD));
		lblDisclaimer
			.setText("Sie benutzen die offene Version des Artikelstammes bei der NICHT sämtliche Informationen zu einem Artikel verfügbar sind. "
				+ "Beachten Sie also dass, obwohl nicht angezeigt, fehlende Flags und Werte für den Artikel zutreffen können. "
				+ "Weitere Informationen unter http://artikelstamm.elexis.info");
	};
}
