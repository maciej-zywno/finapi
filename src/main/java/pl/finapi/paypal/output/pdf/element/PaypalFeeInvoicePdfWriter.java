package pl.finapi.paypal.output.pdf.element;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.Amount;
import pl.finapi.paypal.model.BuyerInfo;
import pl.finapi.paypal.model.Day;
import pl.finapi.paypal.model.output.PaypalFeeInvoiceModel;
import pl.finapi.paypal.output.InvoiceWriter;
import pl.finapi.paypal.util.AmountLiterally;
import pl.finapi.paypal.util.NumberFormatter;
import pl.finapi.paypal.util.PdfFactory;
import pl.finapi.paypal.util.TimeUtil;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

@Component
public class PaypalFeeInvoicePdfWriter implements InvoiceWriter {

	private final String[] headerCells = new String[] { "L.p.", "Treść faktury", "Wartość netto", "Stawka VAT", "Kwota VAT", "Wartość brutto" };
	private final float[] columnWidths = new float[] { 5, 30, 15, 15, 15, 15 };

	private final String dateHeaderPrefix = "Data wystawienia: ";
	private final String dateHeaderSuffix = " r.";
	private final String title = "Faktura VAT wewnętrzna nr";
	private final String seller = "Sprzedawca:";
	private final String buyer = "Nabywca:";
	private final String sellerName = "Luksemburg";
	private final String sellerAddressLine1 = "PayPal (Europe) S.à r.l. & Cie, S.C.A";
	private final String sellerAddressLine2 = "22 – 24 Boulevard Royal";
	private final String sellerAddressLine3 = "L – 2449 Luksemburg";

	private final String descriptionPrefix = "Import usług na podstawie miesięcznego zestawienia transakcji za ";
	private final String amountLiterallyText = "Słownie: ";

	private final Font blackFont;
	private final Font tableFont;

	private final TimeUtil timeUtil;
	private final PdfFactory pdfFactory;
	private final NumberFormatter numberFormatter;
	private final AmountLiterally amountLiterally;

	@Autowired
	public PaypalFeeInvoicePdfWriter(TimeUtil timeUtil, PdfFactory pdfFactory, NumberFormatter numberFormatter, AmountLiterally amountLiterally) {
		this.timeUtil = timeUtil;
		this.pdfFactory = pdfFactory;
		this.numberFormatter = numberFormatter;
		this.amountLiterally = amountLiterally;
		this.blackFont = pdfFactory.createFont(BaseColor.BLACK, 12, Font.NORMAL);
		this.tableFont = pdfFactory.createFont(BaseColor.BLACK, 8, Font.NORMAL);
	}

	public void write(PaypalFeeInvoiceModel invoiceModel, String outputFilePath) {
		try {
			OutputStream outputStream = new FileOutputStream(new File(outputFilePath));
			write(invoiceModel, outputStream);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void write(PaypalFeeInvoiceModel invoiceModel, OutputStream outputStream) {
		try {
			Document document = new Document(PageSize.A4);
			com.itextpdf.text.pdf.PdfWriter.getInstance(document, outputStream);
			document.setMargins(30, 30, 30, 30);
			document.open();
			addContent(document, invoiceModel);
			document.close();
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
	}

	public void addContent(Document document, PaypalFeeInvoiceModel invoiceModel) throws DocumentException {
		document.add(createPreface(invoiceModel.getTransactionMonthYearAsString(), invoiceModel.getBuyerInfo(), invoiceModel.getCreationDate()));
		Paragraph paragraph = new Paragraph();
		PdfPTable table = createTable(invoiceModel.getAmount());
		paragraph.add(table);
		paragraph.add(new Paragraph(amountLiterallyText + amountLiterally.amountLiterally(invoiceModel.getAmount()), blackFont));
		document.add(paragraph);
	}

	private Paragraph createPreface(String transactionMonthYear, BuyerInfo buyerInfo, Day day) {
		Paragraph preface = new Paragraph();
		addEmptyLine(preface, 2);
		String dayFormatted = day == null ? "" : timeUtil.formatDotDelimited(day) + dateHeaderSuffix;
		preface.add(new Paragraph(leadingSpaces(dateHeaderPrefix, 110) + dayFormatted, blackFont));
		addEmptyLine(preface, 3);
		preface.add(new Paragraph(leadingSpaces(title, 50), blackFont));
		addEmptyLine(preface, 2);
		preface.add(new Paragraph(spacesDelimited(seller, buyer, 75), blackFont));
		preface.add(new Paragraph(spacesDelimited(sellerName, buyerInfo.getCompanyName(), 75), blackFont));
		preface.add(new Paragraph(spacesDelimited(sellerAddressLine1, buyerInfo.getAddressLine1(), 64), blackFont));
		preface.add(new Paragraph(spacesDelimited(sellerAddressLine2, buyerInfo.getAddressLine2(), 68), blackFont));
		preface.add(new Paragraph(spacesDelimited(sellerAddressLine3, "NIP: " + buyerInfo.getNIP(), 69), blackFont));
		addEmptyLine(preface, 2);
		preface.add(new Paragraph(descriptionPrefix + transactionMonthYear, blackFont));
		addEmptyLine(preface, 1);
		return preface;
	}

	private String leadingSpaces(String text, int spaceCount) {
		return spaces(spaceCount) + text;
	}

	private String spacesDelimited(String a, String b, int totalLength) {
		String first = a + spaces(totalLength - a.length());
		return first + b;
	}

	private String spaces(int count) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < count; i++) {
			buffer.append(' ');
		}
		return buffer.toString();
	}

	private PdfPTable createTable(Amount amount) {
		PdfPTable table = pdfFactory.createEmptyTable(columnWidths);
		addHeaderRow(table);

		String amountAsString = numberFormatter.formatTwoDecimal(amount.getAmount()) + " zł";

		// 1
		table.addCell(createTableCell("1"));
		table.addCell(createTableCell("Prowizje za korzystanie z serwisu Paypal"));
		table.addCell(createTableCell(amountAsString));
		table.addCell(createTableCell("0%"));
		table.addCell(createTableCell("0 zł"));
		table.addCell(createTableCell(amountAsString));
		// 2
		table.addCell(createTableCell(" "));
		table.addCell(createTableCell(" "));
		table.addCell(createTableCell(" "));
		table.addCell(createTableCell(" "));
		table.addCell(createTableCell(" "));
		table.addCell(createTableCell(" "));
		// 3
		table.addCell(createTableCell(""));
		table.addCell(createTableCell("Razem"));
		table.addCell(createTableCell(amountAsString));
		table.addCell(createTableCell("-"));
		table.addCell(createTableCell("0 zł"));
		table.addCell(createTableCell(amountAsString));
		// 4
		table.addCell(createTableCell(""));
		table.addCell(createTableCell("W tym"));
		table.addCell(createTableCell(amountAsString));
		table.addCell(createTableCell("0%"));
		table.addCell(createTableCell("0 zł"));
		table.addCell(createTableCell(amountAsString));

		return table;
	}

	private PdfPCell createTableCell(String text) {
		return pdfFactory.createCell(text, tableFont);
	}

	private void addHeaderRow(PdfPTable table) {
		for (String headerCell : headerCells) {
			PdfPCell c1 = pdfFactory.createCell(headerCell, tableFont);
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);
		}
		table.setHeaderRows(1);
	}

	private void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}
}
