package at.medevit.elexis.agenda.ui.function;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.util.Base64;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
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

import com.equo.chromium.swt.Browser;
import com.equo.chromium.swt.BrowserFunction;

public class ScreenshotFunction extends BrowserFunction {



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
				// Decodieren des Bildes
				String base64Data = base64Image.split(",")[1];
				byte[] imageBytes = Base64.getDecoder().decode(base64Data);

				ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
				ImageData imageData = new ImageData(inputStream);
				Image image = new Image(Display.getDefault(), imageData);

				// Erstellen eines Vorschaufensters
				Shell previewShell = new Shell(Display.getDefault());
				previewShell.setText("Bildvorschau");
				previewShell.setLayout(new GridLayout(1, false));

				// Canvas für die Bildanzeige
				Canvas canvas = new Canvas(previewShell, SWT.BORDER);
				canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				canvas.addPaintListener(e -> {
					// Bild in Canvas einpassen
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

				// Drucken-Button
				Button printButton = new Button(previewShell, SWT.PUSH);
				printButton.setText("Drucken");
				printButton.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));

				// Event für den Druck-Button
				printButton.addListener(SWT.Selection, event -> {
					// Vorschaufenster schließen
					previewShell.close();
					// Druckdialog öffnen und Bild drucken
					openPrintDialog(imageData, image);
				});

				// Vorschaufenster anzeigen
				previewShell.setSize(800, 600); // Fenstergröße einstellen
				previewShell.open();

				// Event-Loop für das Vorschaufenster
				Display display = Display.getDefault();
				while (!previewShell.isDisposed()) {
					if (!display.readAndDispatch()) {
						display.sleep();
					}
				}

				image.dispose(); // Bildressourcen freigeben

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private void openPrintDialog(ImageData imageData, Image image) {
		Shell shell = Display.getDefault().getActiveShell();
		PrintDialog printDialog = new PrintDialog(shell, SWT.NONE);
		PrinterData printerData = printDialog.open();

		if (printerData != null) {
			Printer printer = new Printer(printerData);

			if (printer.startJob("Screenshot Print Job")) {
				GC gc = new GC(printer); // Graphics Context für den Drucker erstellen
				printer.startPage();

				// Berechnung der Skalierung und Positionierung für den Druck
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

				gc.dispose(); // Ressourcen freigeben
				printer.endPage();
				printer.endJob();
			}

			printer.dispose(); // Druckerressourcen freigeben
		}
	}
}
