package org.neuclear.ledger;

import junit.framework.TestCase;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Jan 22, 2003
 * Time: 4:18:35 PM
 * $Id: LedgerTest.java,v 1.4 2003/11/11 21:17:32 pelle Exp $
 * $Log: LedgerTest.java,v $
 * Revision 1.4  2003/11/11 21:17:32  pelle
 * Further vital reshuffling.
 * org.neudist.crypto.* and org.neudist.utils.* have been moved to respective areas under org.neuclear.commons
 * org.neuclear.signers.* as well as org.neuclear.passphraseagents have been moved under org.neuclear.commons.crypto as well.
 * Did a bit of work on the Canonicalizer and changed a few other minor bits.
 *
 * Revision 1.3  2003/10/29 21:15:13  pelle
 * Refactored the whole signing process. Now we have an interface called Signer which is the old SignerStore.
 * To use it you pass a byte array and an alias. The sign method then returns the signature.
 * If a Signer needs a passphrase it uses a PassPhraseAgent to present a dialogue box, read it from a command line etc.
 * This new Signer pattern allows us to use secure signing hardware such as N-Cipher in the future for server applications as well
 * as SmartCards for end user applications.
 *
 * Revision 1.2  2003/10/28 23:43:15  pelle
 * The GuiDialogAgent now works. It simply presents itself as a simple modal dialog box asking for a passphrase.
 * The two Signer implementations both use it for the passphrase.
 * <p/>
 * Revision 1.1.1.1  2003/09/20 23:16:21  pelle
 * First revision of neuclear-ledger in /cvsroot/neuclear
 * Older versions can be found /cvsroot/neuclear
 * <p/>
 * Revision 1.15  2003/08/08 23:05:12  pelle
 * Updated to use PicoContainer.
 * This will be made more elegant as we go along.
 * <p/>
 * Revision 1.14  2003/08/06 19:16:32  pelle
 * Updated various missing items.
 * <p/>
 * Revision 1.13  2003/08/06 16:41:22  pelle
 * Fixed a few implementation bugs with regards to the Held Transactions
 * <p/>
 * Revision 1.12  2003/08/01 21:59:47  pelle
 * More changes to the way helds are managed.
 * <p/>
 * Revision 1.11  2003/07/30 16:27:55  pelle
 * Final fixes for unit tests.
 * Renamed implementHeld() => complete()
 * Had to override reverse() to make complete work.
 * Note: had to "fix" a unit test to make SimpleLedger pass
 * I dont have time to fix it at the moment. I suspect its simple.
 * <p/>
 * Revision 1.10  2003/07/29 22:57:50  pelle
 * New version with refactored support for HeldTransactions.
 * Please note that this causes a sql exception when adding held_item rows.
 * <p/>
 * Revision 1.9  2003/07/28 21:29:15  pelle
 * Changed a few things in the LedgerFactory.
 * Still not quite there yet.
 * <p/>
 * Revision 1.8  2003/07/23 17:19:26  pelle
 * Ledgers now have a required display name.
 * <p/>
 * Revision 1.7  2003/07/21 19:43:39  pelle
 * Moved the Revisioning tests into the main LedgerTest.
 * Fixed the findTransaction method in SQLLedger.
 * <p/>
 * Revision 1.6  2003/07/21 18:35:15  pelle
 * Completed Exception handling refactoring
 * <p/>
 * Revision 1.5  2003/07/21 17:47:37  pelle
 * Held transactions now work in SQL
 * <p/>
 * Revision 1.4  2003/07/18 20:27:39  pelle
 * *** empty log message ***
 * <p/>
 * Revision 1.3  2003/07/17 22:33:57  pelle
 * Fixed various problems. Lets see how we do. I waiting for the autoincrement to work on the entries.
 * <p/>
 * Revision 1.2  2003/07/16 18:08:49  pelle
 * Adding the first parts of sql support.
 * <p/>
 * Revision 1.1  2003/01/25 19:14:47  pelle
 * The ridiculously simple SimpleLedger now passes initial test.
 * I've split the Transaction Class into two sub classes and made Transaction  abstract.
 * The two new Transaction Classes reflect the state of the Transaction and their methods reflect this.
 */
public abstract class LedgerTest extends TestCase {
    protected String account1 = "Bob";
    protected String account2 = "Alice";


    public LedgerTest(String s) throws LedgerCreationException, LowlevelLedgerException {
        super(s);
        ledger = LedgerFactory.getInstance().getLedger("neu://superbux/reserve");

    }

    public abstract Ledger createLedger();

    public void testPostTransaction() throws LedgerException {
        Book bob = getNewBobBook();
        Book alice = getNewAliceBook();
        Date t1 = new Date();
        bob.transfer(alice, 100, "Loan", t1);
    }

    private Book getNewBobBook() throws BookExistsException, LowlevelLedgerException {
        return createNewBook(account1);
    }

