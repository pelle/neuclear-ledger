package org.neuclear.ledger;

/**
 * $Id: Ledger.java,v 1.4 2003/10/28 23:43:14 pelle Exp $
 * $Log: Ledger.java,v $
 * Revision 1.4  2003/10/28 23:43:14  pelle
 * The PassPhraseDialogue now works. It simply presents itself as a simple modal dialog box asking for a passphrase.
 * The two SignerStore implementations both use it for the passphrase.
 *
 * Revision 1.3  2003/10/25 00:39:05  pelle
 * Fixed SmtpSender it now sends the messages.
 * Refactored CommandLineSigner. Now it simply signs files read from command line. However new class IdentityCreator
 * is subclassed and creates new Identities. You can subclass CommandLineSigner to create your own variants.
 * Several problems with configuration. Trying to solve at the moment. Updated PicoContainer to beta-2
 *
 * Revision 1.2  2003/10/01 17:35:53  pelle
 * Made as much as possible immutable for security and reliability reasons.
 * The only thing that isnt immutable are the items and balance of the
 * UnpostedTransaction
 *
 * Revision 1.1.1.1  2003/09/20 23:16:19  pelle
 * First revision of neuclear-ledger in /cvsroot/neuclear
 * Older versions can be found /cvsroot/neudist
 *
 * Revision 1.13  2003/08/15 22:39:22  pelle
 * Introducing new neuclear-commons project.
 * The commons project will have all the non application specific common stuff such as database connectivity, configuration etc.
 * Did various refactorings to support this.
 *
 * Revision 1.12  2003/08/14 19:12:28  pelle
 * Configuration is now done in an xml file neuclear-conf.xml
 *
 * Revision 1.11  2003/08/06 16:41:21  pelle
 * Fixed a few implementation bugs with regards to the Held Transactions
 *
 * Revision 1.10  2003/08/01 21:59:41  pelle
 * More changes to the way helds are managed.
 *
 * Revision 1.9  2003/07/29 22:57:44  pelle
 * New version with refactored support for HeldTransactions.
 * Please note that this causes a sql exception when adding held_item rows.
 *
 * Revision 1.8  2003/07/23 18:17:44  pelle
 * Added support for display names in the books.
 *
 * Revision 1.7  2003/07/23 17:19:26  pelle
 * Ledgers now have a required display name.
 *
 * Revision 1.6  2003/07/21 18:35:13  pelle
 * Completed Exception handling refactoring
 *
 * Revision 1.5  2003/07/18 20:27:39  pelle
 * *** empty log message ***
 *
 * Revision 1.4  2003/07/17 22:33:57  pelle
 * Fixed various problems. Lets see how we do. I waiting for the autoincrement to work on the entries.
 *
 * Revision 1.3  2003/07/17 21:21:17  pelle
 * Most SQLLedger methods have been implemented (Now on to debugging them)
 *
 * Revision 1.2  2003/01/25 19:14:47  pelle
 * The ridiculously simple SimpleLedger now passes initial test.
 * I've split the Transaction Class into two sub classes and made Transaction  abstract.
 * The two new Transaction Classes reflect the state of the Transaction and their methods reflect this.
 *
 * Revision 1.1  2003/01/18 16:17:46  pelle
 * First checkin of the NeuClear Ledger API.
 * This is meant as a standardized super simple API for applications to use when posting transactions to a ledger.
 * Ledger's could be General Ledger's for accounting applications or Bank Account ledger's for financial applications.
 *
 * Revision 1.1  2003/01/16 23:03:28  pelle
 * Updated Ledger Architecture.
 * We now have a Ledger class and a stubbed out SimpleLedger implementation.
 * Book is now final. Everything it needs to access it's data is defined as abstract methods in Ledger.
 *
 * We still need a Ledger Factory and to actually implement SimpleLedger with a database.
 *
 * User: pelleb
 * Date: Jan 16, 2003
 * Time: 5:31:53 PM
 */

import org.neuclear.commons.configuration.Configuration;
import org.neuclear.commons.configuration.ConfigurationException;

