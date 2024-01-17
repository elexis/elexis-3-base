package ch.framsteg.elexis.finance.analytics.views;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.ui.views.IRefreshable;
import ch.elexis.data.PersistentObject;

public class ReportingView extends ViewPart implements IRefreshable {

	private final static String TAB_1_CAPTION = "reporting.view.tab.1.caption";
	private final static String TAB_2_CAPTION = "reporting.view.tab.2.caption";
	private final static String TAB_3_CAPTION = "reporting.view.tab.3.caption";
	private final static String TAB_4_CAPTION = "reporting.view.tab.4.caption";
	private final static String TAB_5_CAPTION = "reporting.view.tab.5.caption";
	private final static String TAB_6_CAPTION = "reporting.view.tab.6.caption";
	private final static String TAB_7_CAPTION = "reporting.view.tab.7.caption";
	private final static String TAB_8_CAPTION = "reporting.view.tab.8.caption";

	private Properties applicationProperties;
	private Properties messagesProperties;
	private Properties sqlProperties;

	private int insertMark = -1;
	private TabFolder tabFolder;

	public ReportingView() {
		super();
	}

	private void loadProperties() {
		try {
			setApplicationProperties(new Properties());
			setMessagesProperties(new Properties());
			setSqlProperties(new Properties());
			String separator = FileSystems.getDefault().getSeparator();
			getApplicationProperties().load(ReportingView.class.getClassLoader()
					.getResourceAsStream(separator + "resources" + separator + "application.properties"));
			getMessagesProperties().load(ReportingView.class.getClassLoader()
					.getResourceAsStream(separator + "resources" + separator + "messages.properties"));

			if (PersistentObject.getDefaultConnection().getDBFlavor().equalsIgnoreCase("postgresql")) {
				getSqlProperties().load(ReportingView.class.getClassLoader()
						.getResourceAsStream(separator + "resources" + separator + "postgresql.properties"));
			} else if (PersistentObject.getDefaultConnection().getDBFlavor().equalsIgnoreCase("mysql")) {
				getSqlProperties().load(ReportingView.class.getClassLoader()
						.getResourceAsStream(separator + "resources" + separator + "mysql.properties"));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void refresh() {
	}

	@Override
	public void createPartControl(Composite parent) {
		loadProperties();
		tabFolder = new TabFolder(parent, SWT.NONE);
		TabItem cti8 = new TabItem(tabFolder, SWT.NONE);
		TabItem cti7 = new TabItem(tabFolder, SWT.NONE, insertMark + 1);
		TabItem cti6 = new TabItem(tabFolder, SWT.NONE, insertMark + 1);
		TabItem cti5 = new TabItem(tabFolder, SWT.NONE, insertMark + 1);
		TabItem cti4 = new TabItem(tabFolder, SWT.NONE, insertMark + 1);
		TabItem cti3 = new TabItem(tabFolder, SWT.NONE, insertMark + 1);
		TabItem cti2 = new TabItem(tabFolder, SWT.NONE, insertMark + 1);
		TabItem cti1 = new TabItem(tabFolder, SWT.NONE, insertMark + 1);
	
		cti1.setText(getMessagesProperties().getProperty(TAB_1_CAPTION));
		cti2.setText(getMessagesProperties().getProperty(TAB_2_CAPTION));
		cti3.setText(getMessagesProperties().getProperty(TAB_3_CAPTION));
		cti4.setText(getMessagesProperties().getProperty(TAB_4_CAPTION));
		cti5.setText(getMessagesProperties().getProperty(TAB_5_CAPTION));
		cti6.setText(getMessagesProperties().getProperty(TAB_6_CAPTION));
		cti7.setText(getMessagesProperties().getProperty(TAB_7_CAPTION));
		cti8.setText(getMessagesProperties().getProperty(TAB_8_CAPTION));

		TabbedView tabbedView = new TabbedView(tabFolder, getApplicationProperties(), getMessagesProperties(),
				getSqlProperties());
		
		// cti1.setControl(tabbedView.buildTableSalesTotal());
		cti1.setControl(tabbedView.buildSalesTotalPerServiceComposite());
		cti2.setControl(tabbedView.buildSalesTotalPerServiceYearComposite());
		cti3.setControl(tabbedView.buildSalesTotalPerServiceYearMonthComposite());
		cti4.setControl(tabbedView.buildSalesTotalPerYearComposite());
		cti5.setControl(tabbedView.buildSalesTotalPerYearMonthComposite());
		cti6.setControl(tabbedView.buildTarmedPerYearMonthComposite());
		cti7.setControl(tabbedView.buildMedicalPerYearMonthComposite());
		cti8.setControl(tabbedView.buildDailyReportComposite());
		
		tabFolder.setSelection(0);
	}

	@Override
	public void setFocus() {
	}

	public Properties getApplicationProperties() {
		return applicationProperties;
	}

	public void setApplicationProperties(Properties applicationProperties) {
		this.applicationProperties = applicationProperties;
	}

	public Properties getMessagesProperties() {
		return messagesProperties;
	}

	public void setMessagesProperties(Properties messagesProperties) {
		this.messagesProperties = messagesProperties;
	}

	public Properties getSqlProperties() {
		return sqlProperties;
	}

	public void setSqlProperties(Properties sqlProperties) {
		this.sqlProperties = sqlProperties;
	}
}
