package ch.elexis.base.ch.arzttarife.tardoc.model;

import java.util.List;
import java.util.Optional;

import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.services.IContext;
import ch.elexis.core.services.holder.ContextServiceHolder;

/**
 * Class containing custom adjustments to {@link TardocLimitation} instances
 * that are not part of the std. TARDOC implementation.
 */
public class TardocLimitationAdjuster {

	private static List<Adjuster> adjusters = List.of(new AdjusterAA250030());

	public static boolean adjust(TardocLimitation limitation) {
		if (limitation != null && limitation.getTardocLeistung() != null) {
			adjusters.stream().filter(a -> a.canAdjust(limitation)).forEach(a -> a.adjust(limitation));
		}
		return false;
	}

	public static interface Adjuster {

		public boolean canAdjust(TardocLimitation limitation);

		public void adjust(TardocLimitation limitation);
	}

	private static class AdjusterAA250030 implements Adjuster {

		@Override
		public boolean canAdjust(TardocLimitation limitation) {
			return "AA.25.0030".equals(limitation.getTardocLeistung().getCode());
		}

		@Override
		public void adjust(TardocLimitation limitation) {
			ContextServiceHolder.get().getActiveCoverage().ifPresent(coverage -> {
				if (coverage.getBillingSystem() != null && coverage.getBillingSystem().getLaw() != null
						&& coverage.getBillingSystem().getLaw() == BillingLaw.IV) {
					limitation.setAmount(40);
					IContext adjustContext = ContextServiceHolder.get()
							.getNamedContext(TardocLimitationAdjuster.class.getSimpleName())
							.orElseGet(() -> ContextServiceHolder
									.get().createNamedContext(TardocLimitationAdjuster.class.getSimpleName()));
					adjustContext.setTyped(limitation.getTardocLeistung());
				}
			});
		}
	}

	public static boolean limitWasAdjustedFor(TardocLeistung leistung) {
		Optional<IContext> context = ContextServiceHolder.get()
				.getNamedContext(TardocLimitationAdjuster.class.getSimpleName());
		if (context.isPresent()) {
			Optional<TardocLeistung> adjustedLeistung = context.get().getTyped(TardocLeistung.class);
			return adjustedLeistung.isPresent() && adjustedLeistung.get().getId().equals(leistung.getId());
		}
		return false;
	}

	public static void clearContext() {
		Optional<IContext> context = ContextServiceHolder.get()
				.getNamedContext(TardocLimitationAdjuster.class.getSimpleName());
		if (context.isPresent()) {
			ContextServiceHolder.get().releaseContext(TardocLimitationAdjuster.class.getSimpleName());
		}
	}
}
