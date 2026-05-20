package at.medevit.elexis.hin.sign.ui.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IPrescription;
import ch.elexis.core.model.IRecipe;
import ch.elexis.core.model.prescription.EntryType;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.MedicationServiceHolder;
import ch.elexis.core.ui.medication.handlers.PrintTakingsListHandler.SorterAdapter;
import ch.elexis.core.ui.medication.views.MedicationTableViewerItem;
import ch.elexis.core.ui.medication.views.MedicationView;

public class MedicationCreateAndOpenEPrescription extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IPatient patient = ContextServiceHolder.get().getActivePatient().orElse(null);
		if (patient == null)
			return null;

		String medicationType = event.getParameter("ch.elexis.core.ui.medication.commandParameter.medication"); //$NON-NLS-1$
		// if not set use selection
		if (medicationType == null || medicationType.isEmpty()) {
			medicationType = "selection"; //$NON-NLS-1$
		}
		List<IPrescription> prescRecipes = getPrescriptions(patient, medicationType, event);
		// fall back to all
		if (medicationType.equals("selection") && prescRecipes.isEmpty()) {
			medicationType = "all";
		}
		prescRecipes = getPrescriptions(patient, medicationType, event);
		if (!prescRecipes.isEmpty()) {
			prescRecipes = sortPrescriptions(prescRecipes, event);

			IRecipe recipe = MedicationServiceHolder.get().createRecipe(patient, prescRecipes);
			ContextServiceHolder.get().setTyped(recipe);

			CreateAndOpenEPrescription createAndOpenHandler = new CreateAndOpenEPrescription();
			createAndOpenHandler.execute(event);

			ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_RELOAD, IRecipe.class);
		}
		return null;
	}
	
	private List<IPrescription> sortPrescriptions(List<IPrescription> prescRecipes, ExecutionEvent event) {
		SorterAdapter sorter = new SorterAdapter(event);
		IWorkbenchPart part = HandlerUtil.getActivePart(event);
		if (part instanceof MedicationView) {
			return sorter.getSorted(prescRecipes);
		}
		return prescRecipes;
	}

	@SuppressWarnings("unchecked")
	private List<IPrescription> getPrescriptions(IPatient patient, String medicationType, ExecutionEvent event) {
		if ("selection".equals(medicationType)) { //$NON-NLS-1$
			ISelection selection = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getSelection();
			if (selection != null && !selection.isEmpty()) {
				List<IPrescription> ret = new ArrayList<>();
				IStructuredSelection strucSelection = (IStructuredSelection) selection;
				if (strucSelection.getFirstElement() instanceof MedicationTableViewerItem) {
					List<MedicationTableViewerItem> mtvItems = strucSelection
							.toList();
					for (MedicationTableViewerItem mtvItem : mtvItems) {
						IPrescription p = mtvItem.getPrescription();
						if (p != null) {
							ret.add(p);
						}
					}
				} else if (strucSelection.getFirstElement() instanceof IPrescription) {
					ret.addAll(strucSelection.toList());
				}
				return ret;
			}
		} else if ("all".equals(medicationType)) { //$NON-NLS-1$
			return patient.getMedication(Arrays.asList(EntryType.FIXED_MEDICATION, EntryType.RESERVE_MEDICATION,
					EntryType.SYMPTOMATIC_MEDICATION));
		}
		return Collections.emptyList();
	}
}
