package ch.netzkonzept.elexis.medidata.xml;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class XmlEditor {

	private String xmlString;
	private List<XmlRegion> regions;
	private List<StyleRange> computedRegions;
	private String title;
	private Shell parent;

	public XmlEditor(Shell parent, String xmlString, String title) {
		this.parent = parent;
		this.xmlString = xmlString;
		this.regions = new XmlRegionAnalyzer().analyzeXml(xmlString);
		this.computedRegions = computeStyleRanges(regions);
		this.title = title;
	}

	public Shell getShell() {
		Shell shell = new Shell();
		shell.setLayout(new GridLayout());
		shell.setText(title);
		StyledText styledText = new StyledText(shell, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		styledText.setLayoutData(new GridData(GridData.FILL_BOTH));
		styledText.setEditable(false);
		Font font = new Font(shell.getDisplay(), "Courier New", 10, SWT.NORMAL);
		styledText.setFont(font);
		styledText.setText(this.xmlString);
		StyleRange[] srs = new StyleRange[computedRegions.size()];
		int i = 0;
		for (StyleRange sr : computedRegions) {
			srs[i] = sr;
			i++;
		}
		styledText.setStyleRanges(srs);
		// Center the StyleText widget
		Rectangle parentSize = parent.getShell().getBounds();
		Rectangle shellSize = shell.getBounds();
		int locationX = (parentSize.width - shellSize.width) / 2 + parentSize.x;
		int locationY = (parentSize.height - shellSize.height) / 2 + parentSize.y;

		shell.setLocation(new Point(locationX, locationY));
		return shell;
	}

	private static List<StyleRange> computeStyleRanges(List<XmlRegion> regions) {

		List<StyleRange> styleRanges = new ArrayList<StyleRange>();
		for (XmlRegion xr : regions) {

			// The style itself depends on the region type
			// In this example, we use colors from the system
			StyleRange sr = new StyleRange();
			switch (xr.getXmlRegionType()) {
			case MARKUP:
				sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN);
				sr.fontStyle = SWT.BOLD;
				break;

			case ATTRIBUTE:
				sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_RED);
				break;

			// And so on...
			case ATTRIBUTE_VALUE:
				sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_BLUE);
				break;
			case MARKUP_VALUE:
				break;
			case COMMENT:
				sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_YELLOW);
				break;
			case INSTRUCTION:
				sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
				break;
			case CDATA:
				sr.foreground = Display.getDefault().getSystemColor(SWT.COLOR_RED);
				break;
			case WHITESPACE:
				break;
			default:
				break;
			}

			// Define the position and limit
			sr.start = xr.getStart();
			sr.length = xr.getEnd() - xr.getStart();
			styleRanges.add(sr);
		}
		return styleRanges;
	}
}
