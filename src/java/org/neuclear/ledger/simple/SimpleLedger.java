package org.neuclear.ledger.simple;

/**
 * $Id: SimpleLedger.java,v 1.13 2004/04/19 18:57:26 pelle Exp $
 * $Log: SimpleLedger.java,v $
 * Revision 1.13  2004/04/19 18:57:26  pelle
 * Updated Ledger to support more advanced book information.
 * You can now create a book or fetch a book by doing getBook(String id) on the ledger.
 * You can register a book or upddate an existing one using registerBook()
 * SimpleLedger now works and passes all tests.
 * HibernateLedger has been implemented, but there are a few things that dont work yet.
 *
 * Revision 1.12  2004/04/06 22:50:14  pelle
 * Updated Unit Tests
 *
 * Revision 1.11  2004/04/05 22:54:14  pelle
 * API changes in Ledger to support Auditor and CurrencyController in Pay
 *
 * Revision 1.10  2004/04/05 22:06:46  pelle
 * added setHeldReceiptId() method to ledger
 *
 * Revision 1.9  2004/03/31 23:11:09  pelle
 * Reworked the ID's of the transactions. The primary ID is now the request ID.
 * Receipt ID's are optional and added using a separate set method.
 * The various interactive passphrase agents now have shell methods for the new interactive approach.
 *
 * Revision 1.8  2004/03/29 20:05:17  pelle
 * LedgerServlet works now at least for a straight non date restricted browse.
 *
 * Revision 1.7  2004/03/29 16:56:26  pelle
 * AbstractLedgerBrowserTest has been extended to test date ranges
 * SimpleLedger now passes all tests.
 * HibernateLedger passes at times, which is mysterious. More research needed.
 *
 * Revision 1.6  2004/03/26 23:36:34  pelle
 * The simple browse(book) now works on hibernate, I have implemented the other two, which currently don not constrain the query correctly.
 *
 * Revision 1.5  2004/03/26 18:37:56  pelle
 * More work on browsers. Added an AbstractLedgerBrowserTest for unit testing LedgerBrowsers.
 *
 * Revision 1.4  2004/03/25 16:44:21  pelle
 * Added getTestBalance() and isBalanced() to Ledger to see if ledger is balanced.
 * The hibernate implementation has changed the comment size to 255 to work with mysql and now
 * has included hibernates full hibernate.properties to make it easier to try various databases.
 * It has now been tested with hsql and mysql.
 *
 * Revision 1.3  2004/03/22 23:20:50  pelle
 * Working on Hibernate Implementation.
 *
 * Revision 1.2  2004/03/22 21:59:37  pelle
 * SimpleLedger now passes all unit tests
 *
 * Revision 1.1  2004/03/22 20:08:24  pelle
 * Added simple ledger for unit testing and in memory use
 *
 * Revision 1.3  2003/11/21 04:43:20  pelle
 * EncryptedFileStore now works. It uses the PBECipher with DES3 afair.
 * Otherwise You will Finaliate.
 * Anything that can be final has been made final throughout everyting. We've used IDEA's Inspector tool to find all instance of variables that could be final.
 * This should hopefully make everything more stable (and secure).
 *
 * Revision 1.2  2003/11/11 21:17:31  pelle
 * Further vital reshuffling.
 * org.neudist.crypto.* and org.neudist.utils.* have been moved to respective areas under org.neuclear.commons
 * org.neuclear.signers.* as well as org.neuclear.passphraseagents have been moved under org.neuclear.commons.crypto as well.
 * Did a bit of work on the Canonicalizer and changed a few other minor bits.
 *
 * Revision 1.1.1.1  2003/09/20 23:16:20  pelle
 * First revision of neuclear-ledger in /cvsroot/neuclear
 * Older versions can be found /cvsroot/neuclear
 *
 * Revision 1.12  2003/08/06 16:41:22  pelle
 * Fixed a few implementation bugs with regards to the Held Transactions
 *
 * Revision 1.11  2003/08/01 21:59:47  pelle
 * More changes to the way helds are managed.
 *
 * Revision 1.10  2003/07/29 22:57:50  pelle
 * New version with refactored support for HeldTransactions.
 * Please note that this causes a sql exception when adding held_item rows.
 *
 * Revision 1.9  2003/07/23 18:17:52  pelle
 * Added support for display names in the books.
 *
 * Revision 1.8  2003/07/23 17:19:26  pelle
 * Ledgers now have a required display name.
 *
 * Revision 1.7  2003/07/21 18:35:15  pelle
 * Completed Exception handling refactoring
 *
 * Revision 1.6  2003/07/18 20:27:39  pelle
 * *** empty log message ***
 *
 * Revision 1.5  2003/07/17 21:21:08  pelle
 * Most SQLLedger methods have been implemented (Now on to debugging them)
 *
 * Revision 1.4  2003/01/25 23:58:00  pelle
 * Added some new testcases for testing the versioning code.
 * These picked up some errors in SimpleLedger that were fixed.
 *
 * Revision 1.3  2003/01/25 19:14:47  pelle
 * The ridiculously simple SimpleLedger now passes initial test.
 * I've split the Transaction Class into two sub classes and made Transaction  abstract.
 * The two new Transaction Classes reflect the state of the Transaction and their methods reflect this.
 *
 * Revision 1.2  2003/01/18 17:18:56  pelle
 * Added LedgerFactory for creating new Ledger Instances
 *
 * Revision 1.1  2003/01/18 16:17:46  pelle
 * First checkin of the NeuClear Ledger API.
 * This is meant as a standardized super simple API for applications to use when posting transactions to a ledger.
 * Ledger's could be General Ledger's for accounting applications or Bank Account ledger's for financial applications.
 *
 */

