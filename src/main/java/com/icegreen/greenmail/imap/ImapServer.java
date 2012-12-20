/* -------------------------------------------------------------------
* Copyright (c) 2006 Wael Chatila / Icegreen Technologies. All Rights Reserved.
* This software is released under the LGPL which is available at http://www.gnu.org/copyleft/lesser.html
* This file has been modified by the copyright holder. Original file can be found at http://james.apache.org
* -------------------------------------------------------------------
*/
package com.icegreen.greenmail.imap;

import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;
import java.util.concurrent.Semaphore;

import com.icegreen.greenmail.AbstractServer;
import com.icegreen.greenmail.Managers;
import com.icegreen.greenmail.util.ServerSetup;

public final class ImapServer extends AbstractServer {

    private final Semaphore lock;

	public ImapServer(ServerSetup setup, Managers managers, Semaphore lock) {
        super(setup, managers);
		this.lock = lock;
    }



    public synchronized void quit() {
        try {
            for (Iterator<Thread> iterator = handlers.iterator(); iterator.hasNext();) {
                ImapHandler imapHandler = (ImapHandler) iterator.next();
                imapHandler.resetHandler();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try {
            if (null != serverSocket && !serverSocket.isClosed()) {
                serverSocket.close();
                serverSocket = null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        try {

            try {
                serverSocket = openServerSocket();
                setRunning(true);
                synchronized (this) {
                    this.notifyAll();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while (keepOn()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    ImapHandler imapHandler = new ImapHandler(managers.getUserManager(), managers.getImapHostManager(), clientSocket, lock);
                    handlers.add(imapHandler);
                    imapHandler.start();
                } catch (IOException ignored) {
                    //ignored
                }
            }
        } finally{
            quit();
        }
    }
}