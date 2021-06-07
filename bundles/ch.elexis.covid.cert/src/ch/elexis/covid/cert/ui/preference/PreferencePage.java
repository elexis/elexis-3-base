package ch.elexis.covid.cert.ui.preference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.util.CoreUiUtil;
import ch.elexis.covid.cert.service.CertificatesService;
import ch.elexis.covid.cert.service.CertificatesService.Mode;

public class PreferencePage extends org.eclipse.jface.preference.PreferencePage
		implements IWorkbenchPreferencePage {
	
	private ComboViewer modeCombo;
	
	private Text testingCenter;
	
	private Text otpText;
	
	@Inject
	private CertificatesService service;
	
	private Label textLabel;
	
	public PreferencePage(){
		CoreUiUtil.injectServices(this);
	}
	
	public PreferencePage(String title){
		super(title);
	}
	
	public PreferencePage(String title, ImageDescriptor image){
		super(title, image);
	}
	
	@Override
	public void init(IWorkbench workbench){
		
	}
	
	@Override
	protected Control createContents(Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout());
		
		modeCombo = new ComboViewer(ret, SWT.BORDER);
		modeCombo.setContentProvider(ArrayContentProvider.getInstance());
		modeCombo.setLabelProvider(new LabelProvider());
		modeCombo.setInput(CertificatesService.Mode.values());
		modeCombo.setSelection(new StructuredSelection(service.getMode()));
		modeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event){
				service.setMode((Mode) event.getStructuredSelection().getFirstElement());
			}
		});

		testingCenter = new Text(ret, SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		testingCenter.setLayoutData(gd);
		testingCenter.setMessage("Test Ort Name (z.B. Praxis Name) max. 50 Zeichen");
		testingCenter.setToolTipText("Test Ort Name (z.B. Praxis Name) max. 50 Zeichen");
		testingCenter
			.setText(ConfigServiceHolder.get().get(CertificatesService.CFG_TESTCENTERNAME, ""));
		testingCenter.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e){
				ConfigServiceHolder.get().set(CertificatesService.CFG_TESTCENTERNAME,
					testingCenter.getText());
				
			}
		});
		
		Label lbl = new Label(ret, SWT.SEPARATOR | SWT.HORIZONTAL);
		lbl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		textLabel = new Label(ret, SWT.NONE);
		
		otpText = new Text(ret, SWT.MULTI | SWT.BORDER | SWT.WRAP);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 400;
		otpText.setLayoutData(gd);
		otpText
			.setText(ConfigServiceHolder.get().getActiveMandator(CertificatesService.CFG_OTP, ""));
		otpText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e){
				ConfigServiceHolder.get().setActiveMandator(CertificatesService.CFG_OTP,
					otpText.getText());
				ConfigServiceHolder.get().setActiveMandator(CertificatesService.CFG_OTP_TIMESTAMP,
					LocalDateTime.now().toString());
			}
		});
		updateTextLabel();
		return ret;
	}
	
	private void updateTextLabel(){
		String timeStampString = ConfigServiceHolder.get()
			.getActiveMandator(CertificatesService.CFG_OTP_TIMESTAMP, "");
		if (StringUtils.isNotBlank(timeStampString)) {
			LocalDateTime timeStamp = LocalDateTime.parse(timeStampString);
			textLabel.setText("OTP des Mandanten von "
				+ DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").format(timeStamp));
		} else {
			textLabel.setText("OTP des Mandanten");
		}
	}
	
}
