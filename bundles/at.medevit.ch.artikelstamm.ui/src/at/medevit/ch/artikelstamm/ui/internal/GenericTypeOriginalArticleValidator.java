package at.medevit.ch.artikelstamm.ui.internal;

import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.model.common.preference.PreferenceConstants;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IContextService;

@Component
public class GenericTypeOriginalArticleValidator {

	@Reference
	private IContextService contextService;

	@Reference
	private IConfigService configService;

	@Activate
	public void activate() {
		contextService.getRootContext().setNamed("artikelstamm.selected.article.validate", new Supplier<Boolean>() { //$NON-NLS-1$

			private Boolean ret;

			@SuppressWarnings("unchecked")
			@Override
			public synchronized Boolean get() {
				ret = Boolean.TRUE;
				if (configService.get(PreferenceConstants.PREF_SHOW_WARN_ORIGINAL_ARTICLES_MEDILIST, false)) {
					Optional<IArtikelstammItem> selectedArticle = (Optional<IArtikelstammItem>) contextService
							.getNamed("at.medevit.ch.artikelstamm.elexis.common.ui.selection");
					if (selectedArticle.isPresent() && selectedArticle.get() instanceof IArtikelstammItem) {
						IArtikelstammItem artikelstammItem = selectedArticle.get();
						if ("O".equals(artikelstammItem.getGenericType())) { //$NON-NLS-1$
							Display.getDefault().syncExec(() -> {
								int answer = MessageDialog.open(MessageDialog.WARNING,
										Display.getDefault().getActiveShell(), "Originalpräparat",
										artikelstammItem.getLabel() + " ist ein Originalpräparat mit "
												+ artikelstammItem.getDeductible()
												+ "% Selbstbehalt. Soll dieses Präparat hinzugefügt werden?",
										SWT.NONE, "Ja", "Nein");
								if (answer == 1) {
									ret = Boolean.FALSE;
								}
							});
						}
					}
				}
				return ret;
			}
		});
	}
}
