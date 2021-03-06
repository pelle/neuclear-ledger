package org.neuclear.ledger.simple;

/**
 * $Id: SimpleLedgerController.java,v 1.9 2004/07/23 18:55:27 pelle Exp $
 * $Log: SimpleLedgerController.java,v $
 * Revision 1.9  2004/07/23 18:55:27  pelle
 * Added an improved complete method which allows you to specify a book to change to another book when completing a transaction.
 * This is used by the NeuClear Pay Complete Exchange Order process which changes the benificiary book from the agent to the final recipient.
 *
 * Revision 1.8  2004/06/17 15:18:32  pelle
 * Added support for Ledger object within the LedgerController. This is only really implemented in the HibernateLedgerController.
 *
 * Revision 1.7  2004/06/11 22:42:32  pelle
 * Added a new type of BookBrowser which lists transactions beetween two Books.
 *
 * Revision 1.6  2004/05/14 16:23:58  pelle
 * Added PortfolioBrowser to LedgerController and it's implementations.
 *
 * Revision 1.5  2004/05/05 20:46:24  pelle
 * BookListBrowser works both in SimpleLedgerController and HibernateLedgerController
 * Added new interface Browser, which is implemented by both BookBrowser and BookListBrowser
 *
 * Revision 1.4  2004/05/04 23:00:39  pelle
 * Updated SimpleLedgerController to support multiple ledgers as well.
 *
 * Revision 1.3  2004/05/03 23:54:18  pelle
 * HibernateLedgerController now supports multiple ledgers.
 * Fixed many unit tests.
 *
 * Revision 1.2  2004/05/01 00:23:40  pelle
 * Added Ledger field to Transaction as well as to getBalance() and friends.
 *
 * Revision 1.1  2004/04/27 15:23:54  pelle
 * Due to a new API change in 0.5 I have changed the name of Ledger and it's implementers to LedgerController.
 *
 * Revision 1.14  2004/04/22 23:59:21  pelle
 * Added various statistics to Ledger as well as AssetController
 * Improved look and feel in the web app.
 *
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
import org.neuclear.ledger.browser.BookListBrowser;
import org.neuclear.ledger.browser.LedgerBrowser;
import org.neuclear.ledger.browser.PortfolioBrowser;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This implementation is very simple and only is meant for testing. It uses the Java Collection for the implementation and is in no way
 * thread safe or supportive of transactions.
 */
public class SimpleLedgerController extends LedgerController implements LedgerBrowser {

    public SimpleLedgerController(final String name) {
        super(name);
        id = name;
        ledger = new HashMap();
        held = new HashMap();
//        balances = new HashMap();
        books = new HashMap();

    }

