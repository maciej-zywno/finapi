package pl.finapi.paypal.output.pdf.element;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.output.DowodWewnetrznyModel;
import pl.finapi.paypal.output.DowodWewnetrznyWriter;
import pl.finapi.paypal.util.AmountLiterally;
import pl.finapi.paypal.util.NumberFormatter;
import pl.finapi.paypal.util.NumberUtil;
import pl.finapi.paypal.util.PdfFactory;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

@Component
public class DowodWewnetrznyPdfWriter implements DowodWewnetrznyWriter {

	// private final float[] columnWidths = new float[] { 5, 30, 15, 15, 15, 15 };

	private final String amountLiterallyText = "Słownie: ";

	private final Font blackFont;
	private final Font tableFont;
	private final Font tableBoldFont;
	private final Font tableBoldFontSmall;

	private final NumberUtil numberUtil;
	private final PdfFactory pdfFactory;
	private final NumberFormatter numberFormatter;
	private final AmountLiterally amountLiterally;

	@Autowired
	public DowodWewnetrznyPdfWriter(NumberUtil numberUtil, PdfFactory pdfFactory, NumberFormatter numberFormatter, AmountLiterally amountLiterally) {
		this.numberUtil = numberUtil;
		this.pdfFactory = pdfFactory;
		this.numberFormatter = numberFormatter;
		this.amountLiterally = amountLiterally;
		this.blackFont = pdfFactory.createFont(BaseColor.BLACK, 12, Font.NORMAL);
		this.tableFont = pdfFactory.createFont(BaseColor.BLACK, 8, Font.NORMAL);
		this.tableBoldFont = pdfFactory.createFont(BaseColor.BLACK, 8, Font.BOLD);
		this.tableBoldFontSmall = pdfFactory.createFont(BaseColor.BLACK, 7, Font.BOLD);
	}

