package org.neuclear.ledger;

/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Jan 25, 2003
 * Time: 12:54:28 PM
 * $Id: UnPostedTransaction.java,v 1.1 2003/09/20 23:16:18 pelle Exp $
 * $Log: UnPostedTransaction.java,v $
 * Revision 1.1  2003/09/20 23:16:18  pelle
 * Initial revision
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
import java.util.List;
import java.util.LinkedList;

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
     * @param ledger
     * @param comment
     * @param transactionTime
     */
   public UnPostedTransaction(Ledger ledger, String comment, Date transactionTime) throws InvalidTransactionException {
        this(ledger,comment,transactionTime,false);
    }
   UnPostedTransaction(Ledger ledger, String comment, Date transactionTime, boolean posted) throws InvalidTransactionException  {
       super(ledger);
//        if (amount<0)
//            throw new TransactionException("Negative Transactions are not allowed");
        if (transactionTime==null)
            throw new InvalidTransactionException(ledger,"Transaction must have a Transaction Time");

        this.transactionTime=transactionTime;
        this.comment=comment;
        balance=0;
        items=new LinkedList();
    }

    /**
     * Posts a Transaction permanently to it's ledger.
     * Remeber a Transaction must be balanced to be posted.
     * @return A Unique Transaction ID
     */
    public synchronized PostedTransaction post() throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException {
        PostedTransaction postTransaction=null;
        if (isBalanced()) {
            getLedger().beginLinkedTransaction();
            postTransaction=postTransaction();
            posted=true;
            getLedger().endLinkedTransactions();
        } else
            throw new UnBalancedTransactionException(getLedger(),this);
        return postTransaction;
    }

    protected PostedTransaction postTransaction() throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException {
        return getLedger().performTransaction(this);
    }

    /**
     * Is the Transaction Balanced
     * @return
     */
    public boolean isBalanced() {
        return (getBalance()==0);
    }

    /**
     * Get the balance of the Transaction. This should be 0 for posting.
     * @return
     */
    public double getBalance() {
        return balance;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

     public String getComment() {
        return comment;
    }

    public Iterator getItems() {
        return items.iterator();
    }

    public final boolean isPosted() {
        return posted;
    }

    /**
     * Adds an item to an unposted Transaction.
     * Basic Rules for Items:
     * <ul>
     * <li>Transaction must not have been posted already
     * <li>Book must not be null
     * <li>Book must be from the same Ledger as the Transaction
     * </ul>
     * @param book
     * @param amount
     * @return the new balance
     */
    public synchronized double addItem(Book book,double amount) throws InvalidTransactionException {
        if (isPosted())
            throw new InvalidTransactionException(getLedger(),"This Transaction has already been posted. You can no longer add items to it.");
        if (book==null)
            throw new InvalidTransactionException(getLedger(),"You must supply a valid Book");
        if (!book.getLedger().equals(getLedger()))
            throw new InvalidTransactionException(getLedger(),"The book must be part of the same Ledger as the Transaction");
        items.add(new TransactionItem(book,amount));
        balance+=amount;
        return balance;
    }
    private Date transactionTime;
    private String comment;
    private List items;
    private double balance;
    private boolean posted;
}
