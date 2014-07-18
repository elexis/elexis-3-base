package ch.medshare.mediport.config;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import ch.elexis.core.data.util.SortedList;
import ch.medshare.mediport.util.MediPortHelper;
import ch.medshare.util.SystemProperties;
import ch.medshare.util.UtilMisc;

public class Client extends AbstractConfigKeyModel {
	private String ean = ""; //$NON-NLS-1$
	private String send_dir = "data/send"; //$NON-NLS-1$
	private String receive_dir = "data/receive"; //$NON-NLS-1$
	private String receivetest_dir = "data/receive/test"; //$NON-NLS-1$
	private String error_dir = "data/error"; //$NON-NLS-1$
	private String docstat_dir = "data/docstatus"; //$NON-NLS-1$
	private String partner_file = "data/partner/partnerinfo.txt"; //$NON-NLS-1$
	private String stylesheet = ""; //$NON-NLS-1$
	
	public Map<Integer, ClientParam> paramMap = new HashMap<Integer, ClientParam>();
	
	public Client(String installDir){
		super();
		if (installDir != null && installDir.length() > 0) {
			setSend_dir(installDir + "/" + send_dir); //$NON-NLS-1$
			setReceive_dir(installDir + "/" + receive_dir); //$NON-NLS-1$
			setReceivetest_dir(installDir + "/" + receivetest_dir); //$NON-NLS-1$
			setError_dir(installDir + "/" + error_dir); //$NON-NLS-1$
			setDocstat_dir(installDir + "/" + docstat_dir); //$NON-NLS-1$
			setPartner_file(installDir + "/" + partner_file); //$NON-NLS-1$
		}
		setStylesheet(UtilMisc.replaceWithForwardSlash(MediPortHelper
			.getPluginDirectory("ch.medshare.mediport")
			+ File.separator + "rsc" + File.separator + "mediport_response.xsl"));
	}
	
	// C:/src/elexis/workspace/medshare-mediport/MDInvoiceRequest_400.xsd, müsste aber sein:
	// C:/Programme/Elexis131/plugins/ch.medshare.mediport_1.0.1/MDInvoiceRequest_400.xsd
	
	public void add(String[] parts, String value){
		if (DIR.equals(parts[2])) { // Parameter
			Integer num = Integer.parseInt(parts[3]);
			ClientParam param = paramMap.get(num);
			if (param == null) {
				param = new ClientParam(num.toString());
				paramMap.put(num, param);
			}
			param.add(parts, value);
		} else {
			add(parts[2], value);
		}
	}
	
	public void add(final String key, String value){
		if (EAN.equals(key)) {
			setEan(value);
		} else if (SEND_DIR.equals(key)) {
			setSend_dir(value);
		} else if (RECEIVE_DIR.equals(key)) {
			setReceive_dir(value);
		} else if (RECEIVETEST_DIR.equals(key)) {
			setReceivetest_dir(value);
		} else if (ERROR_DIR.equals(key)) {
			setError_dir(value);
		} else if (DOCSTAT_DIR.equals(key)) {
			setDocstat_dir(value);
		} else if (PARTNER_FILE.equals(key)) {
			setPartner_file(value);
		}
	}
	
	public int addNewParam(ClientParam param){
		int nextNumber = getNextParamKey();
		paramMap.put(nextNumber, param);
		return nextNumber;
	}
	
	private int getNextParamKey(){
		int maxNumber = 0;
		for (Integer key : paramMap.keySet()) {
			if (key.intValue() > maxNumber) {
				maxNumber = key;
			}
		}
		return maxNumber + 1;
	}
	
	public ClientParam getParam(Integer pNum){
		if (pNum == null) {
			return null;
		}
		return paramMap.get(pNum);
	}
	
	public Integer getParamKey(String paramBez){
		if (paramBez == null) {
			return null;
		}
		for (Integer paramNum : paramMap.keySet()) {
			ClientParam param = paramMap.get(paramNum);
			if (paramBez.equals(param.getName())) {
				return paramNum;
			}
		}
		return null;
	}
	
	public List<String> getParamNames(){
		SortedList<String> nameList = new SortedList<String>(new StringComparator());
		for (ClientParam param : paramMap.values()) {
			nameList.add(param.getName());
		}
		nameList.sort();
		return nameList;
	}
	
