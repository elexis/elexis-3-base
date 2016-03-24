/****************************************************************************
 *                                                                          *
 * NOA (Nice Office Access)                                     						*
 * ------------------------------------------------------------------------ *
 *                                                                          *
 * The Contents of this file are made available subject to                  *
 * the terms of GNU Lesser General Public License Version 2.1.              *
 *                                                                          * 
 * GNU Lesser General Public License Version 2.1                            *
 * ======================================================================== *
 * Copyright 2003-2006 by IOn AG                                            *
 *                                                                          *
 * This library is free software; you can redistribute it and/or            *
 * modify it under the terms of the GNU Lesser General Public               *
 * License version 2.1, as published by the Free Software Foundation.       *
 *                                                                          *
 * This library is distributed in the hope that it will be useful,          *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU        *
 * Lesser General Public License for more details.                          *
 *                                                                          *
 * You should have received a copy of the GNU Lesser General Public         *
 * License along with this library; if not, write to the Free Software      *
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,                    *
 * MA  02111-1307  USA                                                      *
 *                                                                          *
 * Contact us:                                                              *
 *  http://www.ion.ag																												*
 *  http://ubion.ion.ag                                                     *
 *  info@ion.ag                                                             *
 *                                                                          *
 ****************************************************************************/

/*
 * Last changes made by $Author: markus $, $Date: 2008-11-18 14:07:54 +0100 (Di, 18 Nov 2008) $
 */
package ag.ion.noa4e.ui.wizards.application;

import java.io.File;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import ag.ion.bion.officelayer.application.IApplicationAssistant;
import ag.ion.bion.officelayer.application.IApplicationProperties;
import ag.ion.bion.officelayer.application.ILazyApplicationInfo;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;
import ag.ion.bion.workbench.office.editor.core.EditorCorePlugin;
import ag.ion.noa4e.ui.FormBorderPainter;
import ag.ion.noa4e.ui.NOAUIPlugin;
import ag.ion.noa4e.ui.operations.FindApplicationInfosOperation;

/**
 * Wizard page in order to define the path of a local OpenOffice.org application.
 * 
 * @author Andreas Br�ker
 * @version $Revision: 11685 $
 */
public class LocalApplicationWizardDefinePage extends WizardPage implements IWizardPage {

  /** Name of the page. */
  public static final String     PAGE_NAME             = "LocalApplicationWizardDefinePage"; //$NON-NLS-1$

  private static final String    DEFAULT_PRODUCT_NAME  = "OpenOffice.org";                  //$NON-NLS-1$

  private Text                   textHome              = null;
  private Table                  tableApplicationInfos = null;

  private ILazyApplicationInfo[] applicationInfos      = null;

  private String                 selectedHomePath      = null;

  //----------------------------------------------------------------------------
  /**
   * Constructs new LocalApplicationWizardDefinePage.
   * 
   * @param homePath home path of the office application to be used
   * @param applicationInfos application infos to be used (can be null)
   * 
   * @author Andreas Br�ker
   */
  public LocalApplicationWizardDefinePage(String homePath, ILazyApplicationInfo[] applicationInfos) {
	super(PAGE_NAME);
	System.out.println("LOAWDP: LocalApplicationWizardDefinePage("+homePath+", applicationInfos) - just supered "+PAGE_NAME);
		
    setTitle(Messages.LocalApplicationWizardDefinePage_title);
    setDescription(Messages.LocalApplicationWizardDefinePage_description);

    this.applicationInfos = applicationInfos;
    this.selectedHomePath = homePath;
  }

  //----------------------------------------------------------------------------  
  /**
   * Returns selected home path of an local office application. Returns null
   * if a home path is not available.
   * 
   * @return selected home path of an local office application or null
   * if a home path is not available
   * 
   * @author Andreas Br�ker
   */
  public String getSelectedHomePath() {
	if (selectedHomePath==null) System.out.println("LOAWDP: getSelectedHomePath() returns null");
	else System.out.println("LOAWDP: getSelectedHomePath() returns "+selectedHomePath);
	return selectedHomePath;
  }

  //----------------------------------------------------------------------------
  /**
   * Sets the visibility of this dialog page.
   *
   * @param visible <code>true</code> to make this page visible,
   *  and <code>false</code> to hide it
   * 
   * @author Andreas Br�ker
   */
  public void setVisible(boolean visible) {
	System.out.println("LOAWDP: setVisible");
    super.setVisible(visible);
    if (visible) {
      if (applicationInfos == null) {
        try {
          Display.getCurrent().asyncExec(new Runnable() {
            public void run() {
              try {
                FindApplicationInfosOperation applicationInfosOperation = new FindApplicationInfosOperation();
                getContainer().run(true, true, applicationInfosOperation);
                applicationInfos = applicationInfosOperation.getApplicationsInfos();
                fillTable(tableApplicationInfos);
                init(applicationInfos);
              }
              catch (Throwable throwable) {
                //do not consume
              }
            }
          });
        }
        catch (Throwable throwable) {
          //do not consume
        }
      }
    }
  }

