package org.neuclear.ledger;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 18, 2003
 * Time: 2:55:55 PM
 * To change this template use Options | File Templates.
 */
public class LowlevelLedgerException extends LedgerException{
    public LowlevelLedgerException( Throwable cause) {
        this(null,cause);
    }

    public LowlevelLedgerException(Ledger ledger, Throwable cause) {
        super(ledger, cause);
    }
    public LowlevelLedgerException(Ledger ledger, String message) {
        super(ledger,new Exception(message));

    }

    public String getSubMessage() {
        return "Low Level Error: "+getCause().getLocalizedMessage();
    }

}
