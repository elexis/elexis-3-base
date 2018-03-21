package at.medevit.ch.artikelstamm.medcalendar.ui.provider;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.ResourceManager;

import ch.elexis.core.ui.UiDesk;

public class MedCalFilterInfoElement {
	public static Color BG_COLOR = UiDesk.getColorFromRGB("FFFACD");
	public static Image FILTER_ICON = ResourceManager.getPluginImage(
		"at.medevit.ch.artikelstamm.medcalendar", "icons/medcal.png");
	
	final String description;
	
	public MedCalFilterInfoElement(String description){
		this.description = description;
	}
	
	public String getDescription(){
		return description;
	}
	
}
