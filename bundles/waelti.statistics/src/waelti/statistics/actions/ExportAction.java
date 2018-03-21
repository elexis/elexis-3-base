package waelti.statistics.actions;

import java.io.IOException;
import java.util.Calendar;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import waelti.statistics.export.CSVWriter;
import waelti.statistics.views.OutputView;
import ch.elexis.core.ui.util.SWTHelper;

public class ExportAction extends Action {
	
	private OutputView view;
	
	private static String extension = "csv";
	
	/** constructor */
	public ExportAction(){
		super();
		this.setText("Export");
		this.setToolTipText("Exportiert die Auswertung als CSV.");
		this.setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("Waelti.Statistics",
			"icons/page_excel.png"));
	}
	
	public ExportAction(OutputView view){
		this();
		this.view = view;
	}
	
	@Override
	public void run(){
		FileDialog chooser = new FileDialog(this.view.getSite().getShell(), SWT.SAVE);
		chooser.setFilterExtensions(new String[] {
			extension
		});
		
		String defaultName = this.getNameSuggestion();
		chooser.setFileName(defaultName);
		
		String fileName = chooser.open();
		if (fileName != null) {
			this.saveFile(fileName);
		}
	}
	
	private void saveFile(String fileName){
		String name = fileName + "." + extension;
		try {
			CSVWriter.write(this.view.getQuery(), name);
		} catch (IOException e) {
			// TODO LOG
			SWTHelper.showError("Exportfehler",
				"Beim Erstellen der CSV-Datei ist ein Fehler aufgetreten.");
		}
	}
	
	private String getNameSuggestion(){
		String name = this.view.getQuery().getTitle().toLowerCase();
		name += " ";
		name += Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		name += " ";
		name += (Calendar.getInstance().get(Calendar.MONTH) + 1);
		name += " ";
		name += Calendar.getInstance().get(Calendar.YEAR);
		return name;
	}
}
