package org.neuclear.ledger;

/**
 * 
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 2:24:50 PM
 */
public final class LedgerCreationException extends Exception {
    public LedgerCreationException(final String s, final Throwable throwable) {
        super(s, throwable);
    }

    public LedgerCreationException(final Throwable throwable) {
        super(throwable);
    }

}