    public boolean existsLedger(String id) {
        return false;
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
                addTransactionItem(trans.getLedger(), item);
            }
        }
    }

    private void addTransactionItem(String ledger, TransactionItem item) {
        ((SimpleBook) books.get(item.getBook().getId())).updateBalance(ledger, item.getAmount());
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
            if (item.getAmount() < 0 && getAvailableBalance(trans.getLedger(), item.getBook().getId()) + item.getAmount() < 0)
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
            if (item.getAmount() < 0 && getAvailableBalance(trans.getLedger(), item.getBook().getId()) + item.getAmount() < 0)
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

    public PostedTransaction performCompleteHold(PostedHeldTransaction hold, Book origbook, Book newbook, double amount, String comment) throws InvalidTransactionException, LowlevelLedgerException, TransactionExpiredException, UnknownTransactionException {
        if (!held.containsKey(hold.getRequestId()))
            throw new UnknownTransactionException(this, hold.getRequestId());
        if (hold.getExpiryTime().before(new Date()))
            throw new TransactionExpiredException(this, hold);
        held.remove(hold.getRequestId());
        PostedTransaction posted = new PostedTransaction(hold, origbook, newbook, new Date(), amount, comment);
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
    public double getBalance(String ledger, final String book) {
        if (books.containsKey(book))
            return ((SimpleBook) books.get(book)).getBalance(ledger);
        return 0;
    }

    /**
     * @return the held balance as a double
     */
    private double getHeldBalance(final String ledger, final String book) {
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
                if (tran.getReceiptId() != null && ledger.equals(tran.getLedger())) {
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
    public double getAvailableBalance(String ledger, final String book) {
        return getBalance(ledger, book) + getHeldBalance(ledger, book);
    }

    public long getBookCount(String ledger) throws LowlevelLedgerException {
        return books.size();
    }

    public long getTransactionCount(String ledger) throws LowlevelLedgerException {
        return this.ledger.size();
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

    public Ledger registerLedger(String id, String nickname, String type, String source, String registrationid, String unit, int decimal) throws LowlevelLedgerException {
        return null;
    }

    public Ledger getLedger(String id) throws LowlevelLedgerException, UnknownLedgerException {
        return null;
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

    public double getTestBalance(String ledger) throws LowlevelLedgerException {
        Iterator iter = books.keySet().iterator();
        double test = 0;
        while (iter.hasNext()) {
            String s = (String) iter.next();
            test += ((SimpleBook) books.get(s)).getBalance(ledger);
        }
        return test;
    }

    public void close() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public BookBrowser browse(String ledger, String book) throws LowlevelLedgerException {
        return new SimpleBookBrowser(ledger, book);
    }

    public BookBrowser browseFrom(String ledger, String book, Date from) throws LowlevelLedgerException {
        return new SimpleBookBrowser(ledger, book, from);
    }

    public BookBrowser browseRange(String ledger, String book, Date from, Date until) throws LowlevelLedgerException {
        return new SimpleBookBrowser(ledger, book, from, until);
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

    public BookListBrowser browseBooks(String ledger) throws LowlevelLedgerException {
        return new SimpleBookListBrowser(ledger);
    }

    public PortfolioBrowser browsePortfolio(Book book) throws LowlevelLedgerException {
        return new SimplePortfolioBrowser(book);
    }

    public BookBrowser browseInteractions(String ledger, String book, String counterparty) throws LowlevelLedgerException {
        return new SimpleBookBrowser(ledger, book, counterparty);
    }

    public BookBrowser browseInteractions(String book, String counterparty) throws LowlevelLedgerException {
        return browseInteractions(getId(), book, counterparty);
    }

    public PortfolioBrowser browsePortfolioInteractions(Book book, Book counterparty) throws LowlevelLedgerException {
        return new SimplePortfolioBrowser(book, counterparty);
    }

    private final HashMap ledger;
    private final HashMap held;
    private final String id;
//    private final HashMap balances;
    private final HashMap books;

    private class SimpleBookBrowser extends BookBrowser {
        public SimpleBookBrowser(final String book) {
            this(book, (Date) null);
        }

        public SimpleBookBrowser(final String book, final Date from) {
            this(book, from, null);
        }

        public SimpleBookBrowser(final String ledger, final String book, final String counterparty) {
            this(ledger, book, null, null, counterparty);
        }

        public SimpleBookBrowser(final String ledger, final String book) {
            this(ledger, book, null, null);
        }

        public SimpleBookBrowser(final String ledger, final String book, final Date from) {
            this(ledger, book, from, null);
        }

        public SimpleBookBrowser(final String book, final Date from, final Date to) {
            this(id, book, from, to);
        }

        public SimpleBookBrowser(final String ledger, final String book, final Date from, final Date to) {
            this(ledger, book, from, to, null);
        }

        public SimpleBookBrowser(final String ledger, final String book, final Date from, final Date to, final String counterparty) {
            super(book);
            this.ledger = ledger;
            this.from = from;
            this.to = to;
            this.counterparty = counterparty;
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
            if (!posted.getLedger().equals(ledger))
                return false;
            if (counterparty != null) {
                Iterator iter = posted.getItems();
                boolean iscounterparty = false;
                while (iter.hasNext()) {
                    TransactionItem item = (TransactionItem) iter.next();
                    if (item.getBook().getId().equals(counterparty)) {
                        iscounterparty = true;
                        break;
                    }
                }
                if (iscounterparty == false)
                    return false;
            }
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
        private final String ledger;
        private final String counterparty;
//        private int i=0;
    }

    private class SimpleBookListBrowser extends BookListBrowser {
        public SimpleBookListBrowser(final String ledger) {
            super(ledger);
            iter = books.keySet().iterator();
        }

        public boolean next() throws LowlevelLedgerException {
            if (!iter.hasNext())
                return false;
            SimpleBook book = (SimpleBook) books.get(iter.next());
            if (!isValid(book))
                return next();

            setRow(book);
            return true;
        }

        private boolean isValid(final SimpleBook book) {
            Iterator iter = book.iterator();
            while (iter.hasNext()) {
                Transaction tran = (Transaction) iter.next();
                if (tran.getLedger().equals(ledger))
                    return true;
            }
            return false;
        }

        private void setRow(SimpleBook book) {
            int count = 0;
            Iterator iter = book.iterator();
            while (iter.hasNext()) {
                Transaction tran = (Transaction) iter.next();
                if (tran.getLedger().equals(ledger))
                    count++;
            }

            setRow(book, count, book.getBalance(getLedger()));
        }

        private final Iterator iter;
    }

    private class SimplePortfolioBrowser extends PortfolioBrowser {
        public SimplePortfolioBrowser(final Book book) {
            this(book, null);
        }

        public SimplePortfolioBrowser(final Book book, final Book counterparty) {
            super(book);
            iter = ((SimpleBook) book).ledgerIterator();
            this.counterparty = counterparty;
        }

        public boolean next() throws LowlevelLedgerException {
            if (!iter.hasNext())
                return false;
            String ledger = (String) iter.next();
            setRow(ledger, getCount(ledger), ((SimpleBook) getBook()).getBalance(ledger));
            return true;
        }

        private int getCount(String ledger) {
            int count = 0;
            Iterator iter = ((SimpleBook) getBook()).iterator();
            while (iter.hasNext()) {
                PostedTransaction tran = (PostedTransaction) iter.next();
                if (isValid(ledger, tran))
                    count++;
            }
            return count;
        }

        private boolean isValid(final String ledger, final PostedTransaction posted) {
            if (!posted.getLedger().equals(ledger))
                return false;
            if (counterparty != null) {
                Iterator iter = posted.getItems();
                boolean iscounterparty = false;
                while (iter.hasNext()) {
                    TransactionItem item = (TransactionItem) iter.next();
                    if (item.getBook().getId().equals(counterparty.getId())) {
                        iscounterparty = true;
                        break;
                    }
                }
                if (iscounterparty == false)
                    return false;
            }
            return false;
        }

        private final Iterator iter;
        private final Book counterparty;
    }
}
