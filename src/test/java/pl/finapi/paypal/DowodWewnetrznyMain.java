package pl.finapi.paypal;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import pl.finapi.paypal.model.AccountantInfo;
import pl.finapi.paypal.model.Amount;
import pl.finapi.paypal.model.City;
import pl.finapi.paypal.model.Currency;
import pl.finapi.paypal.model.Day;
import pl.finapi.paypal.model.EmailAddress;
import pl.finapi.paypal.model.output.DowodWewnetrznyModel;
import pl.finapi.paypal.output.DowodWewnetrznyWriter;
import pl.finapi.paypal.util.NumberUtil;

public class DowodWewnetrznyMain {

	public static void main(String[] args) throws IOException {

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("spring/main.xml");
		NumberUtil numberUtil = ctx.getBean(NumberUtil.class);
		DowodWewnetrznyWriter writer = ctx.getBean(DowodWewnetrznyWriter.class);

		String outputFilePathForNegativeRoznice = "E:/dev/DowodWewnetrzny-rozniceKursowe-ujemne.pdf";
		String outputFilePathForPositiveRoznice = "E:/dev/DowodWewnetrzny-rozniceKursowe-dodatnie.pdf";
		Day creationDay = new Day(2012, 31, 1);
		Amount amountNegative = numberUtil.asAmount(-55.09);
		Amount amountPositive = numberUtil.asAmount(37.84);
		City city = new City("Łódź");
		EmailAddress emailAddress = new EmailAddress("jan.kowalski@gmail.com");
		AccountantInfo buyerName = new AccountantInfo("Jan Kowalski");
		DowodWewnetrznyModel dowodWewnetrznyModel1 = new DowodWewnetrznyModel(Currency.EUR, creationDay, amountNegative, city, emailAddress, buyerName);
		DowodWewnetrznyModel dowodWewnetrznyModel2 = new DowodWewnetrznyModel(Currency.EUR, creationDay, amountPositive, city, emailAddress, buyerName);
		writer.write(dowodWewnetrznyModel1, new FileOutputStream(new File(outputFilePathForNegativeRoznice)));
		writer.write(dowodWewnetrznyModel2, new FileOutputStream(new File(outputFilePathForPositiveRoznice)));

		Desktop.getDesktop().open(new File(outputFilePathForNegativeRoznice));
		System.exit(0);
	}

}
