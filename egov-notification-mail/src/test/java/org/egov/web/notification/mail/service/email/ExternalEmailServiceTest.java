package org.egov.web.notification.mail.service.email;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.egov.web.notification.mail.model.Attachment;
import org.egov.web.notification.mail.model.Email;
import org.egov.web.notification.mail.service.ExternalEmailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

@RunWith(MockitoJUnitRunner.class)
public class ExternalEmailServiceTest {

	// @Mock
	private JavaMailSenderImpl javaMailSender;

	private ExternalEmailService externalEmailService;

	@Before
	public void before() {
		javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost("smtp.sendgrid.com");
		javaMailSender.setPort(587);
		javaMailSender.setProtocol("smtps");
		javaMailSender.setUsername("smartcity.chd@nic.in");
		javaMailSender.setPassword("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");
		final Properties mailProperties = new Properties();
		mailProperties.setProperty("mail.smtps.auth", "true");
		mailProperties.setProperty("mail.smtps.starttls.enable", "true");
		mailProperties.setProperty("mail.smtps.debug", "false");
		javaMailSender.setJavaMailProperties(mailProperties);

		externalEmailService = new ExternalEmailService(javaMailSender);
	}

	@Test
	public void test_email_service_uses_mail_sender_to_send_text_email() {
		final String EMAIL_ADDRESS = "tapojit.bhattacharya@pwc.com";
		//final String EMAIL_ADDRESS = "jatinder.s.singh@pwc.com";
		final String SUBJECT = "Test Subject";
		final String BODY = "Test body of the email";
		final Email email = Email.builder().toAddress(EMAIL_ADDRESS).body(BODY).subject(SUBJECT).html(false).build();
		SimpleMailMessage expectedMailMessage = new SimpleMailMessage();
		expectedMailMessage.setTo(EMAIL_ADDRESS);
		expectedMailMessage.setSubject(SUBJECT);
		expectedMailMessage.setText(BODY);

		externalEmailService.sendEmail(email);

		// verify(javaMailSender).send(expectedMailMessage);
	}

	@Test
	public void test_email_service_uses_mail_sender_to_send_html_email() throws MessagingException, IOException {
		
		//final String EMAIL_ADDRESS = "jatinder.s.singh@pwc.com";
		final String EMAIL_ADDRESS = "tapojit.bhattacharya@pwc.com";
		final String SUBJECT = "Email with multiple attachments";
		final String BODY = "Test body of the email with multiple attachment";
		
		
		final File file1 = new File("C:\\Users\\TapojitB\\Desktop\\Email\\ERP.jpeg");
		final String path1 = file1.getPath();
		final String fileName1 = file1.getName();
		final String fileType1 = "application/jpeg";
		final byte[] ATTACHMENT1 = Files.readAllBytes(Paths.get(path1));
		Attachment attachment1 = Attachment.builder().fileName(fileName1).fileType(fileType1).fileContent(ATTACHMENT1).build();
		
		
		final File file2 = new File("C:\\Users\\TapojitB\\Desktop\\Email\\SRS.pdf");
		final String path2 = file2.getPath();
		final String fileName2 = file2.getName();
		final String fileType2 = "application/pdf";
		final byte[] ATTACHMENT2 = Files.readAllBytes(Paths.get(path2));
		Attachment attachment2 = Attachment.builder().fileName(fileName2).fileType(fileType2).fileContent(ATTACHMENT2).build();
		
		List<Attachment> attachments = new ArrayList<>();
		attachments.add(attachment1);
		attachments.add(attachment2);

		final Email email = Email.builder().toAddress(EMAIL_ADDRESS).subject(SUBJECT).body(BODY).html(true).attachments(attachments).build();
				
				
		
		externalEmailService.sendEmail(email);

		// verify(javaMailSender).send(mimeMessage);
	}
	
	
	private Optional<String> getExtensionByStringHandling(String filename) {
	    return Optional.ofNullable(filename)
	      .filter(f -> f.contains("."))
	      .map(f -> f.substring(filename.lastIndexOf(".") + 1));
	}
}
