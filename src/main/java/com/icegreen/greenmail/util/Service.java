/*
 * Copyright (c) 2006 Wael Chatila / Icegreen Technologies. All Rights Reserved.
 * This software is released under the LGPL which is available at http://www.gnu.org/copyleft/lesser.html
 *
 */
package com.icegreen.greenmail.util;

/**
 * A class that facilitate service implementation
 *
 * @author Wael Chatila
 * @version $id: $
 * @since 2005
 */
abstract public class Service extends Thread {
    public abstract void run();

    public abstract void quit();

    private volatile boolean keepRunning = false;
    private volatile boolean running = false;

    final protected boolean keepOn() {
        return keepRunning;
    }

    public synchronized void startService() {
        if (!keepRunning) {
            keepRunning = true;
            start();
        }
    }

    public boolean isRunning() {
        return running;
    }

    protected void setRunning(boolean r) {
        this.running = r;
    }

    public void wait_for_running(long t) throws InterruptedException {
        this.wait(t);
    }

    /**
     * Stops the service. If a timeout is given and the service has still not
     * gracefully been stopped after timeout ms the service is stopped by force.
     *
     */
    public synchronized final void stopService() {
        running = false;
        try {
            if (keepRunning) {
                keepRunning = false;
                interrupt();
                quit();
                join();
            }
        } catch (InterruptedException e) {
            //its possible that the thread exits between the lines keepRunning=false and intertupt above
        }
    }

}

