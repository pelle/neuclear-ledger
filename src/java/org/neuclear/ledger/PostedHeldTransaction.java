package org.neuclear.ledger;

import java.util.Date;
import java.util.Iterator;

/**
 * User: pelleb
 * Date: Jul 29, 2003
 * Time: 3:17:16 PM
 */
public class PostedHeldTransaction extends PostedTransaction implements HeldTransaction {
    public PostedHeldTransaction(UnPostedHeldTransaction orig, String xid) throws InvalidTransactionException {
        super(orig, xid);
        this.expiryTime = orig.getExpiryTime();
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    final private Date expiryTime;

    /**
     * Implements a held asset
     * 
     * @param actualAmount    
     * @param transactionTime 
     * @param comment         
     * @return New Version
     */
    public PostedTransaction complete(double actualAmount, Date transactionTime, String comment) throws TransactionExpiredException, InvalidTransactionException, LowlevelLedgerException {
        return getLedger().performCompleteHold(this, actualAmount, transactionTime, comment);
    }

    public void cancel() throws UnknownTransactionException, LowlevelLedgerException {
        getLedger().performCancelHold(this);
    }

    PostedTransaction reverse(String comment) throws InvalidTransactionException, UnBalancedTransactionException, LowlevelLedgerException {
        UnPostedHeldTransaction reverse = new UnPostedHeldTransaction(getLedger(), "REVERSAL of " + getXid(), getTransactionTime(), getExpiryTime(), false);
        Iterator iter = getItems();
        while (iter.hasNext()) {
            TransactionItem item = (TransactionItem) iter.next();
            reverse.addItem(item.getBook(), -item.getAmount());
        }
        return reverse.post();
    }

}
