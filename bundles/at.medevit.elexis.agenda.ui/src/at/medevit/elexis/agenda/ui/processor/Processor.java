package at.medevit.elexis.agenda.ui.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.descriptor.basic.MPartDescriptor;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

public class Processor {

	private MApplication mApplication;
	private EModelService eModelService;

	@Execute
	public void execute(MApplication mApplication, EModelService eModelService) {
		this.mApplication = mApplication;
		this.eModelService = eModelService;

		updateMenusOfPart("at.medevit.elexis.agenda.ui.view.agenda");
	}

	private void updateMenusOfPart(String descriptiorId) {
		
		Optional<MPartDescriptor> descriptor = mApplication.getDescriptors().stream()
				.filter(d -> descriptiorId.equals(d.getElementId())).findFirst();
		if (descriptor.isPresent()) {
			List<MPart> parts = eModelService.findElements(mApplication, descriptiorId, MPart.class);
			for (MPart mPart : parts) {
				for (MMenu mMenu : mPart.getMenus()) {
					updateMenusOfPart(mMenu, descriptor.get().getMenus().stream()
							.filter(m -> m.getElementId().equals(mMenu.getElementId())).findFirst().orElse(null));
				}
			}
		}
	}

	private void updateMenusOfPart(MMenu partMenu, MMenu descriptorMenu) {
		if (partMenu != null && descriptorMenu != null) {
			for (MMenuElement mMenuElement : new ArrayList<>(descriptorMenu.getChildren())) {
				Optional<MMenuElement> found = partMenu.getChildren().stream()
						.filter(m -> mMenuElement.getElementId().equals(m.getElementId()))
						.findFirst();
				if (found.isEmpty()) {
					partMenu.getChildren().add(descriptorMenu.getChildren().indexOf(mMenuElement), mMenuElement);
				}
			}
		}
	}
}
