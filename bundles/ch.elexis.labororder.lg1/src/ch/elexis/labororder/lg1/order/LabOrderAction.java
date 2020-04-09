package ch.elexis.labororder.lg1.order;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Patient;
import ch.elexis.labororder.lg1.messages.Messages;
import com.google.gson.Gson;

public class LabOrderAction extends Action {
	
	private static final String TEXT_ENCODING = "ISO-8859-1";
	
	public LabOrderAction(){
		setId("ch.elexis.laborder.lg1.laborder"); //$NON-NLS-1$
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.labororder.lg1", //$NON-NLS-1$
			"rsc/lg1_logo.png"));
		setText(Messages.LabOrderAction_nameAction);
	}
	
	@Override
	public void run(){
		Patient patient = ElexisEventDispatcher.getSelectedPatient();
		if (patient != null) {
			try {
				sendPostRequest(patient);
			} catch (IOException e) {
				LoggerFactory.getLogger(getClass()).error("Error contacting LG1 web service", e);
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Fehler",
					"Es ist ein Fehler beim LG1 Aufruf aufgetreten.\n\n"
						+ e.getLocalizedMessage());
			}
		}
	}
	
	private void sendPostRequest(Patient patient) throws IOException{
		HttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost("https://ms2.medapp.ch/rest/lg1/v1/call-medapp/");

		// Request parameters and other properties.
		List<NameValuePair> params = new ArrayList<NameValuePair>(2);
		params.add(new BasicNameValuePair("appkey", "99987id76opkliuhj79joiplhji1doio987poku"));
		params.add(new BasicNameValuePair("service", "orderentry"));
		params.add(new BasicNameValuePair("data", getData(patient)));
		httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		
		//Execute and get the response.
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity entity = response.getEntity();
		
		if (entity != null) {
			try (InputStream instream = entity.getContent()) {
				String responseText = IOUtils.toString(instream, "UTF-8");
				ResponseDialog dialog =
						new ResponseDialog(responseText, Display.getDefault().getActiveShell());
				dialog.open();
			}
		}
	}
	
	private String getData(Patient patient){
		Gson gson = new Gson();
		ch.elexis.labororder.lg1.order.model.Patient lg1Patient =
			ch.elexis.labororder.lg1.order.model.Patient.of(patient);
		Map<String, ch.elexis.labororder.lg1.order.model.Patient> map = new HashMap<>();
		map.put("patient", lg1Patient);
		return gson.toJson(map);
	}
}
