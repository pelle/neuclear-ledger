package org.neuclear.ledger;

/**
 * $Id: LedgerController.java,v 1.10 2004/07/23 18:55:27 pelle Exp $
 * $Log: LedgerController.java,v $
 * Revision 1.10  2004/07/23 18:55:27  pelle
 * Added an improved complete method which allows you to specify a book to change to another book when completing a transaction.
 * This is used by the NeuClear Pay Complete Exchange Order process which changes the benificiary book from the agent to the final recipient.
 *
 * Revision 1.9  2004/06/17 15:18:33  pelle
 * Added support for Ledger object within the LedgerController. This is only really implemented in the HibernateLedgerController.
 *
 * Revision 1.8  2004/05/11 22:53:34  pelle
 * The update to ledger expectedly broke a few things around CurrencyController and friends. Most but not all is now fixed.
 *
 * Revision 1.7  2004/05/05 20:46:24  pelle
 * BookListBrowser works both in SimpleLedgerController and HibernateLedgerController
 * Added new interface Browser, which is implemented by both BookBrowser and BookListBrowser
 *
 * Revision 1.6  2004/05/04 17:39:06  pelle
 * Fixed some things with regards to getTestBalance.
 *
 * Revision 1.5  2004/05/03 23:54:19  pelle
 * HibernateLedgerController now supports multiple ledgers.
 * Fixed many unit tests.
 *
 * Revision 1.4  2004/05/01 00:23:40  pelle
 * Added Ledger field to Transaction as well as to getBalance() and friends.
 *
 * Revision 1.3  2004/04/27 15:23:55  pelle
 * Due to a new API change in 0.5 I have changed the name of Ledger and it's implementers to LedgerController.
 *
 * Revision 1.20  2004/04/23 19:09:16  pelle
 * Lots of cleanups and improvements to the userinterface and look of the bux application.
 *
 * Revision 1.19  2004/04/22 23:59:22  pelle
 * Added various statistics to Ledger as well as AssetController
 * Improved look and feel in the web app.
 *
 * Revision 1.18  2004/04/19 18:57:27  pelle
 * Updated Ledger to support more advanced book information.
 * You can now create a book or fetch a book by doing getBook(String id) on the ledger.
 * You can register a book or upddate an existing one using registerBook()
 * SimpleLedger now works and passes all tests.
 * HibernateLedger has been implemented, but there are a few things that dont work yet.
 *
 * Revision 1.17  2004/04/05 22:54:15  pelle
 * API changes in Ledger to support Auditor and CurrencyController in Pay
 *
 * Revision 1.16  2004/04/05 22:06:46  pelle
 * added setHeldReceiptId() method to ledger
 *
 * Revision 1.15  2004/03/31 23:11:10  pelle
 * Reworked the ID's of the transactions. The primary ID is now the request ID.
 * Receipt ID's are optional and added using a separate set method.
 * The various interactive passphrase agents now have shell methods for the new interactive approach.
 *
 * Revision 1.14  2004/03/25 16:44:22  pelle
 * Added getTestBalance() and isBalanced() to Ledger to see if ledger is balanced.
 * The hibernate implementation has changed the comment size to 255 to work with mysql and now
 * has included hibernates full hibernate.properties to make it easier to try various databases.
 * It has now been tested with hsql and mysql.
 *
 * Revision 1.13  2004/03/23 22:01:43  pelle
 * Bumped version numbers for commons and xmlsig througout.
 * Updated repositories and webservers to use old.neuclear.org
 * Various other fixes in project.xml and project.properties on misc projects.
 *
 * Revision 1.12  2004/03/22 21:59:37  pelle
 * SimpleLedger now passes all unit tests
 *
 * Revision 1.11  2004/03/22 17:33:02  pelle
 * Added a verified transfer to neuclear-ledger.
 * Added InsufficientFundsException to be thrown if transfer isnt verified.
 * HeldTransfers also are now verified.
 *
 * Revision 1.10  2004/03/21 00:48:36  pelle
 * The problem with Enveloped signatures has now been fixed. It was a problem in the way transforms work. I have bandaided it, but in the future if better support for transforms need to be made, we need to rethink it a bit. Perhaps using the new crypto channel's in neuclear-commons.
 *
 * Revision 1.9  2003/12/11 23:56:06  pelle
 * Trying to test the ReceiverServlet with cactus. Still no luck. Need to return a ElementProxy of some sort.
 * Cleaned up some missing fluff in the ElementProxy interface. getTagName(), getQName() and getNameSpace() have been killed.
 *
 * Revision 1.8  2003/12/01 17:11:01  pelle
 * Added initial Support for entityengine (OFBiz)
 *
 * Revision 1.7  2003/11/21 04:43:20  pelle
 * EncryptedFileStore now works. It uses the PBECipher with DES3 afair.
 * Otherwise You will Finaliate.
 * Anything that can be final has been made final throughout everyting. We've used IDEA's Inspector tool to find all instance of variables that could be final.
 * This should hopefully make everything more stable (and secure).
 *
 * Revision 1.6  2003/11/11 21:17:32  pelle
 * Further vital reshuffling.
 * org.neudist.crypto.* and org.neudist.utils.* have been moved to respective areas under org.neuclear.commons
 * org.neuclear.signers.* as well as org.neuclear.passphraseagents have been moved under org.neuclear.commons.crypto as well.
 * Did a bit of work on the Canonicalizer and changed a few other minor bits.
 *
 * Revision 1.5  2003/10/29 21:15:12  pelle
 * Refactored the whole signing process. Now we have an interface called Signer which is the old SignerStore.
 * To use it you pass a byte array and an alias. The sign method then returns the signature.
 * If a Signer needs a passphrase it uses a PassPhraseAgent to present a dialogue box, read it from a command line etc.
 * This new Signer pattern allows us to use secure signing hardware such as N-Cipher in the future for server applications as well
 * as SmartCards for end user applications.
 *
 * Revision 1.4  2003/10/28 23:43:14  pelle
 * The GuiDialogAgent now works. It simply presents itself as a simple modal dialog box asking for a passphrase.
 * The two Signer implementations both use it for the passphrase.
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
 * Older versions can be found /cvsroot/neuclear
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

import org.neuclear.commons.crypto.CryptoTools;

import java.util.Date;

/**
 * This is the abstract Ledger class that implementators of the NeuClear Ledger need to implement.
 */
