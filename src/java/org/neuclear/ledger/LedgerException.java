package org.neuclear.ledger;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 18, 2003
 * Time: 2:22:46 PM
 * To change this template use Options | File Templates.
 */
public abstract class LedgerException extends Exception {
    protected LedgerException(Ledger ledger) {
        this.ledger=ledger;
    }

    protected LedgerException(Ledger ledger,Throwable cause) {
        super(cause);
        this.ledger=ledger;
    }

    public String getMessage() {
        return "NeuClear Ledger Exception: "+((ledger!=null)?ledger.toString():"")+"\n"+getSubMessage();
    }

    abstract public String getSubMessage();

    public Ledger getLedger() {
        return ledger;
    }

    private Ledger ledger;
}
