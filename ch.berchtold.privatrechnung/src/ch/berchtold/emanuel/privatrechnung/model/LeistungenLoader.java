/**
 * 
 */
package ch.berchtold.emanuel.privatrechnung.model;

import org.eclipse.jface.viewers.TreeViewer;

import ch.berchtold.emanuel.privatrechnung.data.Leistung;
import ch.elexis.core.ui.actions.TreeDataLoader;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.rgw.tools.Tree;

public class LeistungenLoader extends TreeDataLoader {
	public LeistungenLoader(CodeSelectorFactory csf, CommonViewer cv,
		Query<? extends PersistentObject> qbe, String parentField){
		super(cv, qbe, parentField, "Kuerzel");
	}
	
	public void updateChildCount(Object element, int currentChildCount){
		int num = 0;
		if (element instanceof Tree) {
			Tree<Leistung> t = (Tree<Leistung>) element;
			if (!t.hasChildren()) {
				qbe.clear();
				qbe.add(parentColumn, "=", t.contents.get("Kuerzel"));
				applyQueryFilters();
				for (PersistentObject po : qbe.execute()) {
					new Tree<Leistung>(t, (Leistung) po);
				}
			}
			num = t.getChildren().size();
		} else {
			num = root.getChildren().size();
		}
		((TreeViewer) cv.getViewerWidget()).setChildCount(element, num);
	}
	
}