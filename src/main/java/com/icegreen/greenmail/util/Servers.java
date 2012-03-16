/*
 * Copyright (c) 2006 Wael Chatila / Icegreen Technologies. All Rights Reserved.
 * This software is released under the LGPL which is available at http://www.gnu.org/copyleft/lesser.html
 *
 */
package com.icegreen.greenmail.util;

/**
 * @author Wael Chatila
 * @version $Id: $
 * @since Jan 28, 2006
 * @deprecated Use GreenMail.java instead
 */
public class Servers extends GreenMail {

    public Servers() {
        super();
    }

    public Servers(ServerSetup config) {
        super(config);
    }

    public Servers(ServerSetup[] config) {
        super(config);
    }
}
