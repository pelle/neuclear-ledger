package org.neuclear.ledger;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 12:52:50 PM
 * To change this template use Options | File Templates.
 */
public final class UnBalancedTransactionException extends LedgerException {
    public UnBalancedTransactionException(final Ledger ledger,final UnPostedTransaction tran) {
        super(ledger);
        transaction=tran;
    }
    private final UnPostedTransaction transaction;

    public final UnPostedTransaction getTransaction() {
        return transaction;
    }

    public final String getSubMessage() {
        return "Transaction was Unbalanced by: "+transaction.getBalance();
    }
}
