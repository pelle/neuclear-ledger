package org.neuclear.ledger;

/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Jan 25, 2003
 * Time: 12:54:28 PM
 * $Id: UnPostedTransaction.java,v 1.3 2003/10/28 23:43:15 pelle Exp $
 * $Log: UnPostedTransaction.java,v $
 * Revision 1.3  2003/10/28 23:43:15  pelle
 * The PassPhraseDialogue now works. It simply presents itself as a simple modal dialog box asking for a passphrase.
 * The two SignerStore implementations both use it for the passphrase.
 *
 * Revision 1.2  2003/10/01 17:35:53  pelle
 * Made as much as possible immutable for security and reliability reasons.
 * The only thing that isnt immutable are the items and balance of the
 * UnpostedTransaction
 *
 * Revision 1.1.1.1  2003/09/20 23:16:18  pelle
 * First revision of neuclear-ledger in /cvsroot/neuclear
 * Older versions can be found /cvsroot/neudist
 *
 * Revision 1.5  2003/07/29 22:57:44  pelle
 * New version with refactored support for HeldTransactions.
 * Please note that this causes a sql exception when adding held_item rows.
 *
 * Revision 1.4  2003/07/23 17:19:26  pelle
 * Ledgers now have a required display name.
 *
 * Revision 1.3  2003/07/21 18:35:13  pelle
 * Completed Exception handling refactoring
 *
 * Revision 1.2  2003/01/25 23:57:51  pelle
 * Added some new testcases for testing the versioning code.
 * These picked up some errors in SimpleLedger that were fixed.
 *
 * Revision 1.1  2003/01/25 19:14:47  pelle
 * The ridiculously simple SimpleLedger now passes initial test.
 * I've split the Transaction Class into two sub classes and made Transaction  abstract.
 * The two new Transaction Classes reflect the state of the Transaction and their methods reflect this.
 *
 */

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Class for building Transactions
 */
public class UnPostedTransaction extends Transaction {
    /**
     * Basic rules for creating Transactions:
     * <ul>
     * <li>Ledger must not be null
     * <li>transactionTime must not be null
     * <li>if there is an expiryTime it must not be before the transactionTime
     * </ul>
     * 
     * @param ledger          
     * @param comment         
     * @param transactionTime 
     */
    public UnPostedTransaction(Ledger ledger, String comment, Date transactionTime) throws InvalidTransactionException {
        this(ledger, comment, transactionTime, false);
    }

    UnPostedTransaction(Ledger ledger, String comment, Date transactionTime, boolean posted) throws InvalidTransactionException {
        super(ledger, transactionTime, comment);
//        if (amount<0)
//            throw new TransactionException("Negative Transactions are not allowed");
        if (transactionTime == null)
            throw new InvalidTransactionException(ledger, "Transaction must have a Transaction Time");

        balance = 0;
        items = new LinkedList();
    }

    /**
     * Posts a Transaction permanently to it's ledger.
     * Remeber a Transaction must be balanced to be posted.
     * 
     * @return A Unique Transaction ID
     */
    public synchronized PostedTransaction post() throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException {
        PostedTransaction postTransaction = null;
        if (isBalanced()) {
            getLedger().beginLinkedTransaction();
            postTransaction = postTransaction();
            getLedger().endLinkedTransactions();
        } else
            throw new UnBalancedTransactionException(getLedger(), this);
        return postTransaction;
    }

    protected PostedTransaction postTransaction() throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException {
        return getLedger().performTransaction(this);
    }

    /**
     * Is the Transaction Balanced
     * 
     * @return 
     */
    public boolean isBalanced() {
        return (getBalance() == 0);
    }

    /**
     * Get the balance of the Transaction. This should be 0 for posting.
     * 
     * @return 
     */
    public double getBalance() {
        return balance;
    }

    public Iterator getItems() {
        return items.iterator();
    }

    TransactionItem[] getItemArray() {
        TransactionItem itemarray[] = new TransactionItem[items.size()];
        for (int i = 0; i < items.size(); i++) {
            itemarray[i] = (org.neuclear.ledger.TransactionItem) items.get(i);

        }
        return itemarray;
    }


    /**
     * Adds an item to an unposted Transaction.
     * Basic Rules for Items:
     * <ul>
     * <li>Transaction must not have been posted already
     * <li>Book must not be null
     * <li>Book must be from the same Ledger as the Transaction
     * </ul>
     * 
     * @param book   
     * @param amount 
     * @return the new balance
     */
    public synchronized double addItem(Book book, double amount) throws InvalidTransactionException {
        if (book == null)
            throw new InvalidTransactionException(getLedger(), "You must supply a valid Book");
        if (!book.getLedger().equals(getLedger()))
            throw new InvalidTransactionException(getLedger(), "The book must be part of the same Ledger as the Transaction");
        items.add(new TransactionItem(book, amount));
        balance += amount;
        return balance;
    }

    private List items;
    private double balance;
}
