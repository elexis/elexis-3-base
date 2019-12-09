package ch.elexis.views;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.jdom.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.medevit.elexis.tarmed.model.TarmedJaxbUtil;
import ch.elexis.TarmedRechnung.XMLExporter;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.interfaces.IRnOutputter;
import ch.elexis.core.ui.text.ITextPlugin;
import ch.elexis.core.ui.text.MimeTypeUtil;
import ch.elexis.core.ui.text.TextContainer;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Brief;
import ch.elexis.data.Query;
import ch.elexis.data.Rechnung;
import ch.elexis.data.RnStatus;
import ch.elexis.tarmed.printer.Complementary44Printer;
import ch.elexis.tarmed.printer.ComplementaryTemplateRequirement;

public class ComplementaryPrintView extends ViewPart {
	public static final String ID = "ch.elexis.arzttarife_ch.complementaryprintview";
	
	private static Logger logger = LoggerFactory.getLogger(ComplementaryPrintView.class);
	
	TextContainer text;
	
	public ComplementaryPrintView(){
		
	}
	
	@Override
	public void createPartControl(final Composite parent){
		text = new TextContainer(getViewSite());
		text.getPlugin().createContainer(parent, new ITextPlugin.ICallback() {
			
			@Override
			public void save(){
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public boolean saveAs(){
				// TODO Auto-generated method stub
				return false;
			}
		});
		text.getPlugin().setParameter(ITextPlugin.Parameter.NOUI);
	}
	
	@Override
	public void setFocus(){
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Druckt die Rechnung auf eine Vorlage, deren Ränder alle auf 0.5cm eingestellt sein müssen,
	 * und die unterhalb von 170 mm leer ist. (Papier mit EZ-Schein wird erwartet) Zweite und
	 * Folgeseiten müssen gem Tarmedrechnung formatiert sein.
	 * 
	 * @param rn
	 *            die Rechnung
	 * @param saveFile
	 *            Filename für eine XML-Kopie der Rechnung oder null: Keine Kopie
	 * @param withForms
	 * @param monitor
	 * @return
	 */
	public boolean doPrint(final Rechnung rn, final IRnOutputter.TYPE rnType,
		final String saveFile, final boolean withESR, final boolean withForms,
		final boolean doVerify, final IProgressMonitor monitor){
		XMLExporter xmlex = new XMLExporter();
		Document xmlRn = xmlex.doExport(rn, saveFile, rnType, doVerify);
		if (rn.getStatus() == RnStatus.FEHLERHAFT) {
			return false;
		}
		// check if we have all req. text templates
		initializeRequiredTemplates();
		
		// complementary starts with 4.4 tarmed xml
		if (TarmedJaxbUtil.getXMLVersion(xmlRn).equals("4.4")) {
			Complementary44Printer xmlPrinter = new Complementary44Printer(text);
			return xmlPrinter.doPrint(rn, xmlRn, rnType, saveFile, withESR, withForms, doVerify,
				monitor);
		} else {
			SWTHelper.showError("Fehler beim Drucken",
				"Die Rechnung ist in keinem gültigen XML Format");
			rn.addTrace(Rechnung.REJECTED, "XML Format");
			return false;
		}
		
	}
	
	private void initializeRequiredTemplates(){
		if (!testTemplate(ComplementaryTemplateRequirement.TT_COMPLEMENTARY_S1)) {
			initializeTemplate(ComplementaryTemplateRequirement.TT_COMPLEMENTARY_S1);
		}
		if (!testTemplate(ComplementaryTemplateRequirement.TT_COMPLEMENTARY_S2)) {
			initializeTemplate(ComplementaryTemplateRequirement.TT_COMPLEMENTARY_S2);
		}
	}
	
	private void initializeTemplate(String name){
		String templateUrl = getTemplateUrl(name);
		if(templateUrl != null) {
			byte[] content = downloadTempalte(templateUrl);
			if (content != null && content.length > 0) {
				Brief template = new Brief(name, null, CoreHub.getLoggedInContact(), null, null, Brief.TEMPLATE);
				template.save(content, text.getPlugin().getMimeType());
				// all tarmed templates are sys templates
				template.set(Brief.FLD_KONSULTATION_ID, Brief.SYS_TEMPLATE);
			}
		}
	}
	
	private byte[] downloadTempalte(String templateUrl){
		BufferedInputStream in = null;
		ByteArrayOutputStream bout = null;
		try {
			in = new BufferedInputStream(new URL(templateUrl).openStream());
			bout = new ByteArrayOutputStream();
			
			final byte data[] = new byte[1024];
			int count;
			while ((count = in.read(data, 0, 1024)) != -1) {
				bout.write(data, 0, count);
			}
		} catch (IOException e) {
			logger.warn("Could not dowload template from [" + templateUrl + "]", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					// ignore
				}
				try {
					if(bout!=null) {
						bout.close();
					}
				} catch (IOException e) {
					// ignore
				}
			}
		}
		return bout != null ? bout.toByteArray() : null;
	}
	
	private String getTemplateUrl(String name){
		if (isWord()) {
			switch (name) {
			case ComplementaryTemplateRequirement.TT_COMPLEMENTARY_S1:
				return "https://medelexis.ch/uploads/media/TR59_S1.docx";
			case ComplementaryTemplateRequirement.TT_COMPLEMENTARY_S2:
				return "https://medelexis.ch/uploads/media/TR59_S2.docx";
			default:
				break;
			}
		} else {
			switch (name) {
			case ComplementaryTemplateRequirement.TT_COMPLEMENTARY_S1:
				return "https://medelexis.ch/uploads/media/TR59_S1.odt";
			case ComplementaryTemplateRequirement.TT_COMPLEMENTARY_S2:
				return "https://medelexis.ch/uploads/media/TR59_S2.odt";
			default:
				break;
			}
		}
		return null;
	}
	
	private boolean isWord(){
		return text.getPlugin().getMimeType().equals(MimeTypeUtil.MIME_TYPE_MSWORD);
	}
	
	private boolean testTemplate(String name){
		Query<Brief> qbe = new Query<Brief>(Brief.class);
		qbe.add(Brief.FLD_TYPE, Query.EQUALS, Brief.TEMPLATE);
		qbe.and();
		qbe.add(Brief.FLD_SUBJECT, Query.EQUALS, name);
		List<Brief> list = qbe.execute();
		if ((list == null) || (list.size() == 0)) {
			return false;
		}
		return true;
	}
}
