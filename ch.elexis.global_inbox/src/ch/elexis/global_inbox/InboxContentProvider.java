package ch.elexis.global_inbox;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.ui.text.GenericDocument;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.viewers.CommonContentProviderAdapter;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.TimeTool;

public class InboxContentProvider extends CommonContentProviderAdapter {
	ArrayList<File> files = new ArrayList<File>();
	InboxView view;
	LoadJob loader;
	
	public void setView(InboxView view){
		this.view = view;
	}
	
	@Override
	public void dispose(){
		super.dispose();
	}
	
	public IStatus reload(){
		return loader.run(null);
	}
	
	public InboxContentProvider(){
		loader = new LoadJob();
		loader.schedule(1000);
	}
	
	@Override
	public Object[] getElements(Object inputElement){
		return files == null ? null : files.toArray();
	}
	
	Pattern patMatch = Pattern.compile("([0-9]+)_(.+)");
	
	private void addFiles(List<File> list, File dir){
		File[] contents = dir.listFiles();
		for (File file : contents) {
			if (file.isDirectory()) {
				addFiles(list, file);
			} else {
				Matcher matcher = patMatch.matcher(file.getName());
				if (matcher.matches()) {
					String num = matcher.group(1);
					String nam = matcher.group(2);
					List<Patient> lPat = new Query(Patient.class, Patient.FLD_PATID, num).execute();
					if (lPat.size() == 1) {
						Patient pat = lPat.get(0);
						String cat = Activator.getDefault().getCategory(file);
						if (cat.equals("-") || cat.equals("??")) {
							cat = null;
						}
						IDocumentManager dm =
							(IDocumentManager) Extensions
								.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
						try {
							GenericDocument fd =
								new GenericDocument(pat, nam, cat, file,
									new TimeTool().toString(TimeTool.DATE_GER), "", null);
							file.delete();
							dm.addDocument(fd);
							Activator.getDefault().getContentProvider().reload();
							return;
						} catch (Exception ex) {
							ExHandler.handle(ex);
							SWTHelper.alert(Messages.InboxView_error, ex.getMessage());
						}
					}
				}
				list.add(file);
			}
		}
	}
	
	class LoadJob extends Job {
		
		public LoadJob(){
			super("GlobalInbox"); //$NON-NLS-1$
			setPriority(DECORATE);
			setUser(false);
			setSystem(true);
		}
		
		@Override
		protected IStatus run(IProgressMonitor monitor){
			String filepath = CoreHub.localCfg.get(Preferences.PREF_DIR, null);
			File dir = null;
			if (filepath == null) {
				filepath = Preferences.PREF_DIR_DEFAULT;
				CoreHub.localCfg.set(Preferences.PREF_DIR, Preferences.PREF_DIR_DEFAULT);
			}
			dir = new File(filepath);
			if (!dir.isDirectory()) {
				if (view != null) {
					return Status.CANCEL_STATUS;
				} else {
					return Status.OK_STATUS;
				}
			}
			Object dm = Extensions.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
			if (dm == null) {
				return Status.OK_STATUS;
			}
			IDocumentManager documentManager = (IDocumentManager) dm;
			String[] cats = documentManager.getCategories();
			
			if (cats != null) {
				for (String cat : cats) {
					File subdir = new File(dir, cat);
					if (!subdir.exists()) {
						subdir.mkdirs();
					}
				}
			}
			
			files.clear();
			addFiles(files, dir);
			if (view != null) {
				view.reload();
			}
			schedule(120000L);
			return Status.OK_STATUS;
		}
		
	}
	
}
