package org.egov.web.notification.mail.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.IOUtils;
import org.egov.web.notification.mail.model.Email;
import org.egov.web.notification.mail.model.EmailAttachment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.sendgrid.Attachments;
import com.sendgrid.Content;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;

import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnProperty(value = "mail.enabled", havingValue = "true")
@Slf4j
public class ExternalEmailService implements EmailService {

	public static final String EXCEPTION_MESSAGE = "Exception creating HTML email";
	private JavaMailSenderImpl mailSender;
	public static final String SMTP_GMAIL = "smtp.gmail.com";
	public static final String SMTP_SENDGRID = "smtp.sendgrid.com";
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
		

		String host = mailSender.getHost();
		if (!StringUtils.isEmpty(host) && SMTP_SENDGRID.equals(host)) {
			com.sendgrid.Email from = new com.sendgrid.Email(mailSender.getUsername());
			com.sendgrid.Email to = new com.sendgrid.Email(email.getToAddress());
			Content content = new Content("text/html", email.getBody());
			Mail mail = new Mail(from, email.getSubject(), to, content);
			SendGrid sg = new SendGrid(mailSender.getPassword());
			Request request = new Request();
			try {
				request.setMethod(Method.POST);
				request.setEndpoint("mail/send");
				request.setBody(mail.build());
				@SuppressWarnings("unused")
				Response response = sg.api(request);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}

		} else {
		final SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(email.getToAddress());
		mailMessage.setSubject(email.getSubject());
		mailMessage.setText(email.getBody());
		log.info("Before mail send to {}",email.getToAddress());
		mailSender.send(mailMessage);
		log.info("Mail sent successfully to {}",email.getToAddress());
		}
	}

	private void sendHTMLEmail(Email email) {
		

		String host = mailSender.getHost();
		/********* Attachment Related Enhancement ************/
		if (!StringUtils.isEmpty(host) && SMTP_SENDGRID.equals(host)) {
			com.sendgrid.Email from = new com.sendgrid.Email(mailSender.getUsername());
			com.sendgrid.Email to = new com.sendgrid.Email(email.getToAddress());
			Content content = new Content("text/html", email.getBody());
			Mail mail = new Mail(from, email.getSubject(), to, content);
			
			try {
			if (!CollectionUtils.isEmpty(email.getAttachments())) {
				for (EmailAttachment eachAttachment : email.getAttachments()) {
					log.info("Attachment Url(s) {}",eachAttachment.getUrl());
					Attachments attachments = new Attachments();
					//attachments.setContent(Base64Utils.encodeToString(FileUtils.readAllBytes(Paths.get(new URL(checkEncode(eachAttachment.getUrl())).openStream()).toFile().toPath())));
					attachments.setContent(Base64Utils.encodeToString(IOUtils.toByteArray(new URL((eachAttachment.getUrl()).replaceAll(" ", "%20")).openStream())));

					attachments.setType(eachAttachment.getMimeType());
					attachments.setFilename(eachAttachment.getName());
					mail.addAttachments(attachments);

				}
			}

			SendGrid sg = new SendGrid(mailSender.getPassword());
			Request request = new Request();
			
				request.setMethod(Method.POST);
				request.setEndpoint("mail/send");
				request.setBody(mail.build());
				@SuppressWarnings("unused")
				Response response = sg.api(request);
			} catch (Exception e) {

				if (e.toString().toLowerCase().contains(("IOException").toLowerCase())
						|| e.toString().toLowerCase().contains(("FileNotFoundException").toLowerCase())) {
					Content newContent = new Content("text/html", (email.getBody() + "<br> <p style='color:red;'>Note: Email attachment could not be uploaded.</p>"));
					Mail newMail = new Mail(from, email.getSubject(), to, newContent);
					SendGrid newSg = new SendGrid(mailSender.getPassword());
					Request newRequest = new Request();
					try {
						newRequest.setMethod(Method.POST);
						newRequest.setEndpoint("mail/send");
						newRequest.setBody(newMail.build());
						@SuppressWarnings("unused")
						Response response = newSg.api(newRequest);
					} catch (Exception ex) {
						log.error(EXCEPTION_MESSAGE, ex);
					}
				} else {
					log.error(EXCEPTION_MESSAGE, e);
				}
			}

		} else {
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(message, true);
			helper.setTo(email.getToAddress());
			helper.setSubject(email.getSubject());
			helper.setText(email.getBody(), true);
			if (email.getAttachments() != null) {
				for (EmailAttachment attachment : email.getAttachments()) {
					log.info("Attachment Url(s) {}",attachment.getUrl());
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
			log.info("Before mail send to {}",email.getToAddress());
			mailSender.send(message);
			log.info("Mail sent successfully to {}",email.getToAddress());
		} catch (MailException e) {
			log.error("Mail sending failed to {}. Trying again...",email.getToAddress());
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
					log.info("Mail sent successfully to {}",email.getToAddress());
				}catch(Exception ex) {
					log.error(EXCEPTION_MESSAGE, ex);
					throw new RuntimeException(ex);
				}
			}
		}
	}}
}