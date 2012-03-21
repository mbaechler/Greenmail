package com.icegreen.greenmail.imap.commands.search;

import com.icegreen.greenmail.imap.ProtocolException;
import com.icegreen.greenmail.store.SimpleStoredMessage;

public class Before extends DateCriteria implements Criteria {

	public Before(String date) throws ProtocolException {
		super(date);
	}
	
	@Override
	public boolean match(SimpleStoredMessage message) {
		return message.getInternalDate().before(getDate());
	}
}
