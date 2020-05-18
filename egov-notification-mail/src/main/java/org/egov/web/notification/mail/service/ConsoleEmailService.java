package org.egov.web.notification.mail.service;

import org.egov.web.notification.mail.model.Email;
import org.egov.web.notification.mail.model.EmailAttachment;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "mail.enabled", havingValue = "false", matchIfMissing = true)
public class ConsoleEmailService implements EmailService {

    @Override
    public void sendEmail(Email email) {
        System.out.println(
                String.format(
                        "Sending email to %s with subject %s and body %s",
                        email.getToAddress(),
                        email.getSubject(),
                        email.getBody()
                )
        );
        if (email.getAttachments() != null) {
        	for (EmailAttachment attachment : email.getAttachments()) {
				System.out.println(String.format("Attaching file %s with mimeType %s with url %s ", 
					attachment.getName(),
					attachment.getMimeType(),
					attachment.getUrl()
				));
			}
        }
    }
}
