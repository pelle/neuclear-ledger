package org.neuclear.ledger;

import java.util.Date;
import java.util.Iterator;

/**
 * User: pelleb
 * Date: Jul 29, 2003
 * Time: 3:17:16 PM
 */
public final class PostedHeldTransaction extends PostedTransaction implements HeldTransaction {
    public PostedHeldTransaction(final UnPostedHeldTransaction orig, final String xid) throws InvalidTransactionException {
        super(orig, xid);
        this.expiryTime = orig.getExpiryTime();
    }

    public final Date getExpiryTime() {
        return expiryTime;
    }

    final private Date expiryTime;

    /**
     * Implements a held assetName
     * 
     * @param actualAmount    
     * @param transactionTime 
     * @param comment         
     * @return New Version
     */
    public final PostedTransaction complete(final double actualAmount, final Date transactionTime, final String comment) throws TransactionExpiredException, InvalidTransactionException, LowlevelLedgerException {
        return getLedger().performCompleteHold(this, actualAmount, transactionTime, comment);
    }

    public final void cancel() throws UnknownTransactionException, LowlevelLedgerException {
        getLedger().performCancelHold(this);
    }

    final PostedTransaction reverse(final String comment) throws InvalidTransactionException, UnBalancedTransactionException, LowlevelLedgerException {
        final UnPostedHeldTransaction reverse = new UnPostedHeldTransaction(getLedger(), "REVERSAL of " + getXid(), getTransactionTime(), getExpiryTime(), false);
        final Iterator iter = getItems();
        while (iter.hasNext()) {
            final TransactionItem item = (TransactionItem) iter.next();
            reverse.addItem(item.getBook(), -item.getAmount());
        }
        return reverse.post();
    }

}
