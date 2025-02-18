package at.medevit.elexis.loinc.ui.parts;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;

import at.medevit.elexis.loinc.model.LoincCode;
import at.medevit.elexis.loinc.ui.dialogs.LoincSelektor;
import at.medevit.elexis.loinc.ui.providers.LoincCodeControlFieldProvider;
import at.medevit.elexis.loinc.ui.providers.LoincLabelProvider;
import at.medevit.elexis.loinc.ui.providers.LoincTableContentProvider;
import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.data.service.ContextServiceHolder;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import jakarta.inject.Inject;

public class LoincCodeSelectorFactory extends CodeSelectorFactory {
	SelectorPanelProvider slp;
	CommonViewer cv;

	private LoincTableContentProvider contentProvider;
	private ISelectionChangedListener selChangeListener = new ISelectionChangedListener() {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			TableViewer tv = (TableViewer) event.getSource();
			StructuredSelection ss = (StructuredSelection) tv.getSelection();

			if (!ss.isEmpty()) {
				LoincCode ea = (LoincCode) ss.getFirstElement();
				ContextServiceHolder.get().getRootContext().setNamed("at.medevit.elexis.loinc.ui.parts.selection", ea);
			} else {
				ContextServiceHolder.get().getRootContext().setNamed("at.medevit.elexis.loinc.ui.parts.selection",
						null);
			}
		}
	};

	public LoincCodeSelectorFactory() {

	}

	@Override
	public SelectionDialog getSelectionDialog(Shell parent, Object data) {
		return new LoincSelektor(parent, data);
	}

	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv) {
		this.cv = cv;
		this.contentProvider = new LoincTableContentProvider();
		ViewerConfigurer vc = new ViewerConfigurer(contentProvider, new LoincLabelProvider(),
				new LoincCodeControlFieldProvider(cv), new ViewerConfigurer.DefaultButtonProvider(),
				new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_TABLE, SWT.NONE, null));

		cv.setNamedSelection("at.medevit.elexis.loinc.ui.parts.selection");
		cv.setSelectionChangedListener(selChangeListener);

		return vc;
	}

	@Override
	public Class getElementClass() {
		return LoincCode.class;
	}

	@Override
	public void dispose() {
		cv.dispose();
	}

	@Override
	public String getCodeSystemName() {
		return "LOINC"; //$NON-NLS-1$
	}

	@Optional
	@Inject
	void reloadLoinc(@UIEventTopic(ElexisEventTopics.EVENT_RELOAD) Class<?> clazz) {
		if (LoincCode.class.equals(clazz)) {
			contentProvider.changed(null);
			cv.notify(CommonViewer.Message.update);
		}
	}
}