public abstract class LedgerController {

    /**
     * The unique id of the ledger
     *
     * @param id
     */
    public LedgerController(final String id) {
        this.id = id;
    }

    public abstract boolean existsLedger(final String id);

    /**
     * The basic interface for creating Transactions in the database.
     * The implementing class takes this transacion information and stores it with an automatically generated uniqueid.
     *
     * @param trans Transaction to perform
     * @return The reference to the transaction
     */
    public abstract PostedTransaction performTransaction(UnPostedTransaction trans) throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException;

    /**
     * Similar to a transaction but guarantees that there wont be any negative balances left after the transaction.
     *
     * @param trans Transaction to perform
     * @return The reference to the transaction
     */
    public abstract PostedTransaction performVerifiedTransfer(UnPostedTransaction trans) throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException;

    /**
     * The basic interface for creating Transactions in the database.
     * The implementing class takes this transacion information and stores it with an automatically generated uniqueid.
     * This transaction guarantees to not leave a negative balance in any account.
     *
     * @param trans Transaction to perform
     */
    public abstract PostedHeldTransaction performHeldTransfer(UnPostedHeldTransaction trans) throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException;

    /**
     * Cancels a Held Transaction.
     *
     * @param hold
     * @throws org.neuclear.ledger.LowlevelLedgerException
     *
     * @throws org.neuclear.ledger.UnknownTransactionException
     *
     */
    public abstract Date performCancelHold(PostedHeldTransaction hold) throws LowlevelLedgerException, UnknownTransactionException;

    /**
     * Completes a held transaction. Which means:
     * cancelling the hold and performing the transfer with the given updated amount and comment.
     *
     * @param hold    HeldTransaction to complete
     * @param amount  The updatd amount. It must be <= than the amount of the hold
     * @param comment
     * @return
     * @throws InvalidTransactionException
     * @throws LowlevelLedgerException
     * @throws TransactionExpiredException
     */
    public abstract PostedTransaction performCompleteHold(PostedHeldTransaction hold, double amount, String comment) throws InvalidTransactionException, LowlevelLedgerException, TransactionExpiredException, UnknownTransactionException;

