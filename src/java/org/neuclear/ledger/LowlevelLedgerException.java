package org.neuclear.ledger;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 18, 2003
 * Time: 2:55:55 PM
 * To change this template use Options | File Templates.
 */
public final class LowlevelLedgerException extends LedgerException{
    public LowlevelLedgerException( final Throwable cause) {
        this(null,cause);
    }

    public LowlevelLedgerException(final Ledger ledger, final Throwable cause) {
        super(ledger, cause);
    }
    public LowlevelLedgerException(final Ledger ledger, final String message) {
        super(ledger,new Exception(message));

    }

    public final String getSubMessage() {
        return "Low Level Error: "+getCause().getLocalizedMessage();
    }

}