  //----------------------------------------------------------------------------
  /**
   * Creates the top level control for this dialog
   * page under the given parent composite.
   *
   * @param parent the parent composite
   * 
   * @author Andreas Br�ker
   */
  public void createControl(Composite parent) {
	System.out.println("LOAWDP: createControl");
	FormToolkit formToolkit = NOAUIPlugin.getFormToolkit();
    ScrolledForm scrolledForm = formToolkit.createScrolledForm(parent);
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);

    GridLayout gridLayout = new GridLayout();
    gridLayout.marginHeight = IDialogConstants.VERTICAL_MARGIN;
    scrolledForm.getBody().setLayout(gridLayout);
    scrolledForm.getBody().setLayoutData(gridData);
    scrolledForm.getBody().setBackground(parent.getBackground());

    constructHomeSection(formToolkit, scrolledForm);
    constructApplicationsSection(formToolkit, scrolledForm);

    setControl(scrolledForm);

    if (applicationInfos != null) {
      fillTable(tableApplicationInfos);
      if (selectedHomePath == null)
        init(applicationInfos);
    }

    if (selectedHomePath != null) {
      textHome.setText(selectedHomePath);
    }

    checkPageState();
  }

  //----------------------------------------------------------------------------
  /**
   * Constructs home section.
   * 
   * @param formToolkit form toolkit to be used
   * @param scrolledForm scrolled form to be used
   * 
   * @author Andreas Br�ker
   */
  private void constructHomeSection(FormToolkit formToolkit, ScrolledForm scrolledForm) {
	System.out.println("LOAWDP: constructHomeSection");
	Section section = formToolkit.createSection(scrolledForm.getBody(),
        Section.DESCRIPTION | ExpandableComposite.CLIENT_INDENT);
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    section.setText(Messages.LocalApplicationWizardDefinePage_section_application_test);
    section.setDescription(Messages.LocalApplicationWizardDefinePage_section_application_description);
    section.setLayoutData(gridData);
    section.setBackground(scrolledForm.getBody().getBackground());
    formToolkit.createCompositeSeparator(section);

    Composite client = formToolkit.createComposite(section);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    client.setLayout(gridLayout);
    client.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    client.setBackground(section.getBackground());

    Label labelHome = formToolkit.createLabel(client,
        Messages.LocalApplicationWizardDefinePage_label_home_text);
    labelHome.setBackground(client.getBackground());

    textHome = formToolkit.createText(client, ""); //$NON-NLS-1$
    textHome.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
    textHome.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent modifyEvent) {
        checkPageState();
        selectedHomePath = textHome.getText();
        ILazyApplicationInfo applicationInfo = selectApplicationInfo(textHome.getText());
        textHome.setFocus();
        if (applicationInfo == null) {
          try {
            IApplicationAssistant applicationAssistant = OfficeApplicationRuntime.getApplicationAssistant(EditorCorePlugin.getDefault().getLibrariesLocation());
            applicationInfo = applicationAssistant.findLocalApplicationInfo(textHome.getText());
            if (applicationInfo != null) {
              addApplicationInfo(tableApplicationInfos, applicationInfo);
            }
          }
          catch (Throwable throwable) {
            //do not consume
          }
        }
      }
    });

    final Link linkBrowse = new Link(client, SWT.NONE);
    linkBrowse.setText("<a>" + Messages.LocalApplicationWizardDefinePage_link_browse_text + "</a>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    linkBrowse.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent selectionEvent) {
        DirectoryDialog directoryDialog = new DirectoryDialog(linkBrowse.getShell());
        directoryDialog.setText(Messages.LocalApplicationWizardDefinePage_directory_dialog_text);
        directoryDialog.setMessage(Messages.LocalApplicationWizardDefinePage_directory_dialog_message);
        String path = directoryDialog.open();
        if (path != null) {
          textHome.setText(path);
          textHome.setSelection(path.length());
        }
      }
    });

    FormBorderPainter.paintBordersFor(client);
    section.setClient(client);
  }

  //----------------------------------------------------------------------------
  /**
   * Constructs application section.
   * 
   * @param formToolkit form toolkit to be used
   * @param scrolledForm scrolled form to be used
   * 
   * @author Andreas Br�ker
   */
  private void constructApplicationsSection(FormToolkit formToolkit, ScrolledForm scrolledForm) {
	System.out.println("LOAWDP: constructApplicationSection");
	Section section = formToolkit.createSection(scrolledForm.getBody(),
        Section.DESCRIPTION | ExpandableComposite.CLIENT_INDENT);
    GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    section.setText(Messages.LocalApplicationWizardDefinePage_section_available_applications_text);
    section.setDescription(Messages.LocalApplicationWizardDefinePage_section_available_applications_description);
    section.setLayoutData(gridData);
    section.setBackground(scrolledForm.getBody().getBackground());
    formToolkit.createCompositeSeparator(section);

    Composite client = formToolkit.createComposite(section);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 3;
    client.setLayout(gridLayout);
    client.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    client.setBackground(section.getBackground());

    tableApplicationInfos = formToolkit.createTable(client, SWT.SINGLE | SWT.FULL_SELECTION);
    gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
    int tableWidth = (int) (tableApplicationInfos.getDisplay().getClientArea().width * 0.3);
    gridData.widthHint = tableWidth;
    tableApplicationInfos.setLayoutData(gridData);

    TableLayout tableLayout = new TableLayout();
    tableApplicationInfos.setLayout(tableLayout);

    TableColumn columnProduct = new TableColumn(tableApplicationInfos, SWT.NONE);
    columnProduct.setText(Messages.LocalApplicationWizardDefinePage_column_product_text);
    int columnProductWidth = (int) (tableWidth * 0.4);
    columnProduct.setWidth(columnProductWidth);

    TableColumn columnHome = new TableColumn(tableApplicationInfos, SWT.NONE);
    columnHome.setText(Messages.LocalApplicationWizardDefinePage_column_home_text);
    columnHome.setWidth(tableWidth - columnProductWidth);

    tableApplicationInfos.setLinesVisible(true);
    tableApplicationInfos.setHeaderVisible(true);

    tableApplicationInfos.addSelectionListener(new SelectionAdapter() {
      public void widgetSelected(SelectionEvent selectionEvent) {
        TableItem tableItem = (TableItem) selectionEvent.item;
        ILazyApplicationInfo applicationInfo = (ILazyApplicationInfo) tableItem.getData();
        if (applicationInfo != null) {
          if (!applicationInfo.getHome().equals(textHome.getText())) {
            textHome.setText(applicationInfo.getHome());
            textHome.setSelection(applicationInfo.getHome().length());
          }
        }
      }
    });

    FormBorderPainter.paintBordersFor(client);
    section.setClient(client);
  }

  //----------------------------------------------------------------------------
  /**
   * Checks page state.
   * 
   * @author Andreas Br�ker
   */
  private void checkPageState() {
	System.out.println("LOAWDP: checkPageState");
	String home = textHome.getText();
    if (home.length() != 0) {
      File file = new File(home);
      if (file.canRead()) {
        try {
          IApplicationAssistant applicationAssistant = OfficeApplicationRuntime.getApplicationAssistant(EditorCorePlugin.getDefault().getLibrariesLocation());
          ILazyApplicationInfo applicationInfo = applicationAssistant.findLocalApplicationInfo(home);
          if (applicationInfo == null) {
            setPageComplete(true);
            setMessage(Messages.LocalApplicationWizardDefinePage_message_warning_path_invalid,
                IMessageProvider.WARNING);
          }
          else {
            if (applicationInfo.getMajorVersion() == 1 && applicationInfo.getMinorVersion() == 9) {
              setPageComplete(true);
              setMessage(Messages.LocalApplicationWizardDefinePage_message_warning_beta_release,
                  IMessageProvider.WARNING);
            }
            else if (applicationInfo.getMajorVersion() == 1) {
              setPageComplete(true);
              setMessage(Messages.LocalApplicationWizardDefinePage_message_warning_version_old,
                  IMessageProvider.WARNING);
            }
            else {
              setPageComplete(true);
              setMessage(null);
            }
          }
        }
        catch (Throwable throwable) {
          setPageComplete(true);
          setMessage(null);
        }
      }
      else {
        setPageComplete(false);
        setMessage(Messages.LocalApplicationWizardDefinePage_message_error_path_not_available,
            IMessageProvider.ERROR);
      }
    }
    else {
      setPageComplete(false);
      setMessage(null);
    }
  }

  //----------------------------------------------------------------------------
  /**
   * Fills table with available applications.
   * 
   * @param table table with available applications
   * 
   * @author Andreas Br�ker
   */
  private void fillTable(Table table) {
	System.out.println("LOAWDP: fillTable");
	if (applicationInfos == null || table == null)
      return;

    for (int i = 0, n = applicationInfos.length; i < n; i++) {
      ILazyApplicationInfo applicationInfo = applicationInfos[i];
      addApplicationInfo(table, applicationInfo);
    }
  }

  //----------------------------------------------------------------------------
  /**
   * Adds new application info to the application infos table.
   * 
   * @param table application infos table to be used
   * @param applicationInfo application info to be added
   * 
   * @author Andreas Br�ker
   */
  private void addApplicationInfo(Table table, ILazyApplicationInfo applicationInfo) {
    if (table != null && applicationInfo != null) {
      if (getApplicationInfo(applicationInfo.getHome()) == null) {
        TableItem tableItem = new TableItem(table, SWT.NONE);
        String productName = DEFAULT_PRODUCT_NAME;
        if (applicationInfo.getProperties() != null) {
          String productKey = applicationInfo.getProperties().getPropertyValue(IApplicationProperties.PRODUCT_KEY_PROPERTY);
          if (productKey != null)
            productName = productKey;
        }
        tableItem.setText(0, productName);
        tableItem.setText(1, applicationInfo.getHome());
        tableItem.setData(applicationInfo);
      }
    }
  }

  //----------------------------------------------------------------------------
  /**
   * Selects an application info object from the application table
   * on the basis of the submitted application home path.
   * 
   * @param home home path to be used
   * 
   * @return selected application info or null if none was selected
   * 
   * @author Andreas Br�ker
   */
  private ILazyApplicationInfo selectApplicationInfo(String home) {
	System.out.println("LOAWDP: selectApplicationInfo(String home)");   
	if (home == null) {
		System.out.println("LOAWDP: Please note: home==null; so just returning null.");
	  return null;
	}
    
    if (tableApplicationInfos != null) {
      TableItem[] tableItems = tableApplicationInfos.getItems();
      for (int i = 0, n = tableItems.length; i < n; i++) {
        TableItem tableItem = tableItems[i];
        ILazyApplicationInfo applicationInfo = (ILazyApplicationInfo) tableItem.getData();
        if (applicationInfo != null) {
          if (applicationInfo.getHome().equals(home)) {
            tableApplicationInfos.select(i);
            return applicationInfo;
          }
        }
      }
    }
    return null;
  }

  //----------------------------------------------------------------------------
  /**
   * Returns an application info object from the application table
   * on the basis of the submitted application home path.
   * 
   * @param home home path to be used
   * 
   * @return application info or null if an application info with the
   * submitted application home path is not available
   * 
   * @author Andreas Br�ker
   */
  private ILazyApplicationInfo getApplicationInfo(String home) {
	System.out.println("LOAWDP: getApplicationInfo(String home)");   
	if (home == null) {
		System.out.println("LOAWDP: Please note: home==null; so just returning null.");
      return null;
  }

    if (tableApplicationInfos != null) {
      TableItem[] tableItems = tableApplicationInfos.getItems();
      for (int i = 0, n = tableItems.length; i < n; i++) {
        TableItem tableItem = tableItems[i];
        ILazyApplicationInfo applicationInfo = (ILazyApplicationInfo) tableItem.getData();
        if (applicationInfo != null) {
          if (applicationInfo.getHome().equals(home)) {
            return applicationInfo;
          }
        }
      }
    }
    return null;
  }

  //----------------------------------------------------------------------------
  /**
   * Inits the applications info objects in order to a suiteable 
   * application.
   * 
   * @param applicationInfos application infos to be used
   * 
   * @author Andreas Br�ker
   */
  private void init(ILazyApplicationInfo[] applicationInfos) {
	System.out.println("LOAWDP: init(applicationInfos)");   
	if (applicationInfos == null) {
		System.out.println("LOAWDP: Please note: applicationInfos==null; so just returning.");
	  return;
	}

    String home = null;
    for (int i = 0, n = applicationInfos.length; i < n; i++) {
      ILazyApplicationInfo applicationInfo = applicationInfos[i];
      if (applicationInfo.getMajorVersion() == 2) {
        home = applicationInfo.getHome();
        break;
      }
      else if (applicationInfo.getMajorVersion() == 1 && applicationInfo.getMinorVersion() == 9)
        home = applicationInfo.getHome();
    }
    if (home != null) {
      textHome.setText(home);
      textHome.setSelection(home.length());
    }
  }
  //----------------------------------------------------------------------------

}