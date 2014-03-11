package com.hilotec.elexis.kgview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.ScrolledFormText;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.data.events.Heartbeat.HeartListener;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.Messages;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.data.Anwender;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

import com.hilotec.elexis.kgview.data.KonsData;

/**
 * Helper-Klasse fuers automatische scrollen mit anpassbarer Geschwindigkeit.
 */
class ScrollHelper implements KeyListener, DisposeListener, FocusListener {
	enum Direction {
		UP, DOWN
	};
	
	private ScrolledFormText comp;
	private Direction dir;
	private Timer timer;
	private final int scPeriod;
	private final int scDistUp;
	private final int scDistDown;
	
	public ScrollHelper(ScrolledFormText comp){
		this.comp = comp;
		
		FormText ft = comp.getFormText();
		ft.addDisposeListener(this);
		ft.addKeyListener(this);
		comp.addKeyListener(this);
		ft.addFocusListener(this);
		
		scPeriod = Preferences.getArchivKGScrollPeriod();
		scDistUp = Preferences.getArchivKGScrollDistUp();
		scDistDown = Preferences.getArchivKGScrollDistDown();
	}
	
	/**
	 * Automatisches scrollen starten. Als Richtung wird 'dir' verwendet.
	 */
	private void start(){
		if (timer != null)
			return;
		
		TimerTask tt = new TimerTask() {
			@Override
			public void run(){
				UiDesk.asyncExec(new Runnable() {
					public void run(){
						tickInGUI();
					}
				});
			}
		};
		
		timer = new Timer();
		timer.scheduleAtFixedRate(tt, 0, scPeriod);
	}
	
	/**
	 * Automatisches scrollen stoppen.
	 */
	private void stop(){
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	
	/**
	 * Wird bei jedem Timer-Tick aufgerufen, scrollt um die festgelegte Distanz.
	 */
	public void tickInGUI(){
		Point p = comp.getOrigin();
		Point q = comp.getContent().getSize();
		
		if (dir == Direction.DOWN && p.y < q.y) {
			p.y = Math.min(q.y, p.y + scDistDown);
		} else if (dir == Direction.UP && p.y > 0) {
			p.y = Math.max(0, p.y - scDistUp);
		}
		comp.setOrigin(p);
	}
	
	/*
	 * Keyboard events
	 */
	
	/**
	 * Prueft ob der Keycode fuer hoch scrollen benutzt wird.
	 */
	private boolean isUp(int kc){
		return (kc == SWT.ARROW_UP || kc == SWT.PAGE_UP);
	}
	
	/**
	 * Prueft ob der Keycode fuer runter scrollen benutzt wird.
	 */
	private boolean isDown(int kc){
		return (kc == SWT.ARROW_DOWN || kc == SWT.PAGE_DOWN);
	}
	
	public void keyReleased(KeyEvent e){
		if (isUp(e.keyCode) || isDown(e.keyCode)) {
			stop();
		}
	}
	
	public void keyPressed(KeyEvent e){
		if (isDown(e.keyCode)) {
			dir = Direction.DOWN;
			start();
		} else if (isUp(e.keyCode)) {
			dir = Direction.UP;
			start();
		}
		
	}
	
	/*
	 * Events to react to
	 */
	public void widgetDisposed(DisposeEvent e){
		stop();
	}
	
	public void focusGained(FocusEvent e){}
	
	public void focusLost(FocusEvent e){
		stop();
	}
}

public class ArchivKG extends ViewPart implements ElexisEventListener, HeartListener {
	public static final String ID = "com.hilotec.elexis.kgview.ArchivKG";
	
	ScrolledFormText text;
	private Action actNeueKons;
	private Action actNeueTelKons;
	private Action actNeuerHausbesuch;
	private Action actKonsAendern;
	private Action actAutoAkt;
	private Action actSortierungUmk;
	private Action actDrucken;
	private boolean sortRev;
	
	@Override
	public void createPartControl(Composite parent){
		parent.setLayout(new FillLayout());
		
		text = new ScrolledFormText(parent, true);
		text.getFormText().addHyperlinkListener(new IHyperlinkListener() {
			public void linkExited(HyperlinkEvent e){}
			
			public void linkEntered(HyperlinkEvent e){}
			
			public void linkActivated(HyperlinkEvent e){
				if (!(e.getHref() instanceof String))
					return;
				String href = (String) e.getHref();
				if (href.startsWith("kons:")) {
					Konsultation kons = Konsultation.load(href.substring(5));
					ElexisEventDispatcher.fireSelectionEvent(kons);
				}
			}
		});
		// text.getVerticalBar().setIncrement(100);
		new ScrollHelper(text);
		
		sortRev = false;
		
		// TODO: Fonts fuer text laden
		// text.setFont("konstitel", Desk.getFont(cfgName));
		
		createActions();
		ViewMenus menus = new ViewMenus(getViewSite());
		menus.createToolbar(actNeueKons, actNeueTelKons, actNeuerHausbesuch, null, actKonsAendern,
			actAutoAkt, actSortierungUmk, null, actDrucken);
		
		// Aktuell ausgewaehlten Patienten laden
		Patient pat = (Patient) ElexisEventDispatcher.getSelected(Patient.class);
		loadPatient(pat);
		
		ElexisEventDispatcher.getInstance().addListeners(this);
	}
	
