package pl.finapi.paypal.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

@Component
public class PdfFactory {

	public PdfPCell createCell(String text, Font font) {
		int defaultColspan = 1;
		return createCell(text, font, defaultColspan);
	}

	public PdfPCell createCell(int text, Font font) {
		int defaultColspan = 1;
		return createCell(text, font, defaultColspan);
	}

	public PdfPCell createCell(String text, Font font, int colspan) {
		int defaultRowspan = 1;
		return createCell(text, font, colspan, defaultRowspan);
	}

	public PdfPCell createCell(int text, Font font, int colspan) {
		int defaultRowspan = 1;
		return createCell(text, font, colspan, defaultRowspan);
	}

	public PdfPCell createCell(int text, Font font, int colspan, int rowspan) {
		return createCell(Integer.toString(text), font, colspan, rowspan);
	}

	public PdfPCell createCell(String text, Font font, int colspan, int rowspan) {
		PdfPCell cell = new PdfPCell(new Phrase(text, font));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		cell.setColspan(colspan);
		cell.setRowspan(rowspan);
		return cell;
	}

	public PdfPCell createCell(int value) {
		return createCell(Integer.toString(value));
	}

	public PdfPCell createCell(String text) {
		PdfPCell cell = new PdfPCell(new Phrase(text));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		return cell;
	}

	public PdfPCell createEmptyCell() {
		return createCell("");
	}

	public PdfPCell createEmptyCell(int colspan) {
		PdfPCell cell = createEmptyCell();
		cell.setColspan(colspan);
		return cell;
	}

	public PdfPCell createEmptyCell(int colspan, int rowspan) {
		PdfPCell cell = createEmptyCell();
		cell.setColspan(colspan);
		cell.setRowspan(rowspan);
		return cell;
	}

	public Font createFont(BaseColor color, int fontSize, int fontStyle) {
		try {
			BaseFont baseFont = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1250, BaseFont.EMBEDDED);
			Font font = new Font(baseFont, fontSize, fontStyle);
			font.setColor(color);
			return font;
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public List<PdfPCell> createEmptyCells(int cellCount) {
		List<PdfPCell> cells = new ArrayList<>();
		for (int i = 0; i < cellCount; i++) {
			cells.add(createEmptyCell());
		}
		return cells;
	}

	public PdfPTable createEmptyTable(float[] columnWidths) {
		try {
			PdfPTable table = new PdfPTable(columnWidths.length);
			table.setWidthPercentage(100);
			table.setTotalWidth(columnWidths);
			return table;
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
	}

	public PdfPTable createEmptyTable(int columnCount) {
		PdfPTable table = new PdfPTable(columnCount);
		table.setWidthPercentage(100);
		return table;
	}

	public void addEmptyCell(PdfPTable table) {
		table.addCell(createEmptyCell());
	}

	public void addEmptyCells(PdfPTable table, int cellCount) {
		for (int i = 0; i < cellCount; i++) {
			addEmptyCell(table);
		}
	}

}
