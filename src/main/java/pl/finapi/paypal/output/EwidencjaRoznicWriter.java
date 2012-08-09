package pl.finapi.paypal.output;

import java.io.OutputStream;

import pl.finapi.paypal.model.output.ExchangeRateDifferenceReportModel;

public interface EwidencjaRoznicWriter {

	void write(ExchangeRateDifferenceReportModel reportModel, OutputStream outputStream);

}