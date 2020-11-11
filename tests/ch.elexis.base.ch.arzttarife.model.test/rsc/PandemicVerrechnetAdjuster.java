package ch.elexis.views;

import java.time.LocalDateTime;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import ch.elexis.core.data.interfaces.IVerrechenbar;
import ch.elexis.core.data.interfaces.IVerrechnetAdjuster;
import ch.elexis.data.Artikel;
import ch.elexis.data.Verrechnet;
import ch.rgw.tools.Money;

public class PandemicVerrechnetAdjuster implements IVerrechnetAdjuster {
	
	private LocalDateTime startOfPandemic = LocalDateTime.of(2020, 11, 2, 0, 0);
	
	@Override
	public void adjust(Verrechnet verrechnet){
		if (verrechnet.getKons().getDateTime().isAfter(startOfPandemic)) {
			IVerrechenbar verrechenbar = verrechnet.getVerrechenbar();
			if (verrechenbar instanceof Artikel) {
				Artikel artikel = (Artikel) verrechenbar;
				if ("406".equals(artikel.getCodeSystemCode()) && "3028".equals(artikel.getCode())) {
					Display.getDefault().asyncExec(() -> {
						MessageDialog.openWarning(Display.getDefault().getActiveShell(),
							"Pandemie Tarif",
							"Für Covid Verrechnungen ab dem 2.11.2020 sollte der Pandemie Tarif verwendet werden.");
					});
				}
			}
		}
	}
	
	@Override
	public void adjustGetNettoPreis(Verrechnet verrechnet, Money price){
		// TODO Auto-generated method stub
		
	}
	
}