import org.neuclear.ledger.*;
import org.neuclear.ledger.browser.BookBrowser;
import org.neuclear.ledger.browser.LedgerBrowser;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This implementation is very simple and only is meant for testing. It uses the Java Collection for the implementation and is in no way
 * thread safe or supportive of transactions.
 */
public class SimpleLedger extends Ledger implements LedgerBrowser {

    public SimpleLedger(final String name) {
        super(name);
        id = name;
        ledger = new HashMap();
        held = new HashMap();
//        balances = new HashMap();
        books = new HashMap();

    }

    /**
     * The basic interface for creating Transactions in the database.
     * The implementing class takes this transacion information and stores it with an automatically generated uniqueid.
     * This id is returned as an identifier of the transaction.
     *
     * @param trans Transaction to perform
     * @return Unique ID
     * @ If there was a problem with the Transaction
     */
    public PostedTransaction performTransaction(final UnPostedTransaction trans) throws UnBalancedTransactionException, InvalidTransactionException {
        final PostedTransaction posted = new PostedTransaction(trans, new Date());
        return post(posted);
    }

    private PostedTransaction post(final PostedTransaction posted) {
        ledger.put(posted.getRequestId(), posted);
        postToBook(posted);
        updateBalances(posted);
        return posted;
    }

    private void postToBook(final PostedTransaction posted) {
        Iterator iter = posted.getItems();
        while (iter.hasNext()) {
            TransactionItem item = (TransactionItem) iter.next();
            if (!books.containsKey(item.getBook().getId()))
                books.put(item.getBook().getId(), new SimpleBook("id", new Date()));
            ((SimpleBook) books.get(item.getBook().getId())).add(posted);
        }
    }

    private void updateBalances(final PostedTransaction trans) {
        if (trans.getReceiptId() == null)
            return;
        synchronized (books) {
            Iterator iter = trans.getItems();
            while (iter.hasNext()) {
                TransactionItem item = (TransactionItem) iter.next();
                addTransactionItem(item);
            }
        }
    }

    private void addTransactionItem(TransactionItem item) {
        ((SimpleBook) books.get(item.getBook().getId())).updateBalance(item.getAmount());
    }

    /**
     * Similar to a transaction but guarantees that there wont be any negative balances left after the transaction.
     *
     * @param trans Transaction to perform
     * @return The reference to the transaction
     */
    public PostedTransaction performVerifiedTransfer(UnPostedTransaction trans) throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException {
        Iterator iter = trans.getItems();
        while (iter.hasNext()) {
            TransactionItem item = (TransactionItem) iter.next();
            if (item.getAmount() < 0 && getAvailableBalance(item.getBook().getId()) + item.getAmount() < 0)
                throw new InsufficientFundsException(this, item.getBook().getId(), item.getAmount());
        }
        return performTransaction(trans);
    }

    /**
     * The basic interface for creating Transactions in the database.
     * The implementing class takes this transacion information and stores it with an automatically generated uniqueid.
     * This id is returned as an identifier of the transaction.
     *
     * @param trans Transaction to perform
     * @return Unique ID
     */
    public PostedHeldTransaction performHeldTransfer(final UnPostedHeldTransaction trans) throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException {
        Iterator iter = trans.getItems();
        while (iter.hasNext()) {
            TransactionItem item = (TransactionItem) iter.next();
            if (item.getAmount() < 0 && getAvailableBalance(item.getBook().getId()) + item.getAmount() < 0)
                throw new InsufficientFundsException(this, item.getBook().getId(), item.getAmount());
        }

        final PostedHeldTransaction posted = new PostedHeldTransaction(trans, new Date());
        held.put(posted.getRequestId(), posted);
        postToBook(posted);
        return posted;
    }

