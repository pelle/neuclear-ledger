package org.neuclear.ledger;

/**
 * 
 * User: pelleb
 * Date: Jul 23, 2003
 * Time: 2:24:50 PM
 */
public class LedgerCreationException extends Exception {
    public LedgerCreationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public LedgerCreationException(Throwable throwable) {
        super(throwable);
    }

}
