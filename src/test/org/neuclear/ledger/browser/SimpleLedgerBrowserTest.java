package org.neuclear.ledger.browser;

import org.neuclear.ledger.Ledger;
import org.neuclear.ledger.simple.SimpleLedger;
import org.neuclear.ledger.tests.AbstractLedgerBrowserTest;

/*
$Id: SimpleLedgerBrowserTest.java,v 1.1 2004/03/26 18:37:56 pelle Exp $
$Log: SimpleLedgerBrowserTest.java,v $
Revision 1.1  2004/03/26 18:37:56  pelle
More work on browsers. Added an AbstractLedgerBrowserTest for unit testing LedgerBrowsers.

*/

/**
 * User: pelleb
 * Date: Mar 26, 2004
 * Time: 12:17:25 PM
 */
public class SimpleLedgerBrowserTest extends AbstractLedgerBrowserTest {
    public SimpleLedgerBrowserTest(String name) {
        super(name);
    }

    public Ledger getLedger() {
        return new SimpleLedger("test browser");
    }
}
