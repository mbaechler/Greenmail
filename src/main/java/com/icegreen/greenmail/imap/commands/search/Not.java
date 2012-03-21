package com.icegreen.greenmail.imap.commands.search;

import com.icegreen.greenmail.store.SimpleStoredMessage;

public class Not implements Criteria {

	private final Criteria criteria;

	public Not(Criteria criteria) {
		this.criteria = criteria;
	}
	
	@Override
	public boolean match(SimpleStoredMessage message) {
		return !criteria.match(message);
	}

}
