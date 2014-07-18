package ch.medshare.swt.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.Accessible;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GCData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.medshare.util.UtilMisc;

public abstract class AbstractBrowseText {
	
	private final Text text;
	private final Button btnBrowse;
	
	public AbstractBrowseText(final Composite parent, int style){
		this.text = new Text(parent, style);
		this.btnBrowse = new Button(parent, SWT.PUSH);
		this.btnBrowse.setText("Browse..."); //$NON-NLS-1$
		this.btnBrowse.addSelectionListener(getBrowseSelectionAdapter());
	}
	
	protected abstract SelectionAdapter getBrowseSelectionAdapter();
	
	public void addModifyListener(ModifyListener listener){
		this.text.addModifyListener(listener);
	}
	
	public void addSelectionListener(SelectionListener listener){
		this.text.addSelectionListener(listener);
	}
	
	public void addVerifyListener(VerifyListener listener){
		this.text.addVerifyListener(listener);
	}
	
	public void append(String string){
		this.text.append(string);
	}
	
	public void clearSelection(){
		this.text.clearSelection();
	}
	
	public Point computeSize(int hint, int hint2, boolean changed){
		return this.text.computeSize(hint, hint2, changed);
	}
	
	public Rectangle computeTrim(int x, int y, int width, int height){
		return this.text.computeTrim(x, y, width, height);
	}
	
	public void copy(){
		this.text.copy();
	}
	
	public void cut(){
		this.text.cut();
	}
	
	public int getBorderWidth(){
		return this.text.getBorderWidth();
	}
	
	public int getCaretLineNumber(){
		return this.text.getCaretLineNumber();
	}
	
	public Point getCaretLocation(){
		return this.text.getCaretLocation();
	}
	
	public int getCaretPosition(){
		return this.text.getCaretPosition();
	}
	
	public int getCharCount(){
		return this.text.getCharCount();
	}
	
	public boolean getDoubleClickEnabled(){
		return this.text.getDoubleClickEnabled();
	}
	
	public char getEchoChar(){
		return this.text.getEchoChar();
	}
	
	public boolean getEditable(){
		return this.text.getEditable();
	}
	
	public int getLineCount(){
		return this.text.getLineCount();
	}
	
	public String getLineDelimiter(){
		return this.text.getLineDelimiter();
	}
	
	public int getLineHeight(){
		return this.text.getLineHeight();
	}
	
	public String getMessage(){
		return this.text.getMessage();
	}
	
	public int getOrientation(){
		return this.text.getOrientation();
	}
	
	public Point getSelection(){
		return this.text.getSelection();
	}
	
	public int getSelectionCount(){
		return this.text.getSelectionCount();
	}
	
	public String getSelectionText(){
		return this.text.getSelectionText();
	}
	
	public int getTabs(){
		return this.text.getTabs();
	}
	
	public String getText(){
		String string = this.text.getText();
		return UtilMisc.replaceWithForwardSlash(string);
	}
	
	public String getText(int start, int end){
		String string = this.text.getText(start, end);
		return UtilMisc.replaceWithForwardSlash(string);
	}
	
	public int getTextLimit(){
		return this.text.getTextLimit();
	}
	
	public int getTopIndex(){
		return this.text.getTopIndex();
	}
	
	public int getTopPixel(){
		return this.text.getTopPixel();
	}
	
	public void insert(String string){
		this.text.insert(string);
	}
	
	public void paste(){
		this.text.paste();
	}
	
	public void removeModifyListener(ModifyListener listener){
		this.text.removeModifyListener(listener);
	}
	
	public void removeSelectionListener(SelectionListener listener){
		this.text.removeSelectionListener(listener);
	}
	
	public void removeVerifyListener(VerifyListener listener){
		this.text.removeVerifyListener(listener);
	}
	
	public void selectAll(){
		this.text.selectAll();
	}
	
	public void setDoubleClickEnabled(boolean doubleClick){
		this.text.setDoubleClickEnabled(doubleClick);
	}
	
	public void setEchoChar(char echo){
		this.text.setEchoChar(echo);
	}
	
	public void setEditable(boolean editable){
		this.text.setEditable(editable);
	}
	
	public void setFont(Font font){
		this.text.setFont(font);
	}
	
	public void setMessage(String message){
		this.text.setMessage(message);
	}
	
	public void setOrientation(int orientation){
		this.text.setOrientation(orientation);
	}
	
	public void setRedraw(boolean redraw){
		this.text.setRedraw(redraw);
	}
	
	public void setSelection(int start, int end){
		this.text.setSelection(start, end);
	}
	
	public void setSelection(int start){
		this.text.setSelection(start);
	}
	
	public void setSelection(Point selection){
		this.text.setSelection(selection);
	}
	
	public void setTabs(int tabs){
		this.text.setTabs(tabs);
	}
	
