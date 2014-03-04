package ch.medshare.connect.abacusjunior;

import java.io.File;
import java.io.FileNotFoundException;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.Hub;
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.core.ui.importer.div.rs232.Connection;
import ch.elexis.core.ui.importer.div.rs232.Connection.ComPortListener;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.LabItem;
import ch.elexis.data.Patient;
import ch.medshare.connect.abacusjunior.packages.DataPackage;
import ch.medshare.connect.abacusjunior.packages.InitPackage;

public class AbacusJuniorAction extends Action implements ComPortListener {
	
	Connection _ctrl;
	Patient _actPatient;
	Logger _log;
	
	public AbacusJuniorAction(){
		super(Messages.getString("AbacusJuniorAction.ButtonName"), AS_CHECK_BOX);
		setToolTipText(Messages.getString("AbacusJuniorAction.ToolTip"));
		setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin(
			"ch.medshare.connect.abacusjunior", "icons/abacusjunior.ico"));
		
		_ctrl =
			new Connection(Messages.getString("AbacusJuniorAction.ConnectionName"),
				CoreHub.localCfg.get(Preferences.PORT,
					Messages.getString("AbacusJuniorAction.DefaultPort")), CoreHub.localCfg.get(
					Preferences.PARAMS, Messages.getString("AbacusJuniorAction.DefaultParams")),
				this);
		
		if (CoreHub.localCfg.get(Preferences.LOG, "n").equalsIgnoreCase("y")) {
			try {
				_log =
					new Logger(System.getProperty("user.home") + File.separator + "elexis"
						+ File.separator + "abacusjunior.log");
			} catch (FileNotFoundException e) {
				SWTHelper.showError(Messages.getString("AbacusJuniorAction.LogError.Title"),
					Messages.getString("AbacusJuniorAction.LogError.Text"));
				_log = new Logger();
			}
		} else {
			_log = new Logger(false);
		}
	}
	
	@Override
	public void run(){
		if (isChecked()) {
			KontaktSelektor ksl =
				new KontaktSelektor(Hub.getActiveShell(), Patient.class,
					Messages.getString("AbacusJuniorAction.Patient.Title"),
					Messages.getString("AbacusJuniorAction.Patient.Text"), Patient.DEFAULT_SORT);
			ksl.create();
			ksl.getShell().setText(Messages.getString("AbacusJuniorAction.Patient.Title"));
			if (ksl.open() == org.eclipse.jface.dialogs.Dialog.OK) {
				_actPatient = (Patient) ksl.getSelection();
				
				_log.logStart();
				if (_ctrl.connect()) {
					_ctrl.awaitFrame(1, 4, 0, 6000);
					return;
				} else {
					_log.log("Error");
					SWTHelper.showError(Messages.getString("AbacusJuniorAction.RS232.Error.Title"),
						Messages.getString("AbacusJuniorAction.RS232.Error.Text"));
				}
			}
		} else {
			if (_ctrl.isOpen()) {
				_actPatient = null;
				_ctrl.sendBreak();
				_ctrl.close();
			}
		}
		setChecked(false);
		_log.logEnd();
	}
	
	public void gotBreak(final Connection connection){
		_actPatient = null;
		connection.close();
		setChecked(false);
		_log.log("Break");
		_log.logEnd();
		SWTHelper.showError(Messages.getString("AbacusJuniorAction.RS232.Break.Title"),
			Messages.getString("AbacusJuniorAction.RS232.Break.Text"));
	}
	
	public void gotChunk(final Connection connection, final String data){
		_log.logRX(data);
		
		char id = data.charAt(1);
		char type = data.charAt(2);
		String message = data.substring(data.indexOf(2) + 1, data.indexOf(3));
		
		switch (type) {
		case 'I':
			InitPackage initPkg = new InitPackage(id, message);
			_log.logTX(initPkg.getResponse());
			_ctrl.send(initPkg.getResponse());
			break;
		case 'D':
			DataPackage dataPkg = new DataPackage(id, message);
			_ctrl.send(dataPkg.getResponse());
			_log.logTX(dataPkg.getResponse());
			if (dataPkg.getAck()) {
				dataPkg.fetchResults(_actPatient);
				_log.log("Saved");
				_actPatient = null;
				_ctrl.close();
				setChecked(false);
				ElexisEventDispatcher.reload(LabItem.class);
				_log.logEnd();
			}
			break;
		}
	}
	
	public void timeout(){
		_ctrl.close();
		_log.log("Timeout");
		SWTHelper.showError(Messages.getString("AbacusJuniorAction.RS232.Timeout.Title"),
			Messages.getString("AbacusJuniorAction.RS232.Timeout.Text"));
		setChecked(false);
		_log.logEnd();
	}
}