    /**
     * Completes a held transaction. Which means:
     * cancelling the hold and performing the transfer with the given updated amount, comment and payee.
     *
     * @param hold     HeldTransaction to complete
     * @param origbook Book to change
     * @param newbook  The new book
     * @param amount   The updatd amount. It must be <= than the amount of the hold
     * @param comment
     * @return
     * @throws InvalidTransactionException
     * @throws LowlevelLedgerException
     * @throws TransactionExpiredException
     */
    public abstract PostedTransaction performCompleteHold(PostedHeldTransaction hold, Book origbook, Book newbook, double amount, String comment) throws InvalidTransactionException, LowlevelLedgerException, TransactionExpiredException, UnknownTransactionException;

    /**
     * Searches for a Transaction based on its Transaction ID
     *
     * @param id A valid ID
     * @return The Transaction object
     */
    public abstract Date getTransactionTime(String id) throws LowlevelLedgerException, UnknownTransactionException;

    /**
     * Calculate the true accounting balance at a given time. This does not take into account any held transactions, thus may not necessarily
     * show the Available balance.<p>
     * Example sql for implementors: <pre>
     * select c.credit - d.debit from
     *      (
     *          select sum(amount) as credit
     *          from ledger
     *          where transactiondate <= sysdate and end_date is null and credit= 'neu://BOB'
     *       ) c,
     *      (
     *          select sum(amount) as debit
     *          from ledger
     *          where transactiondate <= sysdate and end_date is null and debit= 'neu://BOB'
     *       ) d
     * <p/>
     * </pre>
     *
     * @return the balance as a double
     */

    public abstract double getBalance(String ledger, String book) throws LowlevelLedgerException;

    public double getBalance(String book) throws LowlevelLedgerException {
        return getBalance(id, book);
    }


    /**
     * Calculate the available balance at a given time. This DOES take into account any held transactions.
     * Example sql for implementors: <pre>
     * select c.credit - d.debit from
     *      (
     *          select sum(amount) as credit
     *          from ledger
     *          where transactiondate <= sysdate and (end_date is null or end_date>= sysdate) and credit= 'neu://BOB'
     *       ) c,
     *      (
     *          select sum(amount) as debit
     *          from ledger
     *          where transactiondate <= sysdate and end_date is null and debit= 'neu://BOB'
     *       ) d
     * <p/>
     * </pre>
     *
     * @return the balance as a double
     */

    public abstract double getAvailableBalance(String ledger, String book) throws LowlevelLedgerException;

    public double getAvailableBalance(String book) throws LowlevelLedgerException {
        return getAvailableBalance(id, book);
    }

    public abstract long getBookCount(String ledger) throws LowlevelLedgerException;

    public long getBookCount() throws LowlevelLedgerException {
        return getBookCount(id);
    }


//    public abstract long getFundedBookCount() throws LowlevelLedgerException;
    public abstract long getTransactionCount(String ledger) throws LowlevelLedgerException;

    public long getTransactionCount() throws LowlevelLedgerException {
        return getTransactionCount(id);
    }

    public abstract boolean transactionExists(String id) throws LowlevelLedgerException;

    public abstract boolean heldTransactionExists(String id) throws LowlevelLedgerException;

    /**
     * Register a Book in the system
     *
     * @param id
     * @param nickname
     * @param type
     * @param source
     * @param registrationid
     * @return
     * @throws LowlevelLedgerException
     */
    public abstract Book registerBook(String id, String nickname, String type, String source, String registrationid) throws LowlevelLedgerException;

    public abstract Book getBook(String id) throws LowlevelLedgerException, UnknownBookException;

    public abstract Ledger registerLedger(String id, String nickname, String type, String source, String registrationid, String unit, int decimal) throws LowlevelLedgerException;

    public abstract Ledger getLedger(String id) throws LowlevelLedgerException, UnknownLedgerException;

    public String toString() {
        return id;
    }

    public final String getId() {
        return id;
    }

    private final String id;

    /**
     * Searches for a Held Transaction based on its Transaction ID
     *
     * @param idstring A valid ID
     * @return The Transaction object
     */
    public abstract PostedHeldTransaction findHeldTransaction(String idstring) throws LowlevelLedgerException, UnknownTransactionException;

    public abstract void setReceiptId(String id, String receipt) throws LowlevelLedgerException, UnknownTransactionException;

    public abstract void setHeldReceiptId(String id, String receipt) throws LowlevelLedgerException, UnknownTransactionException;

    public abstract double getTestBalance(String ledger) throws LowlevelLedgerException;

