package com.icegreen.greenmail.imap.commands.search;

import com.icegreen.greenmail.store.SimpleStoredMessage;

public class Or implements Criteria {

	private final Criteria first;
	private final Criteria second;

	public Or(Criteria first, Criteria second) {
		this.first = first;
		this.second = second;
	}
	
	@Override
	public boolean match(SimpleStoredMessage message) {
		return first.match(message) || second.match(message);
	}

}
