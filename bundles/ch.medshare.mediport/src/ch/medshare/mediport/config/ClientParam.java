package ch.medshare.mediport.config;

import ch.medshare.mediport.MediPortAbstractPrefPage;
import ch.medshare.util.SystemProperties;

public class ClientParam extends AbstractConfigKeyModel {
	private String name = ""; //$NON-NLS-1$
	private String dir = ""; //$NON-NLS-1$
	private String docattr = MediPortAbstractPrefPage.TIER_PAYANT;
	private String docprinted = "false"; //$NON-NLS-1$
	private String disttype = "0"; //$NON-NLS-1$
	private String printlanguage = "D"; //$NON-NLS-1$
	private String trustcenterean = ""; //$NON-NLS-1$
	private String ispaperinvoice = "false"; //$NON-NLS-1$

	public ClientParam(String name) {
		this.name = name;
	}

	public void add(String[] parts, String value) {
		if (parts.length > 4) {
			add(parts[4], value);
		} else {
			this.dir = value;
		}
	}

	public void add(String key, String value) {
		if (DOCATTR.equals(key)) {
			this.docattr = value;
		} else if (DOCPRINTED.equals(key)) {
			this.docprinted = value;
		} else if (DISTTYPE.equals(key)) {
			this.disttype = value;
		} else if (PRINTLANGUAGE.equals(key)) {
			this.printlanguage = value;
		} else if (TRUSTCENTEREAN.equals(key)) {
			this.trustcenterean = value;
		} else if (ISPAPERINVOICE.equals(key)) {
			this.ispaperinvoice = value;
		} else if (NAME.equals(key)) {
			this.name = value;
		}
	}

	public String toString(String clientPrefix, Integer num) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(clientPrefix + DIR + "." + num + "=" + getDir()); //$NON-NLS-1$ //$NON-NLS-2$
		buffer.append(SystemProperties.LINE_SEPARATOR);
		buffer.append(clientPrefix + DIR + "." + num + "." + NAME + "=" + getName()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		buffer.append(SystemProperties.LINE_SEPARATOR);
		if (!isEmpty(getDocattr())) {
			buffer.append(clientPrefix + DIR + "." + num + "." + DOCATTR + "=" + getDocattr()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			buffer.append(SystemProperties.LINE_SEPARATOR);
		}
		if (!isEmpty(getDocprinted())) {
			buffer.append(clientPrefix + DIR + "." + num + "." + DOCPRINTED + "=" + getDocprinted()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			buffer.append(SystemProperties.LINE_SEPARATOR);
		}
		if (!isEmpty(getDisttype())) {
			buffer.append(clientPrefix + DIR + "." + num + "." + DISTTYPE + "=" + getDisttype()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			buffer.append(SystemProperties.LINE_SEPARATOR);
		}
		if (!isEmpty(getPrintlanguage())) {
			buffer.append(clientPrefix + DIR + "." + num + "." + PRINTLANGUAGE + "=" + getPrintlanguage()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			buffer.append(SystemProperties.LINE_SEPARATOR);
		}
		if (!isEmpty(getTrustcenterean())) {
			buffer.append(clientPrefix + DIR + "." + num + "." + TRUSTCENTEREAN + "=" + getTrustcenterean()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			buffer.append(SystemProperties.LINE_SEPARATOR);
		}
		if (!isEmpty(getIspaperinvoice())) {
			buffer.append(clientPrefix + DIR + "." + num + "." + ISPAPERINVOICE + "=" + getIspaperinvoice()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			buffer.append(SystemProperties.LINE_SEPARATOR);
		}

		return buffer.toString();
	}

	public String getDir() {
		return this.dir;
	}

	public void setDir(String value) {
		String oldValue = this.dir;
		this.dir = value;
		propertyChanged(value, oldValue);
	}

	public String getDocattr() {
		return docattr;
	}

	public void setDocattr(String docattr) {
		String oldValue = this.docattr;
		this.docattr = docattr;
		propertyChanged(docattr, oldValue);
	}

	public String getDocprinted() {
		return docprinted;
	}

	public void setDocprinted(String docprinted) {
		String oldValue = this.docprinted;
		this.docprinted = docprinted;
		propertyChanged(docprinted, oldValue);
	}

	public String getDisttype() {
		return disttype;
	}

	public void setDisttype(String disttype) {
		String oldValue = this.disttype;
		this.disttype = disttype;
		propertyChanged(disttype, oldValue);
	}

	public String getPrintlanguage() {
		return printlanguage;
	}

	public void setPrintlanguage(String printlanguage) {
		String oldValue = this.printlanguage;
		this.printlanguage = printlanguage;
		propertyChanged(printlanguage, oldValue);
	}

	public String getTrustcenterean() {
		return trustcenterean;
	}

	public void setTrustcenterean(String trustcenterean) {
		String oldValue = this.trustcenterean;
		this.trustcenterean = trustcenterean;
		propertyChanged(trustcenterean, oldValue);
	}

	public String getIspaperinvoice() {
		return ispaperinvoice;
	}

	public void setIspaperinvoice(String ispaperinvoice) {
		String oldValue = this.ispaperinvoice;
		this.ispaperinvoice = ispaperinvoice;
		propertyChanged(ispaperinvoice, oldValue);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String oldValue = this.name;
		this.name = name;
		propertyChanged(name, oldValue);
	}
}
