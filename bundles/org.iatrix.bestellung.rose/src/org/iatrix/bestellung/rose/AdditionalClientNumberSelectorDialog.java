package org.iatrix.bestellung.rose;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

import ch.elexis.core.services.holder.ConfigServiceHolder;

public class AdditionalClientNumberSelectorDialog extends ListDialog {

	public AdditionalClientNumberSelectorDialog(Shell parent) {
		super(parent);
		List<AdditionalClientNumber> numbers = new ArrayList<>();
		numbers.add(new AdditionalClientNumber("standard|" + ConfigServiceHolder
				.getGlobal(Constants.CFG_ROSE_CLIENT_NUMBER, Constants.DEFAULT_ROSE_CLIENT_NUMBER).trim()));
		numbers.addAll(AdditionalClientNumber.getConfigured());

		setInput(numbers);
		setContentProvider(ArrayContentProvider.getInstance());
		setLabelProvider(new AdditionalClientNumberProvider());
		setTitle("Bitte Kundennummer auswählen");
	}

	private class AdditionalClientNumberProvider extends LabelProvider {

		@Override
		public String getText(Object element) {
			AdditionalClientNumber clientNumber = (AdditionalClientNumber) element;
			return clientNumber.getClientIdent() + " - " + clientNumber.getClientNumber();
		}
	}
}
