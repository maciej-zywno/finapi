package pl.finapi.paypal.web.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import pl.finapi.paypal.ReportFacade;
import pl.finapi.paypal.email.EmailService;
import pl.finapi.paypal.model.AccountantInfo;
import pl.finapi.paypal.model.BuyerInfo;
import pl.finapi.paypal.model.City;
import pl.finapi.paypal.model.DateTimeRange;
import pl.finapi.paypal.model.EmailAddress;
import pl.finapi.paypal.model.PaypalReportColumn;
import pl.finapi.paypal.model.output.DocumentModels;
import pl.finapi.paypal.output.pdf.PaypalFeeReportAndInvoiceAndEwidencjaAndDowodPdfWriter;
import pl.finapi.paypal.source.report.PaypalReport;
import pl.finapi.paypal.util.TimeUtil;
import pl.finapi.paypal.web.model.UploadItem;

@Controller
public class UploadController {

	private final PaypalFeeReportAndInvoiceAndEwidencjaAndDowodPdfWriter paypalFeeWriter;
	private final ReportFacade reportFacade;
	private final TimeUtil timeUtil;
	private final EmailService emailService;

	@Autowired
	public UploadController(PaypalFeeReportAndInvoiceAndEwidencjaAndDowodPdfWriter paypalFeeWriter, ReportFacade reportFacade, TimeUtil timeUtil,
			EmailService emailService) {
		this.paypalFeeWriter = paypalFeeWriter;
		this.reportFacade = reportFacade;
		this.timeUtil = timeUtil;
		this.emailService = emailService;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getUploadForm(Model model) {
		model.addAttribute(new UploadItem());
		return "uploadForm";
	}

	private final String[] charsetNames = new String[] { "windows-1250", "windows-1252", "UTF-8" };

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String processCreateReportsRequest(UploadItem uploadItem, BindingResult result, HttpServletResponse response) throws IOException {

		byte[] fileAsBytes = null;
		try {
			fileAsBytes = asBytes(uploadItem);
			if (fileAsBytes == null || fileAsBytes.length == 0) {
				return "blad";
			}
		} catch (RuntimeException e1) {
			return "blad";
		}

		boolean success = false;
		try {
			success = writeReport(response, fileAsBytes, charsetNames, uploadItem);
		} catch (RuntimeException e) {
			// if anything happens
			String originalFilename = uploadItem.getFileData().getOriginalFilename();
			emailService.enqueueForSending(fileAsBytes, originalFilename, e);
			throw e;
		}
		if (success) {
			String originalFilename = uploadItem.getFileData().getOriginalFilename();
			emailService.enqueueForSendingSuccess(fileAsBytes, originalFilename);

			return "uploadForm";
		}
		// success is false in case when
		// 1) there is no exception and
		// 2) there is no charset that can handle the file
		else {
			emailService.enqueueForSending(fileAsBytes, uploadItem.getFileData().getOriginalFilename());
			throw new RuntimeException("no charset could find column in header '" + PaypalReportColumn.NAME_AND_SURNAME.getPolishName() + "'");
		}

	}

	private byte[] asBytes(UploadItem uploadItem) throws IOException {
		return IOUtils.toByteArray(uploadItem.getFileData().getInputStream());
	}

	private boolean writeReport(HttpServletResponse response, byte[] fileAsBytes, String[] charsetNames, UploadItem uploadItem) throws IOException {

		boolean addCompanyName = uploadItem.isAddCompanyName();
		EmailAddress emailAddress = addCompanyName ? new EmailAddress(uploadItem.getEmail()) : EmailAddress.NO_EMAIL_ADDRESS;
		City city = addCompanyName ? new City(uploadItem.getCity()) : City.NO_CITY;
		AccountantInfo wystawilName = addCompanyName ? new AccountantInfo(uploadItem.getWystawil()) : AccountantInfo.EMPTY;
		BuyerInfo buyerInfo = addCompanyName ? asBuyerInfo(uploadItem) : BuyerInfo.EMPTY_BUYER;

		for (String charsetName : charsetNames) {

			Reader reader = new InputStreamReader(new ByteArrayInputStream(fileAsBytes), Charset.forName(charsetName));
			List<String> reportLines = IOUtils.readLines(reader);
			assertPaypalReportHasData(reportLines);
			PaypalReport report = new PaypalReport(reportLines.get(0), reportLines.subList(1, reportLines.size()));

			if (isReadable(report.getHeaderLine())) {

				List<DocumentModels> reportModels = reportFacade.createModels(report, /* SampleData.sampleSaldoAndStanWaluty(numberUtil), */
						emailAddress, city, wystawilName, buyerInfo);

				ByteArrayOutputStream tempStream = new ByteArrayOutputStream();
				paypalFeeWriter.writePaypalFeeReportAndInvoiceAndDokumentWewnetrzny(tempStream, reportModels);

				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition", "attachment;filename=" + createFileName(reportModels));

				ServletOutputStream outputStream = response.getOutputStream();
				IOUtils.copy(new ByteArrayInputStream(tempStream.toByteArray()), outputStream);
				outputStream.flush();

				return true;
			}
		}
		return false;
	}

	private BuyerInfo asBuyerInfo(UploadItem uploadItem) {
		String companyName = uploadItem.getCompanyName();
		String addressLine1 = uploadItem.getAddress();
		String addressLine2 = uploadItem.getZipcode() + " " + uploadItem.getCity();
		String nip = uploadItem.getNip();
		return new BuyerInfo(companyName, addressLine1, addressLine2, nip);
	}

	private void assertPaypalReportHasData(List<String> reportLines) {
		if (reportLines.size() <= 1) {
			throw new RuntimeException("no data in paypal report file");
		}
	}

	private boolean isReadable(String headerLine) {
		boolean contains = headerLine.contains(PaypalReportColumn.NAME_AND_SURNAME.getPolishName());
		return contains;
	}

	private String createFileName(List<DocumentModels> reportModels) {
		String dayRangeDotDelimited = timeUtil.formatWarsawDayRangeDotDelimited(findTransactionDateRange(reportModels));
		return "DokumentyKsiegowe_PayPal_" + dayRangeDotDelimited + ".pdf";
	}

	private DateTimeRange findTransactionDateRange(List<DocumentModels> reportModels) {
		if (reportModels.size() > 1) {
			DateTime startDateTime = reportModels.get(0).getPaypalFeeReportModel().getTransactionDateRange().getStartDateTime();
			DateTime endDateTime = reportModels.get(reportModels.size() - 1).getPaypalFeeReportModel().getTransactionDateRange().getEndDateTime();
			DateTimeRange transactionDateRange = new DateTimeRange(startDateTime, endDateTime);
			return transactionDateRange;
		} else {
			DateTimeRange transactionDateRange = reportModels.get(0).getPaypalFeeReportModel().getTransactionDateRange();
			return transactionDateRange;
		}
	}

}