    public final double getTestBalance() throws LowlevelLedgerException {
        return getTestBalance(id);
    }

    public final boolean isBalanced() throws LowlevelLedgerException {
        return isBalanced(id);
    }

    public final boolean isBalanced(final String id) throws LowlevelLedgerException {
        return getTestBalance(id) == 0.0;
    }

    public final PostedTransaction transfer(String ledger, String req, String from, String to, double amount, String comment) throws InvalidTransactionException, LowlevelLedgerException, UnBalancedTransactionException, UnknownBookException {
        UnPostedTransaction tran = new UnPostedTransaction(ledger, req, comment);
        tran.addItem(getBook(from), -amount);
        tran.addItem(getBook(to), amount);
        return performTransaction(tran);
    }

    public final PostedTransaction transfer(String from, String to, double amount, String comment) throws InvalidTransactionException, LowlevelLedgerException, UnBalancedTransactionException, UnknownBookException {
        return transfer(id, from, to, amount, comment);
    }

    public final PostedTransaction transfer(String ledger, String from, String to, double amount, String comment) throws InvalidTransactionException, LowlevelLedgerException, UnBalancedTransactionException, UnknownBookException {
        final PostedTransaction tran = transfer(ledger, CryptoTools.createRandomID(), from, to, amount, comment);
        try {
            setReceiptId(tran.getRequestId(), CryptoTools.createRandomID());
        } catch (UnknownTransactionException e) {
            e.printStackTrace();
        }
        return tran;
    }

    public final PostedTransaction verifiedTransfer(String ledger, String req, String from, String to, double amount, String comment) throws InvalidTransactionException, LowlevelLedgerException, UnBalancedTransactionException, InsufficientFundsException, UnknownBookException {
        UnPostedTransaction tran = new UnPostedTransaction(ledger, req, comment);
        tran.addItem(getBook(from), -amount);
        tran.addItem(getBook(to), amount);
        return performVerifiedTransfer(tran);
    }

    public final PostedTransaction verifiedTransfer(String from, String to, double amount, String comment) throws InvalidTransactionException, LowlevelLedgerException, UnBalancedTransactionException, InsufficientFundsException, UnknownBookException {
        final PostedTransaction tran = verifiedTransfer(id, CryptoTools.createRandomID(), from, to, amount, comment);
        try {
            setReceiptId(tran.getRequestId(), CryptoTools.createRandomID());
        } catch (UnknownTransactionException e) {
            e.printStackTrace();
        }
        return tran;
    }

    public final PostedHeldTransaction hold(String ledger, String req, String from, String to, Date expiry, double amount, String comment) throws InvalidTransactionException, LowlevelLedgerException, UnBalancedTransactionException, InsufficientFundsException, UnknownBookException {
        if (getAvailableBalance(ledger, from) - amount < 0)
            throw new InsufficientFundsException(this, from, amount);
        UnPostedHeldTransaction tran = new UnPostedHeldTransaction(ledger, req, comment, expiry);
        tran.addItem(getBook(from), -amount);
        tran.addItem(getBook(to), amount);
        return performHeldTransfer(tran);
    }

    public final PostedHeldTransaction hold(String from, String to, Date expiry, double amount, String comment) throws InvalidTransactionException, LowlevelLedgerException, UnBalancedTransactionException, InsufficientFundsException, UnknownBookException {
        return hold(id, CryptoTools.createRandomID(), from, to, expiry, amount, comment);
    }

    public final Date cancel(String id) throws LowlevelLedgerException, UnknownTransactionException {
        PostedHeldTransaction tran = findHeldTransaction(id);
        return performCancelHold(tran);
    }

    public final PostedTransaction complete(String id, double amount, String comment) throws LowlevelLedgerException, UnknownTransactionException, TransactionExpiredException, InvalidTransactionException {
        PostedHeldTransaction tran = findHeldTransaction(id);
        return performCompleteHold(tran, amount, comment);
    }

    public final PostedTransaction complete(String id, String origbook, String newbook, double amount, String comment) throws LowlevelLedgerException, UnknownTransactionException, TransactionExpiredException, InvalidTransactionException, UnknownBookException {
        PostedHeldTransaction tran = findHeldTransaction(id);
        return performCompleteHold(tran, getBook(origbook), getBook(newbook), amount, comment);
    }


    public abstract void close() throws LowlevelLedgerException;
}
