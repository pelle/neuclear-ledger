package org.neuclear.ledger.simple;

/**
 * $Id: SimpleLedger.java,v 1.3 2004/03/22 23:20:50 pelle Exp $
 * $Log: SimpleLedger.java,v $
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

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * This implementation is very simple and only is meant for testing. It uses the Java Collection for the implementation and is in no way
 * thread safe or supportive of transactions.
 */
public final class SimpleLedger extends Ledger {

    public SimpleLedger(final String name) {
        super(name);
        id = name;
        ledger = new LinkedHashMap();
        held = new LinkedHashMap();
        balances = new HashMap();

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
        if (!trans.isBalanced())
            throw new UnBalancedTransactionException(this, trans);
        final PostedTransaction posted = new PostedTransaction(trans, new Date());
        ledger.put(id, posted);
        updateBalances(posted);
        return posted;
    }

    private void updateBalances(final PostedTransaction trans) {
        synchronized (balances) {
            Iterator iter = trans.getItems();
            while (iter.hasNext()) {
                TransactionItem item = (TransactionItem) iter.next();
                addTransactionItem(item);
            }
        }
    }

    private void addTransactionItem(TransactionItem item) {
        if (balances.containsKey(item.getBook()))
            balances.put(item.getBook(), new Double(((Double) balances.get(item.getBook())).doubleValue() + item.getAmount()));
        else
            balances.put(item.getBook(), new Double(item.getAmount()));
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
            if (getAvailableBalance(item.getBook()) + item.getAmount() < 0)
                throw new InsufficientFundsException(this, item.getBook(), item.getAmount());
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
        if (!trans.isBalanced())
            throw new UnBalancedTransactionException(this, trans);
        final PostedHeldTransaction posted = new PostedHeldTransaction(trans, new Date());
        held.put(posted.getId(), posted);
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
    public void performCancelHold(PostedHeldTransaction hold) throws LowlevelLedgerException, UnknownTransactionException {
        if (held.containsKey(hold.getId()))
            held.remove(hold.getId());
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
        if (!held.containsKey(hold.getId()))
            throw new UnknownTransactionException(this, hold.getId());
        if (hold.getExpiryTime().before(new Date()))
            throw new TransactionExpiredException(this, hold);
        held.remove(hold.getId());
        PostedTransaction posted = new PostedTransaction(hold, new Date(), amount, comment);
        ledger.put(posted.getId(), posted);
        updateBalances(posted);
        return posted;
    }

    /**
     * Searches for a Transaction based on its Transaction ID
     *
     * @param id A valid ID
     * @return The Transaction object
     */
    public Date getTransactionTime(String id) throws LowlevelLedgerException, UnknownTransactionException, InvalidTransactionException, UnknownBookException {
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
        if (balances.containsKey(book))
            return ((Double) balances.get(book)).doubleValue();
        return 0;
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
                final Iterator items = tran.getItems();
                while (items.hasNext()) {
                    final TransactionItem item = (TransactionItem) items.next();
                    if (item.getBook().equals(book) && item.getAmount() < 0)
                        balance += item.getAmount();
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

    public void close() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private final LinkedHashMap ledger;
    private final LinkedHashMap held;
    private final String id;
    private final HashMap balances;


}