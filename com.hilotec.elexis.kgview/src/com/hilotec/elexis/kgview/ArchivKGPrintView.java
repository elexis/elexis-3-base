package com.hilotec.elexis.kgview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.ui.text.ITextPlugin;
import ch.elexis.core.ui.text.TextContainer;
import ch.elexis.data.Anwender;
import ch.elexis.data.Brief;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Kontakt;
import ch.elexis.data.Patient;
import ch.rgw.tools.StringTool;

import com.hilotec.elexis.kgview.data.KonsData;

public class ArchivKGPrintView extends ViewPart {
	public static final String ID = "com.hilotec.elexis.kgview.ArvchivKGPrintView";
	
	Brief brief;
	TextContainer text;
	
	@Override
	public void createPartControl(Composite parent){
		text = new TextContainer(getViewSite());
		text.getPlugin().createContainer(parent, new ITextPlugin.ICallback() {
			@Override
			public void save(){}
			
			@Override
			public boolean saveAs(){
				return false;
			}
		});
	}
	
	@Override
	public void setFocus(){}
	
	/**
	 * Konsultation in Dokument einfuegen
	 * 
	 * TODO: Code-duplikation mit ArchivKG, sollte zusammengelegt werden.
	 */
	private Object processKonsultation(Konsultation k, ITextPlugin tp, Object pos){
		KonsData kd = KonsData.load(k);
		int typ = kd.getKonsTyp();
		String styp;
		if (typ == KonsData.KONSTYP_TELEFON) {
			styp = "Telefon";
		} else if (typ == KonsData.KONSTYP_HAUSBESUCH) {
			styp = "Hausbesuch";
		} else {
			styp = "Konsultation";
		}
		tp.setStyle(SWT.BOLD);
		pos = tp.insertText(pos, styp, SWT.LEFT);
		pos = tp.insertText(pos, " " + k.getDatum() + " " + kd.getKonsBeginn(), SWT.LEFT);
		tp.setStyle(SWT.NORMAL);
		
		if (k.getFall() != null) {
			pos = tp.insertText(pos, " " + k.getFall().getAbrechnungsSystem(), SWT.LEFT);
		}
		
		String sAutor = "";
		Anwender autor = kd.getAutor();
		if (autor != null) {
			sAutor = autor.getKuerzel();
			if (StringTool.isNothing(sAutor))
				sAutor = autor.getLabel();
		}
		pos = tp.insertText(pos, " (" + sAutor + ")\n", SWT.LEFT);
		
		pos =
			addParagraph("Jetziges Leiden", kd.getJetzigesLeiden(), kd.getJetzigesLeidenICPC(), tp,
				pos);
		pos = addParagraph("Status", kd.getLokalstatus(), tp, pos);
		pos = addParagraph("Röntgen", kd.getRoentgen(), tp, pos);
		pos = addParagraph("EKG", kd.getEKG(), tp, pos);
		pos = addParagraph("Diagnose", kd.getDiagnose(), kd.getDiagnoseICPC(), tp, pos);
		pos = addParagraph("Therapie", kd.getTherapie(), tp, pos);
		pos = addParagraph("Verlauf", kd.getVerlauf(), tp, pos);
		pos = addParagraph("Procedere", kd.getProzedere(), kd.getProzedereICPC(), tp, pos);
		pos = tp.insertText(pos, "\n", 0);
		
		return pos;
	}
	
	private Object addParagraph(String titel, String text, ITextPlugin tp, Object pos){
		return addParagraph(titel, text, null, tp, pos);
	}
	
	private Object addParagraph(String titel, String text, String icpc, ITextPlugin tp, Object pos){
		if ((text == null || text.isEmpty()) && (icpc == null || icpc.isEmpty()))
			return pos;
		
		tp.setStyle(SWT.BOLD);
		pos = tp.insertText(pos, titel, SWT.LEFT);
		tp.setStyle(SWT.NORMAL);
		pos = tp.insertText(pos, "\n", SWT.LEFT);
		
		if (icpc != null && !icpc.isEmpty())
			pos = tp.insertText(pos, "ICPC: " + icpc.replace(",", ", ") + "\n", SWT.LEFT);
		
		pos = tp.insertText(pos, text + "\n\n", SWT.LEFT);
		return pos;
	}
	
	public void doPrint(Konsultation kons, Kontakt adressat, boolean reversed){
		brief = text.createFromTemplateName(kons, "ArchivKG", Brief.UNKNOWN, adressat, "Archiv-KG");
		ITextPlugin tp = text.getPlugin();
		Patient pat = kons.getFall().getPatient();
		
		Object pos = tp.insertText("[ArchivKG]", "", SWT.LEFT);
		;
		for (Konsultation k : ArchivKG.getKonsultationen(pat, reversed)) {
			pos = processKonsultation(k, tp, pos);
		}
		brief.save(tp.storeToByteArray(), tp.getMimeType());
		tp.print(null, null, true);
	}
}