import java.util.Date;
import java.util.Iterator;

/**
 * This is the abstract Ledger class that implementators of the NeuClear Ledger need to implement.
 */
public abstract class Ledger {

    /**
     * The unique id of the ledger
     * 
     * @param id 
     */
    public Ledger(String id, String name) {
        this.name = name;
        this.id = id;
    }

    /**
     * Default implementation allows for new Books to be created on the fly. If you need control over this. Over ride.
     * 
     * @param bookID 
     * @return 
     */
    public abstract Book getBook(String bookID) throws UnknownBookException, LowlevelLedgerException;

    /**
     * Used by implementations to securely create Book Instances
     * 
     * @param bookID 
     * @return Valid Book instance
     */
    protected Book createBookInstance(String bookID, String name) {
        return new Book(bookID, name, this);
    }

    public abstract boolean bookExists(String bookID) throws LowlevelLedgerException;

    public abstract Book createNewBook(String bookID, String title) throws BookExistsException, LowlevelLedgerException;

    public Book createNewBook(String bookID) throws BookExistsException, LowlevelLedgerException {
        return createNewBook(bookID, "Unnamed Account");
    }


    /**
     * The basic interface for creating Transactions in the database.
     * The implementing class takes this transacion information and stores it with an automatically generated uniqueid.
     * This id is returned as an identifier of the transaction.
     * 
     * @param trans Transaction to perform
     * @return Unique ID
     */
    public abstract PostedTransaction performTransaction(UnPostedTransaction trans) throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException;

    /**
     * The basic interface for creating Transactions in the database.
     * The implementing class takes this transacion information and stores it with an automatically generated uniqueid.
     * This id is returned as an identifier of the transaction.
     * 
     * @param trans Transaction to perform
     * @return Unique ID
     */
    public abstract PostedHeldTransaction performHeldTransaction(UnPostedHeldTransaction trans) throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException;

    /**
     * Searches for a Transaction based on its Transaction ID
     * 
     * @param id A valid ID
     * @return The Transaction object
     */
    public abstract PostedTransaction findTransaction(String id) throws LowlevelLedgerException, UnknownTransactionException, InvalidTransactionException, UnknownBookException;

    /**
     * Calculate the true accounting balance at a given time. This does not take into account any held transactions, thus may not necessarily
     * show the Available balance.<p>
     * Example sql for implementors: <pre>
     * select c.credit - d.debit from
     *      (
     *          select sum(amount) as credit
     *          from ledger
     *          where transactiondate <= sysdate and end_date is null and credit= 'neu://bob'
     *       ) c,
     *      (
     *          select sum(amount) as debit
     *          from ledger
     *          where transactiondate <= sysdate and end_date is null and debit= 'neu://bob'
     *       ) d
     * <p/>
     * </pre>
     * 
     * @param balancedate 
     * @return the balance as a double
     */
    public abstract double getBalance(Book book, Date balancedate) throws LowlevelLedgerException;

    public abstract double getBalance(Book book) throws LowlevelLedgerException;

    /**
     * Calculate the available balance at a given time. This DOES take into account any held transactions.
     * Example sql for implementors: <pre>
     * select c.credit - d.debit from
     *      (
     *          select sum(amount) as credit
     *          from ledger
     *          where transactiondate <= sysdate and (end_date is null or end_date>= sysdate) and credit= 'neu://bob'
     *       ) c,
     *      (
     *          select sum(amount) as debit
     *          from ledger
     *          where transactiondate <= sysdate and end_date is null and debit= 'neu://bob'
     *       ) d
     * <p/>
     * </pre>
     * 
     * @param balancedate 
     * @return the balance as a double
     */
    public abstract double getAvailableBalance(Book book, Date balancedate) throws LowlevelLedgerException;

    public abstract double getAvailableBalance(Book book) throws LowlevelLedgerException;

    /**
     * Use this to indicate to the underlying system that we want to start a database transaction.
     * If implementing ledger supports Transactions in the database layer this implements it.
     */
    public abstract void beginLinkedTransaction();

