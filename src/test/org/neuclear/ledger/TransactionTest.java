package org.neuclear.ledger;

import junit.framework.TestCase;

import java.util.Date;

/*
NeuClear Distributed Transaction Clearing Platform
(C) 2003 Pelle Braendgaard

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

$Id: TransactionTest.java,v 1.4 2004/05/03 23:54:19 pelle Exp $
$Log: TransactionTest.java,v $
Revision 1.4  2004/05/03 23:54:19  pelle
HibernateLedgerController now supports multiple ledgers.
Fixed many unit tests.

Revision 1.3  2004/05/01 00:23:40  pelle
Added Ledger field to Transaction as well as to getBalance() and friends.

Revision 1.2  2004/04/19 18:57:27  pelle
Updated Ledger to support more advanced book information.
You can now create a book or fetch a book by doing getBook(String id) on the ledger.
You can register a book or upddate an existing one using registerBook()
SimpleLedger now works and passes all tests.
HibernateLedger has been implemented, but there are a few things that dont work yet.

Revision 1.1  2004/03/31 23:11:10  pelle
Reworked the ID's of the transactions. The primary ID is now the request ID.
Receipt ID's are optional and added using a separate set method.
The various interactive passphrase agents now have shell methods for the new interactive approach.

*/

/**
 * User: pelleb
 * Date: Mar 30, 2004
 * Time: 10:33:14 PM
 */
public class TransactionTest extends TestCase {
    public TransactionTest(String s) {
        super(s);
    }

    public void testUnposted() throws InvalidTransactionException {
        UnPostedTransaction tran = new UnPostedTransaction("digbat", "1234", "test");
        assertNotNull(tran);
        assertEquals("1234", tran.getRequestId());
        assertEquals("test", tran.getComment());
        assertNotNull(tran.getItemList());
        assertEquals(0, tran.getItemList().size());
        assertEquals("digbat", tran.getLedger());

        tran.addItem(BOB, 10);
        assertEquals(1, tran.getItemList().size());

        assertFalse(tran.isBalanced());

        tran.addItem(ALICE, -10);
        assertEquals(2, tran.getItemList().size());

        assertTrue(tran.isBalanced());

        tran.addItem(ALICE, -10);
        assertEquals(3, tran.getItemList().size());
        assertFalse(tran.isBalanced());

    }

    public void testPostedBalanced() throws InvalidTransactionException {
        UnPostedTransaction tran = new UnPostedTransaction("digbat", "1234", "test");
        assertNotNull(tran);
        assertEquals("1234", tran.getRequestId());
        assertEquals("test", tran.getComment());
        assertNotNull(tran.getItemList());
        assertEquals(0, tran.getItemList().size());

        tran.addItem(BOB, 10);
        tran.addItem(ALICE, -10);

        assertTrue(tran.isBalanced());
        final Date time = new Date();
        PostedTransaction posted = new PostedTransaction(tran, time);

        assertNotNull(posted);
        assertEquals("1234", posted.getRequestId());
        assertEquals("test", posted.getComment());
        assertNotNull(posted.getItemList());
        assertEquals(2, posted.getItemList().size());
        assertNotNull(posted.getTransactionTime());
        assertEquals(time.getTime(), posted.getTransactionTime().getTime());
        assertEquals("digbat", posted.getLedger());

        assertNull(posted.getReceiptId());
        posted.setReceiptId("2345");
        assertNotNull(posted.getReceiptId());
        assertEquals("2345", posted.getReceiptId());
    }

    public void testPostedUnBalancedDoesFail() throws InvalidTransactionException {
        UnPostedTransaction tran = new UnPostedTransaction("digbat", "1234", "test");
        assertNotNull(tran);
        assertEquals("1234", tran.getRequestId());
        assertEquals("test", tran.getComment());
        assertNotNull(tran.getItemList());
        assertEquals(0, tran.getItemList().size());

        tran.addItem(BOB, 10);
        tran.addItem(ALICE, -30);

        assertFalse(tran.isBalanced());
        final Date time = new Date();

        try {
            PostedTransaction posted = new PostedTransaction(tran, time);
            assertTrue(false);
        } catch (UnBalancedTransactionException e) {
            assertTrue(true);
        }

    }

    public void testUnpostedHeld() throws InvalidTransactionException {

        final Date time = new Date();
        UnPostedHeldTransaction tran = new UnPostedHeldTransaction("digbat", "1234", "test", time);
        assertNotNull(tran);
        assertEquals("1234", tran.getRequestId());
        assertEquals("test", tran.getComment());

        assertEquals(time, tran.getExpiryTime());
        assertEquals("digbat", tran.getLedger());


        assertNotNull(tran.getItemList());
        assertEquals(0, tran.getItemList().size());

        tran.addItem(BOB, 10);
        assertEquals(1, tran.getItemList().size());

        assertFalse(tran.isBalanced());

        tran.addItem(ALICE, -10);
        assertEquals(2, tran.getItemList().size());

        assertTrue(tran.isBalanced());

        tran.addItem(ALICE, -10);
        assertEquals(3, tran.getItemList().size());
        assertFalse(tran.isBalanced());

    }

