package ch.elexis.icpc.model.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import ch.elexis.core.jpa.entities.ICPCCode;
import ch.elexis.core.jpa.model.adapter.AbstractIdModelAdapter;
import ch.elexis.core.model.IDiagnosisTree;
import ch.elexis.core.model.IXid;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.holder.XidServiceHolder;
import ch.elexis.icpc.model.icpc.IcpcCode;
import ch.elexis.icpc.model.internal.service.IcpcModelServiceHolder;

public class Code extends AbstractIdModelAdapter<ICPCCode> implements Identifiable, IcpcCode {

	public static final String[] classes = { Messages.IcpcCode_class_A, Messages.IcpcCode_class_B,
			Messages.IcpcCode_class_D, Messages.IcpcCode_class_F, Messages.IcpcCode_class_H, Messages.IcpcCode_class_K,
			Messages.IcpcCode_class_L, Messages.IcpcCode_class_N, Messages.IcpcCode_class_P, Messages.IcpcCode_class_R,
			Messages.IcpcCode_class_S, Messages.IcpcCode_class_T, Messages.IcpcCode_class_U, Messages.IcpcCode_class_W,
			Messages.IcpcCode_class_X, Messages.IcpcCode_class_Y, Messages.IcpcCode_class_Z };

	public static final String[] components = { Messages.IcpcCode_comp_1, Messages.IcpcCode_comp_2,
			Messages.IcpcCode_comp_3, Messages.IcpcCode_comp_4, Messages.IcpcCode_comp_5, Messages.IcpcCode_comp_6,
			Messages.IcpcCode_comp_7 };

	public static final String CODESYSTEM_NAME = "ICPC"; //$NON-NLS-1$

	private static List<IDiagnosisTree> rootCodes;

	private IDiagnosisTree parent;

	public static List<IDiagnosisTree> getRootCodes() {
		if (rootCodes == null) {
			initialize();
		}
		return rootCodes;
	}

	public Code(ICPCCode entity) {
		super(entity);
		if (rootCodes == null) {
			initialize();
		}
	}

	private static void initialize() {
		rootCodes = new ArrayList<>();
		for (String string : classes) {
			IDiagnosisTree classCode = new TransientCode(string);
			rootCodes.add(classCode);
			for (String comp : components) {
				IDiagnosisTree compCode = new TransientCode(comp);
				compCode.setParent(classCode);
				((TransientCode) classCode).addChild(compCode);
			}
		}
	}

	@Override
	public IDiagnosisTree getParent() {
		return parent;
	}

	@Override
	public void setParent(IDiagnosisTree value) {
		parent = value;
	}

	@Override
	public List<IDiagnosisTree> getChildren() {
		return Collections.emptyList();
	}

	@Override
	public String getDescription() {
		return getEntity().getText();
	}

	@Override
	public void setDescription(String value) {
		getEntity().setText(value);
	}

	@Override
	public String getCodeSystemName() {
		return CODESYSTEM_NAME;
	}

	@Override
	public String getCode() {
		return getEntity().getId();
	}

	@Override
	public void setCode(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getText() {
		return getEntity().getShortName();
	}

	@Override
	public void setText(String value) {
		getEntity().setShortName(value);
	}

	@Override
	public String getLabel() {
		return getId() + StringUtils.SPACE + getText();
	}

	@Override
	public String getIcd10() {
		return getEntity().getIcd10();
	}

	@Override
	public void setIcd10(String value) {
		getEntity().setIcd10(value);
	}

	@Override
	public String getCriteria() {
		return getEntity().getCriteria();
	}

	@Override
	public void setCriteria(String value) {
		getEntity().setCriteria(value);
	}

	@Override
	public String getInclusion() {
		return getEntity().getInclusion();
	}

	@Override
	public void setInclusion(String value) {
		getEntity().setInclusion(value);
	}

	@Override
	public String getExclusion() {
		return getEntity().getExclusion();
	}

	@Override
	public void setExclusion(String value) {
		getEntity().setExclusion(value);
	}

	@Override
	public String getNote() {
		return getEntity().getNote();
	}

	@Override
	public void setNote(String value) {
		getEntity().setNote(value);
	}

	@Override
	public String getConsider() {
		return getEntity().getConsider();
	}

	@Override
	public void setConsider(String value) {
		getEntity().setConsider(value);
	}

	@Override
	public boolean addXid(String domain, String id, boolean updateIfExists) {
		return XidServiceHolder.get().addXid(this, domain, id, updateIfExists);
	}

	@Override
	public IXid getXid(String domain) {
		return XidServiceHolder.get().getXid(this, domain);
	}

	private static class TransientCode implements IDiagnosisTree {

		private List<IDiagnosisTree> children;

		private IDiagnosisTree parent;

		private String code;
		private String text;

		public TransientCode(String string) {
			String[] parts = string.split(": "); //$NON-NLS-1$
			code = parts[0];
			text = parts[1];
		}

		public void addChild(IDiagnosisTree diagnosis) {
			if (children == null) {
				children = new ArrayList<>();
			}
			children.add(diagnosis);
		}

		@Override
		public String getDescription() {
			return text;
		}

		@Override
		public void setDescription(String value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getCodeSystemName() {
			return CODESYSTEM_NAME;
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
			return text;
		}

		@Override
		public void setText(String value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getId() {
			return code;
		}

		@Override
		public String getLabel() {
			return code + ": " + text; //$NON-NLS-1$
		}

		@Override
		public boolean addXid(String domain, String id, boolean updateIfExists) {
			throw new UnsupportedOperationException();
		}

		@Override
		public IXid getXid(String domain) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Long getLastupdate() {
			return 0L;
		}

		@Override
		public IDiagnosisTree getParent() {
			return parent;
		}

		@Override
		public void setParent(IDiagnosisTree value) {
			parent = value;
		}

		@SuppressWarnings("unchecked")
		@Override
		public List<IDiagnosisTree> getChildren() {
			if (NumberUtils.isCreatable(code) && children == null) {
				// perform db lookup
				IQuery<IcpcCode> query = IcpcModelServiceHolder.get().getQuery(IcpcCode.class);
				query.and("id", COMPARATOR.LIKE, parent.getId() + "%"); //$NON-NLS-1$ //$NON-NLS-2$
				query.and("component", COMPARATOR.EQUALS, getId()); //$NON-NLS-1$
				children = (List<IDiagnosisTree>) (List<?>) query.execute();
				children.forEach(c -> c.setParent(this));
				return children;
			}
			return children;
		}
	}
}