    /**
     * Cancels a Held Transaction.
     *
     * @param hold
     * @throws org.neuclear.ledger.LowlevelLedgerException
     *
     * @throws org.neuclear.ledger.UnknownTransactionException
     *
     */
    public Date performCancelHold(PostedHeldTransaction hold) throws LowlevelLedgerException, UnknownTransactionException {
        if (held.containsKey(hold.getRequestId()))
            held.remove(hold.getRequestId());
        return new Date();
    }

    /**
     * Completes a held transaction. Which means:
     * cancelling the hold and performing the transfer with the given updated amount and comment.
     *
     * @param hold    HeldTransaction to complete
     * @param amount  The updatd amount. It must be <= than the amount of the hold
     * @param comment
     * @return
     * @throws org.neuclear.ledger.InvalidTransactionException
     *
     * @throws org.neuclear.ledger.LowlevelLedgerException
     *
     * @throws org.neuclear.ledger.TransactionExpiredException
     *
     */
    public PostedTransaction performCompleteHold(PostedHeldTransaction hold, double amount, String comment) throws InvalidTransactionException, LowlevelLedgerException, TransactionExpiredException, UnknownTransactionException {
        if (!held.containsKey(hold.getRequestId()))
            throw new UnknownTransactionException(this, hold.getRequestId());
        if (hold.getExpiryTime().before(new Date()))
            throw new TransactionExpiredException(this, hold);
        held.remove(hold.getRequestId());
        PostedTransaction posted = new PostedTransaction(hold, new Date(), amount, comment);
        return post(posted);
    }