	public List<Integer> getParamKeys(){
		List<Integer> keyList = new Vector<Integer>();
		for (Integer key : paramMap.keySet()) {
			keyList.add(key);
		}
		return keyList;
	}
	
	public String toString(Integer num){
		StringBuffer buffer = new StringBuffer();
		String clientPrefix = CLIENT + "." + num + "."; //$NON-NLS-1$ //$NON-NLS-2$
		buffer.append(clientPrefix + EAN + "=" + getEan()); //$NON-NLS-1$
		buffer.append(SystemProperties.LINE_SEPARATOR);
		buffer.append(clientPrefix + SEND_DIR + "=" + getSend_dir()); //$NON-NLS-1$
		buffer.append(SystemProperties.LINE_SEPARATOR);
		buffer.append(clientPrefix + RECEIVE_DIR + "=" + getReceive_dir()); //$NON-NLS-1$
		buffer.append(SystemProperties.LINE_SEPARATOR);
		buffer.append(clientPrefix + RECEIVETEST_DIR + "=" //$NON-NLS-1$
			+ this.receivetest_dir);
		buffer.append(SystemProperties.LINE_SEPARATOR);
		buffer.append(clientPrefix + ERROR_DIR + "=" + getError_dir()); //$NON-NLS-1$
		buffer.append(SystemProperties.LINE_SEPARATOR);
		buffer.append(clientPrefix + DOCSTAT_DIR + "=" + getDocstat_dir()); //$NON-NLS-1$
		buffer.append(SystemProperties.LINE_SEPARATOR);
		buffer.append(clientPrefix + PARTNER_FILE + "=" + getPartner_file()); //$NON-NLS-1$
		buffer.append(SystemProperties.LINE_SEPARATOR);
		
		for (Integer pNum : paramMap.keySet()) {
			ClientParam param = getParam(pNum);
			buffer.append(param.toString(clientPrefix, pNum));
		}
		
		return buffer.toString();
	}
	
	public String getEan(){
		return ean;
	}
	
	public void setEan(String ean){
		String oldValue = this.ean;
		this.ean = ean;
		propertyChanged(ean, oldValue);
	}
	
	public String getSend_dir(){
		return send_dir;
	}
	
	public void setSend_dir(String send_dir){
		String oldValue = this.send_dir;
		this.send_dir = send_dir;
		propertyChanged(send_dir, oldValue);
	}
	
	public String getReceive_dir(){
		return receive_dir;
	}
	
	public void setReceive_dir(String receive_dir){
		String oldValue = this.receive_dir;
		this.receive_dir = receive_dir;
		propertyChanged(receive_dir, oldValue);
	}
	
	public String getReceivetest_dir(){
		return receivetest_dir;
	}
	
	public void setReceivetest_dir(String receivetest_dir){
		String oldValue = this.receivetest_dir;
		this.receivetest_dir = receivetest_dir;
		propertyChanged(receivetest_dir, oldValue);
	}
	
	public String getError_dir(){
		return error_dir;
	}
	
	public void setError_dir(String error_dir){
		String oldValue = this.error_dir;
		this.error_dir = error_dir;
		propertyChanged(error_dir, oldValue);
	}
	
	public String getDocstat_dir(){
		return docstat_dir;
	}
	
	public void setDocstat_dir(String docstat_dir){
		String oldValue = this.docstat_dir;
		this.docstat_dir = docstat_dir;
		propertyChanged(docstat_dir, oldValue);
	}
	
	public String getPartner_file(){
		return partner_file;
	}
	
	public void setPartner_file(String partner_file){
		String oldValue = this.partner_file;
		this.partner_file = partner_file;
		propertyChanged(partner_file, oldValue);
	}
	
	public String getStylesheet(){
		return stylesheet;
	}
	
	public void setStylesheet(String stylesheet){
		String oldValue = this.stylesheet;
		this.stylesheet = stylesheet;
		propertyChanged(stylesheet, oldValue);
	}
	
	private class StringComparator implements Comparator<String> {
		public int compare(String s1, String s2){
			return s1.compareTo(s2);
		}
	}
}