package org.neuclear.ledger;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 17, 2003
 * Time: 4:46:06 PM
 * To change this template use Options | File Templates.
 */
public final class UnknownTransactionException extends LedgerException {
    public UnknownTransactionException(final Ledger ledger,final String xid) {
        super(ledger);
        this.xid=xid;
    }
    public final String getTransactionID() {
        return xid;
    }

    private final String xid;

    public final String getSubMessage() {
        return "Unknown Transaction: "+xid;
    }
}