	public void setText(String string){
		this.text.setText(UtilMisc.replaceWithForwardSlash(string));
	}
	
	public void setTextLimit(int limit){
		this.text.setTextLimit(limit);
	}
	
	public void setTopIndex(int index){
		this.text.setTopIndex(index);
	}
	
	public void showSelection(){
		this.text.showSelection();
	}
	
	public Rectangle getClientArea(){
		return this.text.getClientArea();
	}
	
	public ScrollBar getHorizontalBar(){
		return this.text.getHorizontalBar();
	}
	
	public ScrollBar getVerticalBar(){
		return this.text.getVerticalBar();
	}
	
	public void addControlListener(ControlListener listener){
		this.text.addControlListener(listener);
	}
	
	public void addDragDetectListener(DragDetectListener listener){
		this.text.addDragDetectListener(listener);
	}
	
	public void addFocusListener(FocusListener listener){
		this.text.addFocusListener(listener);
	}
	
	public void addHelpListener(HelpListener listener){
		this.text.addHelpListener(listener);
	}
	
	public void addKeyListener(KeyListener listener){
		this.text.addKeyListener(listener);
	}
	
	public void addMenuDetectListener(MenuDetectListener listener){
		this.text.addMenuDetectListener(listener);
	}
	
	public void addMouseListener(MouseListener listener){
		this.text.addMouseListener(listener);
	}
	
	public void addMouseMoveListener(MouseMoveListener listener){
		this.text.addMouseMoveListener(listener);
	}
	
	public void addMouseTrackListener(MouseTrackListener listener){
		this.text.addMouseTrackListener(listener);
	}
	
	public void addMouseWheelListener(MouseWheelListener listener){
		this.text.addMouseWheelListener(listener);
	}
	
	public void addPaintListener(PaintListener listener){
		this.text.addPaintListener(listener);
	}
	
	public void addTraverseListener(TraverseListener listener){
		this.text.addTraverseListener(listener);
	}
	
	public Point computeSize(int hint, int hint2){
		return this.text.computeSize(hint, hint2);
	}
	
	public boolean dragDetect(Event event){
		return this.text.dragDetect(event);
	}
	
	public boolean dragDetect(MouseEvent event){
		return this.text.dragDetect(event);
	}
	
	public boolean forceFocus(){
		return this.text.forceFocus();
	}
	
	public Accessible getAccessible(){
		return this.text.getAccessible();
	}
	
	public Color getBackground(){
		return this.text.getBackground();
	}
	
	public Image getBackgroundImage(){
		return this.text.getBackgroundImage();
	}
	
	public Rectangle getBounds(){
		return this.text.getBounds();
	}
	
	public Cursor getCursor(){
		return this.text.getCursor();
	}
	
	public boolean getDragDetect(){
		return this.text.getDragDetect();
	}
	
	public boolean getEnabled(){
		return this.text.getEnabled();
	}
	
	public Font getFont(){
		return this.text.getFont();
	}
	
	public Color getForeground(){
		return this.text.getForeground();
	}
	
	public Object getLayoutData(){
		return this.text.getLayoutData();
	}
	
	public Point getLocation(){
		return this.text.getLocation();
	}
	
	public Menu getMenu(){
		return this.text.getMenu();
	}
	
	public Monitor getMonitor(){
		return this.text.getMonitor();
	}
	
	public Composite getParent(){
		return this.text.getParent();
	}
	
	public Shell getShell(){
		return this.text.getShell();
	}
	
	public Point getSize(){
		return this.text.getSize();
	}
	
	public String getToolTipText(){
		return this.text.getToolTipText();
	}
	
	public boolean getVisible(){
		return this.text.getVisible();
	}
	
	public void internal_dispose_GC(int hdc, GCData data){
		this.text.internal_dispose_GC(hdc, data);
	}
	
	public long internal_new_GC(GCData data){
		return this.text.internal_new_GC(data);
	}
	
	public boolean isEnabled(){
		return this.text.isEnabled();
	}
	
	public boolean isFocusControl(){
		return this.text.isFocusControl();
	}
	
	public boolean isReparentable(){
		return this.text.isReparentable();
	}
	
	public boolean isVisible(){
		return this.text.isVisible();
	}
	
	public void moveAbove(Control control){
		this.text.moveAbove(control);
	}
	
	public void moveBelow(Control control){
		this.text.moveBelow(control);
	}
	
	public void pack(){
		this.text.pack();
	}
	
	public void pack(boolean changed){
		this.text.pack(changed);
	}
	
	public void redraw(){
		this.text.redraw();
	}
	
	public void redraw(int x, int y, int width, int height, boolean all){
		this.text.redraw(x, y, width, height, all);
	}
	
	public void removeControlListener(ControlListener listener){
		this.text.removeControlListener(listener);
	}
	
