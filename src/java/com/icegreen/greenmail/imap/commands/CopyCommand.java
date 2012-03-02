/* -------------------------------------------------------------------
 * Copyright (c) 2006 Wael Chatila / Icegreen Technologies. All Rights Reserved.
 * This software is released under the LGPL which is available at http://www.gnu.org/copyleft/lesser.html
 * This file has been modified by the copyright holder. Original file can be found at http://james.apache.org
 * -------------------------------------------------------------------
 */
package com.icegreen.greenmail.imap.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.icegreen.greenmail.imap.*;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.store.MailFolder;

/**
 * Handles processeing for the COPY imap command.
 *
 * @author Darrell DeBoer <darrell@apache.org>
 * @version $Revision: 109034 $
 */
class CopyCommand extends SelectedStateCommand implements UidEnabledCommand {
    public static final String NAME = "COPY";
    public static final String ARGS = "<message-set> <mailbox>";

    /**
     * @see CommandTemplate#doProcess
     */
    protected void doProcess(ImapRequestLineReader request,
                             ImapResponse response,
                             ImapSession session)
            throws ProtocolException, FolderException {
        doProcess(request, response, session, false);
    }

    public void doProcess(ImapRequestLineReader request,
                          ImapResponse response,
                          ImapSession session,
                          boolean useUids)
            throws ProtocolException, FolderException {
        IdRange[] idSet = parser.parseIdRange(request);
        String mailboxName = parser.mailbox(request);
        parser.endLine(request);

        ImapSessionFolder currentMailbox = session.getSelected();
        MailFolder toFolder;
        try {
            toFolder = getMailbox(mailboxName, session, true);
        } catch (FolderException e) {
            e.setResponseCode("TRYCREATE");
            throw e;
        }

//        if (! useUids) {
//            idSet = currentMailbox.toUidSet(idSet);
//        }
//        currentMailbox.copyMessages(toMailbox, idSet);
        long[] uids = currentMailbox.getMessageUids();
        Map uidsOfCopiedAndNewMessages = new LinkedHashMap(uids.length);
        for (int i = 0; i < uids.length; i++) {
            long uid = uids[i];
            boolean inSet;
            if (useUids) {
                inSet = includes(idSet, uid);
            } else {
                int msn = currentMailbox.getMsn(uid);
                inSet = includes(idSet, msn);
            }

            if (inSet) {
            	long newMessageUid = currentMailbox.copyMessage(uid, toFolder);
				uidsOfCopiedAndNewMessages.put(Long.valueOf(uid), Long.valueOf(newMessageUid));
            }
        }
        
        String copyUidResponse = buildCOPYUIDResponse(uidsOfCopiedAndNewMessages);
        session.unsolicitedResponses(response);
        response.taggedResponseCompleted(copyUidResponse);
    }

    private String buildCOPYUIDResponse(Map uidsOfCopiedAndNewMessages) {
    	String UID_SEPARATOR = " ";
    	StringBuilder builder = new StringBuilder("[COPYUID 9999999");
    	Iterator uidsIterator = uidsOfCopiedAndNewMessages.entrySet().iterator();
		while (uidsIterator.hasNext()) {
			Entry uids = (Entry) uidsIterator.next();
			builder.append(UID_SEPARATOR);
    		builder.append(uids.getKey());
			builder.append(UID_SEPARATOR);
    		builder.append(uids.getValue());
    	}
    	builder.append("]");
    	return builder.toString();
	}

	/**
     * @see ImapCommand#getName
     */
    public String getName() {
        return NAME;
    }

    /**
     * @see CommandTemplate#getArgSyntax
     */
    public String getArgSyntax() {
        return ARGS;
    }
}
