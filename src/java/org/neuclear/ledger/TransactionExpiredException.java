package org.neuclear.ledger;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 12:52:50 PM
 * To change this template use Options | File Templates.
 */
public final class TransactionExpiredException extends LedgerException {
    public TransactionExpiredException(final Ledger ledger, final PostedHeldTransaction tran) {
        super(ledger);
        transaction = tran;
    }

    private final PostedHeldTransaction transaction;

    public final PostedHeldTransaction getTransaction() {
        return transaction;
    }

    public final String getSubMessage() {
        return "Transaction: " + transaction.getRequestId() + " expired: " + transaction.getExpiryTime();
    }
}
