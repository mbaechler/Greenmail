package com.icegreen.greenmail.imap.commands.search;

import com.icegreen.greenmail.imap.ProtocolException;
import com.icegreen.greenmail.store.SimpleStoredMessage;

public class After extends DateCriteria implements Criteria {

	public After(String date) throws ProtocolException {
		super(date);
	}
	
	@Override
	public boolean match(SimpleStoredMessage message) {
		return message.getInternalDate().after(getDate());
	}
}
