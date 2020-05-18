package org.egov.web.notification.mail.consumer.contract;

import java.util.List;

import org.egov.web.notification.mail.model.Email;
import org.egov.web.notification.mail.model.Email.EmailAttachment;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class EmailRequest {
    private String email;
    private String subject;
    private String body;
    @JsonProperty("isHTML")
    private boolean isHTML;
    private List<EmailAttachment> attachments;
    
    public Email toDomain() {
        return Email.builder()
				.toAddress(email)
				.subject(subject)
				.body(body)
				.html(isHTML)
				.attachments(attachments)
				.build();
    }
}
