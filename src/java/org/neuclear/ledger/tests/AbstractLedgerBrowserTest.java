package org.neuclear.ledger.tests;

import junit.framework.TestCase;
import org.neuclear.ledger.InvalidTransactionException;
import org.neuclear.ledger.Ledger;
import org.neuclear.ledger.LowlevelLedgerException;
import org.neuclear.ledger.browser.BookBrowser;
import org.neuclear.ledger.browser.LedgerBrowser;

import java.util.Date;

/*
$Id: AbstractLedgerBrowserTest.java,v 1.3 2004/03/29 16:56:26 pelle Exp $
$Log: AbstractLedgerBrowserTest.java,v $
Revision 1.3  2004/03/29 16:56:26  pelle
AbstractLedgerBrowserTest has been extended to test date ranges
SimpleLedger now passes all tests.
HibernateLedger passes at times, which is mysterious. More research needed.

Revision 1.2  2004/03/26 23:36:34  pelle
The simple browse(book) now works on hibernate, I have implemented the other two, which currently don not constrain the query correctly.

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

    protected void setUp() throws Exception {
        ledger = getLedger();
        assertTrue("Ledger is instance of LedgerBrowser", ledger instanceof LedgerBrowser);
        browser = (LedgerBrowser) ledger;
    }

    protected void tearDown() throws Exception {
        ledger.close();
    }

    public static Date getIsolatedTimeStamp() {
        try {
            Thread.currentThread().sleep(5);
        } catch (InterruptedException e) {
            ;
        }
        final Date t = new Date();
        try {
            Thread.currentThread().sleep(5);
        } catch (InterruptedException e) {
            ;
        }
        return t;
    }

    public void testAmountOfEntries() throws LowlevelLedgerException, InvalidTransactionException {
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
    }

    public void testEntryContent() throws LowlevelLedgerException, InvalidTransactionException {
        final String bob = getBobBook();
        final String alice = getAliceBook();

        assertBookBrowserSize(bob, 0, browser.browse(bob));
        assertBookBrowserSize(alice, 0, browser.browse(alice));
        int i;
        for (i = 0; i < 10; i++) {
            ledger.transfer(bob, alice, 10, "test" + i);
        }
        assertVerifyBrowserContent(bob, alice, -10, i, browser.browse(bob));
        assertVerifyBrowserContent(alice, bob, 10, i, browser.browse(alice));


    }

    public void testAmountOfEntriesFromTime() throws LowlevelLedgerException, InvalidTransactionException {
        final String bob = getBobBook();
        final String alice = getAliceBook();

        Date t1 = getIsolatedTimeStamp();
        assertBookBrowserSize(bob, 0, browser.browse(bob));
        assertBookBrowserSize(alice, 0, browser.browse(alice));
        int i;
        for (i = 0; i < 10; i++) {
            ledger.transfer(bob, alice, 10, "test" + i);
        }
        assertBookBrowserSize(bob, i, browser.browse(bob));
        assertBookBrowserSize(alice, i, browser.browse(alice));

        Date t2 = getIsolatedTimeStamp();
        assertTrue(t2.after(t1));

        for (i = 0; i < 10; i++) {
            ledger.transfer(bob, alice, 10, "test" + i);
        }
        assertBookBrowserSize(bob, 20, browser.browse(bob));
        assertBookBrowserSize(alice, 20, browser.browse(alice));

        assertBookBrowserSize(bob, 20, browser.browseFrom(bob, t1));
        assertBookBrowserSize(alice, 20, browser.browseFrom(alice, t1));


        assertBookBrowserSize(bob, i, browser.browseFrom(bob, t2));
        assertBookBrowserSize(alice, i, browser.browseFrom(alice, t2));

    }

    public void testAmountOfEntriesInTimeRange() throws LowlevelLedgerException, InvalidTransactionException {
        final String bob = getBobBook();
        final String alice = getAliceBook();

        Date t1 = getIsolatedTimeStamp();
        assertBookBrowserSize(bob, 0, browser.browse(bob));
        assertBookBrowserSize(alice, 0, browser.browse(alice));
        int i;
        for (i = 0; i < 10; i++) {
            ledger.transfer(bob, alice, 10, "test" + i);
        }
        assertBookBrowserSize(bob, i, browser.browse(bob));
        assertBookBrowserSize(alice, i, browser.browse(alice));

        Date t2 = getIsolatedTimeStamp();

        for (i = 0; i < 10; i++) {
            ledger.transfer(bob, alice, 10, "test" + i);
        }
        assertBookBrowserSize(bob, 20, browser.browse(bob));
        assertBookBrowserSize(alice, 20, browser.browse(alice));
        Date t3 = getIsolatedTimeStamp();

        for (i = 0; i < 10; i++) {
            ledger.transfer(bob, alice, 10, "test" + i);
        }
        Date t4 = getIsolatedTimeStamp();
        assertBookBrowserSize(bob, 30, browser.browse(bob));
        assertBookBrowserSize(alice, 30, browser.browse(alice));

        assertBookBrowserSize(bob, 10, browser.browseRange(bob, t3, t4));
        assertBookBrowserSize(alice, 10, browser.browseRange(alice, t3, t4));
        assertBookBrowserSize(bob, 10, browser.browseRange(bob, t2, t3));
        assertBookBrowserSize(alice, 10, browser.browseRange(alice, t2, t3));
        assertBookBrowserSize(bob, 10, browser.browseRange(bob, t1, t2));
        assertBookBrowserSize(alice, 10, browser.browseRange(alice, t1, t2));


        assertBookBrowserSize(bob, 20, browser.browseRange(bob, t1, t3));
        assertBookBrowserSize(alice, 20, browser.browseRange(alice, t1, t3));
        assertBookBrowserSize(bob, 20, browser.browseRange(bob, t2, t4));
        assertBookBrowserSize(alice, 20, browser.browseRange(alice, t2, t4));

        assertBookBrowserSize(bob, 30, browser.browseRange(bob, t1, t4));
        assertBookBrowserSize(alice, 30, browser.browseRange(alice, t1, t4));

    }

    public void assertVerifyBrowserContent(final String book, final String counterparty, final double amount, final int count, final BookBrowser bb) throws LowlevelLedgerException {
        assertNotNull("null book browser for " + book, bb);
        int total = 0;
        while (bb.next()) {
            assertEquals("book", book, bb.getBook());
            assertEquals("counterparty", counterparty, bb.getCounterparty());
            assertEquals("amount", amount, bb.getAmount(), 0);
            assertEquals("comment", "test" + total, bb.getComment());
            assertNotNull("id", bb.getId());
            assertNotNull("reqid", bb.getRequestId());
            assertNotNull("valuetime", bb.getValuetime());
            assertNull("expiry", bb.getExpirytime());
            assertNull("cancelled", bb.getCancelled());
            assertNull("completed", bb.getCompletedId());
            total++;
        }
        assertEquals("The size doesnt match for: " + book, count, total);

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

    protected Ledger ledger;
    protected LedgerBrowser browser;

}
