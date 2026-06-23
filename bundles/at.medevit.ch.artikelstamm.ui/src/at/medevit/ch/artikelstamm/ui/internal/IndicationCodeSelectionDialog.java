package at.medevit.ch.artikelstamm.ui.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import at.medevit.ch.artikelstamm.IArtikelstammItem;
import at.medevit.ch.artikelstamm.extinfo.ArticleIndication;
import ch.elexis.core.ui.util.NatTableFactory;
import ch.elexis.core.ui.util.NatTableWrapper;

public class IndicationCodeSelectionDialog extends TitleAreaDialog {

	private IArtikelstammItem item;

	private List<ArticleIndication> input;

	private NatTableWrapper natTableWrapper;

	private String selectedCode;

	public IndicationCodeSelectionDialog(IArtikelstammItem item, Shell parentShell) {
		super(parentShell);
		this.item = item;
	}

	@Override
	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite ret = (Composite) super.createDialogArea(parent);

		setTitle("Indikationscode Auswahl");
		setMessage("Indikationscode von Medikament " + item.getLabel() + " für die Verrechnung auswählen.");

		input = new ArrayList<>(item.getIndicationInfo().get().getIndications());
		natTableWrapper = NatTableFactory.createSingleColumnTable(ret,
				new ListDataProvider<ArticleIndication>(input, new IColumnAccessor<ArticleIndication>() {
					
					@Override
					public int getColumnCount() {
						return 1;
					}

					@Override
					public Object getDataValue(ArticleIndication element, int columnIndex) {
						if (element instanceof ArticleIndication) {
							ArticleIndication indication = element;
							StringJoiner sj = new StringJoiner("\n");
							sj.add("<strong>" + indication.getCode() + "</strong><br/>");
							sj.add(indication.getLimText());
							return sj.toString();
						}
						return "";
					}

					@Override
					public void setDataValue(ArticleIndication element, int columnIndex, Object arg2) {
						// TODO Auto-generated method stub

					}
				}), null);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint= 800;
		gd.heightHint = 600;
		natTableWrapper.getNatTable().setLayoutData(gd);
		updateSelection();
		return ret;
	}

	private void updateSelection() {
		if (StringUtils.isNotBlank(selectedCode) && natTableWrapper != null) {
			Optional<ArticleIndication> selectedIndication = input.stream()
					.filter(i -> selectedCode.equals(i.getCode())).findFirst();
			selectedIndication.ifPresent(s -> natTableWrapper.setSelection(new StructuredSelection(s)));
		}
	}

	public String getSelectedCode() {
		IStructuredSelection selection = (IStructuredSelection) natTableWrapper.getSelection();
		if (!selection.isEmpty() && selection.getFirstElement() instanceof ArticleIndication indication) {
			return indication.getCode();
		}
		return null;
	}

	public void setSelectedCode(String code) {
		this.selectedCode = code;
		updateSelection();
	}
}
