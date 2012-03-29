/*
 * Copyright (c) 2006 Wael Chatila / Icegreen Technologies. All Rights Reserved.
 * This software is released under the LGPL which is available at http://www.gnu.org/copyleft/lesser.html
 * This file has been used and modified. Original file can be found on http://foedus.sourceforge.net
 */
package com.icegreen.greenmail.store;

import javax.mail.internet.MimeMessage;

import org.fest.assertions.Assertions;
import org.junit.Test;

import com.icegreen.greenmail.util.GreenMailUtil;

public class SimpleMessageAttributesTest {

	final String sampleEmail = ""
			+ "Message-ID: <0F1E2F68-0DE4-4E96-8B95-31488164613E@localhost.com>\r\n"
			+ "Date: Thu, 29 Mar 2012 11:52:10 -0800\r\n"
			+ "Importance: Normal\r\n" + "MIME-Version: 1.0\r\n"
			+ "To: Support GreenMail <support@localhost.com>\r\n"
			+ "Subject: content-disposition header\r\n"
			+ "Content-Type: text/plain; charset=ISO-8859-1; format=flowed\r\n"
			+ "Content-Transfer-Encoding: 7bit\r\n"
			+ "Content-Disposition: inline\r\n" + "\r\n"
			+ "Fix bug on content-disposition header\r\n";

	@Test
	public void testParseInlineContentDispostionHeaderMimePart() {
		MimeMessage message = GreenMailUtil.newMimeMessage(sampleEmail);
		
		SimpleMessageAttributes simpleMessageAttributes = new SimpleMessageAttributes();
		simpleMessageAttributes.parseMimePart(message);
		
		String bodyStructure = simpleMessageAttributes.getBodyStructure(true);
		Assertions.assertThat(bodyStructure).contains("(\"inline\" NIL)");
	}
}