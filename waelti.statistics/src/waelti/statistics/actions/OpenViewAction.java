package waelti.statistics.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;

import waelti.statistics.views.OutputView;

/**
 * This action opens the statistic view, containing all results and actions.
 * 
 * @author michael waelti
 * @see IWorkbenchWindowActionDelegate
 * @see OutputView
 */
public class OpenViewAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;
	
	/** Name of the view to be opened when the action is activated. */
	private String outputViewName = "waelti.statistics.views.OutputView";
	
	/**
	 * The constructor.
	 */
	public OpenViewAction(){}
	
	/**
	 * The action has been activated. The argument of the method represents the 'real' action
	 * sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action){
		try {
			window.getActivePage().showView(outputViewName);
		} catch (PartInitException e) {
			// TODO Logging
			MessageDialog.openInformation(window.getShell(), "ElexisStatistics Plug-in",
				"Error while opening output view.");
		}
	}
	
	/**
	 * Selection in the workbench has been changed. We can change the state of the 'real' action
	 * here if we want, but this can only happen after the delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection){}
	
	/**
	 * We can use this method to dispose of any system resources we previously allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose(){}
	
	/**
	 * We will cache window object in order to be able to provide parent shell for the message
	 * dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window){
		this.window = window;
	}
}