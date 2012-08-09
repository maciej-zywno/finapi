package pl.finapi.paypal.output.pdf.element;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.Currency;
import pl.finapi.paypal.model.DateTimeRange;
import pl.finapi.paypal.model.ExchangeRate;
import pl.finapi.paypal.model.SposobObliczania;
import pl.finapi.paypal.model.StanWaluty;
import pl.finapi.paypal.model.output.ExchangeRateDifferenceReportLine;
import pl.finapi.paypal.model.output.ExchangeRateDifferenceReportModel;
import pl.finapi.paypal.model.output.TrescOperacji;
import pl.finapi.paypal.model.roznice.AmountAtRate;
import pl.finapi.paypal.output.EwidencjaRoznicWriter;
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
public class EwidencjaRoznicPdfWriter implements EwidencjaRoznicWriter {

	private final String[] headerCells = new String[] { "L.p.", "Data operacji", "Treść operacji", "Kurs waluty", "Identyfikator kursu",
			"Wpływ (${currency})", "Wpływ (PLN)", "Wypływ (${currency})", "Wypływ (PLN)", "Stan waluty",
			"Sposób obliczania różnic kursowych", "Wartość różnic kursowych (PLN)" };
	private final float[] columnWidth = new float[] { 5, 12, 12, 8, 19, 9, 9, 9, 9, 16, 30, 11 };

	private final Font blackFont;
	private final Font tableFont;

	private final DateFormat yearMonthDayDotDelimitedFormat;
	private final TimeUtil timeUtil;
	private final PdfFactory pdfFactory;
	private final NumberFormatter numberFormatter;

	@Autowired
	public EwidencjaRoznicPdfWriter(
			@Value("#{yearMonthDayDotDelimitedFormat_WarsawTimeZone}") DateFormat yearMonthDayDotDelimitedFormat_WarsawTimeZone,
			TimeUtil timeUtil, PdfFactory pdfFactory, NumberFormatter numberFormatter) {
		this.yearMonthDayDotDelimitedFormat = yearMonthDayDotDelimitedFormat_WarsawTimeZone;
		this.timeUtil = timeUtil;
		this.pdfFactory = pdfFactory;
		this.numberFormatter = numberFormatter;
		this.blackFont = pdfFactory.createFont(BaseColor.BLACK, 12, Font.NORMAL);
		this.tableFont = pdfFactory.createFont(BaseColor.BLACK, 8, Font.NORMAL);
	}

