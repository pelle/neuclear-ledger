package org.neuclear.ledger;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 12:52:50 PM
 * To change this template use Options | File Templates.
 */
public class UnBalancedTransactionException extends LedgerException {
    public UnBalancedTransactionException(Ledger ledger,UnPostedTransaction tran) {
        super(ledger);
        transaction=tran;
    }
    private UnPostedTransaction transaction;

    public UnPostedTransaction getTransaction() {
        return transaction;
    }

    public String getSubMessage() {
        return "Transaction was Unbalanced by: "+transaction.getBalance();
    }
}