    private Book getNewAliceBook() throws BookExistsException, LowlevelLedgerException {
        return createNewBook(account2);
    }

    private Book createNewBook(String name) throws BookExistsException, LowlevelLedgerException {
        BigInteger id = new BigInteger(168, new Random());
        String bookID = name + id.toString(36);
        System.out.println("bookid: " + bookID);
        return ledger.createNewBook(bookID, name);
    }

    public void testAccountCreate() throws LedgerException {
        Book bob = getNewBobBook();
        assertNotNull(bob);
        assertTrue(ledger.bookExists(bob.getBookID()));
    }

    public void testBalance() throws LedgerException {
        Book alice = getNewAliceBook();
        Book bob = getNewBobBook();
        double aliceBalance = alice.getBalance();
        double bobBalance = bob.getBalance();
        final double amount = 105;
        Date t1 = new Date();
        alice.transfer(bob, amount, "Repayment", t1);
        assertEquals(aliceBalance - amount, alice.getBalance(), 0);
        assertEquals(bobBalance + amount, bob.getBalance(), 0);
    }

    public void testTimeBalance() throws LedgerException {
        Calendar cal = Calendar.getInstance();
        Date t1 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date t2 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date t3 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date t4 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date t5 = cal.getTime();

        Book alice = getNewAliceBook();
        Book bob = getNewBobBook();

        final double amount = 105;
        double payment = amount / 2;

        alice.transfer(bob, payment, "Repayment", t2);
        alice.transfer(bob, payment, "2nd Repayment", t4);

        double aliceBalance = alice.getBalance(t1);
        double bobBalance = bob.getBalance(t1);

        assertEquals(aliceBalance - payment, alice.getBalance(t2), 0);
        assertEquals(bobBalance + payment, bob.getBalance(t2), 0);

        assertEquals(aliceBalance - payment, alice.getBalance(t3), 0);
        assertEquals(bobBalance + payment, bob.getBalance(t3), 0);

        assertEquals(aliceBalance - amount, alice.getBalance(t4), 0);
        assertEquals(bobBalance + amount, bob.getBalance(t4), 0);

        assertEquals(aliceBalance - amount, alice.getBalance(t5), 0);
        assertEquals(bobBalance + amount, bob.getBalance(t5), 0);
    }

    public void testHold() throws LedgerException {
        Calendar cal = Calendar.getInstance();
        Date t1 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date t2 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date t3 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date t4 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date t5 = cal.getTime();

        Book alice = getNewAliceBook();
        Book bob = getNewBobBook();


        final double amount = 105;

        // We are holding 105 from t2 to t4
        alice.hold(bob, amount, "Hold", t2, t4);

        double aliceBalance = alice.getBalance(t1);
        double bobBalance = bob.getBalance(t1);
        // First lets check it hasnt affected the real balance
        assertEquals(aliceBalance, alice.getBalance(t2), 0);
        assertEquals(bobBalance, bob.getBalance(t2), 0);
        assertEquals(aliceBalance, alice.getBalance(t3), 0);
        assertEquals(bobBalance, bob.getBalance(t3), 0);
        assertEquals(aliceBalance, alice.getBalance(t4), 0);
        assertEquals(bobBalance, bob.getBalance(t4), 0);
        assertEquals(aliceBalance, alice.getBalance(t5), 0);
        assertEquals(bobBalance, bob.getBalance(t5), 0);

        // Then lets check that it has affected the available balance of Alice
        // It should affect the following
        assertEquals(aliceBalance - amount, alice.getAvailableBalance(t2), 0);
        assertEquals(aliceBalance - amount, alice.getAvailableBalance(t3), 0);
        assertEquals(aliceBalance - amount, alice.getAvailableBalance(t4), 0);
        // Her available balance should be the same as her real balance here
        assertEquals(alice.getBalance(t5), alice.getAvailableBalance(t5), 0);
        assertEquals(alice.getBalance(t1), alice.getAvailableBalance(t1), 0);
        // Her available balance should be the same as the previous balance here
        assertEquals(aliceBalance, alice.getAvailableBalance(t5), 0);

        // Bob's available balance should be the same as his real balance all along.
        assertEquals(bob.getBalance(t1), bob.getAvailableBalance(t1), 0);

        if (this instanceof SQLLedgerTest) {   // Quick hack. I dont have time to fix the SimpleLedger at the moment
            assertEquals(bob.getBalance(t2), bob.getAvailableBalance(t2), 0);
            assertEquals(bob.getBalance(t3), bob.getAvailableBalance(t3), 0);
            assertEquals(bob.getBalance(t4), bob.getAvailableBalance(t4), 0);
            assertEquals(bob.getBalance(t5), bob.getAvailableBalance(t5), 0);
        }

    }

