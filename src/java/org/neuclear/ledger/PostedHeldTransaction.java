package org.neuclear.ledger;

import java.util.Date;

/**
 * User: pelleb
 * Date: Jul 29, 2003
 * Time: 3:17:16 PM
 */
public final class PostedHeldTransaction extends PostedTransaction implements HeldTransaction {
    public PostedHeldTransaction(final UnPostedHeldTransaction orig, Date time) throws InvalidTransactionException {
        super(orig, time);
        this.expiryTime = orig.getExpiryTime();
    }

    public final Date getExpiryTime() {
        return expiryTime;
    }

    final private Date expiryTime;

}
