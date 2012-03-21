package com.icegreen.greenmail.imap.commands.search;

import com.icegreen.greenmail.store.SimpleStoredMessage;

public interface Criteria {
	boolean match(SimpleStoredMessage message);
}