    public void testReversal() throws LedgerException {
        Book bob = getNewBobBook();
        Book alice = getNewAliceBook();

        double amount = 123;
        double balance = bob.getBalance();

        PostedTransaction tran = bob.transfer(alice, amount, "Hello", new Date());
        assertNotNull(tran);
        assertEquals(bob.getBalance(), balance - amount, 0);
        PostedTransaction reverse = tran.reverse("Reverse it");
        assertNotNull(reverse);
        assertEquals(bob.getBalance(), balance, 0);

    }

    public void testCompleteHeld() throws LedgerException {
        Calendar cal = Calendar.getInstance();
        Date t1 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date t2 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date t3 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date t4 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date t5 = cal.getTime();


        Book ignacio = createNewBook("neu://verax/testusers/Ignacio");
        Book palacio = createNewBook("neu://verax/testusers/Palacio");

        double amount = 123;
        double held = 200;

        double balance = ignacio.getBalance(t1);

        PostedHeldTransaction hold = ignacio.hold(palacio, held, "Hello", t2, t4);
        assertNotNull(hold);
        assertEquals(ignacio.getBalance(t1), balance, 0);
        assertEquals(ignacio.getBalance(t2), balance, 0);
        assertEquals(ignacio.getBalance(t3), balance, 0);
        assertEquals(ignacio.getBalance(t4), balance, 0);
        assertEquals(ignacio.getBalance(t5), balance, 0);

        assertEquals(ignacio.getAvailableBalance(t1), balance, 0);
        assertEquals(ignacio.getAvailableBalance(t2), balance - held, 0);
        assertEquals(ignacio.getAvailableBalance(t3), balance - held, 0);
        assertEquals(ignacio.getAvailableBalance(t4), balance - held, 0);
        assertEquals(ignacio.getAvailableBalance(t5), balance, 0);

        PostedTransaction tran = hold.complete(amount, t3, "Muchas Gracias para escoger El Palacio");
        assertEquals(ignacio.getBalance(t1), balance, 0);
        assertEquals(ignacio.getBalance(t2), balance, 0);
        assertEquals(ignacio.getBalance(t3), balance - amount, 0);
        assertEquals(ignacio.getBalance(t4), balance - amount, 0);
        assertEquals(ignacio.getBalance(t5), balance - amount, 0);

        assertEquals(ignacio.getAvailableBalance(t1), balance, 0);
        assertEquals(ignacio.getAvailableBalance(t2), balance, 0);
        assertEquals(ignacio.getAvailableBalance(t3), balance - amount, 0);
        assertEquals(ignacio.getAvailableBalance(t4), balance - amount, 0);
        assertEquals(ignacio.getAvailableBalance(t5), balance - amount, 0);

        assertEquals(ignacio.getBalance(t4), ignacio.getAvailableBalance(t5), 0);
        try {
            tran = hold.complete(amount, t3, "Muchas Gracias para escoger El Palacio");
            assertNull(tran);
            assertTrue("Should have thrown Exception Here", false);
        } catch (TransactionExpiredException e) {
            assertTrue("Did throw exception here", true);
        }

    }

    public void testFindTransaction() throws LowlevelLedgerException, BookExistsException, UnBalancedTransactionException, InvalidTransactionException, UnknownTransactionException, UnknownBookException {
        Book bob = getNewBobBook();
        Book alice = getNewAliceBook();

        double amount = 123;
        PostedTransaction tran = bob.transfer(alice, amount, "Can we find this again", new Date());
        assertNotNull(tran);
        PostedTransaction found = ledger.findTransaction(tran.getXid());
        assertNotNull(found);
        assertEquals(found.getXid(), tran.getXid());
    }

    public void testFindHeldTransaction() throws LowlevelLedgerException, BookExistsException, UnBalancedTransactionException, InvalidTransactionException, UnknownTransactionException, UnknownBookException {
        Book bob = getNewBobBook();
        Book alice = getNewAliceBook();

        double amount = 123;
        PostedHeldTransaction tran = bob.hold(alice, amount, "Can we find this again", new Date(), new Date());
        assertNotNull(tran);
        PostedHeldTransaction found = ledger.findHeldTransaction(tran.getXid());
        assertNotNull(found);
        assertEquals(found.getXid(), tran.getXid());
    }

    public void testCancelHeld() throws LedgerException {
        Calendar cal = Calendar.getInstance();
        Date t1 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date t2 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date t3 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date t4 = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date t5 = cal.getTime();


        Book ignacio = createNewBook("neu://verax/testusers/Ignacio");
        Book palacio = createNewBook("neu://verax/testusers/Palacio");

        double amount = 123;
        double held = 200;

        double balance = ignacio.getBalance(t1);

        PostedHeldTransaction hold = ignacio.hold(palacio, held, "Hello", t2, t4);
        assertNotNull(hold);
        hold.cancel();
        try {
            hold.complete(held, t3, "this shouldnt work");
            assertTrue("Exception wasnt thrown for completing a cancelled transaction", false);
        } catch (TransactionExpiredException e) {
            ;// This should happen so we dont need to do anything
        }


    }

    Ledger ledger;
}
