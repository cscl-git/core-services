package org.egov.web.notification.mail.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.egov.web.notification.mail.model.Email;
import org.egov.web.notification.mail.model.EmailAttachment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnProperty(value = "mail.enabled", havingValue = "true")
@Slf4j
public class ExternalEmailService implements EmailService {

	public static final String EXCEPTION_MESSAGE = "Exception creating HTML email";
	private JavaMailSenderImpl mailSender;

	public ExternalEmailService(JavaMailSenderImpl mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	public void sendEmail(Email email) {
		if (email.isHtml()) {
			sendHTMLEmail(email);
		} else {
			sendTextEmail(email);
		}
	}

	private void sendTextEmail(Email email) {
		final SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(email.getToAddress());
		mailMessage.setSubject(email.getSubject());
		mailMessage.setText(email.getBody());
		mailSender.send(mailMessage);
	}

	private void sendHTMLEmail(Email email) {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(message, true);
			helper.setTo(email.getToAddress());
			helper.setSubject(email.getSubject());
			helper.setText(email.getBody(), true);
			if (email.getAttachments() != null) {
				for (EmailAttachment attachment : email.getAttachments()) {
					InputStreamSource iss = new InputStreamSource() {

						@Override
						public InputStream getInputStream() throws IOException {
							return new BufferedInputStream(
									new URL((attachment.getUrl()).replaceAll(" ", "%20")).openStream());
						}
					};
					helper.addAttachment(attachment.getName(), iss, attachment.getMimeType());
				}
			}
		} catch (MessagingException e) {
			log.error(EXCEPTION_MESSAGE, e);
			throw new RuntimeException(e);
		}
		try {
			mailSender.send(message);

		} catch (MailException e) {

			if (e.getMessage().toLowerCase().contains(("IOException").toLowerCase())
					|| e.getMessage().toLowerCase().contains(("FileNotFoundException").toLowerCase())) {
				MimeMessage newMessage = mailSender.createMimeMessage();
				MimeMessageHelper newHelper;
				try {
					newHelper = new MimeMessageHelper(newMessage, true);
					newHelper.setTo(email.getToAddress());
					newHelper.setSubject(email.getSubject());
					newHelper.setText((email.getBody()+ "<br> <p style='color:red;'>Note: Email attachment could not be uploaded.</p>"), true);
				
					mailSender.send(newMessage);
				}catch(Exception ex) {
					log.error(EXCEPTION_MESSAGE, ex);
					throw new RuntimeException(ex);
				}
				}
		}
	}
}
