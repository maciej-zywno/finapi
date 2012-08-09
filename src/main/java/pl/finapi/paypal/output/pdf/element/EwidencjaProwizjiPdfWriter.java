package pl.finapi.paypal.output.pdf.element;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.Amount;
import pl.finapi.paypal.model.Currency;
import pl.finapi.paypal.model.DateTimeRange;
import pl.finapi.paypal.model.TransactionLine;
import pl.finapi.paypal.model.output.PaypalFeeReportModel;
import pl.finapi.paypal.output.EwidencjaProwizjiWriter;
import pl.finapi.paypal.util.NumberFormatter;
import pl.finapi.paypal.util.NumberUtil;
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
public class EwidencjaProwizjiPdfWriter implements EwidencjaProwizjiWriter {

	private final String[] headerCells = new String[] { "L.p.", "Data operacji", "Typ operacji", "Wartość prowizji w walucie", "Waluta", "Kurs waluty",
			"Identyfikacja kursu przeliczeniowego", "Wartość prowizji (PLN)" };
	private final float[] columnWidth = new float[] { 5, 10, 27, 8, 7, 7, 30, 8 };

	private final Font blackFont;
	private final Font tableFont;

	private final DateFormat yearMonthDayDotDelimitedFormat;
	private final TimeUtil timeUtil;
	private final NumberUtil numberUtil;
	private final PdfFactory pdfFactory;
	private final NumberFormatter numberFormatter;

	@Autowired
	public EwidencjaProwizjiPdfWriter(@Value("#{yearMonthDayDotDelimitedFormat_WarsawTimeZone}") DateFormat yearMonthDayDotDelimitedFormat, TimeUtil timeUtil,
			NumberUtil numberUtil, PdfFactory pdfFactory, NumberFormatter numberFormatter) {
		this.yearMonthDayDotDelimitedFormat = yearMonthDayDotDelimitedFormat;
		this.timeUtil = timeUtil;
		this.numberUtil = numberUtil;
		this.pdfFactory = pdfFactory;
		this.numberFormatter = numberFormatter;
		this.blackFont = pdfFactory.createFont(BaseColor.BLACK, 12, Font.NORMAL);
		this.tableFont = pdfFactory.createFont(BaseColor.BLACK, 8, Font.NORMAL);
	}

	public void createPdf(PaypalFeeReportModel reportModel, File outputFile) {
		OutputStream outputStream;
		try {
			outputStream = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e1) {
			throw new RuntimeException(e1);
		}
		write(reportModel, outputStream);
	}

	@Override
	public void write(PaypalFeeReportModel reportModel, OutputStream outputStream) {
		try {
			Document document = new Document(PageSize.A4);
			com.itextpdf.text.pdf.PdfWriter.getInstance(document, outputStream);
			document.setMargins(30, 30, 30, 30);
			document.open();
			addContent(document, reportModel);
			document.close();
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
	}

	public void addContent(Document document, PaypalFeeReportModel reportModel) throws DocumentException {
		Paragraph preface = new Paragraph();
		preface.add(buildTitleParapraph(reportModel));
		addEmptyLine(preface, 1);
		document.add(preface);

		Paragraph paragraph = new Paragraph();
		PdfPTable table = createTable(reportModel.getTransactionLines(), reportModel.getTransactionFeeInPlnSum());
		paragraph.add(table);
		addEmptyLine(paragraph, 2);
		document.add(paragraph);
	}

	private Paragraph buildTitleParapraph(PaypalFeeReportModel reportModel) {
		return new Paragraph(buildTitle(reportModel.getTransactionDateRange()), blackFont);
	}

	private String buildTitle(DateTimeRange dateRange) {
		return "Ewidencja prowizji Paypal za okres " + timeUtil.dayRangeDotFormatted(dateRange);
	}

	private PdfPTable createTable(List<TransactionLine> transactionLines, Amount amount) {
		PdfPTable table = pdfFactory.createEmptyTable(columnWidth);
		addHeaderRow(table);
		addDataRows(transactionLines, table);
		addFooterRow(table, amount);
		return table;
	}

	private void addDataRows(List<TransactionLine> transactionLines, PdfPTable table) {
		for (TransactionLine transactionLine : removeZeroFeeLines(transactionLines)) {
			table.addCell(pdfFactory.createCell(transactionLine.getNumber()));
			table.addCell(createTableCell(yearMonthDayDotDelimitedFormat.format(transactionLine.getTransactionDateTime().toDate())));
			table.addCell(createTableCell(transactionLine.getTransactionType().getPolishName()));
			table.addCell(createTableCell(numberFormatter.formatTwoDecimal(numberUtil.negateAndFixZeroCase(transactionLine.getFeeInForeignCurrency())
					.getAmount())));
			table.addCell(createTableCell(transactionLine.getCurrency().name()));
			table.addCell(createTableCell(isPlnTransaction(transactionLine) ? "-" : numberFormatter.formatFourDecimal(transactionLine.getExchangeRate()
					.getExchangeRate().getAmount())));
			table.addCell(createTableCell(isPlnTransaction(transactionLine) ? "-" : "Tabela nr " + transactionLine.getTableName() + " z dnia "
					+ timeUtil.formatDotDelimited(transactionLine.getNbpExchangeRateDay())));
			String format = numberFormatter.formatTwoDecimal(numberUtil.negateAndFixZeroCase(transactionLine.getFeeInPln()).getAmount());
			table.addCell(createTableCell(format));
		}
	}

	private List<TransactionLine> removeZeroFeeLines(List<TransactionLine> transactionLines) {
		List<TransactionLine> filtered = new ArrayList<>();
		for (TransactionLine transactionLine : transactionLines) {
			if (!transactionLine.getFeeInPln().isZero()) {
				filtered.add(transactionLine);
			}
		}
		return filtered;
	}

	private boolean isPlnTransaction(TransactionLine transactionLine) {
		return transactionLine.getCurrency().equals(Currency.PLN);
	}

	private PdfPCell createTableCell(String text) {
		return pdfFactory.createCell(text, tableFont);
	}

	private void addHeaderRow(PdfPTable table) {
		for (String headerCell : getHeaderCells()) {
			PdfPCell c1 = createTableCell(headerCell);
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);
		}
		table.setHeaderRows(1);
	}

	private String[] getHeaderCells() {
		return headerCells;
	}

	private void addFooterRow(PdfPTable table, Amount amount) {
		table.addCell(pdfFactory.createEmptyCell());
		table.addCell(pdfFactory.createEmptyCell());
		table.addCell(pdfFactory.createEmptyCell());
		table.addCell(pdfFactory.createEmptyCell());
		table.addCell(pdfFactory.createEmptyCell());
		table.addCell(pdfFactory.createEmptyCell());
		table.addCell(pdfFactory.createCell("Suma:", tableFont));
		table.addCell(pdfFactory.createCell(numberFormatter.formatTwoDecimal((amount.getAmount())), tableFont));
	}

	private void addEmptyLine(Paragraph paragraph, int emptyLineCount) {
		for (int i = 0; i < emptyLineCount; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}
}
