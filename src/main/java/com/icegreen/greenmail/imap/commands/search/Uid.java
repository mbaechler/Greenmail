package com.icegreen.greenmail.imap.commands.search;

import java.util.Iterator;

import com.google.common.base.Splitter;
import com.google.common.collect.Range;
import com.icegreen.greenmail.imap.ProtocolException;
import com.icegreen.greenmail.store.SimpleStoredMessage;

public class Uid implements Criteria {

	private final Range<Long> range;

	public Uid(String simpleClosedRange) throws ProtocolException {
		Iterator<String> split = Splitter.on(':').split(simpleClosedRange).iterator();
		if (!split.hasNext()) {
			throw new IllegalStateException(String.format("Not enough values in %s", simpleClosedRange));
		}
		long lower = Long.parseLong(split.next());
		
		if (!split.hasNext()) {
			throw new IllegalStateException(String.format("Not enough values in %s", simpleClosedRange));
		}
		long higher = higher(split.next());
		
		range = Range.closed(lower, higher);
	}

	private long higher(String value) {
		if (value.equals("*")) {
			return Long.MAX_VALUE;
		}
		return Long.parseLong(value);
	}
	
	@Override
	public boolean match(SimpleStoredMessage message) {
		return range.contains(message.getUid());
	}
}
