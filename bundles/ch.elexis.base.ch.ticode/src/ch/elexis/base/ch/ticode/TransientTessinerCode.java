package ch.elexis.base.ch.ticode;

import org.apache.commons.lang3.StringUtils;

import ch.elexis.core.model.IDiagnosis;
import ch.elexis.core.model.IXid;

public class TransientTessinerCode implements IDiagnosis {

	private String code;

	public TransientTessinerCode(String code) {
		this.code = code;
	}

	@Override
	public String getDescription() {
		return code;
	}

	@Override
	public void setDescription(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getCodeSystemName() {
		return "TI-Code";
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public void setCode(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getText() {
		return getDescription();
	}

	@Override
	public void setText(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getId() {
		return getCode();
	}

	@Override
	public String getLabel() {
		return getCode() + StringUtils.SPACE + getText();
	}

	@Override
	public boolean addXid(String domain, String id, boolean updateIfExists) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IXid getXid(String domain) {
		return null;
	}

	@Override
	public Long getLastupdate() {
		return 0L;
	}
}
