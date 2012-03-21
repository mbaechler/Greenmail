package com.icegreen.greenmail.imap.commands.search;

import javax.mail.Flags.Flag;

import com.icegreen.greenmail.store.SimpleStoredMessage;


public class Deleted implements Criteria {

	@Override
	public boolean match(SimpleStoredMessage message) {
		return message.getFlags().contains(Flag.DELETED);
	}

}
