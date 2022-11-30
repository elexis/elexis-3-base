package at.medevit.elexis.agenda.ui.function;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import com.equo.chromium.swt.Browser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import at.medevit.elexis.agenda.ui.composite.IAgendaComposite;

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
		Set<Resource> _selectedResources = new LinkedHashSet<Resource>();
		int order = 0;
		for (String selectedResource : selectedResources) {
			_selectedResources.add(new Resource(selectedResource, selectedResource, order));
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

		public Resource(String id, String title, int order) {
			this.id = id;
			this.title = title;
			this.order = order;
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
