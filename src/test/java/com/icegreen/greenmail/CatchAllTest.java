package com.icegreen.greenmail;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;

/**
 * @author Wael Chatila
 * @version $Id: $
 * @since May 27th, 2009
 */
public class CatchAllTest {
    GreenMail greenMail;

    @After
    public void tearDown() {
        try {
            greenMail.stop();
        } catch (NullPointerException ignored) {
            //empty
        }
    }

    @Test
    public void testSmtpServerBasic() {
        greenMail = new GreenMail(ServerSetupTest.SMTP);
        greenMail.start();
        GreenMailUtil.sendTextEmailTest("to11@domain1.com", "from@localhost.com", "subject", "body");
        GreenMailUtil.sendTextEmailTest("to12@domain1.com", "from@localhost.com", "subject", "body");
        GreenMailUtil.sendTextEmailTest("to21@domain2.com", "from@localhost.com", "subject", "body");
        GreenMailUtil.sendTextEmailTest("to31@domain3.com", "from@localhost.com", "subject", "body");
        GreenMailUtil.sendTextEmailTest("to32@domain3.com", "from@localhost.com", "subject", "body");
        GreenMailUtil.sendTextEmailTest("to33@domain3.com", "from@localhost.com", "subject", "body");
        assertEquals(6, greenMail.getReceivedMessages().length);
        assertEquals(2, greenMail.getReceivedMessagesForDomain("domain1.com").length);
        assertEquals(1, greenMail.getReceivedMessagesForDomain("domain2.com").length);
        assertEquals(3, greenMail.getReceivedMessagesForDomain("domain3.com").length);
    }
}
