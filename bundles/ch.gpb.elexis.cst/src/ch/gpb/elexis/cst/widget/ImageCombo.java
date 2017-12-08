package ch.gpb.elexis.cst.widget;

/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Tom Seidel      - enhancements for image-handling
 *     Michael Edwards - further enhancements to support image AND text in 
 *                       collapsed state of combo
 *     Norbert Spiess  - setter for background and foreground color, variable icon 
 *                       side in collapsed state, select all only called
 *                       if textfield is editable
 *******************************************************************************/
import java.util.Arrays;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.eclipse.swt.accessibility.AccessibleTextAdapter;
import org.eclipse.swt.accessibility.AccessibleTextEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TypedListener;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * Instances of this class are controls that allow the user to choose an item from a list of items,
 * or optionally enter a new value by typing it into an editable text field. Often,
 * <code>Combo</code>s are used in the same place where a single selection <code>List</code> widget
 * could be used but space is limited. A <code>Combo</code> takes less space than a
 * <code>List</code> widget and shows similar information.
 * <p>
 * Note: Since <code>Combo</code>s can contain both a list and an editable text field, it is
 * possible to confuse methods which access one versus the other (compare for example,
 * <code>clearSelection()</code> and <code>deselectAll()</code>). The API documentation is careful
 * to indicate either "the receiver's list" or the "the receiver's text field" to distinguish
 * between the two cases.
 * </p>
 * <p>
 * Note that although this class is a subclass of <code>Composite</code>, it does not make sense to
 * add children to it, or set a layout on it.
 * </p>
 * <dl>
 * <dt><b>Styles:</b></dt>
 * <dd>DROP_DOWN, READ_ONLY, SIMPLE</dd>
 * <dt><b>Events:</b></dt>
 * <dd>DefaultSelection, Modify, Selection, Verify</dd>
 * </dl>
 * <p>
 * Note: Only one of the styles DROP_DOWN and SIMPLE may be specified.
 * </p>
 * <p>
 * IMPORTANT: This class is <em>not</em> intended to be subclassed.
 * </p>
 * @see List
 */

public class ImageCombo extends Composite {
    boolean noSelection, ignoreDefaultSelection, ignoreCharacter, ignoreModify;

    int cbtHook, scrollWidth, visibleCount = 5;

    private Composite comboComposite;

    private Label imageLabel;

    private Text text;

    private Table table;

    private int visibleItemCount = 4;

    private Shell popup;

    private Button arrow;

    private boolean hasFocus;

    private Listener listener, filter;

    private Color foreground, background;

    private Font font;

    private int style;

    /**
     * Equal to {@link ImageCombo#ImageCombo(Composite, int, SWT.LEFT)}.
     * @param parent
     * @param style
     */
    public ImageCombo(Composite parent, int style) {
	this(parent, style, SWT.LEFT);
    }

