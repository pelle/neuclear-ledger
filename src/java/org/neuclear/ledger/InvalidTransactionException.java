package org.neuclear.ledger;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 21, 2003
 * Time: 1:12:31 PM
 * To change this template use Options | File Templates.
 */
public final class InvalidTransactionException extends LedgerException{
    public InvalidTransactionException(final Ledger ledger, final String message) {
        super(ledger);
    }

    public final String getSubMessage() {
        return "Validation Error: "+message;

    }
    private String message;
}
