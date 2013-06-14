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
	
	@Test
	public void testParseContentLocationAndContentId() {
		String message = ""
				+ "Return-Path: <test.ext@obm15.lyn.lng>\r\n"
				+ "X-Sieve: CMU Sieve 2.2\r\n"
				+ "Content-Class: urn:content-classes:message\r\n"
				+ "Content-Type: multipart/related; type=\"multipart/alternative\"; boundary=\"----_=_NextPart_001_01CE6676.A2EDD0CD\"\r\n"
				+ "X-MimeOLE: Produced By Microsoft Exchange V6.5\r\n"
				+ "Subject: 5. une image\r\n"
				+ "Date: Tue, 11 Jun 2013 09:38:16 +0200\r\n"
				+ "Message-ID: <C79DAE2366C0B149BD1337EE01F27DCB0C9AA8@wappmese1be2.ad.fr>\r\n"
				+ "X-MS-Has-Attach: yes\r\n"
				+ "X-MS-TNEF-Correlator: \r\n"
				+ "Thread-Topic: une image\r\n"
				+ "Thread-Index: Ac5mdqELvr6tZ5cFTieh4HTfAw877Q==\r\n"
				+ "From: TEST M. <test.ext@obm15.lyn.lng>\r\n"
				+ "To: Sylvain <usera@obm15.lyn.lng>, Christophe <userb@obm15.lyn.lng>, Julien\r\n"
				+ " <userc@obm15.lyn.lng>\r\n"
				+ "X-OriginalArrivalTime: 11 Jun 2013 07:38:17.0046 (UTC)\r\n"
				+ " FILETIME=[A331FF60:01CE6676]\r\n"
				+ "X-Evolution-Source: 1371112195.13581.2@debian\r\n"
				+ "MIME-Version: 1.0\r\n"
				+ "\r\n"
				+ "\r\n"
				+ "------_=_NextPart_001_01CE6676.A2EDD0CD\r\n"
				+ "Content-Type: multipart/alternative; boundary=\"----_=_NextPart_002_01CE6676.A2EDD0CD\"\r\n"
				+ "\r\n"
				+ "\r\n"
				+ "------_=_NextPart_002_01CE6676.A2EDD0CD\r\n"
				+ "Content-Type: text/plain; charset=\"us-ascii\"\r\n"
				+ "Content-Transfer-Encoding: quoted-printable\r\n"
				+ "\r\n"
				+ "http=20\r\n"
				+ "<http://kb.mozillazine.org/index.php?title=3DIMAP%3A_advanced_account_conf\r\n"
				+ "iguration&diff=3D43408&oldid=3D42987>=20\r\n"
				+ "\r\n"
				+ "------_=_NextPart_002_01CE6676.A2EDD0CD\r\n"
				+ "Content-Type: text/html; charset=\"us-ascii\"\r\n"
				+ "Content-Transfer-Encoding: quoted-printable\r\n"
				+ "\r\n"
				+ "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\"><html><head>\r\n"
				+ "<meta http-equiv=3D\"Content-Type\" content=3D\"text/html; charset=3Dus-ascii\"=\r\n"
				+ ">\r\n"
				+ "<meta name=3D\"GENERATOR\" content=3D\"MSHTML 8.00.6001.19298\"></head>\r\n"
				+ "<body>\r\n"
				+ "<div><font size=3D\"2\" face=3D\"Arial\"><a href=3D\"http://kb.mozillazine.org/i=\r\n"
				+ "ndex.php?title=3DIMAP%3A_advanced_account_configuration&amp;diff=3D43408&am=\r\n"
				+ "p;oldid=3D42987\">http<img border=3D\"0\" hspace=3D\"0\" alt=3D\"\" align=3D\"basel=\r\n"
				+ "ine\" src=3D\"cid:555343607@11062013-0EC1\"></a></font></div></body></html>\r\n"
				+ "\r\n"
				+ "------_=_NextPart_002_01CE6676.A2EDD0CD--\r\n"
				+ "\r\n"
				+ "------_=_NextPart_001_01CE6676.A2EDD0CD\r\n"
				+ "Content-Type: image/jpeg; name=\"TB_import.JPG\"\r\n"
				+ "Content-Transfer-Encoding: base64\r\n"
				+ "Content-ID: <555343607@11062013-0EC1>\r\n"
				+ "Content-Description: TB_import.JPG\r\n"
				+ "Content-Location: TB_import.JPG\r\n"
				+ "\r\n"
				+ "HBwgJ\r\n"
				+ "------_=_NextPart_001_01CE6676.A2EDD0CD--\r\n"
				+ "\r\n";
		
		MimeMessage mimeMessage = GreenMailUtil.newMimeMessage(message);

		SimpleMessageAttributes simpleMessageAttributes = new SimpleMessageAttributes();
		simpleMessageAttributes.parseMimePart(mimeMessage);

		String bodyStructure = simpleMessageAttributes.getBodyStructure(true);
		Assertions.assertThat(bodyStructure).isEqualTo(
				"(" +
						"(" +
							"(\"TEXT\" \"PLAIN\" (\"charset\" \"us-ascii\") NIL NIL \"quoted-printable\" 125 3 NIL NIL NIL NIL)" +
							"(\"TEXT\" \"HTML\" (\"charset\" \"us-ascii\") NIL NIL \"quoted-printable\" 540 9 NIL NIL NIL NIL)" +
							" \"alternative\" (\"boundary\" \"----_=_NextPart_002_01CE6676.A2EDD0CD\") NIL NIL NIL)" +
						"(\"IMAGE\" \"JPEG\" (\"name\" \"TB_import.JPG\") " +
							"\"<555343607@11062013-0EC1>\" \"TB_import.JPG\" \"base64\" 5 NIL NIL NIL \"TB_import.JPG\") " +
							"\"related\" (\"type\" \"multipart/alternative\" \"boundary\" \"----_=_NextPart_001_01CE6676.A2EDD0CD\") NIL NIL NIL)"
				);
	}
}