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
	/********* Attachment Related Enhancement ************/
	private List<Attachment> attachments;

}
