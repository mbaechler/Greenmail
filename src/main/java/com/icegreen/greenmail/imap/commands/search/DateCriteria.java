package com.icegreen.greenmail.imap.commands.search;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.icegreen.greenmail.imap.ProtocolException;

public class DateCriteria {

	private final Date date;

	public DateCriteria(String date) throws ProtocolException {
		try {
			this.date = new SimpleDateFormat("d-MMM-yyyy", Locale.ENGLISH).parse(date);
		} catch (ParseException e) {
			throw new ProtocolException(e);
		}
	}

	public Date getDate() {
		return date;
	}
}