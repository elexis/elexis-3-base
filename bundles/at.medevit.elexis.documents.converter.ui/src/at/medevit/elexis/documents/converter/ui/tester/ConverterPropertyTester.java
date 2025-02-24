package at.medevit.elexis.documents.converter.ui.tester;

import org.eclipse.core.expressions.PropertyTester;

import ch.elexis.core.services.IDocumentConverter;
import ch.elexis.core.ui.e4.util.CoreUiUtil;
import jakarta.inject.Inject;

public class ConverterPropertyTester extends PropertyTester {

	@Inject
	private IDocumentConverter converter;

	public ConverterPropertyTester() {
		CoreUiUtil.injectServices(this);
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if ("available".equals(property)) {
			return converter.isAvailable();
		}
		return false;
	}

}
