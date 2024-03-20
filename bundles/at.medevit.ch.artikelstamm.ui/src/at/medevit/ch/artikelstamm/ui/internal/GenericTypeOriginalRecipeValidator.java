package at.medevit.ch.artikelstamm.ui.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.model.common.preference.PreferenceConstants;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.IRecipe;
import ch.elexis.core.services.IConfigService;
import ch.elexis.core.services.IContextService;

@Component
public class GenericTypeOriginalRecipeValidator {

	@Reference
	private IContextService contextService;

	@Reference
	private IConfigService configService;

	@Activate
	public void activate() {
		contextService.getRootContext().setNamed("artikelstamm.selected.recipe.validate", new Supplier<Boolean>() { //$NON-NLS-1$

			private Boolean ret;

			@Override
			public synchronized Boolean get() {
				ret = Boolean.TRUE;
				if (configService.get(PreferenceConstants.PREF_SHOW_WARN_ORIGINAL_ARTICLES_RECIPE, false)) {
					Optional<IRecipe> selectedRecipe = contextService.getTyped(IRecipe.class);
					if (selectedRecipe.isPresent()) {
						List<IArtikelstammItem> originals = new ArrayList<>();
						for (IPrescription prescription : selectedRecipe.get().getPrescriptions()) {
							if (prescription.getArticle() instanceof IArtikelstammItem
									&& "O".equals(((IArtikelstammItem) prescription.getArticle()).getGenericType())) { //$NON-NLS-1$
								originals.add((IArtikelstammItem) prescription.getArticle());
							}
						}
						if (!originals.isEmpty()) {
							Display.getDefault().syncExec(() -> {
								int answer = MessageDialog.open(MessageDialog.WARNING,
										Display.getDefault().getActiveShell(), "Originalpräparat",
										"Folgende Originalpräparate sind auf dem Rezept\n\n"
												+ originals.stream()
														.map(a -> a.getLabel() + ", Selbstbehalt " + a.getDeductible()
																+ "%")
														.collect(Collectors.joining("\n"))
												+ "\n\nSoll dieses Rezept gedruckt werden?",
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
