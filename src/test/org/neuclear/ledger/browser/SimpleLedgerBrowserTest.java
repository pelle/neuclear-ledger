package org.neuclear.ledger.browser;

import org.neuclear.ledger.LedgerController;
import org.neuclear.ledger.simple.SimpleLedgerController;
import org.neuclear.ledger.tests.AbstractLedgerBrowserTest;

/*
$Id: SimpleLedgerBrowserTest.java,v 1.3 2004/05/04 17:39:06 pelle Exp $
$Log: SimpleLedgerBrowserTest.java,v $
Revision 1.3  2004/05/04 17:39:06  pelle
Fixed some things with regards to getTestBalance.

Revision 1.2  2004/04/27 15:23:56  pelle
Due to a new API change in 0.5 I have changed the name of Ledger and it's implementers to LedgerController.

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

    public LedgerController getLedger() {
        return new SimpleLedgerController("test");
    }
}
