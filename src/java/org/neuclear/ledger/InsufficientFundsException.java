package org.neuclear.ledger;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Mar 22, 2004
 * Time: 11:40:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class InsufficientFundsException extends InvalidTransactionException {
    public InsufficientFundsException(final LedgerController ledger, final String book, final double amount, final double balance) {
        super(ledger, " Insufficient funds in account: " + book + " to cover the amount: " + amount + " available: " + balance);
        this.amount = amount;
        this.book = book;
    }

    public InsufficientFundsException(final LedgerController ledger, final String book, final double amount) {
        super(ledger, " Insufficient funds in account: " + book + " to cover the amount: " + amount);
        this.amount = amount;
        this.book = book;
    }

    public double getAmount() {
        return amount;
    }

    public String getBook() {
        return book;
    }

    private final double amount;
    private final String book;
}
