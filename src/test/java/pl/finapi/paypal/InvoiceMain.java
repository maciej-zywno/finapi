package pl.finapi.paypal;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import pl.finapi.paypal.model.Amount;
import pl.finapi.paypal.model.BuyerInfo;
import pl.finapi.paypal.model.Day;
import pl.finapi.paypal.model.output.PaypalFeeInvoiceModel;
import pl.finapi.paypal.output.pdf.element.PaypalFeeInvoicePdfWriter;
import pl.finapi.paypal.util.NumberUtil;

public class InvoiceMain {

	public static void main(String[] args) {

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/main.xml");

		PaypalFeeInvoicePdfWriter invoicePdfWriter = ctx.getBean(PaypalFeeInvoicePdfWriter.class);

		Amount amount = new NumberUtil().asAmount(12345.67);
		String outputFilePath = "e:/dev/PaypalFeeInvoice.pdf";
		BuyerInfo buyerInfo = new BuyerInfo("Sedasystem Maciej Żywno", "ul. Łukowa 12R m.2", "93-410 Łódź", "743-433-12-31");
		Day creationDay = new Day(2012, 2, 12);
		PaypalFeeInvoiceModel invoiceModel = new PaypalFeeInvoiceModel(amount, "styczeń 2012", buyerInfo, creationDay);
		invoicePdfWriter.write(invoiceModel, outputFilePath);
	}
}