    /**
     * Use this to indicate to the underlying system that we want to end a database transaction.
     * If implementing ledger supports Transactions in the database layer this implements it.
     */
    public abstract void endLinkedTransactions();

    /**
     * Ledger Implementations use this to create Transactions.
     * The reason for this kind of round the way contructions is that we dont want dodgy Implementations to cause security problems for
     * other implementations.
     * @param comment
     * @param transactionTime
     * @param expiryTime
     * @param xid
     * @return PostedTransaction
     */
/*    protected final PostedTransaction createTransaction(String comment,Date transactionTime, Date expiryTime,String xid) throws TransactionException {
        return new PostedTransaction(this,comment,transactionTime,expiryTime,xid);
    }
 */
    /**
     * Ledger Implementations use this to create Transactions.
     * The reason for this kind of round the way contructions is that we dont want dodgy Implementations to cause security problems for
     * other implementations.
     * 
     * @param transaction An Unposted Transaction containing the Transaction details
     * @param xid         Unique Transaction ID
     * @return PostedTransaction
     */
    protected final PostedTransaction createTransaction(UnPostedTransaction transaction, String xid) throws InvalidTransactionException {
        return new PostedTransaction(transaction, xid);
    }

    /**
     * Ledger Implementations use this to create Transactions.
     * The reason for this kind of round the way contructions is that we dont want dodgy Implementations to cause security problems for
     * other implementations.
     * 
     * @param transaction An Unposted Transaction containing the Transaction details
     * @param xid         Unique Transaction ID
     * @return PostedTransaction
     */
    protected final PostedHeldTransaction createHeldTransaction(UnPostedHeldTransaction transaction, String xid) throws InvalidTransactionException {
        return new PostedHeldTransaction(transaction, xid);
    }

    protected final PostedTransaction createHeldComplete(PostedHeldTransaction hold, double amount, Date time, String comment) throws TransactionExpiredException, InvalidTransactionException, LowlevelLedgerException {
        //TODO Rework these Exception
        if (hold.getTransactionTime().after(hold.getExpiryTime()))
            throw new TransactionExpiredException(this, hold);
        if (amount < 0)
            throw new InvalidTransactionException(this, "The amount must be positive");

        try {
            beginLinkedTransaction();
            //PostedTransaction rev=hold.reverse(comment); // We dont need to reverse this
            UnPostedTransaction tran = new UnPostedTransaction(this, comment, time);
            Iterator iter = hold.getItems();
            while (iter.hasNext()) {
                TransactionItem item = (TransactionItem) iter.next();
                if (item.getAmount() >= 0)
                    tran.addItem(item.getBook(), amount);
                else
                    tran.addItem(item.getBook(), -amount);
            }
            endLinkedTransactions();
            return tran.post();
        } catch (UnBalancedTransactionException e) {
            throw new LowlevelLedgerException(this, e);
        }
    }

    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    private final String name;
    private final String id;

    /**
     * Searches for a Held Transaction based on its Transaction ID
     * 
     * @param idstring A valid ID
     * @return The Transaction object
     */
    public abstract PostedHeldTransaction findHeldTransaction(String idstring) throws LowlevelLedgerException, UnknownTransactionException;

    /**
     * Cancels a Held Transaction.
     * 
     * @param hold 
     * @throws org.neuclear.ledger.LowlevelLedgerException
     *          
     * @throws org.neuclear.ledger.UnknownTransactionException
     *          
     */
    public abstract void performCancelHold(PostedHeldTransaction hold) throws LowlevelLedgerException, UnknownTransactionException;

    public abstract PostedTransaction performCompleteHold(PostedHeldTransaction hold, double amount, Date time, String comment) throws InvalidTransactionException, LowlevelLedgerException, TransactionExpiredException;

    public static Ledger getInstance() throws ConfigurationException {
        return (Ledger) Configuration.getComponent(Ledger.class, "neuclear-ledger");
    }
}
