package ch.elexis.extdoc.omnivore;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.LoggerFactory;

import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.model.ICategory;
import ch.elexis.core.model.IDocument;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.extdoc.preferences.PreferenceConstants;
import ch.elexis.extdoc.util.MatchPatientToPath;

public class OmnivoreImporter {
	
	public boolean isAvailable(){
		return DocumentStoreHolder.get().isPresent();
	}
	
	public Optional<ICategory> getCategory(){
		if (DocumentStoreHolder.get().isPresent()) {
			CategorySelectionDialog dialog = new CategorySelectionDialog(Display.getDefault().getActiveShell());
			if (dialog.open() == Window.OK) {
				return Optional.ofNullable(dialog.getSelectedCategory());
			}
		}
		return Optional.empty();
	}
	
	public class CategorySelectionDialog extends TitleAreaDialog {
		
		private ComboViewer categoriesViewer;
		
		private ICategory selectedCategory;
		
		public CategorySelectionDialog(Shell parentShell){
			super(parentShell);
		}
		
		public ICategory getSelectedCategory(){
			return selectedCategory;
		}
		
		@Override
		public void create(){
			super.create();
			setTitle("Omnivore Kategorie Auswahl");
			setMessage(
				"Omnivore Kategorie in die die ext. Dokumente importiert werden sollen auswählen.");
		}
		
		@Override
		protected Control createContents(Composite parent){
			Composite ret = new Composite(parent, SWT.NONE);
			ret.setLayout(new GridLayout());
			ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
			
			categoriesViewer = new ComboViewer(ret, SWT.BORDER);
			categoriesViewer.setContentProvider(ArrayContentProvider.getInstance());
			categoriesViewer.setLabelProvider(new LabelProvider() {
				@Override
				public String getText(Object element){
					if (element instanceof ICategory) {
						return ((ICategory) element).getName();
					}
					return super.getText(element);
				}
			});
			categoriesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent event){
					IStructuredSelection selection = categoriesViewer.getStructuredSelection();
					if (selection.isEmpty()) {
						selectedCategory = null;
					} else {
						selectedCategory = (ICategory) selection.getFirstElement();
					}
				}
			});
			
			return ret;
		}
	}
	
	public void importAll(ICategory category, IProgressMonitor progress){
		Query<Patient> query = new Query<Patient>(Patient.class);
		List<Patient> allPatients = query.execute();
		progress.beginTask("Externe Dokumente Importieren ...", allPatients.size());
		
		String[] activePaths = PreferenceConstants.getActiveBasePaths();
		for (Patient patient : allPatients) {
			importPatient(category, patient, activePaths);
			progress.worked(1);
			if (progress.isCanceled()) {
				break;
			}
		}
	}
	
	public void importPatient(ICategory category, Patient patient, String[] activePaths){
		Object object = MatchPatientToPath.getFilesForPatient(patient, activePaths);
		if (object instanceof List) {
			@SuppressWarnings("unchecked")
			List<File> list = (List<File>) object;
			List<IDocument> importedDocuments = DocumentStoreHolder.get().get()
					.getDocuments(patient.getId(), null, category, null);
			for (File file : list) {
				boolean imported = false;
				String fileName = file.getName();
				if (!importedDocuments.isEmpty()) {
					for (IDocument iDocument : importedDocuments) {
						if (iDocument.getTitle().equals(fileName)) {
							imported = true;
						}
					}
				}
				if (!imported) {
					IDocument document =
						DocumentStoreHolder.get().get().createDocument(patient.getId(),
						fileName, category.getName());
					try (FileInputStream fis = new FileInputStream(file)) {
						DocumentStoreHolder.get().get().saveDocument(document, fis);
					} catch (ElexisException | IOException e) {
						LoggerFactory.getLogger(getClass())
							.error("Error importing file [" + file.getAbsolutePath() + "]", e);
					}
				}
			}
		}
	}
}
