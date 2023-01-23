/*******************************************************************************
 * Copyright (c) 2007-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *
 *******************************************************************************/
package ch.elexis.icpc;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.custom.StyleRange;

import ch.elexis.core.common.ElexisEventTopics;
import ch.elexis.core.model.IDiagnosis;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.services.holder.CoreModelServiceHolder;
import ch.elexis.core.text.model.Samdas;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.services.EncounterServiceHolder;
import ch.elexis.core.ui.text.IRichTextDisplay;
import ch.elexis.core.ui.util.IKonsExtension;
import ch.elexis.icpc.model.icpc.IcpcEncounter;
import ch.elexis.icpc.model.icpc.IcpcEpisode;
import ch.elexis.icpc.service.IcpcModelServiceHolder;

public class KonsExtension implements IKonsExtension {
	IRichTextDisplay mine;
	static final String EPISODE_TITLE = "Problem: ";

	public String connect(final IRichTextDisplay tf) {
		mine = tf;
		mine.addDropReceiver(IcpcEpisode.class, this);
		return Activator.PLUGIN_ID;
	}

	public boolean doLayout(final StyleRange n, final String provider, final String id) {
		n.background = UiDesk.getColor(UiDesk.COL_GREEN);
		return true;
	}

	public boolean doXRef(final String refProvider, final String refID) {
		IcpcEncounter enc = IcpcModelServiceHolder.get().load(refID, IcpcEncounter.class).orElse(null);
		if (enc != null) {
			ContextServiceHolder.get().getRootContext().setTyped(enc);
		}
		return true;
	}

	public IAction[] getActions() {
		// TODO Auto-generated method stub
		return null;
	}

	public void insert(final Object o, final int pos) {
		if (o instanceof IcpcEpisode) {
			IcpcEpisode ep = (IcpcEpisode) o;
			final IEncounter encounter = ContextServiceHolder.get().getTyped(IEncounter.class).orElse(null);
			if (encounter != null) {
				IcpcEncounter icpcEncounter = IcpcModelServiceHolder.get().create(IcpcEncounter.class);
				icpcEncounter.setEncounter(encounter);
				icpcEncounter.setEpisode(ep);
				IcpcModelServiceHolder.get().save(icpcEncounter);
				List<IDiagnosis> diags = ep.getDiagnosis();
				for (IDiagnosis dg : diags) {
					encounter.addDiagnosis(dg);
				}
				mine.insertXRef(pos, EPISODE_TITLE + ep.getLabel(), Activator.PLUGIN_ID, icpcEncounter.getId());
				EncounterServiceHolder.get().updateVersionedEntry(encounter, new Samdas(mine.getContentsAsXML()));

				CoreModelServiceHolder.get().save(encounter);
				ContextServiceHolder.get().postEvent(ElexisEventTopics.EVENT_UPDATE, encounter);
			}
		}

	}

	public void removeXRef(final String refProvider, final String refID) {
		IcpcEncounter enc = IcpcModelServiceHolder.get().load(refID, IcpcEncounter.class).orElse(null);
		if (enc != null) {
			IcpcModelServiceHolder.get().delete(enc);
		}
	}

	public void setInitializationData(final IConfigurationElement config, final String propertyName, final Object data)
			throws CoreException {
		// TODO Auto-generated method stub

	}

}