    public void testPosteHelddBalanced() throws InvalidTransactionException {
        final Date time = new Date();
        UnPostedHeldTransaction tran = new UnPostedHeldTransaction("digbat", "1234", "test", time);
        assertNotNull(tran);
        assertEquals("1234", tran.getRequestId());
        assertEquals("test", tran.getComment());

        assertEquals(time, tran.getExpiryTime());
        assertNotNull(tran.getItemList());
        assertEquals(0, tran.getItemList().size());

        tran.addItem(BOB, 10);
        tran.addItem(ALICE, -10);
        assertEquals(10.0, tran.getAmount(), 0);

        assertTrue(tran.isBalanced());
        PostedHeldTransaction posted = new PostedHeldTransaction(tran, time);

        assertNotNull(posted);
        assertEquals("1234", posted.getRequestId());
        assertEquals("test", posted.getComment());
        assertEquals(time, posted.getExpiryTime());
        assertEquals("digbat", posted.getLedger());

        assertNotNull(posted.getItemList());
        assertEquals(2, posted.getItemList().size());
        assertNotNull(posted.getTransactionTime());
        assertEquals(time.getTime(), posted.getTransactionTime().getTime());

        assertNull(posted.getReceiptId());
        posted.setReceiptId("2345");
        assertNotNull(posted.getReceiptId());
        assertEquals("2345", posted.getReceiptId());
    }

    public void testPostedHeldUnBalancedDoesFail() throws InvalidTransactionException {
        final Date time = new Date();
        UnPostedHeldTransaction tran = new UnPostedHeldTransaction("digbat", "1234", "test", time);
        assertNotNull(tran);
        assertEquals("1234", tran.getRequestId());
        assertEquals("test", tran.getComment());

        assertEquals(time, tran.getExpiryTime());

        assertNotNull(tran.getItemList());
        assertEquals(0, tran.getItemList().size());

        tran.addItem(BOB, 10);
        tran.addItem(ALICE, -30);

        assertFalse(tran.isBalanced());

        try {
            PostedHeldTransaction posted = new PostedHeldTransaction(tran, time);
            assertTrue(false);
        } catch (UnBalancedTransactionException e) {
            assertTrue(true);
        }

    }

    public void testPostedHeldCompleted() throws InvalidTransactionException {
        final Date time = new Date();
        UnPostedHeldTransaction tran = new UnPostedHeldTransaction("digbat", "1234", "test", time);
        assertNotNull(tran);
        assertEquals("1234", tran.getRequestId());
        assertEquals("test", tran.getComment());

        assertEquals(time, tran.getExpiryTime());
        assertNotNull(tran.getItemList());
        assertEquals(0, tran.getItemList().size());

        tran.addItem(BOB, 10);
        tran.addItem(ALICE, -10);

        assertEquals(10.0, tran.getAmount(), 0);
        assertTrue(tran.isBalanced());
        PostedHeldTransaction held = new PostedHeldTransaction(tran, time);

        assertNotNull(held);
        Date time2 = new Date();
        try {
            PostedTransaction posted = new PostedTransaction(held, time2, 15, "complete");
            assertFalse("Didnt throw Exceeded Amount Transaction ", true);
        } catch (ExceededHeldAmountException e) {
            ;
        }

        PostedTransaction posted = new PostedTransaction(held, time2, 5, "complete");

        assertNotNull(posted);
        assertEquals("1234", posted.getRequestId());
        assertEquals("complete", posted.getComment());
        assertNotNull(posted.getItemList());
        assertEquals(2, posted.getItemList().size());
        assertNotNull(posted.getTransactionTime());
        assertEquals(time2.getTime(), posted.getTransactionTime().getTime());
        assertEquals(5.0, posted.getAmount(), 0);
        assertEquals("digbat", posted.getLedger());


        assertNull(posted.getReceiptId());
        posted.setReceiptId("2345");
        assertNotNull(posted.getReceiptId());
        assertEquals("2345", posted.getReceiptId());

        posted = new PostedTransaction(held, time2, 10, "complete");

        assertNotNull(posted);
        assertEquals("1234", posted.getRequestId());
        assertEquals("complete", posted.getComment());
        assertNotNull(posted.getItemList());
        assertEquals(2, posted.getItemList().size());
        assertNotNull(posted.getTransactionTime());
        assertEquals(time2.getTime(), posted.getTransactionTime().getTime());
        assertEquals(10.0, posted.getAmount(), 0);

        assertNull(posted.getReceiptId());
        posted.setReceiptId("2345");
        assertNotNull(posted.getReceiptId());
        assertEquals("2345", posted.getReceiptId());

    }

    private static final Book BOB = new Book("bob", new Date());
    private static final Book ALICE = new Book("alice", new Date());
}