	public void write(DowodWewnetrznyModel invoiceModel, String outputFilePath) {
		try {
			OutputStream outputStream = new FileOutputStream(new File(outputFilePath));
			write(invoiceModel, outputStream);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void write(DowodWewnetrznyModel dowodWewnetrznyModel, OutputStream outputStream) {
		try {
			Document document = new Document(PageSize.A4);
			com.itextpdf.text.pdf.PdfWriter.getInstance(document, outputStream);
			document.setMargins(30, 30, 30, 30);
			document.open();
			addContent(dowodWewnetrznyModel, document);
			document.close();
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
	}

	public void addContent(DowodWewnetrznyModel dowodWewnetrznyModel, Document document) throws DocumentException {
		Paragraph paragraph = new Paragraph();
		PdfPTable table = createTable(dowodWewnetrznyModel);
		paragraph.add(table);
		paragraph.add(new Paragraph(amountLiterallyText + amountLiterally.amountLiterally(dowodWewnetrznyModel.getAmount()), blackFont));
		document.add(paragraph);
	}

	private PdfPTable createTable(DowodWewnetrznyModel dowodWewnetrznyModel) {
		PdfPTable table = pdfFactory.createEmptyTable(18);

		String amountAsString = amountLiterally.amountLiterally(dowodWewnetrznyModel.getAmount());

		float padding = 10.0f;

		// 1
		table.addCell(addPadding(pdfFactory.createCell("Pieczęć firmowa", tableBoldFont, 8, 4), padding));
		// 1
		table.addCell(addPadding(pdfFactory.createCell("MIEJSCOWOŚĆ", tableBoldFont, 4), padding));
		table.addCell(addPadding(pdfFactory.createCell("DZIEŃ", tableBoldFont, 2), padding));
		table.addCell(addPadding(pdfFactory.createCell("MIESIĄC", tableBoldFont, 2), padding));
		table.addCell(addPadding(pdfFactory.createCell("ROK", tableBoldFont, 2), padding));
		// 2
		table.addCell(addPadding(pdfFactory.createCell(dowodWewnetrznyModel.getCity().getCity(), tableFont, 4), padding));
		table.addCell(addPadding(pdfFactory.createCell(numberUtil.expandToTwoDigits(dowodWewnetrznyModel.getCreationDay().getDay()), tableFont, 2), padding));
		table.addCell(addPadding(pdfFactory.createCell(numberUtil.expandToTwoDigits(dowodWewnetrznyModel.getCreationDay().getMonth()), tableFont, 2), padding));
		table.addCell(addPadding(pdfFactory.createCell(dowodWewnetrznyModel.getCreationDay().getYear(), tableFont, 2), padding));
		// 3
		table.addCell(addPadding(pdfFactory.createCell("DOWÓD WEWNĘTRZNY Nr ", tableBoldFont, 10, 2), padding));
		// 4
		table.addCell(addPadding(pdfFactory.createCell("Lp.", tableBoldFontSmall, 1), padding));
		table.addCell(addPadding(pdfFactory.createCell("Nazwa towaru, opłaty lub tytuł i cel wydatku", tableBoldFontSmall, 9), padding));
		table.addCell(addPadding(pdfFactory.createCell("Ilość", tableBoldFontSmall, 2), padding));
		table.addCell(addPadding(pdfFactory.createCell("Jednostka miary", tableBoldFontSmall, 2), padding));
		table.addCell(addPadding(pdfFactory.createCell("Cena jednostkowa (PLN)", tableBoldFontSmall, 2), padding));
		table.addCell(addPadding(pdfFactory.createCell("Wartość (PLN)", tableBoldFontSmall, 2), padding));
		// 5
		table.addCell(addPadding(pdfFactory.createCell("1", tableFont, 1), padding));
		table.addCell(addPadding(
				pdfFactory.createCell("Różnice kursowe wynikające z rozchodu waluty " + dowodWewnetrznyModel.getCurrency() + " na koncie Paypal (adres email: "
						+ dowodWewnetrznyModel.getEmailAddress().getEmailAddress() + ")", tableFont, 9), padding));
		table.addCell(addPadding(pdfFactory.createEmptyCell(2), padding));
		table.addCell(addPadding(pdfFactory.createEmptyCell(2), padding));
		table.addCell(addPadding(pdfFactory.createCell(numberFormatter.formatTwoDecimal(dowodWewnetrznyModel.getAmount().getAmount()), tableFont, 2), padding));
		table.addCell(addPadding(pdfFactory.createCell(numberFormatter.formatTwoDecimal(dowodWewnetrznyModel.getAmount().getAmount()), tableBoldFont, 2),
				padding));
		// 6
		PdfPCell emptyCell1 = pdfFactory.createEmptyCell(16, 5);
		PdfPCell emptyCell2 = pdfFactory.createEmptyCell(2, 5);
		emptyCell1.setMinimumHeight(150);
		emptyCell2.setMinimumHeight(150);
		table.addCell(addPadding(emptyCell1, padding));
		table.addCell(addPadding(emptyCell2, padding));
		// 7
		table.addCell(addPadding(pdfFactory.createCell("słownie zł/gr", tableBoldFont, 3), padding));
		table.addCell(addPadding(pdfFactory.createCell(amountAsString, tableFont, 11), padding));
		table.addCell(addPadding(pdfFactory.createCell("RAZEM", tableBoldFont, 2), padding));
		table.addCell(addPadding(pdfFactory.createCell(numberFormatter.formatTwoDecimal(dowodWewnetrznyModel.getAmount().getAmount()), tableBoldFont, 2),
				padding));
		// 8
		table.addCell(addPadding(pdfFactory.createCell("Sporządził", tableBoldFont, 3), padding));
		table.addCell(addPadding(pdfFactory.createCell(dowodWewnetrznyModel.getAccountantInfo().getAccountantInfo(), tableFont, 6), padding));
		table.addCell(addPadding(pdfFactory.createEmptyCell(9), padding));
		// 9
		table.addCell(addPadding(pdfFactory.createEmptyCell(9, 3), padding));
		// 9
		table.addCell(addPadding(pdfFactory.createCell("Zatwierdził", tableBoldFont, 5), padding));
		table.addCell(addPadding(pdfFactory.createCell("Pozycja księgowa", tableBoldFont, 4), padding));
		// 10
		table.addCell(addPadding(pdfFactory.createEmptyCell(5), padding));
		PdfPCell cell = pdfFactory.createCell("Nr .......................", tableBoldFont, 4);
		table.addCell(addPadding(cell, padding));
		// 11
		table.addCell(addPadding(pdfFactory.createCell("Podpis właściciela", tableBoldFont, 5), padding));
		table.addCell(addPadding(pdfFactory.createCell("Kolumna nr ...............", tableBoldFont, 4), padding));

		return table;
	}

	private PdfPCell addPadding(PdfPCell cell, float padding) {
		cell.setPadding(padding);
		return cell;
	}

}
