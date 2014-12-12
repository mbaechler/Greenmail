/* -------------------------------------------------------------------
 * Copyright (c) 2006 Wael Chatila / Icegreen Technologies. All Rights Reserved.
 * This software is released under the LGPL which is available at http://www.gnu.org/copyleft/lesser.html
 * This file has been modified by the copyright holder. Original file can be found at http://james.apache.org
 * -------------------------------------------------------------------
 */
package com.icegreen.greenmail.imap.commands;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Joiner;
import com.icegreen.greenmail.imap.ImapRequestLineReader;
import com.icegreen.greenmail.imap.ImapResponse;
import com.icegreen.greenmail.imap.ImapSession;
import com.icegreen.greenmail.imap.ImapSessionFolder;
import com.icegreen.greenmail.imap.ProtocolException;
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
        Map<Long, Long> uidsOfCopiedAndNewMessages = new LinkedHashMap<Long, Long>(uids.length);
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
				uidsOfCopiedAndNewMessages.put(uid, newMessageUid);
            }
        }
        
        String copyUidResponse = buildCOPYUIDResponse(uidsOfCopiedAndNewMessages, currentMailbox.getUidValidity());
        session.unsolicitedResponses(response);
        response.taggedResponseCompleted(copyUidResponse);
    }

    private String buildCOPYUIDResponse(Map<Long, Long> uidsOfCopiedAndNewMessages, long mailboxUidValidity) {
    	return new StringBuilder("[COPYUID ")
    		.append(mailboxUidValidity)
    		.append(" ")
    		.append(Joiner.on(",").join(uidsOfCopiedAndNewMessages.keySet()))
    		.append(" ")
    		.append(Joiner.on(",").join(uidsOfCopiedAndNewMessages.values()))
    		.append("]").toString();
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
