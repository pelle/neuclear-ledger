package org.neuclear.ledger;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 17, 2003
 * Time: 4:46:06 PM
 * To change this template use Options | File Templates.
 */
public class UnknownTransactionException extends LedgerException {
    public UnknownTransactionException(Ledger ledger,String xid) {
        super(ledger);
        this.xid=xid;
    }
    public String getTransactionID() {
        return xid;
    }

    private String xid;

    public String getSubMessage() {
        return "Unknown Transaction: "+xid;
    }
}
