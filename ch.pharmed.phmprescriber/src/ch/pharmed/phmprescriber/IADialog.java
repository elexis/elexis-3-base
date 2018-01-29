package ch.pharmed.phmprescriber;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class IADialog extends TitleAreaDialog  {

	//Variables
	List<String> productDescr;
	Shell shell;
	ResourceBundle messages;
	
  public void setProductDescr(List<String> productDescr) {
		this.productDescr = productDescr;
	}
  
  public void setResourceBundle(ResourceBundle messages) {
	  
	  this.messages = messages;
	  
  }

  public IADialog(Shell parentShell) {
    super(parentShell);
    shell = parentShell;
    messages = ResourceBundle.getBundle("ch.pharmed.phmprescriber.MessagesBundle", new Locale("de", "CH"));
    
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    Composite container = (Composite) super.createDialogArea(parent);
    
    GridLayout layout = new GridLayout(1, false);
    layout.marginRight = 5;
    layout.marginLeft = 10;
    container.setLayout(layout);
    
    setTitle(messages.getString("IADialog_0"));
    setMessage(messages.getString("IADialog_1"), IMessageProvider.INFORMATION);
    
    
    for (int i = 0; i < productDescr.size()-1; i = i+2) {
    	
    	final Label lblProduct = new Label(container, SWT.WRAP);
    	final Label lblAction = new Label(container, SWT.WRAP);
    	
    	final GridData data = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
    	lblProduct.setLayoutData(data);
    	lblProduct.setText(productDescr.get(i));
    	
    	lblAction.setLayoutData(data);
    	lblAction.setText("   " + productDescr.get(i+1)); //$NON-NLS-1$
    	
     
    }
    


    return container;
  }

 
// overriding this methods allows you to set the
  // title of the custom dialog
  @Override
  protected void configureShell(Shell newShell) {
    super.configureShell(newShell);
    newShell.setText(messages.getString("IADialog_3"));
    
  }


  
  @Override
  protected void createButtonsForButtonBar(Composite parent) {
   super.createButtonsForButtonBar(parent);

   Button ok = getButton(IDialogConstants.OK_ID);
   ok.setText(messages.getString("IADialog_4"));
   setButtonLayoutData(ok);

   Button cancel = getButton(IDialogConstants.CANCEL_ID);
   cancel.setText(messages.getString("IADialog_5"));
   setButtonLayoutData(cancel);
}

} 