    /**
     * Searches for a Transaction based on its Transaction ID
     *
     * @param id A valid ID
     * @return The Transaction object
     */
    public Date getTransactionTime(String id) throws LowlevelLedgerException, UnknownTransactionException {
        return ((PostedTransaction) ledger.get(id)).getTransactionTime();  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Calculate the true accounting balance at a given time. This does not take into account any held transactions, thus may not necessarily
     * show the Available balance.<p>
     * Basic Algorithm:
     * <ol><li>If transactiondate is AFTER balance date SKIP
     * <li>If Transaction DOES NOT have an expiry date its a regular transaction and ADD to balance and SKIP
     * </ol>
     *
     * @return the balance as a double
     */
    public double getBalance(final String book) {
        if (books.containsKey(book))
            return ((SimpleBook) books.get(book)).getBalance();
        return 0;
    }

    /**
     * @return the held balance as a double
     */
    private double getHeldBalance(final String book) {
        double balance = 0;
        Date now = new Date();
        // Very silly slow and lazy implementation
        final Iterator iter = held.keySet().iterator();
        while (iter.hasNext()) {
            final Object o = held.get(iter.next());
            final PostedHeldTransaction tran = (PostedHeldTransaction) o;
            if (now.after(tran.getExpiryTime())) {
                iter.remove();
            } else {
                if (tran.getReceiptId() != null) {
                    final Iterator items = tran.getItems();
                    while (items.hasNext()) {
                        final TransactionItem item = (TransactionItem) items.next();
                        if (item.getBook().getId().equals(book) && item.getAmount() < 0)
                            balance += item.getAmount();
                    }
                }
            }
        }
        return balance;
    }

    /**
     * Calculate the available balance at a given time. This DOES take into account any held transactions.
     * Basic Algorithm:
     * <ol><li>If transactiondate is AFTER balance date SKIP
     * <li>If Transaction DOES NOT have an expiry date its a regular transaction and ADD to balance and SKIP
     * <li>If Transaction DOES HAVE an expiry date it is a held transaction. If Expiry date is AFTER balance date SKIP
     * <li>If Transaction amount is POSITIVE it is a Credit, we dont take held credits into accout on balance SKIP
     * <li>If Transaction Amount is NEGATIVE it is a Debit and ADD to balance
     * </ol>
     *
     * @return the balance as a double
     */
    public double getAvailableBalance(final String book) {
        return getBalance(book) + getHeldBalance(book);
    }

    public boolean transactionExists(String id) throws LowlevelLedgerException {
        return ledger.containsKey(id);
    }

    public boolean heldTransactionExists(String id) throws LowlevelLedgerException {
        return held.containsKey(id);
    }

    /**
     * Register a Book in the system
     *
     * @param id
     * @param nickname
     * @param type
     * @param source
     * @param registrationid
     * @return
     * @throws org.neuclear.ledger.LowlevelLedgerException
     *
     */
    public Book registerBook(String id, String nickname, String type, String source, String registrationid) throws LowlevelLedgerException {
        if (books.containsKey(id)) {
            SimpleBook orig = (SimpleBook) books.get(id);
            final SimpleBook book = new SimpleBook(orig, nickname, type, new Date(), source, registrationid);
            books.put(id, book);
            return book;
        }
        return (Book) books.put(id, new SimpleBook(id, nickname, type, source, new Date(), new Date(), registrationid));
    }

    public Book getBook(String id) throws LowlevelLedgerException {
        Book book = (Book) books.get(id);
        if (book != null) return book;
        book = new SimpleBook(id, new Date());
        books.put(id, book);
        return book;
    }


    public String toString() {
        return "Simple Ledger: " + id;
    }

    /**
     * Searches for a Held Transaction based on its Transaction ID
     *
     * @param idstring A valid ID
     * @return The Transaction object
     */
    public PostedHeldTransaction findHeldTransaction(String idstring) throws LowlevelLedgerException, UnknownTransactionException {
        return (PostedHeldTransaction) held.get(idstring);
    }

    public void setReceiptId(String id, String receipt) throws LowlevelLedgerException, UnknownTransactionException {
        if (!ledger.containsKey(id))
            throw new UnknownTransactionException(this, id);

        final PostedTransaction tran = (PostedTransaction) ledger.get(id);
        if (tran.getReceiptId() == null) {
            tran.setReceiptId(receipt);
            updateBalances(tran);
        }
    }

    public void setHeldReceiptId(String id, String receipt) throws LowlevelLedgerException, UnknownTransactionException {
        if (!held.containsKey(id))
            throw new UnknownTransactionException(this, id);
        final PostedTransaction tran = (PostedTransaction) held.get(id);
        if (tran.getReceiptId() == null)
            tran.setReceiptId(receipt);

    }

    public double getTestBalance() throws LowlevelLedgerException {
        Iterator iter = books.keySet().iterator();
        double test = 0;
        while (iter.hasNext()) {
            String s = (String) iter.next();
            test += ((SimpleBook) books.get(s)).getBalance();
        }
        return test;
    }

    public void close() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public BookBrowser browse(String book) throws LowlevelLedgerException {
        return new SimpleBookBrowser(book);
    }

    public BookBrowser browseFrom(String book, Date from) throws LowlevelLedgerException {
        return new SimpleBookBrowser(book, from);
    }

    public BookBrowser browseRange(String book, Date from, Date until) throws LowlevelLedgerException {
        return new SimpleBookBrowser(book, from, until);
    }

    private final HashMap ledger;
    private final HashMap held;
    private final String id;
//    private final HashMap balances;
    private final HashMap books;

    private class SimpleBookBrowser extends BookBrowser {
        public SimpleBookBrowser(final String book) {
            this(book, null, null);
        }

        public SimpleBookBrowser(final String book, final Date from) {
            this(book, from, null);
        }

        public SimpleBookBrowser(final String book, final Date from, final Date to) {
            super(book);
            this.from = from;
            this.to = to;
            if (books.containsKey(book)) {
                iter = ((SimpleBook) books.get(book)).iterator();
//                System.out.println("book contains: " + ((List)books.get(book)).size());
            } else {
                iter = new Iterator() {
                    public void remove() {

                    }

                    public boolean hasNext() {
                        return false;
                    }

                    public Object next() {
                        return null;
                    }

                };
            }
        }

        public boolean next() throws LowlevelLedgerException {
            if (!iter.hasNext())
                return false;
            PostedTransaction tran = (PostedTransaction) iter.next();
            if (!isValid(tran))
                return next();

            setRow(tran);
            return true;
        }

        private boolean isValid(final PostedTransaction posted) {
            if (from == null)
                return true;
            if (posted.getTransactionTime().after(from)) {
                return (to == null) || posted.getTransactionTime().before(to);
            }
            return false;
        }

        private void setRow(PostedTransaction tran) {
            Iterator iter = tran.getItems();
            TransactionItem item = null;
            Book counterparty = null;
            while (iter.hasNext()) {
                TransactionItem party = (TransactionItem) iter.next();
                if (!party.getBook().getId().equals(getBook())) {
                    counterparty = party.getBook();
                } else {
                    item = party;
                }
            }

            setRow(tran.getRequestId(), counterparty, tran.getComment(), tran.getTransactionTime(), item.getAmount(), null, null, null);
        }

        private final Iterator iter;
        private final Date from;
        private final Date to;
//        private int i=0;
    }


}
