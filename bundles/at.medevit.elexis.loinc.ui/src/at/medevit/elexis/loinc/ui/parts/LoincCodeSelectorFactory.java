package at.medevit.elexis.loinc.ui.parts;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;

import at.medevit.elexis.loinc.model.LoincCode;
import at.medevit.elexis.loinc.ui.dialogs.LoincSelektor;
import at.medevit.elexis.loinc.ui.providers.LoincCodeControlFieldProvider;
import at.medevit.elexis.loinc.ui.providers.LoincLabelProvider;
import at.medevit.elexis.loinc.ui.providers.LoincTableContentProvider;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.SelectorPanelProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;

public class LoincCodeSelectorFactory extends CodeSelectorFactory {
	SelectorPanelProvider slp;
	CommonViewer cv;
	
	private LoincTableContentProvider contentProvider;
	
	public LoincCodeSelectorFactory(){
		
	}
	
	@Override
	public SelectionDialog getSelectionDialog(Shell parent, Object data){
		return new LoincSelektor(parent, data);
	}
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		this.cv = cv;
		this.contentProvider = new LoincTableContentProvider();
		ViewerConfigurer vc =
			new ViewerConfigurer(contentProvider, new LoincLabelProvider(),
				new LoincCodeControlFieldProvider(cv),
				new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
					SimpleWidgetProvider.TYPE_TABLE, SWT.NONE, null));
		
		ElexisEventDispatcher.getInstance().addListeners(
			new UpdateEventListener(cv, LoincCode.class, ElexisEvent.EVENT_RELOAD));
		
		return vc;
	}
	
	@Override
	public Class getElementClass(){
		return LoincCode.class;
	}
	
	@Override
	public void dispose(){
		cv.dispose();
	}
	
	@Override
	public String getCodeSystemName(){
		return "LOINC"; //$NON-NLS-1$
	}
	
	private class UpdateEventListener extends ElexisUiEventListenerImpl {
		
		CommonViewer viewer;
		
		UpdateEventListener(CommonViewer viewer, final Class<?> clazz, int mode){
			super(clazz, mode);
			this.viewer = viewer;
		}
		
		@Override
		public void runInUi(ElexisEvent ev){
			contentProvider.changed(null);
			viewer.notify(CommonViewer.Message.update);
		}
	}
}
