package ch.elexis.dialogs;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.agenda.Messages;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.services.holder.ConfigServiceHolder;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore;
import ch.elexis.core.ui.preferences.ConfigServicePreferenceStore.Scope;

public class FarbenSelektor extends TitleAreaDialog {
	private java.util.List<String> areas;
	private Map<String, Color> areaColors;
	private Map<String, Color> tempAreaColors;
	private static final RGB DEFAULT_RGB = new RGB(0, 0, 0);
	private ConfigServicePreferenceStore prefs = new ConfigServicePreferenceStore(Scope.GLOBAL);
	private static final Logger logger = LoggerFactory.getLogger(FarbenSelektor.class);
	public FarbenSelektor(Shell parentShell) {
		super(parentShell);
		areaColors = new HashMap<>();
		tempAreaColors = new HashMap<>();
		areas = new ArrayList<>(ConfigServiceHolder.getGlobalAsList(PreferenceConstants.AG_BEREICHE));
		loadColorPreferences();
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
			Label areaLabel = new Label(groupAreas, SWT.NONE);
			areaLabel.setText(area);
			areaLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			Label colorLabel = new Label(groupColors, SWT.NONE);
			colorLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
			colorLabel.setBackground(areaColors.get(area));
			colorLabel.setData(area);
			colorLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseDown(MouseEvent e) {
					Label sourceLabel = (Label) e.widget;
					String selectedArea = (String) sourceLabel.getData();
					ColorDialog colorDialog = new ColorDialog(getShell());
					RGB rgb = colorDialog.open();
					if (rgb != null) {
						Color newColor = new Color(getShell().getDisplay(), rgb);
						tempAreaColors.put(selectedArea, newColor);
						sourceLabel.setBackground(newColor);
					}
				}
			});
		}

	private void saveColorPreferences() {
		StringBuilder sb = new StringBuilder();
		for (String area : areas) {
			Color color = areaColors.get(area);
			if (color != null) {
				RGB rgb = color.getRGB();
				sb.append(area).append(":").append(rgb.red).append(",").append(rgb.green).append(",").append(rgb.blue)
						.append(";");
			}
		}
		prefs.setValue(PreferenceConstants.AG_BEREICH_FARBEN, sb.toString());
	}

	private void loadColorPreferences() {
		areaColors.clear();
		String savedColors = prefs.getString(PreferenceConstants.AG_BEREICH_FARBEN);
		Map<String, RGB> loadedColors = new HashMap<>();
		if (savedColors != null && !savedColors.isEmpty()) {
			String[] colorEntries = savedColors.split(";");
			for (String entry : colorEntries) {
				try {
					String[] parts = entry.split(":");
					if (parts.length == 2) {
						String area = parts[0];
						String[] rgbParts = parts[1].split(",");
						if (rgbParts.length == 3) {
							int red = Integer.parseInt(rgbParts[0].trim());
							int green = Integer.parseInt(rgbParts[1].trim());
							int blue = Integer.parseInt(rgbParts[2].trim());
							RGB rgb = new RGB(red, green, blue);
							loadedColors.put(area, rgb);
						}
					}
				} catch (NumberFormatException e) {
					logger.error("Error loading the color preferences: {}", e.getMessage(), e);
				}
			}
		}
		for (String area : areas) {
			RGB rgb = loadedColors.getOrDefault(area, DEFAULT_RGB);
			areaColors.put(area, new Color(Display.getCurrent(), rgb));
		}
	}


	@Override
	public void create() {
		super.create();
		loadColorPreferences();
	}

	@Override
	protected void okPressed() {
		for (Map.Entry<String, Color> entry : tempAreaColors.entrySet()) {
			Color oldColor = areaColors.get(entry.getKey());
			if (oldColor != null && !oldColor.isDisposed()) {
				oldColor.dispose();
			}
			areaColors.put(entry.getKey(), entry.getValue());
		}
		tempAreaColors.clear();
		saveColorPreferences();
		super.okPressed();
	}
	@Override
	public boolean close() {
		for (Color color : areaColors.values()) {
			if (color != null && !color.isDisposed())
				color.dispose();
		}
		return super.close();
	}
}