    /**
     * Constructs a new instance of this class given its parent and a style value describing its
     * behavior and appearance.
     * <p>
     * The style value is either one of the style constants defined in class <code>SWT</code> which
     * is applicable to instances of this class, or must be built by <em>bitwise OR</em>'ing
     * together (that is, using the <code>int</code> "|" operator) two or more of those
     * <code>SWT</code> style constants. The class description lists the style constants that are
     * applicable to the class. Style bits are also inherited from superclasses.
     * </p>
     * @param parent a composite control which will be the parent of the new instance (cannot be
     *        null)
     * @param style the style of control to construct
     * @param collapsedIconPosition the position of the icon in collapsed state (SWT.LEFT (default)
     *        or SWT.RIGHT)
     * @exception IllegalArgumentException <ul>
     *            <li>ERROR_NULL_ARGUMENT - if the parent is null</li>
     *            </ul>
     * @exception SWTException <ul>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            parent</li>
     *            <li>ERROR_INVALID_SUBCLASS - if this class is not an allowed subclass</li>
     *            </ul>
     * @see SWT#DROP_DOWN
     * @see SWT#READ_ONLY
     * @see SWT#SIMPLE
     * @see Widget#checkSubclass
     * @see Widget#getStyle
     */
    public ImageCombo(Composite parent, int style, int collapsedIconPosition) {
	super(parent, style = checkStyle(style));
	this.style = style;
	/* This code is intentionally commented */
	// if ((style & SWT.H_SCROLL) != 0) this.style |= SWT.H_SCROLL;
	this.style |= SWT.H_SCROLL;

	int textStyle = SWT.SINGLE;
	if ((style & SWT.READ_ONLY) != 0) {
	    textStyle |= SWT.READ_ONLY;
	}
	if ((style & SWT.FLAT) != 0) {
	    textStyle |= SWT.FLAT;
	}

	this.comboComposite = new Composite(this, SWT.NONE);
	TableWrapLayout tableWrapLayout = new TableWrapLayout();
	tableWrapLayout.numColumns = 2;
	tableWrapLayout.topMargin = 2;
	tableWrapLayout.bottomMargin = 0;
	tableWrapLayout.leftMargin = 2;
	tableWrapLayout.rightMargin = 0;
	this.comboComposite.setLayout(tableWrapLayout);
	this.comboComposite.setLayoutData(new TableWrapData());

	switch (collapsedIconPosition) {
	case SWT.RIGHT:
	    this.text = new Text(this.comboComposite, SWT.READ_ONLY);
	    this.text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

	    this.imageLabel = new Label(this.comboComposite, SWT.NONE);
	    this.imageLabel.setLayoutData(new TableWrapData());
	    break;
	case SWT.LEFT:
	default:
	    this.imageLabel = new Label(this.comboComposite, SWT.NONE);
	    this.imageLabel.setLayoutData(new TableWrapData());

	    this.text = new Text(this.comboComposite, SWT.READ_ONLY);
	    this.text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
	}

	this.comboComposite.setCursor(getDisplay().getSystemCursor(SWT.CURSOR_ARROW));

	int arrowStyle = SWT.ARROW | SWT.DOWN;
	if ((style & SWT.FLAT) != 0) {
	    arrowStyle |= SWT.FLAT;
	}
	this.arrow = new Button(this, arrowStyle);

	this.listener = new Listener() {
	    public void handleEvent(Event event) {
		if (ImageCombo.this.popup == event.widget) {
		    popupEvent(event);
		    return;
		}
		if (ImageCombo.this.text == event.widget) {
		    textEvent(event);
		    return;
		}
		if (ImageCombo.this.table == event.widget) {
		    listEvent(event);
		    return;
		}
		if (ImageCombo.this.arrow == event.widget) {
		    arrowEvent(event);
		    return;
		}
		if (ImageCombo.this == event.widget) {
		    comboEvent(event);
		    return;
		}
		if (getShell() == event.widget) {
		    handleFocus(SWT.FocusOut);
		}
	    }
	};
	this.filter = new Listener() {
	    public void handleEvent(Event event) {
		Shell shell = ((Control) event.widget).getShell();
		if (shell == ImageCombo.this.getShell()) {
		    handleFocus(SWT.FocusOut);
		}
	    }
	};

	int[] comboEvents = { SWT.Dispose, SWT.Move, SWT.Resize };
	for (int i = 0; i < comboEvents.length; i++) {
	    this.addListener(comboEvents[i], this.listener);
	}

	int[] textEvents = { SWT.KeyDown, SWT.KeyUp, SWT.Modify, SWT.MouseDown, SWT.MouseUp,
		SWT.Traverse, SWT.FocusIn };
	for (int i = 0; i < textEvents.length; i++) {
	    this.text.addListener(textEvents[i], this.listener);
	}

	int[] arrowEvents = { SWT.Selection, SWT.FocusIn };
	for (int i = 0; i < arrowEvents.length; i++) {
	    this.arrow.addListener(arrowEvents[i], this.listener);
	}

	createPopup(-1);
	initAccessible();
    }

    /**
     * Adds the argument to the end of the receiver's list.
     * @param string the new item
     * @exception IllegalArgumentException <ul>
     *            <li>ERROR_NULL_ARGUMENT - if the string is null</li>
     *            </ul>
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     * @see #add(String,int)
     */
    public void add(Image image, String string) {
	checkWidget();
	if (string == null) {
	    SWT.error(SWT.ERROR_NULL_ARGUMENT);
	}

	TableItem newItem = new TableItem(this.table, SWT.FILL);
	newItem.setText(string);//
	if (image != null) {
	    newItem.setImage(image);
	    this.imageLabel.setImage(image);
	}
    }

    /**
     * Adds the listener to the collection of listeners who will be notified when the receiver's
     * text is modified, by sending it one of the messages defined in the
     * <code>ModifyListener</code> interface.
     * @param listener the listener which should be notified
     * @exception IllegalArgumentException <ul>
     *            <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     *            </ul>
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     * @see ModifyListener
     * @see #removeModifyListener
     */
    public void addModifyListener(ModifyListener listener) {
	checkWidget();
	if (listener == null) {
	    SWT.error(SWT.ERROR_NULL_ARGUMENT);
	}
	TypedListener typedListener = new TypedListener(listener);
	addListener(SWT.Modify, typedListener);
    }

    /**
     * Adds the listener to the collection of listeners who will be notified when the user changes
     * the receiver's selection, by sending it one of the messages defined in the
     * <code>SelectionListener</code> interface.
     * <p>
     * <code>widgetSelected</code> is called when the user changes the combo's list selection.
     * <code>widgetDefaultSelected</code> is typically called when ENTER is pressed the combo's text
     * area.
     * </p>
     * @param listener the listener which should be notified
     * @exception IllegalArgumentException <ul>
     *            <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     *            </ul>
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     * @see SelectionListener
     * @see #removeSelectionListener
     * @see SelectionEvent
     */
    public void addSelectionListener(SelectionListener listener) {
	checkWidget();
	if (listener == null) {
	    SWT.error(SWT.ERROR_NULL_ARGUMENT);
	}
	TypedListener typedListener = new TypedListener(listener);
	addListener(SWT.Selection, typedListener);
	addListener(SWT.DefaultSelection, typedListener);
    }