	/**
	 * @return Sortierte Liste aller Konsultation dieses Patienten
	 */
	public static ArrayList<Konsultation> getKonsultationen(Patient pat, final boolean reversed){
		// Konsultationen sammeln
		ArrayList<Konsultation> konsliste = new ArrayList<Konsultation>();
		for (Fall f : pat.getFaelle()) {
			for (Konsultation k : f.getBehandlungen(true)) {
				konsliste.add(k);
			}
		}
		
		// Konsultationen sortieren
		Comparator<Konsultation> comp = new Comparator<Konsultation>() {
			public int compare(Konsultation k0, Konsultation k1){
				KonsData kd0 = KonsData.load(k0);
				TimeTool tt0 = new TimeTool(k0.getDatum());
				tt0.setTime(new TimeTool(kd0.getKonsBeginn()));
				
				KonsData kd1 = KonsData.load(k1);
				TimeTool tt1 = new TimeTool(k1.getDatum());
				tt1.setTime(new TimeTool(kd1.getKonsBeginn()));
				
				if (reversed) {
					return tt0.compareTo(tt1);
				} else {
					return tt1.compareTo(tt0);
				}
			}
		};
		Collections.sort(konsliste, comp);
		
		return konsliste;
	}
	
	/**
	 * ArchivKG zum angegebenen Patienten laden.
	 */
	private void loadPatient(Patient pat){
		if (pat == null) {
			text.setText("Kein Patient ausgewählt!");
			return;
		}
		
		// Inhalt fuer Textfeld generieren
		StringBuilder sb = new StringBuilder();
		sb.append("<form>");
		for (Konsultation k : getKonsultationen(pat, sortRev)) {
			processKonsultation(k, sb);
		}
		sb.append("</form>");
		text.setText(sb.toString());
	}
	
	/**
	 * Neu laden
	 */
	private void refresh(){
		loadPatient(ElexisEventDispatcher.getSelectedPatient());
	}
	
	/**
	 * sb um die die Konsultation k erweitern
	 */
	private void processKonsultation(Konsultation k, StringBuilder sb){
		KonsData kd = KonsData.load(k);
		
		sb.append("<p>");
		
		int typ = kd.getKonsTyp();
		if (typ == KonsData.KONSTYP_TELEFON) {
			sb.append("<b>Telefon</b> ");
		} else if (typ == KonsData.KONSTYP_HAUSBESUCH) {
			sb.append("<b>Hausbesuch</b> ");
		} else {
			sb.append("<b>Konsultation</b> ");
		}
		sb.append("<a href=\"kons:" + k.getId() + "\">");
		sb.append(k.getDatum() + " " + kd.getKonsBeginn() + "</a>");
		
		// FIXME: Warum ist das noetig?
		if (k.getFall() != null) {
			sb.append(" " + k.getFall().getAbrechnungsSystem());
		}
		
		String sAutor = "";
		Anwender autor = kd.getAutor();
		if (autor != null) {
			sAutor = autor.getKuerzel();
			if (StringTool.isNothing(sAutor))
				sAutor = autor.getLabel();
		}
		sb.append(" (" + sAutor + ")");
		sb.append("<br/>");
		
		addParagraph("Jetziges Leiden", kd.getJetzigesLeiden(), kd.getJetzigesLeidenICPC(), sb);
		addParagraph("Status", kd.getLokalstatus(), sb);
		addParagraph("Röntgen", kd.getRoentgen(), sb);
		addParagraph("EKG", kd.getEKG(), sb);
		addParagraph("Diagnose", kd.getDiagnose(), kd.getDiagnoseICPC(), sb);
		addParagraph("Therapie", kd.getTherapie(), sb);
		addParagraph("Verlauf", kd.getVerlauf(), sb);
		addParagraph("Procedere", kd.getProzedere(), kd.getProzedereICPC(), sb);
		sb.append("</p>");
	}
	
	private void addParagraph(String titel, String text, StringBuilder sb){
		addParagraph(titel, text, null, sb);
	}
	
	private void addParagraph(String titel, String text, String icpc, StringBuilder sb){
		if ((text == null || text.isEmpty()) && (icpc == null || icpc.isEmpty()))
			return;
		
		sb.append("<b>" + titel + "</b><br/>");
		if (icpc != null && !icpc.isEmpty())
			sb.append("ICPC: " + icpc.replace(",", ", ") + "<br/>");
		sb.append(cleanUp(text));
		sb.append("<br/><br/>");
	}
	
	private String cleanUp(String text){
		return text.replace(">", "&gt;").replace("<", "&lt;").replace("\n", "<br/>");
	}
	
