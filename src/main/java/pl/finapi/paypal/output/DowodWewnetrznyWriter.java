package pl.finapi.paypal.output;

import java.io.OutputStream;

import pl.finapi.paypal.model.output.DowodWewnetrznyModel;

public interface DowodWewnetrznyWriter {

	void write(DowodWewnetrznyModel dowodWewnetrznyModel, OutputStream outputStream);

}