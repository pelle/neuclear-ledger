package org.neuclear.ledger;

/**
 * Created by IntelliJ IDEA.
 * User: pelleb
 * Date: Jul 18, 2003
 * Time: 2:22:46 PM
 * To change this template use Options | File Templates.
 */
public abstract class LedgerException extends Exception {
    protected LedgerException(final LedgerController ledger) {
        this.ledger = ledger;
    }

    protected LedgerException(final LedgerController ledger, final Throwable cause) {
        super(cause);
        this.ledger = ledger;
    }

    public final String getMessage() {
        return "NeuClear Ledger Exception: " + ((ledger != null) ? ledger.toString() : "") + "\n" + getSubMessage();
    }

    abstract public String getSubMessage();

    public final LedgerController getLedger() {
        return ledger;
    }

    private final LedgerController ledger;
}
