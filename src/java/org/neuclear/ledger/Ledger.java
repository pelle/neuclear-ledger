package org.neuclear.ledger;

/**
 * $Id: Ledger.java,v 1.13 2004/03/23 22:01:43 pelle Exp $
 * $Log: Ledger.java,v $
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
public abstract class Ledger {

    /**
     * The unique id of the ledger
     * 
     * @param id 
     */
    public Ledger(final String id) {
        this.id = id;
    }


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
    public abstract void performCancelHold(PostedHeldTransaction hold) throws LowlevelLedgerException, UnknownTransactionException;

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
     * Searches for a Transaction based on its Transaction ID
     * 
     * @param id A valid ID
     * @return The Transaction object
     */
    public abstract Date getTransactionTime(String id) throws LowlevelLedgerException, UnknownTransactionException, InvalidTransactionException, UnknownBookException;

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

    public abstract double getBalance(String book) throws LowlevelLedgerException;

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

    public abstract double getAvailableBalance(String book) throws LowlevelLedgerException;

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


    public final PostedTransaction transfer(String req, String id, String from, String to, double amount, String comment) throws InvalidTransactionException, LowlevelLedgerException, UnBalancedTransactionException {
        UnPostedTransaction tran = new UnPostedTransaction(req, id, comment);
        tran.addItem(from, -amount);
        tran.addItem(to, amount);
        return performTransaction(tran);
    }

    public final PostedTransaction transfer(String from, String to, double amount, String comment) throws InvalidTransactionException, LowlevelLedgerException, UnBalancedTransactionException {
        return transfer(CryptoTools.createRandomID(), CryptoTools.createRandomID(), from, to, amount, comment);
    }

    public final PostedTransaction verifiedTransfer(String req, String id, String from, String to, double amount, String comment) throws InvalidTransactionException, LowlevelLedgerException, UnBalancedTransactionException, InsufficientFundsException {
        UnPostedTransaction tran = new UnPostedTransaction(req, id, comment);
        tran.addItem(from, -amount);
        tran.addItem(to, amount);
        return performVerifiedTransfer(tran);
    }

    public final PostedTransaction verifiedTransfer(String from, String to, double amount, String comment) throws InvalidTransactionException, LowlevelLedgerException, UnBalancedTransactionException, InsufficientFundsException {
        return verifiedTransfer(CryptoTools.createRandomID(), CryptoTools.createRandomID(), from, to, amount, comment);
    }

    public final PostedHeldTransaction hold(String req, String id, String from, String to, Date expiry, double amount, String comment) throws InvalidTransactionException, LowlevelLedgerException, UnBalancedTransactionException, InsufficientFundsException {
        UnPostedHeldTransaction tran = new UnPostedHeldTransaction(req, id, comment, expiry);
        tran.addItem(from, -amount);
        tran.addItem(to, amount);
        return performHeldTransfer(tran);
    }

    public final PostedHeldTransaction hold(String from, String to, Date expiry, double amount, String comment) throws InvalidTransactionException, LowlevelLedgerException, UnBalancedTransactionException, InsufficientFundsException {
        if (getAvailableBalance(from) - amount < 0)
            throw new InsufficientFundsException(this, from, amount);
        return hold(CryptoTools.createRandomID(), CryptoTools.createRandomID(), from, to, expiry, amount, comment);
    }

    public final void cancel(String id) throws LowlevelLedgerException, UnknownTransactionException {
        PostedHeldTransaction tran = findHeldTransaction(id);
        performCancelHold(tran);
    }

    public final PostedTransaction complete(String id, double amount, String comment) throws LowlevelLedgerException, UnknownTransactionException, TransactionExpiredException, InvalidTransactionException {
        PostedHeldTransaction tran = findHeldTransaction(id);
        return performCompleteHold(tran, amount, comment);
    }


    public abstract void close() throws LowlevelLedgerException;
}
