/*******************************************************************************
 * Copyright 2024 Framsteg GmbH / olivier.debenath@framsteg.ch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package ch.framsteg.elexis.covercard.views.dialogs;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.Collator;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.HttpHostConnectException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.jdom2.JDOMException;
import org.xml.sax.SAXException;

import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.framsteg.elexis.covercard.control.CardInfoRetriever;
import ch.framsteg.elexis.covercard.control.PatientInfoRetriever;
import ch.framsteg.elexis.covercard.dao.CardInfoData;
import ch.framsteg.elexis.covercard.dao.PatientInfoData;
import ch.framsteg.elexis.covercard.exceptions.BlockedCardException;
import ch.framsteg.elexis.covercard.exceptions.InvalidCardException;
import ch.framsteg.elexis.covercard.exceptions.UnsupportedCardException;
import ch.framsteg.elexis.covercard.views.wizard.RegisterWizard;

public class PatientsInfoDialog extends Dialog {

	private boolean nameAsc;
	private boolean prenameAsc;
	private boolean birthdayAsc;
	private boolean sexAsc;
	private boolean addressAsc;
	private boolean zipAsc;
	private boolean locationAsc;
	private boolean countryAsc;
	private boolean ahvAsc;
	private boolean mobilAsc;
	private boolean phoneAsc;
	private boolean insurantNrAsc;
	private boolean covercardNrAsc;
	private boolean idCardNrAsc;

	private Composite container;
	private Table table;
	private Properties applicationProperties;
	private Properties messagesProperties;

	private final static String TITLE = "msg.PatientsInfoDialog.title";
	private final static String INFO = "msg.PatientsInfoDialog.info";
	private final static String NO_COVERCARD = "msg.PatientsInfoDialog.no.covercard";
	private final static String QUESTION = "msg.PatientsInfoDialog.question";
	private final static String INFORMATION_AVAILABLE = "msg.PatientsInfoDialog.information.available";
	private final static String NAME = "msg.PatientsInfoDialog.name";
	private final static String PRENAME = "msg.PatientsInfoDialog.prename";
	private final static String BIRTHDAY = "msg.PatientsInfoDialog.birthday";
	private final static String SEX = "msg.PatientsInfoDialog.sex";
	private final static String ADDRESS = "msg.PatientsInfoDialog.address";
	private final static String ZIP = "msg.PatientsInfoDialog.zip";
	private final static String LOCATION = "msg.PatientsInfoDialog.location";
	private final static String COUNTRY = "msg.PatientsInfoDialog.country";
	private final static String AHV = "msg.PatientsInfoDialog.ahv";
	private final static String MOBIL = "msg.PatientsInfoDialog.mobil";
	private final static String PHONE = "msg.PatientsInfoDialog.phone";
	private final static String INSURANT_NUMBER = "msg.PatientsInfoDialog.insurant.number";
	private final static String CARD_NUMBER = "msg.PatientsInfoDialog.card.number";
	private final static String CARD_ID_NUMBER = "msg.PatientsInfoDialog.card.id.number";

	private static final String ERR_MSG_PARSER_CONFIGURATION_EXCEPTION = "error.searchView.ParserConfigurationException";
	private static final String ERR_MSG_CLIENT_PROTOCOL_EXCEPTION = "error.searchView.ClientProtocolException";
	private static final String ERR_MSG_HTTP_HOST_CONNECT_EXCEPTION = "error.searchView.HttpHostConnectException";
	private static final String ERR_MSG_IO_EXCEPTION = "error.searchView.IOException";
	private static final String ERR_MSG_UNSUPPORTED_OPERATION_EXCEPTION = "error.searchView.UnsupportedOperationException";
	private static final String ERR_MSG_SAX_EXCEPTION = "error.searchView.SAXException";
	private static final String ERR_MSG_JDOM_EXCEPTION = "error.searchView.JDOMException";
	private static final String ERR_MSG_NULL_POINTER_EXCEPTION = "error.searchView.NullPointerException";
	private static final String ERR_MSG_TITLE = "error.searchView.title";
	private static final String ERR_MSG_INVALID_CARD = "error.searchView.invalid.card";
	private static final String ERR_MSG_UNSUPPORTED_CARD = "error.searchView.unsupported.card";
	private static final String ERR_MSG_BLOCKED_CARD = "error.searchView.blocked.card";

	public PatientsInfoDialog(Shell parentShell, Properties applicationProperties, Properties messagesProperties) {
		super(parentShell);
		loadPatients();
		parentShell.setText(messagesProperties.getProperty(TITLE));
		nameAsc = true;
		prenameAsc = true;
		birthdayAsc = true;
		sexAsc = true;
		addressAsc = true;
		zipAsc = true;
		locationAsc = true;
		countryAsc = true;
		ahvAsc = true;
		mobilAsc = true;
		phoneAsc = true;
		insurantNrAsc = true;
		covercardNrAsc = true;
		idCardNrAsc = true;
		this.applicationProperties = applicationProperties;
		setMessagesProperties(messagesProperties);
	}
	
	public PatientsInfoDialog(Shell parentShell, Properties applicationProperties, Properties messagesProperties, Table table) {
		super(parentShell);
		loadPatients();
		parentShell.setText(messagesProperties.getProperty(TITLE));
		nameAsc = true;
		prenameAsc = true;
		birthdayAsc = true;
		sexAsc = true;
		addressAsc = true;
		zipAsc = true;
		locationAsc = true;
		countryAsc = true;
		ahvAsc = true;
		mobilAsc = true;
		phoneAsc = true;
		insurantNrAsc = true;
		covercardNrAsc = true;
		idCardNrAsc = true;
		this.table=table;
		this.applicationProperties = applicationProperties;
		setMessagesProperties(messagesProperties);
	}
	//
	private void loadPatients() {
		Query<Patient> query = new Query<>(Patient.class);
		query.execute();

	}

	protected Control createDialogArea(Composite parent) {

		Composite rootComposite = new Composite(parent, SWT.NONE);
		rootComposite.setLayout(GridLayoutFactory.fillDefaults().create());

		ScrolledComposite scrolledComposite = new ScrolledComposite(rootComposite, SWT.BORDER | SWT.V_SCROLL);
		scrolledComposite
				.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 800).create());
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		container = new Composite(scrolledComposite, SWT.NULL);
		container.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());

		GridLayout groupLayout = new GridLayout();
		groupLayout.numColumns = 1;

		Group filterGroup = new Group(container, SWT.NONE);
		filterGroup.setLayout(groupLayout);
		
		final Button indeterminate = new Button(container, SWT.CHECK);
	    indeterminate.setText("Indeterminate");

	    // Create the ShowProgress button
	    Button showProgress = new Button(container, SWT.NONE);
	    showProgress.setText("Show Progress");

	    final Shell shell = parent.getShell();

	    // Display the ProgressMonitorDialog
	    showProgress.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	        try {
	          new ProgressMonitorDialog(shell).run(true, true,
	              new LongRunningOperation(indeterminate.getSelection()));
	        } catch (InvocationTargetException e) {
	          MessageDialog.openError(shell, "Error", e.getMessage());
	        } catch (InterruptedException e) {
	          MessageDialog.openInformation(shell, "Cancelled", e.getMessage());
	        }
	      }
	    });

		table = new Table(container, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		table.setLinesVisible(true);

		TableColumn tableColumn1 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn2 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn3 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn4 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn5 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn6 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn7 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn8 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn9 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn10 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn11 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn12 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn13 = new TableColumn(table, SWT.LEFT);
		TableColumn tableColumn14 = new TableColumn(table, SWT.LEFT);

		table.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {

				Point pt = new Point(event.x, event.y);
				TableItem item = table.getItem(pt);
				int columnCount = 14;
				if (item == null)
					return;
				for (int i = 0; i < columnCount; i++) {
					Rectangle rect = item.getBounds(i);
					if (rect.contains(pt)) {
						String cardnumber = item.getText(13);
						if (cardnumber.isEmpty()) {
							MessageDialog.openInformation(container.getShell(), messagesProperties.getProperty(INFO),
									messagesProperties.getProperty(NO_COVERCARD));
						} else {
							boolean decision = MessageDialog.openQuestion(container.getShell(),
									messagesProperties.getProperty(QUESTION), MessageFormat
											.format(messagesProperties.getProperty(INFORMATION_AVAILABLE), cardnumber));
							if (decision) {
								CardInfoRetriever cardInfoRetriever = new CardInfoRetriever(applicationProperties,
										messagesProperties);
								try {
									CardInfoData cardInfoData = cardInfoRetriever.getCardInfo(cardnumber);
									PatientInfoRetriever patientInfoRetriever = new PatientInfoRetriever(cardInfoData,
											applicationProperties, messagesProperties);
									PatientInfoData patientInfoData = patientInfoRetriever.getPatientInfo();
									WizardDialog wizardDialog = new WizardDialog(parent.getShell(),
											new RegisterWizard(cardnumber, applicationProperties, messagesProperties,
													cardInfoData, patientInfoData));
									wizardDialog.open();
								} catch (ParserConfigurationException pce) {
									MessageDialog.openInformation(parent.getShell(),
											getMessagesProperties().getProperty(ERR_MSG_TITLE), getMessagesProperties()
													.getProperty(ERR_MSG_PARSER_CONFIGURATION_EXCEPTION));
									pce.printStackTrace();
								} catch (ClientProtocolException cpe) {
									MessageDialog.openInformation(parent.getShell(),
											getMessagesProperties().getProperty(ERR_MSG_TITLE),
											getMessagesProperties().getProperty(ERR_MSG_CLIENT_PROTOCOL_EXCEPTION));
									cpe.printStackTrace();
								} catch (HttpHostConnectException hhce) {
									MessageDialog.openInformation(parent.getShell(),
											getMessagesProperties().getProperty(ERR_MSG_TITLE),
											getMessagesProperties().getProperty(ERR_MSG_HTTP_HOST_CONNECT_EXCEPTION));
									hhce.printStackTrace();
								} catch (IOException ioe) {
									MessageDialog.openInformation(parent.getShell(),
											getMessagesProperties().getProperty(ERR_MSG_TITLE),
											getMessagesProperties().getProperty(ERR_MSG_IO_EXCEPTION));
									ioe.printStackTrace();
								} catch (UnsupportedOperationException uoe) {
									MessageDialog.openInformation(parent.getShell(),
											getMessagesProperties().getProperty(ERR_MSG_TITLE), getMessagesProperties()
													.getProperty(ERR_MSG_UNSUPPORTED_OPERATION_EXCEPTION));
									uoe.printStackTrace();
								} catch (SAXException sae) {
									MessageDialog.openInformation(parent.getShell(),
											getMessagesProperties().getProperty(ERR_MSG_TITLE),
											getMessagesProperties().getProperty(ERR_MSG_SAX_EXCEPTION));
									sae.printStackTrace();
								} catch (JDOMException jde) {
									MessageDialog.openInformation(parent.getShell(),
											getMessagesProperties().getProperty(ERR_MSG_TITLE),
											getMessagesProperties().getProperty(ERR_MSG_JDOM_EXCEPTION));
									jde.printStackTrace();
								} catch (NullPointerException npe) {
									MessageDialog.openInformation(parent.getShell(),
											getMessagesProperties().getProperty(ERR_MSG_TITLE),
											getMessagesProperties().getProperty(ERR_MSG_NULL_POINTER_EXCEPTION));
									npe.printStackTrace();

								} catch (InvalidCardException ice) {
									MessageDialog.openInformation(parent.getShell(),
											getMessagesProperties().getProperty(ERR_MSG_TITLE),
											getMessagesProperties().getProperty(ERR_MSG_INVALID_CARD));
									ice.printStackTrace();
								} catch (UnsupportedCardException usc) {
									MessageDialog.openInformation(parent.getShell(),
											getMessagesProperties().getProperty(ERR_MSG_TITLE),
											getMessagesProperties().getProperty(ERR_MSG_UNSUPPORTED_CARD));
									usc.printStackTrace();
								} catch (BlockedCardException bce) {
									MessageDialog.openInformation(parent.getShell(),
											getMessagesProperties().getProperty(ERR_MSG_TITLE),
											getMessagesProperties().getProperty(ERR_MSG_BLOCKED_CARD));
									bce.printStackTrace();
								}
							}
						}
					}
				}
			}
		});

		tableColumn1.setText(messagesProperties.getProperty(NAME));
		tableColumn1.setAlignment(SWT.LEFT);
		tableColumn1.addListener(SWT.Selection, nameSortListener);
		tableColumn2.setText(messagesProperties.getProperty(PRENAME));
		tableColumn2.setAlignment(SWT.LEFT);
		tableColumn2.addListener(SWT.Selection, prenameSortListener);
		tableColumn3.setText(messagesProperties.getProperty(BIRTHDAY));
		tableColumn3.setAlignment(SWT.LEFT);
		tableColumn3.addListener(SWT.Selection, dateSortListener);
		tableColumn4.setText(messagesProperties.getProperty(SEX));
		tableColumn4.setAlignment(SWT.LEFT);
		tableColumn4.addListener(SWT.Selection, sexSortListener);
		tableColumn5.setText(messagesProperties.getProperty(ADDRESS));
		tableColumn5.setAlignment(SWT.LEFT);
		tableColumn5.addListener(SWT.Selection, addressSortListener);
		tableColumn6.setText(messagesProperties.getProperty(ZIP));
		tableColumn6.setAlignment(SWT.LEFT);
		tableColumn6.addListener(SWT.Selection, zipSortListener);
		tableColumn7.setText(messagesProperties.getProperty(LOCATION));
		tableColumn7.setAlignment(SWT.LEFT);
		tableColumn7.addListener(SWT.Selection, locationSortListener);
		tableColumn8.setText(messagesProperties.getProperty(COUNTRY));
		tableColumn8.setAlignment(SWT.LEFT);
		tableColumn8.addListener(SWT.Selection, countrySortListener);
		tableColumn9.setText(messagesProperties.getProperty(AHV));
		tableColumn9.setAlignment(SWT.LEFT);
		tableColumn9.addListener(SWT.Selection, ahvSortListener);
		tableColumn10.setText(messagesProperties.getProperty(MOBIL));
		tableColumn10.setAlignment(SWT.LEFT);
		tableColumn10.addListener(SWT.Selection, mobilSortListener);
		tableColumn11.setText(messagesProperties.getProperty(PHONE));
		tableColumn11.setAlignment(SWT.LEFT);
		tableColumn11.addListener(SWT.Selection, phoneSortListener);
		tableColumn12.setText(messagesProperties.getProperty(INSURANT_NUMBER));
		tableColumn12.setAlignment(SWT.LEFT);
		tableColumn12.addListener(SWT.Selection, insurantNrSortListener);
		tableColumn13.setText(messagesProperties.getProperty(CARD_NUMBER));
		tableColumn13.setAlignment(SWT.LEFT);
		tableColumn13.addListener(SWT.Selection, coverCardSortListener);
		tableColumn14.setText(messagesProperties.getProperty(CARD_ID_NUMBER));
		tableColumn14.setAlignment(SWT.LEFT);
		tableColumn14.addListener(SWT.Selection, idCardNrSortListener);

		table.setHeaderVisible(true);
		//loadTable(table, allPatients,parent);

		for (TableColumn tableColumn : table.getColumns()) {
			tableColumn.pack();
		}

		sortTable(table, 0);
		table.pack();
		table.deselectAll();
		container.pack();

		scrolledComposite.setContent(container);
		scrolledComposite.setMinSize(container.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		return parent;
	}

	private void sortTable(Table table, int columnNumber) {
		TableItem[] items = table.getItems();
		Collator collator = Collator.getInstance(Locale.getDefault());
		// Sets the column1 as default sorted
		TableColumn column = table.getColumn(columnNumber);

		int index = column == table.getColumn(columnNumber) ? 0 : 1;
		for (int i = 1; i < items.length; i++) {
			String value1 = items[i].getText(index);
			for (int j = 0; j < i; j++) {
				String value2 = items[j].getText(index);
				if (collator.compare(value1, value2) < 0) {
					String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
							items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
							items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
							items[i].getText(11), items[i].getText(12), items[i].getText(13), items[i].getText(14) };
					items[i].dispose();
					TableItem item = new TableItem(table, SWT.NONE, j);
					item.setText(values);
					items = table.getItems();
					break;
				}
			}
		}
		table.setSortColumn(column);
		table.deselectAll();
	}

	/* Sort Listeners */
	Listener nameSortListener = new Listener() {

		@Override
		public void handleEvent(Event e) {
			TableItem[] items = table.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());
			TableColumn column = (TableColumn) e.widget;
			int index = 0;
			if (nameAsc) {
				nameAsc = false;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();

							break;
						}
					}
				}
			} else {
				nameAsc = true;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) > 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
			}
			table.setSortColumn(column);
		}
	};

	Listener prenameSortListener = new Listener() {

		@Override
		public void handleEvent(Event e) {
			TableItem[] items = table.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());
			TableColumn column = (TableColumn) e.widget;
			int index = 1;
			if (prenameAsc) {
				prenameAsc = false;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();

							break;
						}
					}
				}
			} else {
				prenameAsc = true;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) > 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
			}
			table.setSortColumn(column);
		}
	};

	Listener dateSortListener = new Listener() {

		@Override
		public void handleEvent(Event e) {
			try {
				TableItem[] items = table.getItems();
				TableColumn column = (TableColumn) e.widget;
				DateFormat sourceFormat = new SimpleDateFormat("dd.MM.yyyy");
				int index = 2;

				if (birthdayAsc) {
					birthdayAsc = false;
					for (int i = 1; i < items.length; i++) {

						String value1 = items[i].getText(index);

						Date date1 = sourceFormat.parse(value1);
						for (int j = 0; j < i; j++) {

							String value2 = items[j].getText(index);
							Date date2 = sourceFormat.parse(value2);

							if (date1.compareTo(date2) < 0) {
								String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
										items[i].getText(3), items[i].getText(4), items[i].getText(5),
										items[i].getText(6), items[i].getText(7), items[i].getText(8),
										items[i].getText(9), items[i].getText(10), items[i].getText(11),
										items[i].getText(12), items[i].getText(13), items[i].getText(14) };
								items[i].dispose();
								TableItem item = new TableItem(table, SWT.NONE, j);
								item.setText(values);
								items = table.getItems();
								break;
							}
						}
					}
				} else {
					birthdayAsc = true;
					for (int i = 1; i < items.length; i++) {

						String value1 = items[i].getText(index);

						Date date1 = sourceFormat.parse(value1);
						for (int j = 0; j < i; j++) {

							String value2 = items[j].getText(index);
							Date date2 = sourceFormat.parse(value2);

							if (date1.compareTo(date2) > 0) {
								String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
										items[i].getText(3), items[i].getText(4), items[i].getText(5),
										items[i].getText(6), items[i].getText(7), items[i].getText(8),
										items[i].getText(9), items[i].getText(10), items[i].getText(11),
										items[i].getText(12), items[i].getText(13), items[i].getText(14) };
								items[i].dispose();
								TableItem item = new TableItem(table, SWT.NONE, j);
								item.setText(values);
								items = table.getItems();
								break;
							}
						}
					}
					table.setSortColumn(column);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	};

	Listener sexSortListener = new Listener() {

		@Override
		public void handleEvent(Event e) {
			TableItem[] items = table.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());
			TableColumn column = (TableColumn) e.widget;
			int index = 3;
			if (sexAsc) {
				sexAsc = false;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();

							break;
						}
					}
				}
			} else {
				sexAsc = true;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) > 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
			}
			table.setSortColumn(column);
		}
	};

	Listener addressSortListener = new Listener() {

		@Override
		public void handleEvent(Event e) {
			TableItem[] items = table.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());
			TableColumn column = (TableColumn) e.widget;
			int index = 4;
			if (addressAsc) {
				addressAsc = false;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();

							break;
						}
					}
				}
			} else {
				addressAsc = true;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) > 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
			}
			table.setSortColumn(column);
		}
	};

	Listener zipSortListener = new Listener() {

		@Override
		public void handleEvent(Event e) {
			TableItem[] items = table.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());
			TableColumn column = (TableColumn) e.widget;
			int index = 5;
			if (zipAsc) {
				zipAsc = false;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();

							break;
						}
					}
				}
			} else {
				zipAsc = true;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) > 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
			}
			table.setSortColumn(column);
		}
	};

	Listener locationSortListener = new Listener() {

		@Override
		public void handleEvent(Event e) {
			TableItem[] items = table.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());
			TableColumn column = (TableColumn) e.widget;
			int index = 6;
			if (locationAsc) {
				locationAsc = false;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();

							break;
						}
					}
				}
			} else {
				locationAsc = true;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) > 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
			}
			table.setSortColumn(column);
		}
	};

	Listener countrySortListener = new Listener() {

		@Override
		public void handleEvent(Event e) {
			TableItem[] items = table.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());
			TableColumn column = (TableColumn) e.widget;
			int index = 7;
			if (countryAsc) {
				countryAsc = false;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();

							break;
						}
					}
				}
			} else {
				countryAsc = true;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) > 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
			}
			table.setSortColumn(column);
		}
	};

	Listener ahvSortListener = new Listener() {

		@Override
		public void handleEvent(Event e) {
			TableItem[] items = table.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());
			TableColumn column = (TableColumn) e.widget;
			int index = 8;
			if (ahvAsc) {
				ahvAsc = false;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();

							break;
						}
					}
				}
			} else {
				ahvAsc = true;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) > 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
			}
			table.setSortColumn(column);
		}
	};

	Listener mobilSortListener = new Listener() {

		@Override
		public void handleEvent(Event e) {
			TableItem[] items = table.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());
			TableColumn column = (TableColumn) e.widget;
			int index = 9;
			if (mobilAsc) {
				mobilAsc = false;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();

							break;
						}
					}
				}
			} else {
				mobilAsc = true;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) > 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
			}
			table.setSortColumn(column);
		}
	};

	Listener phoneSortListener = new Listener() {

		@Override
		public void handleEvent(Event e) {
			TableItem[] items = table.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());
			TableColumn column = (TableColumn) e.widget;
			int index = 10;
			if (phoneAsc) {
				phoneAsc = false;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();

							break;
						}
					}
				}
			} else {
				phoneAsc = true;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) > 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
			}
			table.setSortColumn(column);
		}
	};

	Listener insurantNrSortListener = new Listener() {

		@Override
		public void handleEvent(Event e) {
			TableItem[] items = table.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());
			TableColumn column = (TableColumn) e.widget;
			int index = 11;
			if (insurantNrAsc) {
				insurantNrAsc = false;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();

							break;
						}
					}
				}
			} else {
				insurantNrAsc = true;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) > 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
			}
			table.setSortColumn(column);
		}
	};

	Listener coverCardSortListener = new Listener() {

		@Override
		public void handleEvent(Event e) {
			TableItem[] items = table.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());
			TableColumn column = (TableColumn) e.widget;
			int index = 12;
			if (covercardNrAsc) {
				covercardNrAsc = false;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();

							break;
						}
					}
				}
			} else {
				covercardNrAsc = true;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) > 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
			}
			table.setSortColumn(column);
		}
	};

	Listener idCardNrSortListener = new Listener() {

		@Override
		public void handleEvent(Event e) {
			TableItem[] items = table.getItems();
			Collator collator = Collator.getInstance(Locale.getDefault());
			TableColumn column = (TableColumn) e.widget;
			int index = 13;
			if (idCardNrAsc) {
				idCardNrAsc = false;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) < 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();

							break;
						}
					}
				}
			} else {
				idCardNrAsc = true;
				for (int i = 1; i < items.length; i++) {
					String value1 = items[i].getText(index);
					for (int j = 0; j < i; j++) {

						String value2 = items[j].getText(index);
						if (collator.compare(value1, value2) > 0) {
							String[] values = { items[i].getText(0), items[i].getText(1), items[i].getText(2),
									items[i].getText(3), items[i].getText(4), items[i].getText(5), items[i].getText(6),
									items[i].getText(7), items[i].getText(8), items[i].getText(9), items[i].getText(10),
									items[i].getText(11), items[i].getText(12), items[i].getText(13),
									items[i].getText(14) };
							items[i].dispose();
							TableItem item = new TableItem(table, SWT.NONE, j);
							item.setText(values);
							items = table.getItems();
							break;
						}
					}
				}
			}
			table.setSortColumn(column);
		}
	};

	public Properties getMessagesProperties() {
		return messagesProperties;
	}

	public void setMessagesProperties(Properties messagesProperties) {
		this.messagesProperties = messagesProperties;
	}
	
	//Progressbar
	class LongRunningOperation implements IRunnableWithProgress {
		  // The total sleep time
		  private static final int TOTAL_TIME = 10000;

		  // The increment sleep time
		  private static final int INCREMENT = 500;

		  private boolean indeterminate;

		  /**
		   * LongRunningOperation constructor
		   * 
		   * @param indeterminate whether the animation is unknown
		   */
		  public LongRunningOperation(boolean indeterminate) {
		    this.indeterminate = indeterminate;
		  }

		  /**
		   * Runs the long running operation
		   * 
		   * @param monitor the progress monitor
		   */
		  public void run(IProgressMonitor monitor) throws InvocationTargetException,
		      InterruptedException {
		    monitor.beginTask("Running long running operation",
		        indeterminate ? IProgressMonitor.UNKNOWN : TOTAL_TIME);
		    for (int total = 0; total < TOTAL_TIME && !monitor.isCanceled(); total += INCREMENT) {
		      Thread.sleep(INCREMENT);
		      monitor.worked(INCREMENT);
		      if (total == TOTAL_TIME / 2) monitor.subTask("Doing second half");
		    }
		    monitor.done();
		    if (monitor.isCanceled())
		        throw new InterruptedException("The long running operation was cancelled");
		  }}
}
