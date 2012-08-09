package pl.finapi.paypal.output.pdf;

import java.io.OutputStream;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.model.Currency;
import pl.finapi.paypal.model.output.DocumentModels;
import pl.finapi.paypal.model.output.ExchangeRateDifferenceReportModels;
import pl.finapi.paypal.output.pdf.element.DowodWewnetrznyPdfWriter;
import pl.finapi.paypal.output.pdf.element.EwidencjaProwizjiPdfWriter;
import pl.finapi.paypal.output.pdf.element.EwidencjaRoznicPdfWriter;
import pl.finapi.paypal.output.pdf.element.PaypalFeeInvoicePdfWriter;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;

@Component
public class PaypalFeeReportAndInvoiceAndEwidencjaAndDowodPdfWriter {

	private final EwidencjaProwizjiPdfWriter reportPdfWriter;
	private final EwidencjaRoznicPdfWriter ewidencjaRoznicPdfWriter;
	private final PaypalFeeInvoicePdfWriter invoicePdfWriter;
	private final DowodWewnetrznyPdfWriter dowodWewnetrznyPdfWriter;

	@Autowired
	public PaypalFeeReportAndInvoiceAndEwidencjaAndDowodPdfWriter(EwidencjaProwizjiPdfWriter reportPdfWriter,
			EwidencjaRoznicPdfWriter ewidencjaRoznicPdfWriter, PaypalFeeInvoicePdfWriter invoicePdfWriter, DowodWewnetrznyPdfWriter dowodWewnetrznyPdfWriter) {
		this.reportPdfWriter = reportPdfWriter;
		this.ewidencjaRoznicPdfWriter = ewidencjaRoznicPdfWriter;
		this.invoicePdfWriter = invoicePdfWriter;
		this.dowodWewnetrznyPdfWriter = dowodWewnetrznyPdfWriter;
	}

	public void writePaypalFeeReportAndInvoiceAndDokumentWewnetrzny(OutputStream outputStream, DocumentModels documentModels, Document document)
			throws DocumentException {

		// 1 paypal fees - faktura wewnetrzna
		document.newPage();
		invoicePdfWriter.addContent(document, documentModels.getPaypalFeeInvoiceModel());

		// 2 paypal fees table
		document.newPage();
		reportPdfWriter.addContent(document, documentModels.getPaypalFeeReportModel());

		for (Entry<Currency, ExchangeRateDifferenceReportModels> entry : documentModels.getExchangeRateDifferenceReportModel().entrySet()) {

			// 3 roznice kursowe - dowod wewnetrzny x 2
			if (!entry.getValue().getExchangeRateDifferenceReportModel().getSumOfNegatives().isZero()) {
				document.newPage();
				dowodWewnetrznyPdfWriter.addContent(entry.getValue().getDowodWewnetrznyModelForNegative(), document);
			}
			if (!entry.getValue().getExchangeRateDifferenceReportModel().getSumOfPositives().isZero()) {
				document.newPage();
				dowodWewnetrznyPdfWriter.addContent(entry.getValue().getDowodWewnetrznyModelForPositive(), document);
			}

			// 4 roznice kursowe table
			document.newPage();
			ewidencjaRoznicPdfWriter.addContent(entry.getValue().getExchangeRateDifferenceReportModel(), document);
			
		}

	}

	public void writePaypalFeeReportAndInvoiceAndDokumentWewnetrzny(OutputStream outputStream, List<DocumentModels> documentModelsList) {
		try {
			Document document = new Document(PageSize.A4);
			com.itextpdf.text.pdf.PdfWriter.getInstance(document, outputStream);
			document.setMargins(30, 30, 30, 30);
			document.open();

			for (DocumentModels documentModels : documentModelsList) {
				writePaypalFeeReportAndInvoiceAndDokumentWewnetrzny(outputStream, documentModels, document);
			}

			document.close();
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
	}

}