	public void catchElexisEvent(final ElexisEvent ev){
		UiDesk.syncExec(new Runnable() {
			@Override
			public void run(){
				Patient p = (Patient) ev.getObject();
				if (ev.getType() == ElexisEvent.EVENT_SELECTED) {
					loadPatient(p);
				} else if (ev.getType() == ElexisEvent.EVENT_DESELECTED) {
					loadPatient(null);
				}
			}
		});
	}
	
	private final ElexisEvent eetmpl = new ElexisEvent(null, Patient.class,
		ElexisEvent.EVENT_SELECTED | ElexisEvent.EVENT_DESELECTED);
	
	public ElexisEvent getElexisEventFilter(){
		return eetmpl;
	}
	
	public void setFocus(){}
	
	public static class NeueKonsAct extends Action {
		private int typ;
		
		public NeueKonsAct(int typ){
			super(Messages.GlobalActions_NewKons);
			setImageDescriptor(Images.IMG_NEW.getImageDescriptor());
			
			if (typ == KonsData.KONSTYP_TELEFON) {
				setText("Neue Telefonkonsultation");
				setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID,
					"rsc/phone.png"));
				setToolTipText("Neue Telefonkonsultation anlegen");
			} else if (typ == KonsData.KONSTYP_HAUSBESUCH) {
				setText("Neuer Hausbesuch");
				setImageDescriptor(Images.IMG_HOME.getImageDescriptor());
				setToolTipText("Neuen Hausbesuch anlegen");
			} else {
				setImageDescriptor(Images.IMG_NEW.getImageDescriptor());
				setToolTipText(Messages.GlobalActions_NewKonsToolTip); //$NON-NLS-1$
			}
			this.typ = typ;
		}
		
		@Override
		public void run(){
			Fall fall = (Fall) ElexisEventDispatcher.getSelected(Fall.class);
			Patient pat = ElexisEventDispatcher.getSelectedPatient();
			if (fall == null || !fall.isOpen() || !fall.getPatient().equals(pat)) {
				MessageDialog.openError(null, "Kein offener Fall ausgewählt",
					"Um eine neue Konsultation erstellen zu können, muss "
						+ "ein offener Fall ausgewählt werden");
				return;
			}
			new NeueKonsDialog(Hub.getActiveShell(), fall, typ).open();
		}
	}
	
	public static class KonsAendernAct extends Action {
		KonsAendernAct(){
			super("Konsultations Datum/Zeit ändern");
			setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
			setToolTipText("Konsultations Datum/Zeit ändern");
		}
		
		@Override
		public void run(){
			Konsultation kons =
				(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
			if (kons == null || !kons.isEditable(false)) {
				MessageDialog.openError(null, "Keine/Ungültige " + "Konsultation ausgewählt",
					"Es muss eine veränderbare Konsultation " + "ausgewählt sein.");
				return;
			}
			new NeueKonsDialog(Hub.getActiveShell(), kons).open();
		}
	}
	
	private void createActions(){
		actNeueKons = new NeueKonsAct(KonsData.KONSTYP_NORMAL);
		actNeueTelKons = new NeueKonsAct(KonsData.KONSTYP_TELEFON);
		actNeuerHausbesuch = new NeueKonsAct(KonsData.KONSTYP_HAUSBESUCH);
		
		actKonsAendern = new KonsAendernAct();
		
		final ArchivKG akg = this;
		actAutoAkt = new Action("Automatisch aktualisieren", Action.AS_CHECK_BOX) {
			{
				setImageDescriptor(Images.IMG_REFRESH.getImageDescriptor());
				setToolTipText("Automatisch aktualisieren");
			}
			
			@Override
			public void run(){
				boolean ch = isChecked();
				if (ch)
					CoreHub.heart.addListener(akg);
				else
					CoreHub.heart.removeListener(akg);
			}
		};
		
		actSortierungUmk = new Action("Sortierung umkehren", Action.AS_CHECK_BOX) {
			{
				setImageDescriptor(Images.IMG_ARROWUP.getImageDescriptor());
				setToolTipText("Sortierung umkehren, älteste zuoberst");
			}
			
			@Override
			public void run(){
				sortRev = isChecked();
				refresh();
			}
		};
		actDrucken = new Action("KG Drucken") {
			{
				setImageDescriptor(Images.IMG_PRINTER.getImageDescriptor());
				setToolTipText("Krankengeschichte zum Drucken vorbereiten");
			}
			
			@Override
			public void run(){
				IWorkbenchPage p =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				ArchivKGPrintView apv = null;
				try {
					apv = (ArchivKGPrintView) p.showView(ArchivKGPrintView.ID);
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Konsultation kons =
					(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
				if (kons == null) {
					SWTHelper.showError("Keine Konsultation aktiv", "Es wird"
						+ "eine aktive Konsultation benötigt um die KG "
						+ "drucken und ablegen zu können.");
					return;
				}
				apv.doPrint(kons, null, sortRev);
				p.hideView(apv);
			}
		};
	}
	
	@Override
	public void dispose(){
		if (actAutoAkt.isChecked())
			CoreHub.heart.removeListener(this);
		ElexisEventDispatcher.getInstance().removeListeners(this);
		super.dispose();
	}
	
	@Override
	public void heartbeat(){
		refresh();
	}
}
