package waelti.statistics.views;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import waelti.statistics.queries.AbstractQuery;
import waelti.statistics.queries.SetDataException;
import waelti.statistics.queries.annotations.GetProperty;
import waelti.statistics.queries.annotations.SetProperty;
import ch.elexis.core.ui.util.SWTHelper;

/**
 * View of the meta model. All label - text field pairs are obtained by reflecting on the given
 * AbtractQuery object. At the moment, all query classes are represented in a map containing an
 * object of each query class. Might be changed in the future and done by reflecting on the whole
 * query package.
 * 
 * @author michael waelti
 * @see SetProperty
 * @see GetProperty
 * @see AbstractQuery
 */
public class OptionPanel extends Composite {
	
	/**
	 * Map containing all text fields an their name. Used to feed the query with the user input.
	 */
	private Map<String, Text> fieldMap;
	
	/**
	 * The query which is selected at the moment and will be configured by the user input.
	 */
	private AbstractQuery query;
	
	/** Background color for this composite */
	private Color background;;
	
	/** Standard constructor. */
	public OptionPanel(Composite parent){
		super(parent, SWT.BORDER);
		this.fieldMap = new TreeMap<String, Text>();
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		this.setLayout(layout);
		
		this.background = parent.getBackground();
	}
	
	public OptionPanel(Composite parent, Color background){
		this(parent);
		this.background = background;
		this.setBackground(background);
	}
	
	/** {@inheritDoc} */
	public void updateContent(AbstractQuery selectedQuery){
		this.query = selectedQuery; // the query which was selected.
		
		// clear this composite
		for (Control child : this.getChildren()) {
			child.dispose();
		}
		this.fieldMap = new TreeMap<String, Text>();
		
		// populate again
		createQueryField();
		
		this.layout();
	}
	
	/**
	 * Populates this composite via reflection using the getProperty annotation.
	 */
	private void createQueryField(){
		ArrayList<Method> getterMethodList = new ArrayList<Method>();
		
		for (Method method : query.getClass().getMethods()) {
			if (method.isAnnotationPresent(GetProperty.class)) {
				getterMethodList.add(method);
			}
		}
		
		this.sortMethodList(getterMethodList);
		this.createFields(getterMethodList);
		
	}
	
	/**
	 * Sorts the methods according to the index of the getProperty annotation.
	 * 
	 * @param methodList
	 *            a list containing only methods having a Set/GetProperty annotation.
	 */
	private void sortMethodList(ArrayList<Method> methodList){
		Collections.sort(methodList, new Comparator<Method>() {
			
			public int compare(Method o1, Method o2){
				int index1 = 0;
				int index2 = 0;
				if (o1.isAnnotationPresent(GetProperty.class)) {
					GetProperty anno1 = o1.getAnnotation(GetProperty.class);
					GetProperty anno2 = o2.getAnnotation(GetProperty.class);
					index1 = anno1.index();
					index2 = anno2.index();
				} else { // has to have a SetProperty annotation
					SetProperty anno1 = o1.getAnnotation(SetProperty.class);
					SetProperty anno2 = o2.getAnnotation(SetProperty.class);
					index1 = anno1.index();
					index2 = anno2.index();
				}
				
				return index1 - index2;
			}
		});
	}
	
	private void createFields(ArrayList<Method> getterList){
		for (Method method : getterList) {
			GetProperty getter = method.getAnnotation(GetProperty.class);
			this.createLabel(getter.value());
			
			String value = this.getValue(method);
			Text field = this.createTextField(value);
			
			fieldMap.put(getter.value(), field);
		}
	}
	
	/**
	 * Normally, a getter should not throw an exception, just return an empty string. Still, since
	 * it is not clear, what the query is, any exception is caught and just logged.
	 */
	private String getValue(Method method){
		try {
			return (String) method.invoke(query);
		} catch (Exception e) {
			// TODO: log
		}
		return "error";
	}
	
	private Text createTextField(String value){
		Text text = new Text(this, SWT.BORDER);
		text.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		text.setText(value);
		return text;
	}
	
	private Label createLabel(String text){
		Label lab = new Label(this, SWT.WRAP);
		lab.setLayoutData(SWTHelper.getFillGridData(1, false, 1, false));
		lab.setBackground(this.background);
		lab.setText(text);
		return lab;
	}
	
	/**
	 * Reads all fields, sets them via the meta model and returns the configured query.
	 */
	public AbstractQuery getQuery() throws SetDataException{
		this.setQueryData();
		return this.query;
	}
	
	/** Sets all fields via the meta model in the given query. */
	private void setQueryData() throws SetDataException{
		assert (this.query != null);
		ArrayList<Method> setterMethodList = new ArrayList<Method>();
		
		for (Method method : query.getClass().getMethods()) {
			if (method.isAnnotationPresent(SetProperty.class)) {
				setterMethodList.add(method);
			}
		}
		
		this.sortMethodList(setterMethodList);
		this.setData(setterMethodList);
	}
	
	private void setData(ArrayList<Method> setterList) throws SetDataException{
		for (Method method : setterList) {
			SetProperty setter = method.getAnnotation(SetProperty.class);
			Text field = this.fieldMap.get(setter.value());
			String value = field.getText();
			this.setValue(query, method, value);
		}
	}
	
	private void setValue(AbstractQuery query, Method method, String value) throws SetDataException{
		try {
			method.invoke(query, value);
		} catch (Exception e) {
			throw (SetDataException) e.getCause();
		}
		
	}
}