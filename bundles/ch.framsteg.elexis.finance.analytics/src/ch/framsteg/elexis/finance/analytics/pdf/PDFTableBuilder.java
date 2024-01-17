package ch.framsteg.elexis.finance.analytics.pdf;

import java.util.List;

import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

public class PDFTableBuilder {

    private PDFTable table = new PDFTable();

    public PDFTableBuilder setHeight(float height) {
        table.setHeight(height);
        return this;
    }

    public PDFTableBuilder setNumberOfRows(Integer numberOfRows) {
        table.setNumberOfRows(numberOfRows);
        return this;
    }

    public PDFTableBuilder setRowHeight(float rowHeight) {
        table.setRowHeight(rowHeight);
        return this;
    }

    public PDFTableBuilder setContent(String[][] content) {
        table.setContent(content);
        return this;
    }

    public PDFTableBuilder setColumns(List<PDFColumn> columns) {
        table.setColumns(columns);
        return this;
    }

    public PDFTableBuilder setCellMargin(float cellMargin) {
        table.setCellMargin(cellMargin);
        return this;
    }

    public PDFTableBuilder setMargin(float margin) {
        table.setMargin(margin);
        return this;
    }

    public PDFTableBuilder setPageSize(PDRectangle pageSize) {
        table.setPageSize(pageSize);
        return this;
    }

    public PDFTableBuilder setLandscape(boolean landscape) {
        table.setLandscape(landscape);
        return this;
    }

    public PDFTableBuilder setTextFont(PDFont textFont) {
        table.setTextFont(textFont);
        return this;
    }

    public PDFTableBuilder setFontSize(float fontSize) {
        table.setFontSize(fontSize);
        return this;
    }

    public PDFTable build() {
        return table;
    }
}