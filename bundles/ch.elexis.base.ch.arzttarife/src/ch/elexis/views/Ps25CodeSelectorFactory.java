package ch.elexis.views;

import java.time.LocalDate;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import ch.elexis.base.ch.arzttarife.ps25.IPs25Leistung;
import ch.elexis.base.ch.arzttarife.service.ArzttarifeModelServiceHolder;
import ch.elexis.core.model.IEncounter;
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
import jakarta.inject.Inject;

public class Ps25CodeSelectorFactory extends CodeSelectorFactory {

	public static final String SELECTION_NAME = "ch.elexis.views.codeselector.ps25.selection";

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
				ContextServiceHolder.get().getRootContext().setNamed(SELECTION_NAME,
						ss.isEmpty() ? null : (IPs25Leistung) ss.getFirstElement());
			}
		});
		FieldDescriptor<?>[] fd = new FieldDescriptor<?>[] {
				new FieldDescriptor<IPs25Leistung>("Position", "code", null),
				new FieldDescriptor<IPs25Leistung>("Mehrleistung", "mehrleistung", null),
				new FieldDescriptor<IPs25Leistung>("Kapitel", "fachgebietKapitel", null),
				new FieldDescriptor<IPs25Leistung>("Typ", "mehrleistungstyp", null),
				new FieldDescriptor<IPs25Leistung>("Stufe", "stufe", null),
				new FieldDescriptor<IPs25Leistung>("Gueltig von", "validFrom", null),
				new FieldDescriptor<IPs25Leistung>("Gueltig bis", "validTo", null) };
		SelectorPanelProvider slp = new SelectorPanelProvider(fd, true);
		vc = new ViewerConfigurer(new Ps25ContentProvider(cv, slp), new DefaultLabelProvider(), slp,
				new ViewerConfigurer.DefaultButtonProvider(),
				new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_LAZYLIST, SWT.NONE, cv));
		return vc.setContentType(ContentType.GENERICOBJECT);
	}

	private class Ps25ContentProvider extends CommonViewerContentProvider {

		private ControlFieldProvider controlFieldProvider;

		public Ps25ContentProvider(CommonViewer commonViewer, ControlFieldProvider controlFieldProvider) {
			super(commonViewer);
			this.controlFieldProvider = controlFieldProvider;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			IQuery<?> query = getBaseQuery();
			LocalDate date = ContextServiceHolder.get().getTyped(IEncounter.class).map(IEncounter::getDate)
					.orElse(LocalDate.now());
			query.and("validFrom", COMPARATOR.LESS_OR_EQUAL, date);
			query.startGroup();
			query.or("validUntil", COMPARATOR.GREATER_OR_EQUAL, date);
			query.or("validUntil", COMPARATOR.EQUALS, null);
			query.andJoinGroups();

			controlFieldProvider.setQuery(query);
			applyQueryFilters(query);
			query.orderBy("code", ORDER.ASC);
			List<?> elements = query.execute();
			return elements.toArray(new Object[elements.size()]);
		}

		@Override
		protected IQuery<?> getBaseQuery() {
			return ArzttarifeModelServiceHolder.get().getQuery(IPs25Leistung.class);
		}
	}

	@Override
	public void dispose() {
	}

	@Override
	public String getCodeSystemName() {
		return "PS25";
	}

	@Override
	public Class<?> getElementClass() {
		return IPs25Leistung.class;
	}
}
