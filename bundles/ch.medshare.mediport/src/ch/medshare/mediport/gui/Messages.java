package ch.medshare.mediport.gui;


import org.eclipse.osgi.util.NLS;
public class Messages extends NLS {
  public static final String BUNDLE_NAME = "ch.medshare.mediport.gui.messages";

  public static String ShowErrorInvoices_title_Antworten;
  public static String ShowErrorInvoices_msg_Rechnungsantworten;
  public static String ShowErrorInvoices_msg2_Rechnungsantworten;
  public static String ErrorInvoiceForm_title_VerzeichnisOeffnen;
  public static String ErrorInvoiceForm_msg_Fehlerverzeichnis;
  public static String ErrorInvoiceForm_msg_copyStylesheet;
  public static String ErrorInvoiceForm_error_copyStylesheet;
  public static String ErrorInvoiceForm_msg_Antwortverzeichnis;
  static { // load message values from bundle file
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages() {
  }
}

