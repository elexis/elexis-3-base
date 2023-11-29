
package ch.elexis.dialogs;


import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import ch.elexis.agenda.Messages;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.ui.data.UiMandant;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;
import ch.elexis.data.Mandant;

public class FarbenSelektor extends TitleAreaDialog {
	private java.util.List<String> areas;
	private Map<String, Color> areaColors;
	private Map<String, Color> tempAreaColors;
	private ConfigServicePreferenceStore prefs = new ConfigServicePreferenceStore(Scope.GLOBAL);
	private Map<String, Boolean> useMandatorColors;
	private Map<String, Label> areaToColorLabelMap = new HashMap<>();
	public FarbenSelektor(Shell parentShell) {
		super(parentShell);
		useMandatorColors = new HashMap<>();
		areaColors = new HashMap<>();
		loadCheckboxPreferences();
		tempAreaColors = new HashMap<>();
		areas = new ArrayList<>(ConfigServiceHolder.getGlobalAsList(PreferenceConstants.AG_BEREICHE));
		loadColorPreferences();
		checkAreaTypes();
	}

		@Override
		protected Control createDialogArea(Composite parent) {
			Composite container = (Composite) super.createDialogArea(parent);
			setTitle(Messages.AgendaFarben_Titel);
			setMessage(Messages.AgendaFarben_Bereich_Description);
			Composite groupContainer = new Composite(container, SWT.NONE);
			GridLayout groupLayout = new GridLayout(2, false);
			groupContainer.setLayout(groupLayout);
			groupContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			Group groupAreas = new Group(groupContainer, SWT.NONE);
			groupAreas.setText(Messages.Agenda_Bereiche_Title);
			groupAreas.setLayout(new GridLayout(1, false));
			groupAreas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			Group groupColors = new Group(groupContainer, SWT.NONE);
			groupColors.setText(Messages.AgendaFarben_Title);
			groupColors.setLayout(new GridLayout(1, false));
			groupColors.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			for (String area : areas) {
				createAreaColorSelector(groupAreas, groupColors, area);
			}
			return container;
		}

