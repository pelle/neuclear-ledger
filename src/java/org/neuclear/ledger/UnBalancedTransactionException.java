package org.neuclear.ledger;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 12:52:50 PM
 * To change this template use Options | File Templates.
 */
public final class UnBalancedTransactionException extends InvalidTransactionException {
    public UnBalancedTransactionException(final Ledger ledger, final Transaction tran, final double amount) {
        super(ledger, "Transaction was Unbalanced by: " + amount);
        transaction = tran;
    }

    private final Transaction transaction;

    public final Transaction getTransaction() {
        return transaction;
    }

}
