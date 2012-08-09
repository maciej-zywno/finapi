package pl.finapi.paypal.output;

import java.io.OutputStream;

import pl.finapi.paypal.model.output.PaypalFeeInvoiceModel;

public interface InvoiceWriter {

	void write(PaypalFeeInvoiceModel invoiceModel, OutputStream outputStream);

}