	public void removeDragDetectListener(DragDetectListener listener){
		this.text.removeDragDetectListener(listener);
	}
	
	public void removeFocusListener(FocusListener listener){
		this.text.removeFocusListener(listener);
	}
	
	public void removeHelpListener(HelpListener listener){
		this.text.removeHelpListener(listener);
	}
	
	public void removeKeyListener(KeyListener listener){
		this.text.removeKeyListener(listener);
	}
	
	public void removeMenuDetectListener(MenuDetectListener listener){
		this.text.removeMenuDetectListener(listener);
	}
	
	public void removeMouseListener(MouseListener listener){
		this.text.removeMouseListener(listener);
	}
	
	public void removeMouseMoveListener(MouseMoveListener listener){
		this.text.removeMouseMoveListener(listener);
	}
	
	public void removeMouseTrackListener(MouseTrackListener listener){
		this.text.removeMouseTrackListener(listener);
	}
	
	public void removeMouseWheelListener(MouseWheelListener listener){
		this.text.removeMouseWheelListener(listener);
	}
	
	public void removePaintListener(PaintListener listener){
		this.text.removePaintListener(listener);
	}
	
	public void removeTraverseListener(TraverseListener listener){
		this.text.removeTraverseListener(listener);
	}
	
	public void setBackground(Color color){
		this.text.setBackground(color);
	}
	
	public void setBackgroundImage(Image image){
		this.text.setBackgroundImage(image);
	}
	
	public void setBounds(Rectangle rect){
		this.text.setBounds(rect);
	}
	
	public void setBounds(int x, int y, int width, int height){
		this.text.setBounds(x, y, width, height);
	}
	
	public void setCapture(boolean capture){
		this.text.setCapture(capture);
	}
	
	public void setCursor(Cursor cursor){
		this.text.setCursor(cursor);
	}
	
	public void setDragDetect(boolean dragDetect){
		this.text.setDragDetect(dragDetect);
	}
	
	public void setEnabled(boolean enabled){
		this.text.setEnabled(enabled);
	}
	
	public boolean setFocus(){
		return this.text.setFocus();
	}
	
	public void setForeground(Color color){
		this.text.setForeground(color);
	}
	
	public void setLayoutData(Object layoutData){
		this.text.setLayoutData(layoutData);
	}
	
	public void setLocation(Point location){
		this.text.setLocation(location);
	}
	
	public void setLocation(int x, int y){
		this.text.setLocation(x, y);
	}
	
	public void setMenu(Menu menu){
		this.text.setMenu(menu);
	}
	
	public boolean setParent(Composite parent){
		return this.text.setParent(parent);
	}
	
	public void setSize(Point size){
		this.text.setSize(size);
	}
	
	public void setSize(int width, int height){
		this.text.setSize(width, height);
	}
	
	public void setToolTipText(String string){
		this.text.setToolTipText(string);
	}
	
	public void setVisible(boolean visible){
		this.text.setVisible(visible);
	}
	
	public Point toControl(Point point){
		return this.text.toControl(point);
	}
	
	public Point toControl(int x, int y){
		return this.text.toControl(x, y);
	}
	
	public Point toDisplay(Point point){
		
		return this.text.toDisplay(point);
	}
	
	public Point toDisplay(int x, int y){
		return this.text.toDisplay(x, y);
	}
	
	public boolean traverse(int traversal){
		return this.text.traverse(traversal);
	}
	
	public void update(){
		this.text.update();
	}
	
	public void addDisposeListener(DisposeListener listener){
		this.text.addDisposeListener(listener);
	}
	
	public void addListener(int eventType, Listener listener){
		this.text.addListener(eventType, listener);
	}
	
	public void dispose(){
		this.text.dispose();
	}
	
	public Object getData(){
		return this.text.getData();
	}
	
	public Object getData(String key){
		return this.text.getData(key);
	}
	
	public Display getDisplay(){
		return this.text.getDisplay();
	}
	
	public int getStyle(){
		return this.text.getStyle();
	}
	
	public boolean isDisposed(){
		return this.text.isDisposed();
	}
	
	public boolean isListening(int eventType){
		return this.text.isListening(eventType);
	}
	
	public void notifyListeners(int eventType, Event event){
		this.text.notifyListeners(eventType, event);
	}
	
	public void removeDisposeListener(DisposeListener listener){
		this.text.removeDisposeListener(listener);
	}
	
	public void removeListener(int eventType, Listener listener){
		this.text.removeListener(eventType, listener);
	}
	
	public void setData(Object data){
		this.text.setData(data);
	}
	
	public void setData(String key, Object value){
		this.text.setData(key, value);
	}
	
	public String toString(){
		return this.text.toString();
	}
	
	public boolean equals(Object arg0){
		return this.text.equals(arg0);
	}
	
	public int hashCode(){
		return this.text.hashCode();
	}
}
