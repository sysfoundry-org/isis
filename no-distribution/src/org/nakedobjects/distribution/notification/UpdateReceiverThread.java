
package org.nakedobjects.distribution.notification;

import org.nakedobjects.distribution.ObjectUpdateMessage;
import org.nakedobjects.distribution.ProxyObjectManager;

import org.apache.log4j.Category;


public class UpdateReceiverThread extends Thread {
    final static Category LOG = Category.getInstance(UpdateReceiverThread.class);
    private boolean acceptUpdates = true;
    private final ProxyObjectManager objectManager;
    private ConnectionToServer receiver;

    public UpdateReceiverThread(ProxyObjectManager objectManager) {
    	super("Update-Reciever");
        this.objectManager = objectManager;
    }

    public void run() {
        while (acceptUpdates) {
            ObjectUpdateMessage msg = null;

            try {
                msg = receiver.receive();

                if (msg == null) {
                	LOG.warn("Null update recieved");
                	try {
						Thread.sleep(100);
					} catch (InterruptedException ignore) {
					}
                    continue;
                }

                LOG.info("Received update message " + msg);
                msg.update(objectManager);
            } catch (RuntimeException e) {
                LOG.error("Update receiver is terminating due to a runtime error!",
                    e);
                shutdown();
            }
        }
    }

    public void setReceiver(ConnectionToServer newReceiver) {
        receiver = newReceiver;
    }

    public void shutdown() {
        LOG.info("Shutting down update receiver " + receiver);
        acceptUpdates = false;
        receiver.shutdown();
        try {
			this.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		LOG.info("Update receiver shutdown complete");
    }
}


/*
Naked Objects - a framework that exposes behaviourally complete
business objects directly to the user.
Copyright (C) 2000 - 2005  Naked Objects Group Ltd

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

The authors can be contacted via www.nakedobjects.org (the
registered address of Naked Objects Group is Kingsway House, 123 Goldworth
Road, Woking GU21 1NR, UK).
*/