	@Override
	public void write(ExchangeRateDifferenceReportModel reportModel, OutputStream outputStream) {
		try {
			Document document = new Document(PageSize.A4);
			com.itextpdf.text.pdf.PdfWriter.getInstance(document, outputStream);
			document.setMargins(30, 30, 30, 30);
			document.open();
			addContent(reportModel, document);
			document.close();
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
	}

	public void addContent(ExchangeRateDifferenceReportModel reportModel, Document document) throws DocumentException {

		// preface
		Paragraph preface = new Paragraph();
		preface.add(buildTitleParagraph(reportModel.getTransactionDateRange()));
		addEmptyLine(preface, 1);
		document.add(preface);

		// build the table
		PdfPTable table = pdfFactory.createEmptyTable(columnWidth);
		addHeaderRow(table, reportModel.getCurrency());
		addDataRows(reportModel.getTransactionLines(), table);
		addFooterRows(table, reportModel.getSumOfNegatives().getAmount(), reportModel.getSumOfPositives().getAmount());

		// add the table
		Paragraph paragraph = new Paragraph();
		paragraph.add(table);

		addEmptyLine(paragraph, 2);
		document.add(paragraph);

	}

	private Paragraph buildTitleParagraph(DateTimeRange transactionDateRange) {
		return new Paragraph(buildTitle(transactionDateRange), blackFont);
	}

	private String buildTitle(DateTimeRange transactionDateRange) {
		return "Ewidencja różnic kursowych za okres " + timeUtil.dayRangeDotFormatted(transactionDateRange);
	}

	private void addDataRows(List<ExchangeRateDifferenceReportLine> list, PdfPTable table) {

		int lp = 0;
		for (ExchangeRateDifferenceReportLine line : list) {
			lp++;
			table.addCell(pdfFactory.createCell(lp, tableFont));
			table.addCell(pdfFactory.createCell(yearMonthDayDotDelimitedFormat.format(line.getTransactionDateTime().toDate()), tableFont));
			table.addCell(pdfFactory.createCell(asText(line.getTrescOperacji()), tableFont));
			table.addCell(pdfFactory.createCell(asKursWaluty(line), tableFont));
			table.addCell(pdfFactory.createCell(asIdentyfikatorKursu(line), tableFont));

			List<PdfPCell> wplywWyplywCells = buildWplywWyplywCells(table, line);
			table.addCell(wplywWyplywCells.get(0));
			table.addCell(wplywWyplywCells.get(1));
			table.addCell(wplywWyplywCells.get(2));
			table.addCell(wplywWyplywCells.get(3));

			table.addCell(pdfFactory.createCell(textLines(line.getStanWaluty()), tableFont));
			table.addCell(pdfFactory.createCell(oneLine(line.getSposobObliczania()), tableFont));
			table.addCell(pdfFactory.createCell(
					line.getTrescOperacji().isSaldoPoczatkoweLubKoncowe() || line.getSposobObliczania() == null ? "" : numberFormatter
							.formatTwoDecimal((line.getSposobObliczania().calculateResult().getAmount())), tableFont));
		}
	}

	private List<PdfPCell> buildWplywWyplywCells(PdfPTable table, ExchangeRateDifferenceReportLine line) {
		if (line.getTrescOperacji().isSaldoPoczatkoweLubKoncowe()) {
			return pdfFactory.createEmptyCells(4);
		} else {
			if (line.getAmountFlow().isPositive()) {
				List<PdfPCell> cells = new ArrayList<>();
				cells.add(pdfFactory.createCell(numberFormatter.formatTwoDecimal(line.getAmountFlow().getAmount()), tableFont));
				cells.add(pdfFactory.createCell(numberFormatter.formatTwoDecimal(line.getAmountFlow().getAmount()), tableFont));
				cells.add(pdfFactory.createEmptyCell());
				cells.add(pdfFactory.createEmptyCell());
				return cells;
			} else {
				List<PdfPCell> cells = new ArrayList<>();
				cells.add(pdfFactory.createEmptyCell());
				cells.add(pdfFactory.createEmptyCell());
				cells.add(pdfFactory.createCell(numberFormatter.formatTwoDecimal(line.getAmountFlow().getAmount().negate()), tableFont));
				cells.add(pdfFactory.createCell(
						numberFormatter.formatTwoDecimal(line.getAmountFlow().times(line.getExchangeRate().getExchangeRate()).negate()
								.getAmount()), tableFont));
				return cells;
			}
		}
	}

	private String asIdentyfikatorKursu(ExchangeRateDifferenceReportLine line) {
		return line.getTrescOperacji().isSaldoPoczatkoweLubKoncowe() ? "" : asIdentyfikatorKursu(line.getExchangeRate());
	}

	private String asKursWaluty(ExchangeRateDifferenceReportLine line) {
		return line.getTrescOperacji().isSaldoPoczatkoweLubKoncowe() ? "" : numberFormatter.formatFourDecimal(line.getExchangeRate()
				.getExchangeRate().getAmount());
	}

	private String asIdentyfikatorKursu(ExchangeRate exchangeRate) {
		switch (exchangeRate.getIdentyfikatorKursu().getIdentyfikatorKursuSource()) {
		case NBP:
			// Tabela nr 12/A/NBP/2012 z dnia 2012-01-18
			return "Tabela nr " + exchangeRate.getIdentyfikatorKursu().getIdentyfikatorKursu() + " z dnia "
					+ timeUtil.formatDashDelimited(exchangeRate.getIdentyfikatorKursu().getDay());
		case PAYPAL:
			return "Kurs przewalutowania Paypal";
		default:
			throw new RuntimeException("unsupported tresc IdentyfikatorKursuSource "
					+ exchangeRate.getIdentyfikatorKursu().getIdentyfikatorKursuSource());
		}
	}

	private String asText(TrescOperacji trescOperacji) {
		switch (trescOperacji) {
		case KUPNO_WALUTY:
			return "Kupno waluty";
		case SPRZEDAZ_WALUTY:
			return "Sprzedaż waluty";
		case SALDO_KONCOWE:
			return "Saldo końcowe";
		case SALDO_POCZATKOWE:
			return "Saldo początkowe";
		case WPLYW_SRODKOW:
			return "Wpływ środków";
		case WYPLYW_SRODKOW:
			return "Wypływ środków";
		default:
			throw new RuntimeException("unsupported tresc operacji " + trescOperacji);
		}
	}

	private String oneLine(SposobObliczania sposobObliczania) {
		if (sposobObliczania == null) {
			return "";
		}
		StringBuffer buffer = new StringBuffer();

		AmountAtRate transaction = sposobObliczania.getAmountAtRate();
		buffer.append(numberFormatter.formatTwoDecimal(transaction.getAmount().getAmount()));
		buffer.append(" x ");
		buffer.append(numberFormatter.formatFourDecimal(transaction.getExchangeRate().getAmount()));

		buffer.append(" - (");

		for (AmountAtRate amountAtRate : sposobObliczania.getCalculations()) {
			buffer.append(numberFormatter.formatTwoDecimal(amountAtRate.getAmount().getAmount()));
			buffer.append(" x ");
			buffer.append(numberFormatter.formatFourDecimal(amountAtRate.getExchangeRate().getAmount()));
			buffer.append(" + ");
		}
		if (!sposobObliczania.getCalculations().isEmpty()) {
			buffer.delete(buffer.length() - 3, buffer.length());
		}
		buffer.append(")");

		return buffer.toString();
	}

	private String textLines(StanWaluty stanWaluty) {
		StringBuffer buffer = new StringBuffer();
		for (AmountAtRate amountAtRate : stanWaluty.getStanWalutyList()) {
			buffer.append(numberFormatter.formatTwoDecimal(amountAtRate.getAmount().getAmount()));
			buffer.append(" x ");
			buffer.append(numberFormatter.formatFourDecimal(amountAtRate.getExchangeRate().getAmount()));
			buffer.append("\n\r");
		}
		return buffer.toString();
	}

	private void addHeaderRow(PdfPTable table, Currency currency) {
		for (String headerCell : getHeaderCells(currency)) {
			PdfPCell cell = pdfFactory.createCell(headerCell, tableFont);
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		}
		table.setHeaderRows(1);
	}

	private String[] getHeaderCells(Currency currency) {
		String[] headerCellsProcessed = new String[headerCells.length];
		for (int i = 0; i < headerCells.length; i++) {
			headerCellsProcessed[i] = headerCells[i].replace("${currency}", currency.name());
		}
		return headerCellsProcessed;
	}

	private void addFooterRows(PdfPTable table, BigDecimal sumOfNegatives, BigDecimal sumOfPositives) {
		pdfFactory.addEmptyCells(table, 10);
		table.addCell(pdfFactory.createCell("Suma ujemnych różnic kursowych:", tableFont));
		table.addCell(pdfFactory.createCell(numberFormatter.formatTwoDecimal(sumOfNegatives), tableFont));
		pdfFactory.addEmptyCells(table, 10);
		table.addCell(pdfFactory.createCell("Suma dodatnich różnic kursowych:", tableFont));
		table.addCell(pdfFactory.createCell(numberFormatter.formatTwoDecimal(sumOfPositives), tableFont));
	}

	private void addEmptyLine(Paragraph paragraph, int emptyLineCount) {
		for (int i = 0; i < emptyLineCount; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}
}