		private void createAreaColorSelector(Group groupAreas, Group groupColors, String area) {
			Composite areaComposite = new Composite(groupAreas, SWT.NONE);
			areaComposite.setLayout(new GridLayout(2, false));
			areaComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			Label areaLabel = new Label(areaComposite, SWT.NONE);
			areaLabel.setText(area);
			areaLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			String type = ConfigServiceHolder.getGlobal(
					PreferenceConstants.AG_BEREICH_PREFIX + area + PreferenceConstants.AG_BEREICH_TYPE_POSTFIX, null);
			final Button useMandatorColorsCheckbox;
			final boolean isCheckboxAvailable;
			if (type != null) {
				useMandatorColorsCheckbox = new Button(areaComposite, SWT.CHECK);
				useMandatorColorsCheckbox.setText(Messages.Agenda_Mandator);
				useMandatorColorsCheckbox.setSelection(useMandatorColors.getOrDefault(area, false));
				isCheckboxAvailable = true;
			} else {
				useMandatorColorsCheckbox = null;
				isCheckboxAvailable = false;
			}
			Composite colorComposite = new Composite(groupColors, SWT.NONE);
			colorComposite.setLayout(new GridLayout(2, false));
			colorComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			Label colorLabel = new Label(colorComposite, SWT.NONE);
			colorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			colorLabel.setBackground(areaColors.get(area));
			colorLabel.setData(colorLabel);
			areaToColorLabelMap.put(area, colorLabel);
			Label deleteImageLabel = new Label(colorComposite, SWT.NONE);
			deleteImageLabel.setImage(Images.IMG_DELETE.getImage());
			GridData imageLabelGridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
			imageLabelGridData.heightHint = 16;
			deleteImageLabel.setLayoutData(imageLabelGridData);
			deleteImageLabel.setEnabled(!isCheckboxAvailable
					|| (useMandatorColorsCheckbox != null && !useMandatorColorsCheckbox.getSelection()));
			if (isCheckboxAvailable && useMandatorColorsCheckbox != null) {
				useMandatorColorsCheckbox.addListener(SWT.Selection, e -> {
					boolean isSelected = useMandatorColorsCheckbox.getSelection();
					useMandatorColors.put(area, isSelected);
					deleteImageLabel.setEnabled(!isSelected);
					if (isSelected) {
						String mandatorId = extractMandatorId(type);
						Color mandatorColor = getColorForMandator(mandatorId);
						if (mandatorColor != null) {
							areaColors.put(area, mandatorColor);
							refreshColorDisplay(areaComposite, area);
						}
					} else {
						loadColorFromPreferences(area);
						refreshColorDisplay(areaComposite, area);
					}
				});
			}
			deleteImageLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (deleteImageLabel.isEnabled()) {
						areaColors.put(area, null);
						colorLabel.setBackground(null);
						saveColorPreferences();
					}
				}
			});
			colorLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					if (!isCheckboxAvailable || !useMandatorColors.getOrDefault(area, false)) {
						Label sourceLabel = (Label) e.widget;
						ColorDialog colorDialog = new ColorDialog(getShell());
						RGB rgb = colorDialog.open();
						if (rgb != null) {
							Color newColor = new Color(getShell().getDisplay(), rgb);
							tempAreaColors.put(area, newColor);
							areaColors.put(area, newColor);
							sourceLabel.setBackground(newColor);
							saveColorPreferences();
						}
					}
				}
			});
		}

		private void saveColorPreferences() {
		    StringBuilder sb = new StringBuilder();
			StringBuilder checkboxState = new StringBuilder();
			for (String area : areas) {
				Color color = areaColors.get(area);
				if (color != null && !color.isDisposed()) {
					RGB rgb = color.getRGB();
					sb.append(area).append(":").append(rgb.red).append(",").append(rgb.green).append(",")
							.append(rgb.blue).append(";");
				} else {
					sb.append(area).append("::;");
				}
				Boolean useMandatorColor = useMandatorColors.getOrDefault(area, false);
				checkboxState.append(area).append(":").append(useMandatorColor ? "1" : "0").append(";");
		    }
		    prefs.setValue(PreferenceConstants.AG_BEREICH_FARBEN, sb.toString());
			prefs.setValue(PreferenceConstants.AG_USE_MANDATOR_COLORS, checkboxState.toString());
		}

		private void loadCheckboxPreferences() {
			useMandatorColors.clear();
			Optional.ofNullable(prefs.getString(PreferenceConstants.AG_USE_MANDATOR_COLORS))
					.map(savedStates -> Arrays.stream(savedStates.split(";"))).orElse(Stream.empty())
					.map(entry -> entry.split(":")).filter(parts -> parts.length == 2).forEach(parts -> {
						String area = parts[0];
						boolean state = "1".equals(parts[1]);
						useMandatorColors.put(area, state);
					});
		}

	@Override
	public void create() {
		super.create();
		loadColorPreferences();
	}

	@Override
	protected void okPressed() {
		tempAreaColors.entrySet().stream().filter(entry -> entry.getValue() != null && !entry.getValue().isDisposed())
				.forEach(entry -> areaColors.put(entry.getKey(), entry.getValue()));
		tempAreaColors.clear();
		saveColorPreferences();
		super.okPressed();
	}
	@Override
	public boolean close() {
		return super.close();
	}

	private void checkAreaTypes() {
		areas.stream()
				.map(area -> new AbstractMap.SimpleEntry<>(area,
						ConfigServiceHolder.getGlobal(PreferenceConstants.AG_BEREICH_PREFIX + area
								+ PreferenceConstants.AG_BEREICH_TYPE_POSTFIX, null)))
				.filter(entry -> entry.getValue() != null).forEach(entry -> {
					String area = entry.getKey();
					String type = entry.getValue();
					String mandatorId = extractMandatorId(type);
					Optional.ofNullable(getColorForMandator(mandatorId))
							.filter(color -> !color.isDisposed() && useMandatorColors.getOrDefault(area, false))
							.ifPresent(color -> areaColors.put(area, color));
				});
	}

	private String extractMandatorId(String type) {
		if (type.startsWith("CONTACT/")) {
			return type.substring("CONTACT/".length());
		}
		return type;
	}

	private Color getColorForMandator(String mandatorId) {
		Optional<IMandator> mandator = CoreModelServiceHolder.get().load(mandatorId, IMandator.class);
		if (mandator.isPresent()) {
			return UiMandant.getColorForMandator(Mandant.load(mandator.get().getId()));
		} else {
			return null;
		}
	}

	private void refreshColorDisplay(Composite areaComposite, String area) {
		Color newColor = areaColors.get(area);
		Label colorLabel = areaToColorLabelMap.get(area);
		if (newColor != null && !newColor.isDisposed() && colorLabel != null) {
			colorLabel.setBackground(newColor);
		}
	}

	private void loadColorFromPreferences(String area) {
		String savedColors = prefs.getString(PreferenceConstants.AG_BEREICH_FARBEN);
		Arrays.stream(savedColors.split(";")).map(entry -> entry.split(":"))
				.filter(parts -> parts.length == 2 && parts[0].equals(area)).findFirst()
				.map(parts -> parseColorFromString(parts[1])).ifPresent(color -> areaColors.put(area, color));
	}

	private void loadColorPreferences() {
		areaColors.clear();
		Optional.ofNullable(prefs.getString(PreferenceConstants.AG_BEREICH_FARBEN))
				.map(savedColors -> Arrays.stream(savedColors.split(";"))).orElse(Stream.empty()).forEach(entry -> {
					String[] parts = entry.split(":");
					if (parts.length == 2) {
						String area = parts[0];
						Color color = parseColorFromString(parts[1]);
						areaColors.put(area, color);
					}
				});
	}

	private Color parseColorFromString(String colorString) {
		return Optional.ofNullable(colorString).filter(str -> !str.isEmpty())
				.map(str -> Arrays.stream(str.split(",")).mapToInt(Integer::parseInt).toArray())
				.filter(rgbParts -> rgbParts.length == 3)
				.map(rgbParts -> new Color(Display.getCurrent(), new RGB(rgbParts[0], rgbParts[1], rgbParts[2])))
				.orElse(null);
	}

}