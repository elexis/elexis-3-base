package at.medevit.elexis.agenda.ui.function;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.equo.chromium.swt.Browser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import at.medevit.elexis.agenda.ui.composite.IAgendaComposite;
import ch.elexis.agenda.preferences.PreferenceConstants;
import ch.elexis.core.services.holder.ConfigServiceHolder;

public class LoadResourcesFunction extends AbstractBrowserFunction {

	private IAgendaComposite agendaComposite;
	private Gson gson;

	public LoadResourcesFunction(Browser browser, String name, IAgendaComposite agendaComposite) {
		super(browser, name);
		this.agendaComposite = agendaComposite;
		gson = new GsonBuilder().create();
	}

	@Override
	public Object function(Object[] arguments) {
	    Set<String> selectedResources = agendaComposite.getSelectedResources();
		String colorPrefs = ConfigServiceHolder.get().get(PreferenceConstants.AG_BEREICH_FARBEN, null);
	    Map<String, String> resourceColors = new HashMap<>();
	    String[] colorAssignments = colorPrefs.split(";");
	    for (String assignment : colorAssignments) {
	        String[] parts = assignment.split(":");
	        if (parts.length == 2) {
				resourceColors.put(parts[0], parts[1]);
	        }
	    }
	    Set<Resource> _selectedResources = new LinkedHashSet<Resource>();
	    int order = 0;
	    for (String selectedResource : selectedResources) {
			String color = resourceColors.getOrDefault(selectedResource, null);
			_selectedResources.add(new Resource(selectedResource, selectedResource, order, color));
	        order++;
	    }
	    String json = gson.toJson(_selectedResources);
	    return json;
	}
	private class Resource {

		private String id;
		private String title;
		@SuppressWarnings("unused")
		private int order;
		private String color;

		public Resource(String id, String title, int order, String color) {
			this.id = id;
			this.title = title;
			this.order = order;
			this.color = color;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + Objects.hash(id, title);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Resource other = (Resource) obj;
			if (!getEnclosingInstance().equals(other.getEnclosingInstance()))
				return false;
			return Objects.equals(id, other.id) && Objects.equals(title, other.title);
		}

		private LoadResourcesFunction getEnclosingInstance() {
			return LoadResourcesFunction.this;
		}

	}

}
