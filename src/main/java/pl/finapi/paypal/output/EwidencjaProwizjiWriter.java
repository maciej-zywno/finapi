package pl.finapi.paypal.output;

import java.io.OutputStream;

import pl.finapi.paypal.model.output.PaypalFeeReportModel;

public interface EwidencjaProwizjiWriter {

	void write(PaypalFeeReportModel reportModel, OutputStream outputStream);

}