package org.egov.web.notification.mail.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Builder
public class Email {
    private String toAddress;
    private String subject;
    private String body;
    private boolean html;
    private List<EmailAttachment> attachments;
    @AllArgsConstructor
    @Builder
    @Getter
    public static class EmailAttachment {
    	private String name;
    	private String url;
    	private String mimeType;
    }
}
