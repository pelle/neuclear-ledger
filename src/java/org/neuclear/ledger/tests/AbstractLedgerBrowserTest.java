package org.neuclear.ledger.tests;

import junit.framework.TestCase;
import org.neuclear.commons.crypto.CryptoTools;
import org.neuclear.commons.time.TimeTools;
import org.neuclear.ledger.*;
import org.neuclear.ledger.browser.*;

import java.util.Date;

/*
$Id: AbstractLedgerBrowserTest.java,v 1.13 2004/06/11 22:42:32 pelle Exp $
$Log: AbstractLedgerBrowserTest.java,v $
Revision 1.13  2004/06/11 22:42:32  pelle
Added a new type of BookBrowser which lists transactions beetween two Books.

Revision 1.12  2004/05/14 16:23:58  pelle
Added PortfolioBrowser to LedgerController and it's implementations.

Revision 1.11  2004/05/05 20:46:24  pelle
BookListBrowser works both in SimpleLedgerController and HibernateLedgerController
Added new interface Browser, which is implemented by both BookBrowser and BookListBrowser

Revision 1.10  2004/05/04 23:00:39  pelle
Updated SimpleLedgerController to support multiple ledgers as well.

Revision 1.9  2004/05/03 23:54:18  pelle
HibernateLedgerController now supports multiple ledgers.
Fixed many unit tests.

Revision 1.8  2004/04/27 15:23:54  pelle
Due to a new API change in 0.5 I have changed the name of Ledger and it's implementers to LedgerController.

Revision 1.7  2004/04/23 19:09:15  pelle
Lots of cleanups and improvements to the userinterface and look of the bux application.

Revision 1.6  2004/04/20 00:15:32  pelle
Updated test to use books

Revision 1.5  2004/04/19 18:57:27  pelle
Updated Ledger to support more advanced book information.
You can now create a book or fetch a book by doing getBook(String id) on the ledger.
You can register a book or upddate an existing one using registerBook()
SimpleLedger now works and passes all tests.
HibernateLedger has been implemented, but there are a few things that dont work yet.

Revision 1.4  2004/03/31 23:11:09  pelle
Reworked the ID's of the transactions. The primary ID is now the request ID.
Receipt ID's are optional and added using a separate set method.
The various interactive passphrase agents now have shell methods for the new interactive approach.

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

    public abstract LedgerController getLedger() throws LowlevelLedgerException;

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
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
            ;
        }
        final Date t = new Date();
        System.out.println(TimeTools.formatTimeStamp(t));
        try {
            Thread.currentThread().sleep(1000);
        } catch (InterruptedException e) {
            ;
        }
        return t;
    }

    public void testAmountOfEntries() throws LowlevelLedgerException, InvalidTransactionException, UnknownBookException {
        final String bob = getBobBook();
        final String alice = getAliceBook();

        assertBrowserSize(bob, 0, browser.browse(bob));
        assertBrowserSize(alice, 0, browser.browse(alice));
        int i;
        for (i = 0; i < 10; i++) {
            ledger.transfer(bob, alice, 10, "test" + i);
        }
        assertBrowserSize(bob, i, browser.browse(bob));
        assertBrowserSize(alice, i, browser.browse(alice));
    }

    public void testEntryContent() throws LowlevelLedgerException, InvalidTransactionException, UnknownBookException {
        final String bob = getBobBook();
        final String alice = getAliceBook();

        assertBrowserSize(bob, 0, browser.browse(bob));
        assertBrowserSize(alice, 0, browser.browse(alice));
        int i;
        for (i = 0; i < 10; i++) {
            ledger.transfer(bob, alice, 10, "test" + i);
        }
        assertVerifyBookBrowserContent(bob, alice, -10, i, browser.browse(bob));
        assertVerifyBookBrowserContent(alice, bob, 10, i, browser.browse(alice));


    }

    public void testAmountOfEntriesFromTime() throws LowlevelLedgerException, InvalidTransactionException, UnknownBookException {
        final String bob = getBobBook();
        final String alice = getAliceBook();

        Date t1 = getIsolatedTimeStamp();
        assertBrowserSize(bob, 0, browser.browse(bob));
        assertBrowserSize(alice, 0, browser.browse(alice));
        int i;
        for (i = 0; i < 10; i++) {
            ledger.transfer(bob, alice, 10, "test" + i);
        }
        assertBrowserSize(bob, i, browser.browse(bob));
        assertBrowserSize(alice, i, browser.browse(alice));

        Date t2 = getIsolatedTimeStamp();
        assertTrue(t2.after(t1));

        for (i = 0; i < 10; i++) {
            ledger.transfer(bob, alice, 10, "test" + i);
        }
        Date t3 = getIsolatedTimeStamp();
        assertTrue(t3.after(t2));
        assertBrowserSize(bob, 20, browser.browse(bob));
        assertBrowserSize(alice, 20, browser.browse(alice));

        assertBrowserSize(bob, 20, browser.browseFrom(bob, t1));
        assertBrowserSize(alice, 20, browser.browseFrom(alice, t1));

        assertBrowserSize(bob, 0, browser.browseFrom(bob, t3));
        assertBrowserSize(alice, 0, browser.browseFrom(alice, t3));

        assertBrowserSize(bob, i, browser.browseFrom(bob, t2));
        assertBrowserSize(alice, i, browser.browseFrom(alice, t2));

    }

    public void testAmountOfEntriesInTimeRange() throws LowlevelLedgerException, InvalidTransactionException, UnknownBookException {
        final String bob = getBobBook();
        final String alice = getAliceBook();

        Date t1 = getIsolatedTimeStamp();
        assertBrowserSize(bob, 0, browser.browse(bob));
        assertBrowserSize(alice, 0, browser.browse(alice));
        int i;
        for (i = 0; i < 10; i++) {
            ledger.transfer(bob, alice, 10, "t1-t2: " + i);
        }
        assertBrowserSize(bob, i, browser.browse(bob));
        assertBrowserSize(alice, i, browser.browse(alice));

        Date t2 = getIsolatedTimeStamp();

        for (i = 0; i < 10; i++) {
            ledger.transfer(bob, alice, 10, "t2-t3: " + i);
        }
        assertBrowserSize(bob, 20, browser.browse(bob));
        assertBrowserSize(alice, 20, browser.browse(alice));
        Date t3 = getIsolatedTimeStamp();

        for (i = 0; i < 10; i++) {
            ledger.transfer(bob, alice, 10, "t3-t4: " + i);
        }
        Date t4 = getIsolatedTimeStamp();
        assertBrowserSize(bob, 30, browser.browse(bob));
        assertBrowserSize(alice, 30, browser.browse(alice));

        assertBrowserSize(bob, 10, browser.browseRange(bob, t3, t4));
        assertBrowserSize(alice, 10, browser.browseRange(alice, t3, t4));
        assertBrowserSize(bob, 10, browser.browseRange(bob, t2, t3));
        assertBrowserSize(alice, 10, browser.browseRange(alice, t2, t3));
        assertBrowserSize(bob, 10, browser.browseRange(bob, t1, t2));
        assertBrowserSize(alice, 10, browser.browseRange(alice, t1, t2));


        assertBrowserSize(bob, 20, browser.browseRange(bob, t1, t3));
        assertBrowserSize(alice, 20, browser.browseRange(alice, t1, t3));
        assertBrowserSize(bob, 20, browser.browseRange(bob, t2, t4));
        assertBrowserSize(alice, 20, browser.browseRange(alice, t2, t4));

        assertBrowserSize(bob, 30, browser.browseRange(bob, t1, t4));
        assertBrowserSize(alice, 30, browser.browseRange(alice, t1, t4));

    }

    public void testBookList() throws LowlevelLedgerException, UnknownBookException, InvalidTransactionException {
        final String bob = getBobBook();
        final String alice = getAliceBook();
        final String carol = getCarolBook();

        assertBrowserSize("test", 0, browser.browseBooks("test"));
        ledger.transfer(bob, alice, 10, "test");
        assertBrowserSize("test", 2, browser.browseBooks("test"));
        ledger.transfer(bob, alice, 10, "test");
        assertBrowserSize("test", 2, browser.browseBooks("test"));
        ledger.transfer(alice, carol, 20, "test");
        assertBrowserSize("test", 3, browser.browseBooks("test"));

        assertBrowserSize("other", 0, browser.browseBooks("other"));
        ledger.transfer("other", alice, carol, 20, "test");
        assertBrowserSize("test", 3, browser.browseBooks("test"));
        assertBrowserSize("other", 2, browser.browseBooks("other"));

        BookListBrowser bl = browser.browseBooks("other");
        assertTrue(bl.next());
        assertNotNull(bl.getBook());
        assertNotNull(bl.getBook().getNickname());
        assertEquals("other", bl.getLedger());
        assertEquals(1, bl.getCount());
        assertEquals(20, Math.abs(bl.getBalance()), 0);
        assertTrue(bl.next());
        assertNotNull(bl.getBook());
        assertNotNull(bl.getBook().getNickname());
        assertEquals("other", bl.getLedger());
        assertEquals(1, bl.getCount());
        assertEquals(20, Math.abs(bl.getBalance()), 0);
        assertFalse(bl.next());
    }

    public void testPortfolio() throws LowlevelLedgerException, UnknownBookException, InvalidTransactionException {
        final Book bob = ledger.getBook(getBobBook());
        final Book alice = ledger.getBook(getAliceBook());
        final Book carol = ledger.getBook(getCarolBook());

        assertBrowserSize("bob", 0, browser.browsePortfolio(bob));
        assertBrowserSize("alice", 0, browser.browsePortfolio(alice));
        assertBrowserSize("carol", 0, browser.browsePortfolio(carol));
        ledger.transfer("bux", bob.getId(), alice.getId(), 10, "test");
        assertBrowserSize("bob", 1, browser.browsePortfolio(bob));
        assertBrowserSize("alice", 1, browser.browsePortfolio(alice));
        assertBrowserSize("carol", 0, browser.browsePortfolio(carol));
        ledger.transfer("shoes", alice.getId(), carol.getId(), 10, "test");
        assertBrowserSize("bob", 1, browser.browsePortfolio(bob));
        assertBrowserSize("alice", 2, browser.browsePortfolio(alice));
        assertBrowserSize("carol", 1, browser.browsePortfolio(carol));

        PortfolioBrowser portfolio = browser.browsePortfolio(alice);
        assertTrue(portfolio.next());
        assertNotNull(portfolio.getBook());
        assertEquals(alice.getId(), portfolio.getBook().getId());
        assertNotNull(portfolio.getLedger());
        assertEquals(1, portfolio.getCount());
        assertEquals(10, Math.abs(portfolio.getBalance()), 0);
        assertTrue(portfolio.next());
        assertNotNull(portfolio.getBook());
        assertEquals(alice.getId(), portfolio.getBook().getId());
        assertNotNull(portfolio.getLedger());
        assertEquals(1, portfolio.getCount());
        assertEquals(10, Math.abs(portfolio.getBalance()), 0);
        assertFalse(portfolio.next());

    }

    public void testBookInteraction() throws LowlevelLedgerException, InvalidTransactionException, UnknownBookException {
        final String bob = getBobBook();
        final String alice = getAliceBook();
        final String carol = getCarolBook();

        assertBrowserSize(bob, 0, browser.browse(bob));
        assertBrowserSize(alice, 0, browser.browse(alice));
        assertBrowserSize(alice, 0, browser.browse(carol));
        assertBrowserSize(bob, 0, browser.browseInteractions(bob, alice));
        assertBrowserSize(bob, 0, browser.browseInteractions(bob, carol));
        assertBrowserSize(alice, 0, browser.browseInteractions(alice, bob));
        assertBrowserSize(alice, 0, browser.browseInteractions(alice, carol));
        assertBrowserSize(carol, 0, browser.browseInteractions(carol, bob));
        assertBrowserSize(carol, 0, browser.browseInteractions(carol, alice));


        ledger.transfer(bob, alice, 10, "test");
        assertBrowserSize(bob, 1, browser.browseInteractions(bob, alice));
        assertBrowserSize(alice, 1, browser.browseInteractions(alice, bob));
        assertBrowserSize(bob, 0, browser.browseInteractions(bob, carol));
        ledger.transfer(bob, alice, 10, "test");
        assertBrowserSize(bob, 2, browser.browseInteractions(bob, alice));
        assertBrowserSize(alice, 2, browser.browseInteractions(alice, bob));
        assertBrowserSize(bob, 0, browser.browseInteractions(bob, carol));
        ledger.transfer(alice, carol, 20, "test");
        assertBrowserSize(bob, 2, browser.browseInteractions(bob, alice));
        assertBrowserSize(alice, 1, browser.browseInteractions(alice, carol));
        assertBrowserSize(carol, 1, browser.browseInteractions(carol, alice));
        assertBrowserSize(bob, 0, browser.browseInteractions(bob, carol));
        assertBrowserSize(bob, 2, browser.browse(bob));
        assertBrowserSize(alice, 3, browser.browse(alice));
        assertBrowserSize(alice, 1, browser.browse(carol));


    }

    public void testPortfolioInteraction() throws LowlevelLedgerException, InvalidTransactionException, UnknownBookException {
        final Book bob = ledger.getBook(getBobBook());
        final Book alice = ledger.getBook(getAliceBook());
        final Book carol = ledger.getBook(getCarolBook());

        assertBrowserSize(bob.toString(), 0, browser.browsePortfolio(bob));
        assertBrowserSize(alice.toString(), 0, browser.browsePortfolio(alice));
        assertBrowserSize(carol.toString(), 0, browser.browsePortfolio(carol));
        assertBrowserSize(bob.toString(), 0, browser.browsePortfolioInteractions(bob, alice));
        assertBrowserSize(bob.toString(), 0, browser.browsePortfolioInteractions(bob, carol));
        assertBrowserSize(alice.toString(), 0, browser.browsePortfolioInteractions(alice, bob));
        assertBrowserSize(alice.toString(), 0, browser.browsePortfolioInteractions(alice, carol));
        assertBrowserSize(carol.toString(), 0, browser.browsePortfolioInteractions(carol, bob));
        assertBrowserSize(carol.toString(), 0, browser.browsePortfolioInteractions(carol, alice));


        ledger.transfer("a", bob.getId(), alice.getId(), 10, "test");
        assertBrowserSize(bob.toString(), 1, browser.browsePortfolioInteractions(bob, alice));
        assertBrowserSize(alice.toString(), 1, browser.browsePortfolioInteractions(alice, bob));
        assertBrowserSize(bob.toString(), 0, browser.browsePortfolioInteractions(bob, carol));
        ledger.transfer("b", bob.getId(), alice.getId(), 10, "test");
        assertBrowserSize(bob.toString(), 2, browser.browsePortfolioInteractions(bob, alice));
        assertBrowserSize(alice.toString(), 2, browser.browsePortfolioInteractions(alice, bob));
        assertBrowserSize(bob.toString(), 0, browser.browsePortfolioInteractions(bob, carol));
        ledger.transfer("b", alice.getId(), carol.getId(), 10, "test");
        assertBrowserSize(alice.toString(), 1, browser.browsePortfolioInteractions(alice, carol));
        assertBrowserSize(carol.toString(), 1, browser.browsePortfolioInteractions(carol, alice));
        assertBrowserSize(bob.toString(), 2, browser.browsePortfolioInteractions(bob, alice));
        assertBrowserSize(alice.toString(), 2, browser.browsePortfolioInteractions(alice, bob));
        assertBrowserSize(bob.toString(), 0, browser.browsePortfolioInteractions(bob, carol));


    }


    public void assertVerifyBookBrowserContent(final String book, final String counterparty, final double amount, final int count, final BookBrowser bb) throws LowlevelLedgerException {
        assertNotNull("null book browser for " + book, bb);
        int total = 0;
        while (bb.next()) {
            assertEquals("book", book, bb.getBook());
            assertEquals("counterparty", counterparty, bb.getCounterparty().getId());
            assertEquals("amount", amount, bb.getAmount(), 0);
            assertEquals("comment", "test" + total, bb.getComment());
            assertNotNull("request", bb.getRequestId());
            assertNotNull("valuetime", bb.getValuetime());
            assertNull("expiry", bb.getExpirytime());
            assertNull("cancelled", bb.getCancelled());
            assertNull("completed", bb.getCompletedId());
            total++;
        }
        assertEquals("The size doesnt match for: " + book, count, total);

    }

    public void assertBrowserSize(final String book, final int count, final Browser bb) throws LowlevelLedgerException {
        assertNotNull("null browser for " + book, bb);
        int total = 0;
        while (bb.next()) {
            total++;
        }
        assertEquals("The size doesnt match for: " + book, count, total);
    }

    public String getNewBook(String root) {
        return (root + CryptoTools.createRandomID()).substring(0, 20);
    }

    public String getBobBook() {
        return getNewBook("roberto");
    }

    public String getAliceBook() {
        return getNewBook("alicia");
    }

    public String getCarolBook() {
        return getNewBook("carol");
    }

    protected LedgerController ledger;
    protected LedgerBrowser browser;

}
