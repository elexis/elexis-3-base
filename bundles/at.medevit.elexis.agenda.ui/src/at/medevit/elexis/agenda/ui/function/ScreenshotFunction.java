package at.medevit.elexis.agenda.ui.function;

import java.io.ByteArrayInputStream;
import java.util.Base64;

import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.equo.chromium.swt.Browser;
import com.equo.chromium.swt.BrowserFunction;

public class ScreenshotFunction extends BrowserFunction {
	private static final Logger logger = LoggerFactory.getLogger(ScreenshotFunction.class);

	@Inject
	public ScreenshotFunction(Browser browser, String name) {
		super(browser, name);
	}

	@Override
	public Object function(Object[] arguments) {
		if (arguments.length > 0 && arguments[0] instanceof String) {
			String base64Image = (String) arguments[0];
			showPreviewAndPrint(base64Image);
		}
		return null;
	}

	private void showPreviewAndPrint(String base64Image) {
		Display.getDefault().asyncExec(() -> {
			try {
				String base64Data = base64Image.split(",")[1];
				byte[] imageBytes = Base64.getDecoder().decode(base64Data);

				ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
				ImageData imageData = new ImageData(inputStream);
				Image image = new Image(Display.getDefault(), imageData);

				Shell previewShell = new Shell(Display.getDefault());
				previewShell.setText(Messages.ScreenshotFunction_PreviewShell_Text);
				previewShell.setLayout(new GridLayout(1, false));

				Canvas canvas = new Canvas(previewShell, SWT.BORDER);
				canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				canvas.addPaintListener(e -> {
					int canvasWidth = canvas.getSize().x;
					int canvasHeight = canvas.getSize().y;
					float aspectRatio = (float) imageData.width / imageData.height;

					int scaledWidth = canvasWidth;
					int scaledHeight = (int) (canvasWidth / aspectRatio);

					if (scaledHeight > canvasHeight) {
						scaledHeight = canvasHeight;
						scaledWidth = (int) (canvasHeight * aspectRatio);
					}
					int xOffset = (canvasWidth - scaledWidth) / 2;
					int yOffset = (canvasHeight - scaledHeight) / 2;
					e.gc.drawImage(image, 0, 0, imageData.width, imageData.height, xOffset, yOffset, scaledWidth,
							scaledHeight);
				});
				Button printButton = new Button(previewShell, SWT.PUSH);
				printButton.setText(Messages.PrintChartAction_printTitle);
				printButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
				printButton.addListener(SWT.Selection, event -> {
					previewShell.close();
					openPrintDialog(imageData, image);
				});
				previewShell.setSize(800, 600);
				previewShell.open();
				Display display = Display.getDefault();
				while (!previewShell.isDisposed()) {
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				}
				image.dispose();
			} catch (Exception e) {
				logger.error("An error occurred while processing the screenshot:", e);
			}
		});
	}

	private void openPrintDialog(ImageData imageData, Image image) {
		Shell shell = Display.getDefault().getActiveShell();
		PrintDialog printDialog = new PrintDialog(shell, SWT.NONE);
		PrinterData printerData = printDialog.open();
		if (printerData != null) {
			Printer printer = new Printer(printerData);
			if (printer.startJob(Messages.ScreenshotFunction_PrintJob_Title)) {
				GC gc = new GC(printer);
				printer.startPage();
				int pageWidth = printer.getClientArea().width;
				int pageHeight = printer.getClientArea().height;
				float aspectRatio = (float) imageData.width / imageData.height;
				int scaledWidth = pageWidth;
				int scaledHeight = (int) (pageWidth / aspectRatio);
				if (scaledHeight > pageHeight) {
					scaledHeight = pageHeight;
					scaledWidth = (int) (pageHeight * aspectRatio);
				}
				int xOffset = (pageWidth - scaledWidth) / 2;
				int yOffset = (pageHeight - scaledHeight) / 2;
				gc.drawImage(image, 0, 0, imageData.width, imageData.height, xOffset, yOffset, scaledWidth,
						scaledHeight);
				gc.dispose();
				printer.endPage();
				printer.endJob();
			}
			printer.dispose();
		}
	}
}
