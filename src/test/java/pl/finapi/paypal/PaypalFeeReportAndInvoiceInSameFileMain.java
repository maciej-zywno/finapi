package pl.finapi.paypal;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import pl.finapi.paypal.model.AccountantInfo;
import pl.finapi.paypal.model.BuyerInfo;
import pl.finapi.paypal.model.City;
import pl.finapi.paypal.model.Currency;
import pl.finapi.paypal.model.EmailAddress;
import pl.finapi.paypal.model.SaldoAndStanWaluty;
import pl.finapi.paypal.model.output.DocumentModels;
import pl.finapi.paypal.output.pdf.PaypalFeeReportAndInvoiceAndEwidencjaAndDowodPdfWriter;
import pl.finapi.paypal.source.report.PaypalReport;
import pl.finapi.paypal.util.NumberUtil;

public class PaypalFeeReportAndInvoiceInSameFileMain {

	public static void main(String[] args) throws IOException {
		ToStringBuilder.setDefaultStyle(ToStringStyle.SHORT_PREFIX_STYLE);
		try {
			String outputFilePath = "/home/maciek/dev/finapi/output/foo.pdf";

			FileOutputStream outputStream = new FileOutputStream(outputFilePath);

			ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/main.xml");

			ReportFacade reportFacade = ctx.getBean(ReportFacade.class);
			NumberUtil numberUtil = ctx.getBean(NumberUtil.class);
			PaypalFeeReportAndInvoiceAndEwidencjaAndDowodPdfWriter writer = ctx.getBean(PaypalFeeReportAndInvoiceAndEwidencjaAndDowodPdfWriter.class);

			// File file = new File("src/test/resources/paypal/" + "Pobierz-2012.01.csv");
			// File file = new File("src/test/resources/paypal/" + "wrzesien-marzec2012-PL.csv");
			// File file = new File("src/test/resources/paypal/" + "styczen-marzec-onlydefaultcolumns.csv");
			// File file = new File("src/test/resources/paypal/" + "grudzien-marzec-szczegoly-koszyka-onlydefaultcolumns.csv");
			// File file = new File("src/test/resources/paypal/" + "grudzien-marzec-szczegoly-koszyka-allcolumns.csv");
			// File file = new File("src/test/resources/paypal/" + "hanatopshop-onlydefaultfields-PL.csv");
			// File file = new File("src/test/resources/paypal/" + "hanatopshop-onlydefaultfields-grudzien-luty-EN.csv");
			// File file = new File("C:/Documents and Settings/maciek/Desktop/Pobierz.csv");

			// File file = new File("src/test/resources/paypal/" + "infant-luty-PL.csv");
			File file = new File("/home/maciek/dev/finapi/Pobierz4.csv");

			// List<String> reportLines = FileUtils.readLines(file, "UTF-8");
			List<String> reportLines = FileUtils.readLines(file, "windows-1250");
			PaypalReport report = new PaypalReport(reportLines.get(0), reportLines.subList(1, reportLines.size()));

			// 3 exchange rate difference model
			City city = new City("");
			EmailAddress emailAddress = new EmailAddress("");
			AccountantInfo accountantInfo = new AccountantInfo("");

			Map<Currency, SaldoAndStanWaluty> initialSaldoAndStanWaluty = SampleData.emptySaldoAndStanWalutyForEachCurrency(numberUtil);
			BuyerInfo buyerInfo = BuyerInfo.EMPTY_BUYER;

			List<DocumentModels> models = reportFacade.createModels(report, /* initialSaldoAndStanWaluty, */emailAddress, city, accountantInfo, buyerInfo);

			writer.writePaypalFeeReportAndInvoiceAndDokumentWewnetrzny(outputStream, models);

			flush(outputStream);

			Desktop.getDesktop().open(new File(outputFilePath));
		} finally {
			// System.exit(0);
		}
	}

	private static BuyerInfo sampleBuyerInfo() {
		return new BuyerInfo("P.H.U. Biedronka", "address line 1", "address line 2", "1234567890");
	}

	private static void flush(OutputStream outputStream) {
		try {
			outputStream.flush();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
