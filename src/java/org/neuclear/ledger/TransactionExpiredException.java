package org.neuclear.ledger;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 12:52:50 PM
 * To change this template use Options | File Templates.
 */
public class TransactionExpiredException extends LedgerException {
    public TransactionExpiredException(Ledger ledger,PostedHeldTransaction tran) {
        super(ledger);
        transaction=tran;
    }
    private PostedHeldTransaction transaction;

    public PostedHeldTransaction getTransaction() {
        return transaction;
    }

    public String getSubMessage() {
        return "Transaction: "+transaction.getXid()+" expired: "+transaction.getExpiryTime();
    }
}