    /**
     * Adds the listener to the collection of listeners who will be notified when the receiver's
     * text is verified, by sending it one of the messages defined in the
     * <code>VerifyListener</code> interface.
     * @param listener the listener which should be notified
     * @exception IllegalArgumentException <ul>
     *            <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     *            </ul>
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     * @see VerifyListener
     * @see #removeVerifyListener
     * @since 3.1
     */
    public void addVerifyListener(VerifyListener listener) {
	checkWidget();
	if (listener == null) {
	    SWT.error(SWT.ERROR_NULL_ARGUMENT);
	}
	TypedListener typedListener = new TypedListener(listener);
	addListener(SWT.Verify, typedListener);
    }

    static int checkStyle(int style) {
	int mask = SWT.BORDER | SWT.READ_ONLY | SWT.FLAT | SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT;
	return style & mask;
    }

    public Point computeSize(int wHint, int hHint, boolean changed) {
	checkWidget();
	int width = 0, height = 0;
	String[] items = getStringsFromTable();
	int textWidth = 0;
	GC gc = new GC(this.comboComposite);
	int spacer = gc.stringExtent(" ").x; //$NON-NLS-1$
	for (int i = 0; i < items.length; i++) {
	    textWidth = Math.max(gc.stringExtent(items[i]).x, textWidth);
	}
	gc.dispose();
	Point textSize = this.comboComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
	Point arrowSize = this.arrow.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed);
	Point listSize = this.table.computeSize(wHint, SWT.DEFAULT, changed);
	int borderWidth = getBorderWidth();

