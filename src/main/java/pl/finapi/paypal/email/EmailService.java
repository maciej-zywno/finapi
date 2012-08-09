package pl.finapi.paypal.email;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.activation.DataSource;
import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import pl.finapi.paypal.FallbackService;

@Component
public class EmailService {

	private final BlockingQueue<EmailMessage> queue = new LinkedBlockingQueue<>();

	private final Logger log = Logger.getLogger(this.getClass());

	private final JavaMailSender mailSender;
	private final FallbackService fallbackService;

	@Autowired
	public EmailService(JavaMailSender mailSender, FallbackService fallbackService) {
		this.mailSender = mailSender;
		this.fallbackService = fallbackService;
	}

	public void enqueueForSending(byte[] fileAsBytes, String originalFilename, RuntimeException e) {
		log.info("enqueing email");
		queue.add(new EmailFailureMessage(fileAsBytes, originalFilename, e));
	}

	public void enqueueForSendingSuccess(byte[] csvFileAsBytes, String originalFilename) {
		log.info("enqueing email");
		queue.add(new EmailSuccessMessage(csvFileAsBytes, originalFilename));
	}

	public void enqueueForSending(byte[] csvFileAsBytes, String originalFilename) {
		enqueueForSending(csvFileAsBytes, originalFilename, null);
	}

	private void send(EmailMessage emailMessage) {
		// if (!isTest(emailMessage)) {
		log.info("sending email");
		try {
			MimeMessage mimeMessage = createMimeMessage(emailMessage);
			mailSender.send(mimeMessage);
			log.info("email sent");
		} catch (MailAuthenticationException e) {
			log.error(e);
			fallbackService.store(emailMessage);
		} catch (MailSendException e) {
			log.error(e);
			fallbackService.store(emailMessage);
		} catch (MailException e) {
			log.error(e);
			fallbackService.store(emailMessage);
		}
		// }
	}

	private boolean isTest(EmailMessage emailMessage) {
		byte[] csvFileAsBytes = emailMessage.getCsvFileAsBytes();
		Reader reader = new InputStreamReader(new ByteArrayInputStream(csvFileAsBytes), Charset.forName("UTF-8")/*
																												 * we just need email
																												 * address so utf-8 is
																												 * enough
																												 */);
		try {
			List<String> reportLines = IOUtils.readLines(reader);
			for (String reportLine : reportLines) {
				if (reportLine.contains("infantfashion@gmail.com") || reportLine.contains("ostpreis@gmail.com")) {
					return true;
				}
			}
			return false;
		} catch (RuntimeException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

	}

	@PostConstruct
	public void startEmailSendingThread() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						send(queue.take());
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}
		};
		new Thread(runnable).start();
	}

	private MimeMessage createMimeMessage(EmailMessage emailMessage) {
		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
			DataSource dataSource = new ByteArrayDataSource(emailMessage.getCsvFileAsBytes(), "text/csv");
			helper.addAttachment(emailMessage.getOriginalFilename(), dataSource);
			helper.setFrom("FinapiWebapp");
			helper.setTo("errors@finapi.pl");
			helper.setSubject(emailMessage.getSubject());
			helper.setText(emailMessage.getMessage(), false);
			return mimeMessage;
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

}
