package org.neuclear.ledger.tests;

import junit.framework.TestCase;
import org.neuclear.ledger.InvalidTransactionException;
import org.neuclear.ledger.Ledger;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.browser.BookBrowser;
import org.neuclear.ledger.browser.LedgerBrowser;

/*
$Id: AbstractLedgerBrowserTest.java,v 1.1 2004/03/26 18:37:56 pelle Exp $
$Log: AbstractLedgerBrowserTest.java,v $
Revision 1.1  2004/03/26 18:37:56  pelle
More work on browsers. Added an AbstractLedgerBrowserTest for unit testing LedgerBrowsers.

*/

/**
 * User: pelleb
 * Date: Mar 26, 2004
 * Time: 12:19:26 PM
 */
public abstract class AbstractLedgerBrowserTest extends TestCase {

    public AbstractLedgerBrowserTest(String name) {
        super(name);
    }

    public abstract Ledger getLedger() throws LowlevelLedgerException;

    public void testIsBrowser() throws LowlevelLedgerException {
        final Ledger ledger = getLedger();
        assertTrue("Ledger is instance of LedgerBrowser", ledger instanceof LedgerBrowser);
        ledger.close();
    }

    public void testAmountOfEntries() throws LowlevelLedgerException, InvalidTransactionException {
        final Ledger ledger = getLedger();
        assertTrue("Ledger is instance of LedgerBrowser", ledger instanceof LedgerBrowser);

        final LedgerBrowser browser = (LedgerBrowser) ledger;

        final String bob = getBobBook();
        final String alice = getAliceBook();

        assertBookBrowserSize(bob, 0, browser.browse(bob));
        assertBookBrowserSize(alice, 0, browser.browse(alice));
        int i;
        for (i = 0; i < 10; i++) {
            ledger.transfer(bob, alice, 10, "test" + i);
        }
        assertBookBrowserSize(bob, i, browser.browse(bob));
        assertBookBrowserSize(alice, i, browser.browse(alice));


        ledger.close();
    }

    public void assertBookBrowserSize(final String book, final int count, final BookBrowser bb) throws LowlevelLedgerException {
        assertNotNull("null book browser for " + book, bb);
        int total = 0;
        while (bb.next()) {
            total++;
        }
        assertEquals("The size doesnt match for: " + book, count, total);
    }

    public String getNewBook(String root) {
        return root + System.currentTimeMillis();
    }

    public String getBobBook() {
        return getNewBook("Roberto");
    }

    public String getAliceBook() {
        return getNewBook("Alicia");
    }

}