	height = Math.max(hHint, Math.max(textSize.y, arrowSize.y) + 2 * borderWidth);
	width = Math.max(wHint,
		Math.max(textWidth + 2 * spacer + arrowSize.x + 2 * borderWidth, listSize.x));
	return new Point(width + 10, height);
    }

    /**
     * Deselects the item at the given zero-relative index in the receiver's list. If the item at
     * the index was already deselected, it remains deselected. Indices that are out of range are
     * ignored.
     * @param index the index of the item to deselect
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     */
    public void deselect(int index) {
	checkWidget();
	this.table.deselect(index);
    }

    /**
     * Deselects all selected items in the receiver's list.
     * <p>
     * Note: To clear the selection in the receiver's text field, use <code>clearSelection()</code>.
     * </p>
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     * @see #clearSelection
     */
    public void deselectAll() {
	checkWidget();
	this.text.clearSelection();
	this.table.deselectAll();
    }

    /**
     * Returns the item at the given, zero-relative index in the receiver's list. Throws an
     * exception if the index is out of range.
     * @param index the index of the item to return
     * @return the item at the given index
     * @exception IllegalArgumentException <ul>
     *            <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements
     *            in the list minus 1 (inclusive)</li>
     *            </ul>
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     */
    public String getItem(int index) {
	checkWidget();
	return this.table.getItem(index).getText();
    }

    /**
     * Returns the number of items contained in the receiver's list.
     * @return the number of items
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     */
    public int getItemCount() {
	checkWidget();
	return this.table.getItemCount();
    }

    /**
     * Returns the height of the area which would be used to display <em>one</em> of the items in
     * the receiver's list.
     * @return the height of one item
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     */
    public int getItemHeight() {
	checkWidget();
	return this.table.getItemHeight();
    }

    /**
     * Returns a (possibly empty) array of <code>String</code>s which are the items in the
     * receiver's list.
     * <p>
     * Note: This is not the actual structure used by the receiver to maintain its list of items, so
     * modifying the array will not affect the receiver.
     * </p>
     * @return the items in the receiver's list
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     */
    public TableItem[] getItems() {
	checkWidget();
	return this.table.getItems();
    }

    /**
     * Returns the orientation of the receiver.
     * @return the orientation style
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     * @since 2.1.2
     */
    public int getOrientation() {
	checkWidget();
	return this.style & (SWT.LEFT_TO_RIGHT | SWT.RIGHT_TO_LEFT);
    }

    /**
     * Returns a <code>Point</code> whose x coordinate is the character position representing the
     * start of the selection in the receiver's text field, and whose y coordinate is the character
     * position representing the end of the selection. An "empty" selection is indicated by the x
     * and y coordinates having the same value.
     * <p>
     * Indexing is zero based. The range of a selection is from 0..N where N is the number of
     * characters in the widget.
     * </p>
     * @return a point representing the selection start and end
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     */
    public Point getSelection() {
	checkWidget();
	return this.text.getSize();
    }

    /**
     * Returns the zero-relative index of the item which is currently selected in the receiver's
     * list, or -1 if no item is selected.
     * @return the index of the selected item
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     */
    public int getSelectionIndex() {
	checkWidget();
	return this.table.getSelectionIndex();
    }

    /**
     * Returns a string containing a copy of the contents of the receiver's text field, or an empty
     * string if there are no contents.
     * @return the receiver's text
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     */
    public String getText() {
	checkWidget();
	return this.text.getText();
    }

    /**
     * Returns the height of the receivers's text field.
     * @return the text height
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     */
    public int getTextHeight() {
	checkWidget();
	return this.text.getSize().x;
    }

    /**
     * Gets the number of items that are visible in the drop down portion of the receiver's list.
     * <p>
     * Note: This operation is a hint and is not supported on platforms that do not have this
     * concept.
     * </p>
     * @return the number of items that are visible
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     * @since 3.0
     */
    public int getVisibleItemCount() {
	checkWidget();
	return this.visibleCount;
    }

    /**
     * Searches the receiver's list starting at the first item (index 0) until an item is found that
     * is equal to the argument, and returns the index of that item. If no item is found, returns
     * -1.
     * @param string the search item
     * @return the index of the item
     * @exception IllegalArgumentException <ul>
     *            <li>ERROR_NULL_ARGUMENT - if the string is null</li>
     *            </ul>
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     */
    public int indexOf(String string) {
	checkWidget();
	if (string == null) {
	    SWT.error(SWT.ERROR_NULL_ARGUMENT);
	}
	return Arrays.asList(getStringsFromTable()).indexOf(string);
    }

    /**
     * Removes the item from the receiver's list at the given zero-relative index.
     * @param index the index for the item
     * @exception IllegalArgumentException <ul>
     *            <li>ERROR_INVALID_RANGE - if the index is not between 0 and the number of elements
     *            in the list minus 1 (inclusive)</li>
     *            </ul>
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     */
    public void remove(int index) {
	checkWidget();
	this.table.remove(index);
    }

    /**
     * Removes the items from the receiver's list which are between the given zero-relative start
     * and end indices (inclusive).
     * @param start the start of the range
     * @param end the end of the range
     * @exception IllegalArgumentException <ul>
     *            <li>ERROR_INVALID_RANGE - if either the start or end are not between 0 and the
     *            number of elements in the list minus 1 (inclusive)</li>
     *            </ul>
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     */
    public void remove(int start, int end) {
	checkWidget();
	this.table.remove(start, end);
    }

    /**
     * Searches the receiver's list starting at the first item until an item is found that is equal
     * to the argument, and removes that item from the list.
     * @param string the item to remove
     * @exception IllegalArgumentException <ul>
     *            <li>ERROR_NULL_ARGUMENT - if the string is null</li>
     *            <li>ERROR_INVALID_ARGUMENT - if the string is not found in the list</li>
     *            </ul>
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     */
    public void remove(String string) {
	checkWidget();
	if (string == null) {
	    SWT.error(SWT.ERROR_NULL_ARGUMENT);
	}
	int index = -1;
	for (int i = 0, n = this.table.getItemCount(); i < n; i++) {
	    if (this.table.getItem(i).getText().equals(string)) {
		index = i;
		break;
	    }
	}
	remove(index);
    }

    /**
     * Removes all of the items from the receiver's list and clear the contents of receiver's text
     * field.
     * <p>
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li> <li>
     *            ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     */
    public void removeAll() {
	checkWidget();
	this.text.setText(""); //$NON-NLS-1$
	this.table.removeAll();
    }

    /**
     * Removes the listener from the collection of listeners who will be notified when the
     * receiver's text is modified.
     * @param listener the listener which should no longer be notified
     * @exception IllegalArgumentException <ul>
     *            <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     *            </ul>
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     * @see ModifyListener
     * @see #addModifyListener
     */
    public void removeModifyListener(ModifyListener listener) {
	checkWidget();
	if (listener == null) {
	    SWT.error(SWT.ERROR_NULL_ARGUMENT);
	}
	removeListener(SWT.Modify, listener);
    }

    /**
     * Removes the listener from the collection of listeners who will be notified when the user
     * changes the receiver's selection.
     * @param listener the listener which should no longer be notified
     * @exception IllegalArgumentException <ul>
     *            <li>ERROR_NULL_ARGUMENT - if the listener is null</li>
     *            </ul>
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     * @see SelectionListener
     * @see #addSelectionListener
     */
    public void removeSelectionListener(SelectionListener listener) {
	checkWidget();
	if (listener == null) {
	    SWT.error(SWT.ERROR_NULL_ARGUMENT);
	}
	removeListener(SWT.Selection, listener);
	removeListener(SWT.DefaultSelection, listener);
    }

    /**
     * Selects the item at the given zero-relative index in the receiver's list. If the item at the
     * index was already selected, it remains selected. Indices that are out of range are ignored.
     * @param index the index of the item to select
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     */
    public void select(int index) {
	checkWidget();
	if (index == -1) {
	    this.table.deselectAll();
	    this.text.setText(""); //$NON-NLS-1$
	    return;
	}
	if (0 <= index && index < this.table.getItemCount()) {
	    if (index != getSelectionIndex()) {
		this.imageLabel.setImage(this.table.getItem(index).getImage());
		this.text.setText(this.table.getItem(index).getText());
		if (text.getEditable()) {
		    this.text.selectAll();
		}
		this.table.select(index);
		this.table.showSelection();
	    }
	}
    }

    public void setFont(Font font) {
	checkWidget();
	super.setFont(font);
	this.font = font;
	this.text.setFont(font);
	this.table.setFont(font);
	internalLayout(true);
    }

    /**
     * Sets the contents of the receiver's text field to the given string.
     * <p>
     * Note: The text field in a <code>Combo</code> is typically only capable of displaying a single
     * line of text. Thus, setting the text to a string containing line breaks or other special
     * characters will probably cause it to display incorrectly.
     * </p>
     * @param string the new text
     * @exception IllegalArgumentException <ul>
     *            <li>ERROR_NULL_ARGUMENT - if the string is null</li>
     *            </ul>
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     */
    public void setText(String string) {
	checkWidget();
	if (string == null) {
	    SWT.error(SWT.ERROR_NULL_ARGUMENT);
	}
	int index = -1;
	for (int i = 0, n = this.table.getItemCount(); i < n; i++) {
	    if (this.table.getItem(i).getText().equals(string)) {
		index = i;
		break;
	    }
	}
	if (index == -1) {
	    this.table.deselectAll();
	    this.text.setText(string);
	    return;
	}
	this.text.setText(string);
	if (text.getEditable()) {
	    this.text.selectAll();
	}
	this.table.setSelection(index);
	this.table.showSelection();
    }

    public void setToolTipText(String string) {
	checkWidget();
	super.setToolTipText(string);
	this.arrow.setToolTipText(string);
	this.comboComposite.setToolTipText(string);
    }

    /**
     * Sets the number of items that are visible in the drop down portion of the receiver's list.
     * <p>
     * Note: This operation is a hint and is not supported on platforms that do not have this
     * concept.
     * </p>
     * @param count the new number of items to be visible
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     * @since 3.0
     */
    public void setVisibleItemCount(int count) {
	checkWidget();
	if (count < 0) {
	    return;
	}
	this.visibleItemCount = count;
    }

    /**
     * Gets the editable state.
     * @return whether or not the reciever is editable
     * @exception SWTException <ul>
     *            <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *            <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the
     *            receiver</li>
     *            </ul>
     * @since 3.0
     */
    public boolean getEditable() {
	checkWidget();
	return this.text.getEditable();
    }

    void handleFocus(int type) {
	if (isDisposed()) {
	    return;
	}
	switch (type) {
	case SWT.FocusIn: {
	    if (this.hasFocus) {
		return;
	    }
	    if (getEditable()) {
		this.text.selectAll();
	    }
	    this.hasFocus = true;
	    Shell shell = getShell();
	    shell.removeListener(SWT.Deactivate, this.listener);
	    shell.addListener(SWT.Deactivate, this.listener);
	    Display display = getDisplay();
	    display.removeFilter(SWT.FocusIn, this.filter);
	    display.addFilter(SWT.FocusIn, this.filter);
	    Event e = new Event();
	    notifyListeners(SWT.FocusIn, e);
	    break;
	}
	case SWT.FocusOut: {
	    if (!this.hasFocus) {
		return;
	    }
	    Control focusControl = getDisplay().getFocusControl();
	    if (focusControl == this.arrow || focusControl == this.table
		    || focusControl == this.comboComposite) {
		return;
	    }
	    this.hasFocus = false;
	    Shell shell = getShell();
	    shell.removeListener(SWT.Deactivate, this.listener);
	    Display display = getDisplay();
	    display.removeFilter(SWT.FocusIn, this.filter);
	    Event e = new Event();
	    notifyListeners(SWT.FocusOut, e);
	    break;
	}
	}
    }

    void createPopup(int selectionIndex) {
	// create shell and list
	this.popup = new Shell(getShell(), SWT.NO_TRIM | SWT.ON_TOP);
	int style = getStyle();
	int listStyle = SWT.SINGLE | SWT.V_SCROLL;
	if ((style & SWT.FLAT) != 0) {
	    listStyle |= SWT.FLAT;
	}
	if ((style & SWT.RIGHT_TO_LEFT) != 0) {
	    listStyle |= SWT.RIGHT_TO_LEFT;
	}
	if ((style & SWT.LEFT_TO_RIGHT) != 0) {
	    listStyle |= SWT.LEFT_TO_RIGHT;
	}
	// create a table instead of a list.
	this.table = new Table(this.popup, listStyle);
	if (this.font != null) {
	    this.table.setFont(this.font);
	}
	if (this.foreground != null) {
	    this.table.setForeground(this.foreground);
	}
	if (this.background != null) {
	    this.table.setBackground(this.background);
	}

	int[] popupEvents = { SWT.Close, SWT.Paint, SWT.Deactivate };
	for (int i = 0; i < popupEvents.length; i++) {
	    this.popup.addListener(popupEvents[i], this.listener);
	}
	int[] listEvents = { SWT.MouseUp, SWT.Selection, SWT.Traverse, SWT.KeyDown, SWT.KeyUp,
		SWT.FocusIn, SWT.Dispose };
	for (int i = 0; i < listEvents.length; i++) {
	    this.table.addListener(listEvents[i], this.listener);
	}

	if (selectionIndex != -1) {
	    this.table.setSelection(selectionIndex);
	}
    }

    boolean isDropped() {
	return this.popup.getVisible();
    }

    void dropDown(boolean drop) {
	if (drop == isDropped()) {
	    return;
	}
	if (!drop) {
	    this.popup.setVisible(false);
	    if (!isDisposed() && this.arrow.isFocusControl()) {
		this.comboComposite.setFocus();
	    }
	    return;
	}

	if (getShell() != this.popup.getParent()) {
	    int selectionIndex = this.table.getSelectionIndex();
	    this.table.removeListener(SWT.Dispose, this.listener);
	    this.popup.dispose();
	    this.popup = null;
	    this.table = null;
	    createPopup(selectionIndex);
	}

	Point size = getSize();
	int itemCount = this.table.getItemCount();
	itemCount = (itemCount == 0) ? this.visibleItemCount : Math.min(this.visibleItemCount,
		itemCount);
	int itemHeight = this.table.getItemHeight() * itemCount;
	Point listSize = this.table.computeSize(SWT.DEFAULT, itemHeight, false);
	this.table.setBounds(1, 1, Math.max(size.x - 2, listSize.x), listSize.y);

	int index = this.table.getSelectionIndex();
	if (index != -1) {
	    this.table.setTopIndex(index);
	}
	Display display = getDisplay();
	Rectangle listRect = this.table.getBounds();
	Rectangle parentRect = display.map(getParent(), null, getBounds());
	Point comboSize = getSize();
	Rectangle displayRect = getMonitor().getClientArea();
	int width = Math.max(comboSize.x, listRect.width + 2);
	int height = listRect.height + 2;
	int x = parentRect.x;
	int y = parentRect.y + comboSize.y;
	if (y + height > displayRect.y + displayRect.height) {
	    y = parentRect.y - height;
	}
	this.popup.setBounds(x, y, width, height);
	this.popup.setVisible(true);
	this.table.setFocus();
    }

    void listEvent(Event event) {
	switch (event.type) {
	case SWT.Dispose:
	    if (getShell() != this.popup.getParent()) {
		int selectionIndex = this.table.getSelectionIndex();
		this.popup = null;
		this.table = null;
		createPopup(selectionIndex);
	    }
	    break;
	case SWT.FocusIn: {
	    handleFocus(SWT.FocusIn);
	    break;
	}
	case SWT.MouseUp: {
	    if (event.button != 1) {
		return;
	    }
	    dropDown(false);
	    break;
	}
	case SWT.Selection: {
	    int index = this.table.getSelectionIndex();
	    if (index == -1) {
		return;
	    }
	    this.text.setText(this.table.getItem(index).getText());
	    if (text.getEditable()) {
		this.text.selectAll();
	    }
	    ;
	    this.imageLabel.setImage(this.table.getItem(index).getImage());
	    this.table.setSelection(index);
	    Event e = new Event();
	    e.time = event.time;
	    e.stateMask = event.stateMask;
	    e.doit = event.doit;
	    notifyListeners(SWT.Selection, e);
	    event.doit = e.doit;
	    break;
	}
	case SWT.Traverse: {
	    switch (event.detail) {
	    case SWT.TRAVERSE_RETURN:
	    case SWT.TRAVERSE_ESCAPE:
	    case SWT.TRAVERSE_ARROW_PREVIOUS:
	    case SWT.TRAVERSE_ARROW_NEXT:
		event.doit = false;
		break;
	    }
	    Event e = new Event();
	    e.time = event.time;
	    e.detail = event.detail;
	    e.doit = event.doit;
	    e.character = event.character;
	    e.keyCode = event.keyCode;
	    notifyListeners(SWT.Traverse, e);
	    event.doit = e.doit;
	    event.detail = e.detail;
	    break;
	}
	case SWT.KeyUp: {
	    Event e = new Event();
	    e.time = event.time;
	    e.character = event.character;
	    e.keyCode = event.keyCode;
	    e.stateMask = event.stateMask;
	    notifyListeners(SWT.KeyUp, e);
	    break;
	}
	case SWT.KeyDown: {
	    if (event.character == SWT.ESC) {
		// Escape key cancels popup list
		dropDown(false);
	    }
	    if ((event.stateMask & SWT.ALT) != 0
		    && (event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN)) {
		dropDown(false);
	    }
	    if (event.character == SWT.CR) {
		// Enter causes default selection
		dropDown(false);
		Event e = new Event();
		e.time = event.time;
		e.stateMask = event.stateMask;
		notifyListeners(SWT.DefaultSelection, e);
	    }
	    // At this point the widget may have been disposed.
	    // If so, do not continue.
	    if (isDisposed()) {
		break;
	    }
	    Event e = new Event();
	    e.time = event.time;
	    e.character = event.character;
	    e.keyCode = event.keyCode;
	    e.stateMask = event.stateMask;
	    notifyListeners(SWT.KeyDown, e);
	    break;

	}
	}
    }

    void arrowEvent(Event event) {
	switch (event.type) {
	case SWT.FocusIn: {
	    handleFocus(SWT.FocusIn);
	    break;
	}
	case SWT.Selection: {
	    dropDown(!isDropped());
	    break;
	}
	}
    }

    void comboEvent(Event event) {
	switch (event.type) {
	case SWT.Dispose:
	    if (this.popup != null && !this.popup.isDisposed()) {
		this.table.removeListener(SWT.Dispose, this.listener);
		this.popup.dispose();
	    }
	    Shell shell = getShell();
	    shell.removeListener(SWT.Deactivate, this.listener);
	    Display display = getDisplay();
	    display.removeFilter(SWT.FocusIn, this.filter);
	    this.popup = null;
	    this.comboComposite = null;
	    this.table = null;
	    this.arrow = null;
	    break;
	case SWT.Move:
	    dropDown(false);
	    break;
	case SWT.Resize:
	    internalLayout(false);
	    break;
	}
    }

    void internalLayout(boolean changed) {
	if (isDropped()) {
	    dropDown(false);
	}
	Rectangle rect = getClientArea();
	int width = rect.width;
	int height = rect.height;
	Point arrowSize = this.arrow.computeSize(SWT.DEFAULT, height, changed);
	this.comboComposite.setBounds(0, 0, width - arrowSize.x, height);
	this.arrow.setBounds(width - arrowSize.x, 0, arrowSize.x, arrowSize.y);
    }

    void popupEvent(Event event) {
	switch (event.type) {
	case SWT.Paint:
	    // draw black rectangle around list
	    Rectangle listRect = this.table.getBounds();
	    Color black = getDisplay().getSystemColor(SWT.COLOR_BLACK);
	    event.gc.setForeground(black);
	    event.gc.drawRectangle(0, 0, listRect.width + 1, listRect.height + 1);
	    break;
	case SWT.Close:
	    event.doit = false;
	    dropDown(false);
	    break;
	case SWT.Deactivate:
	    dropDown(false);
	    break;
	}
    }

    Label getAssociatedLabel() {
	Control[] siblings = getParent().getChildren();
	for (int i = 0; i < siblings.length; i++) {
	    if (siblings[i] == ImageCombo.this) {
		if (i > 0 && siblings[i - 1] instanceof Label) {
		    return (Label) siblings[i - 1];
		}
	    }
	}
	return null;
    }

    char getMnemonic(String string) {
	int index = 0;
	int length = string.length();
	do {
	    while ((index < length) && (string.charAt(index) != '&')) {
		index++;
	    }
	    if (++index >= length) {
		return '\0';
	    }
	    if (string.charAt(index) != '&') {
		return string.charAt(index);
	    }
	    index++;
	} while (index < length);
	return '\0';
    }

    String[] getStringsFromTable() {
	String[] items = new String[this.table.getItems().length];
	for (int i = 0, n = items.length; i < n; i++) {
	    items[i] = this.table.getItem(i).getText();
	}
	return items;
    }

    String stripMnemonic(String string) {
	int index = 0;
	int length = string.length();
	do {
	    while ((index < length) && (string.charAt(index) != '&')) {
		index++;
	    }
	    if (++index >= length) {
		return string;
	    }
	    if (string.charAt(index) != '&') {
		return string.substring(0, index - 1) + string.substring(index, length);
	    }
	    index++;
	} while (index < length);
	return string;
    }

    void initAccessible() {
	AccessibleAdapter accessibleAdapter = new AccessibleAdapter() {
	    public void getName(AccessibleEvent e) {
		String name = null;
		Label label = getAssociatedLabel();
		if (label != null) {
		    name = stripMnemonic(label.getText());
		}
		e.result = name;
	    }

	    public void getKeyboardShortcut(AccessibleEvent e) {
		String shortcut = null;
		Label label = getAssociatedLabel();
		if (label != null) {
		    String text = label.getText();
		    if (text != null) {
			char mnemonic = getMnemonic(text);
			if (mnemonic != '\0') {
			    shortcut = "Alt+" + mnemonic; //$NON-NLS-1$
			}
		    }
		}
		e.result = shortcut;
	    }

	    public void getHelp(AccessibleEvent e) {
		e.result = getToolTipText();
	    }
	};
	getAccessible().addAccessibleListener(accessibleAdapter);
	this.comboComposite.getAccessible().addAccessibleListener(accessibleAdapter);
	this.table.getAccessible().addAccessibleListener(accessibleAdapter);

	this.arrow.getAccessible().addAccessibleListener(new AccessibleAdapter() {
	    public void getName(AccessibleEvent e) {
		e.result = isDropped() ? SWT.getMessage("SWT_Close") : SWT.getMessage("SWT_Open"); //$NON-NLS-1$ //$NON-NLS-2$
	    }

	    public void getKeyboardShortcut(AccessibleEvent e) {
		e.result = "Alt+Down Arrow"; //$NON-NLS-1$
	    }

	    public void getHelp(AccessibleEvent e) {
		e.result = getToolTipText();
	    }
	});

	getAccessible().addAccessibleTextListener(new AccessibleTextAdapter() {
	    public void getCaretOffset(AccessibleTextEvent e) {
		e.offset = ImageCombo.this.text.getCaretPosition();
	    }
	});

	getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
	    public void getChildAtPoint(AccessibleControlEvent e) {
		Point testPoint = toControl(e.x, e.y);
		if (getBounds().contains(testPoint)) {
		    e.childID = ACC.CHILDID_SELF;
		}
	    }

	    public void getLocation(AccessibleControlEvent e) {
		Rectangle location = getBounds();
		Point pt = toDisplay(location.x, location.y);
		e.x = pt.x;
		e.y = pt.y;
		e.width = location.width;
		e.height = location.height;
	    }

	    public void getChildCount(AccessibleControlEvent e) {
		e.detail = 0;
	    }

	    public void getRole(AccessibleControlEvent e) {
		e.detail = ACC.ROLE_COMBOBOX;
	    }

	    public void getState(AccessibleControlEvent e) {
		e.detail = ACC.STATE_NORMAL;
	    }

	    public void getValue(AccessibleControlEvent e) {
		e.result = getText();
	    }
	});

	this.text.getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
	    public void getRole(AccessibleControlEvent e) {
		e.detail = ACC.ROLE_LABEL;
	    }
	});

	this.arrow.getAccessible().addAccessibleControlListener(new AccessibleControlAdapter() {
	    public void getDefaultAction(AccessibleControlEvent e) {
		e.result = isDropped() ? SWT.getMessage("SWT_Close") : SWT.getMessage("SWT_Open"); //$NON-NLS-1$ //$NON-NLS-2$
	    }
	});
    }

    void textEvent(Event event) {
	switch (event.type) {
	case SWT.FocusIn: {
	    handleFocus(SWT.FocusIn);
	    break;
	}
	case SWT.KeyDown: {
	    if (event.character == SWT.CR) {
		dropDown(false);
		Event e = new Event();
		e.time = event.time;
		e.stateMask = event.stateMask;
		notifyListeners(SWT.DefaultSelection, e);
	    }
	    // At this point the widget may have been disposed.
	    // If so, do not continue.
	    if (isDisposed()) {
		break;
	    }

	    if (event.keyCode == SWT.ARROW_UP || event.keyCode == SWT.ARROW_DOWN) {
		event.doit = false;
		if ((event.stateMask & SWT.ALT) != 0) {
		    boolean dropped = isDropped();
		    if (text.getEditable()) {
			this.text.selectAll();
		    }
		    ;
		    if (!dropped) {
			setFocus();
		    }
		    dropDown(!dropped);
		    break;
		}

		int oldIndex = getSelectionIndex();
		if (event.keyCode == SWT.ARROW_UP) {
		    select(Math.max(oldIndex - 1, 0));
		}
		else {
		    select(Math.min(oldIndex + 1, getItemCount() - 1));
		}
		if (oldIndex != getSelectionIndex()) {
		    Event e = new Event();
		    e.time = event.time;
		    e.stateMask = event.stateMask;
		    notifyListeners(SWT.Selection, e);
		}
		// At this point the widget may have been disposed.
		// If so, do not continue.
		if (isDisposed()) {
		    break;
		}
	    }

	    // Further work : Need to add support for incremental search in
	    // pop up list as characters typed in text widget

	    Event e = new Event();
	    e.time = event.time;
	    e.character = event.character;
	    e.keyCode = event.keyCode;
	    e.stateMask = event.stateMask;
	    notifyListeners(SWT.KeyDown, e);
	    break;
	}
	case SWT.KeyUp: {
	    Event e = new Event();
	    e.time = event.time;
	    e.character = event.character;
	    e.keyCode = event.keyCode;
	    e.stateMask = event.stateMask;
	    notifyListeners(SWT.KeyUp, e);
	    break;
	}
	case SWT.Modify: {
	    this.table.deselectAll();
	    Event e = new Event();
	    e.time = event.time;
	    notifyListeners(SWT.Modify, e);
	    break;
	}
	case SWT.MouseDown: {
	    if (event.button != 1) {
		return;
	    }
	    if (this.text.getEditable()) {
		return;
	    }
	    boolean dropped = isDropped();
	    if (text.getEditable()) {
		this.text.selectAll();
	    }
	    ;
	    if (!dropped) {
		setFocus();
	    }
	    dropDown(!dropped);
	    break;
	}
	case SWT.MouseUp: {
	    if (event.button != 1) {
		return;
	    }
	    if (this.text.getEditable()) {
		return;
	    }
	    if (text.getEditable()) {
		this.text.selectAll();
	    }
	    ;
	    break;
	}
	case SWT.Traverse: {
	    switch (event.detail) {
	    case SWT.TRAVERSE_RETURN:
	    case SWT.TRAVERSE_ARROW_PREVIOUS:
	    case SWT.TRAVERSE_ARROW_NEXT:
		// The enter causes default selection and
		// the arrow keys are used to manipulate the list contents so
		// do not use them for traversal.
		event.doit = false;
		break;
	    }

	    Event e = new Event();
	    e.time = event.time;
	    e.detail = event.detail;
	    e.doit = event.doit;
	    e.character = event.character;
	    e.keyCode = event.keyCode;
	    notifyListeners(SWT.Traverse, e);
	    event.doit = e.doit;
	    event.detail = e.detail;
	    break;
	}
	}
    }

    public void setBackground(Color color) {
	super.setBackground(color);
	if (this.imageLabel != null)
	    this.imageLabel.setBackground(color);
	if (this.comboComposite != null)
	    this.comboComposite.setBackground(color);
	if (this.text != null)
	    this.text.setBackground(color);
	if (this.table != null)
	    this.table.setBackground(color);
	if (this.arrow != null)
	    this.arrow.setBackground(color);
    }

    public void setForeground(Color color) {
	super.setForeground(color);
	if (this.text != null) {
	    this.text.setForeground(color);
	}
	if (this.table != null)
	    this.table.setForeground(color);
    }
}
