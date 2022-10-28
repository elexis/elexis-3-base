package ch.elexis.views;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import ch.elexis.base.ch.arzttarife.psycho.IPsychoLeistung;
import ch.elexis.base.ch.arzttarife.service.ArzttarifeModelServiceHolder;
import ch.elexis.core.model.IBillingSystem;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.ch.BillingLaw;
import ch.elexis.core.services.IQuery;
import ch.elexis.core.services.IQuery.COMPARATOR;
import ch.elexis.core.services.IQuery.ORDER;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.CommonViewerContentProvider;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ContentType;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer.ControlFieldProvider;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;

public class PsychoLeistungCodeSelectorFactory extends CodeSelectorFactory {
	private ViewerConfigurer vc;

	@Inject
	public void selectedEncounter(@Optional IEncounter encounter) {
		if (vc != null && vc.getControlFieldProvider() != null) {
			vc.getControlFieldProvider().fireChangedEvent();
		}
	}

	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv) {
		cv.setSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				TableViewer tv = (TableViewer) event.getSource();
				StructuredSelection ss = (StructuredSelection) tv.getSelection();
				if (!ss.isEmpty()) {
					IPsychoLeistung selected = (IPsychoLeistung) ss.getFirstElement();
					ContextServiceHolder.get().getRootContext()
							.setNamed("ch.elexis.views.codeselector.psycho.selection", selected);
				} else {
					ContextServiceHolder.get().getRootContext()
							.setNamed("ch.elexis.views.codeselector.psycho.selection", null);
				}
			}
		});
		FieldDescriptor<?>[] fd = new FieldDescriptor<?>[] {
				new FieldDescriptor<IPsychoLeistung>("Ziffer", "code", null),
				new FieldDescriptor<IPsychoLeistung>("Text", "codeText", null), };
		SelectorPanelProvider slp = new SelectorPanelProvider(fd, true);
		vc = new ViewerConfigurer(new PsychoContentProvider(cv, slp), new DefaultLabelProvider(), slp,
				new ViewerConfigurer.DefaultButtonProvider(),
				new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, cv));
		return vc.setContentType(ContentType.GENERICOBJECT);

	}

	private class PsychoContentProvider extends CommonViewerContentProvider {

		private ControlFieldProvider controlFieldProvider;

		public PsychoContentProvider(CommonViewer commonViewer, ControlFieldProvider controlFieldProvider) {
			super(commonViewer);
			this.controlFieldProvider = controlFieldProvider;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			java.util.Optional<IEncounter> encounter = ContextServiceHolder.get().getTyped(IEncounter.class);
			if (encounter.isPresent() && encounter.get().getCoverage().getBillingSystem().getLaw() == BillingLaw.KVG
					&& isPsycho(encounter.get())) {
				IQuery<?> query = getBaseQuery();

				query.and("validFrom", COMPARATOR.LESS_OR_EQUAL, encounter.get().getDate());
				query.startGroup();
				query.or("validUntil", COMPARATOR.GREATER_OR_EQUAL, encounter.get().getDate());
				query.or("validUntil", COMPARATOR.EQUALS, null);
				query.andJoinGroups();

				// apply filters from control field provider
				controlFieldProvider.setQuery(query);
				applyQueryFilters(query);
				query.orderBy("code", ORDER.ASC);
				List<?> elements = query.execute();

				return elements.toArray(new Object[elements.size()]);
			}
			return Collections.emptyList().toArray();
		}

		@Override
		protected IQuery<?> getBaseQuery() {
			IQuery<IPsychoLeistung> query = ArzttarifeModelServiceHolder.get().getQuery(IPsychoLeistung.class);
			query.and("id", COMPARATOR.NOT_EQUALS, "VERSION");
			return query;
		}
	}

	public static boolean isPsycho(IEncounter encounter) {
		IBillingSystem billingSystem = encounter.getCoverage().getBillingSystem();
		if (billingSystem != null) {
			return "tarpsy".equalsIgnoreCase(billingSystem.getName())
					|| "psycho".equalsIgnoreCase(billingSystem.getName())
					|| "psychotherapie".equals(billingSystem.getName());
		}
		return false;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getCodeSystemName() {
		return "Psychotherapie";
	}

	@Override
	public Class<?> getElementClass() {
		return IPsychoLeistung.class;
	}
}
