package org.neuclear.ledger;
/**
 * $Id: Transaction.java,v 1.2 2003/10/01 17:35:53 pelle Exp $
 * $Log: Transaction.java,v $
 * Revision 1.2  2003/10/01 17:35:53  pelle
 * Made as much as possible immutable for security and reliability reasons.
 * The only thing that isnt immutable are the items and balance of the
 * UnpostedTransaction
 *
 * Revision 1.1.1.1  2003/09/20 23:16:18  pelle
 * First revision of neuclear-ledger in /cvsroot/neuclear
 * Older versions can be found /cvsroot/neudist
 *
 * Revision 1.6  2003/07/29 22:57:44  pelle
 * New version with refactored support for HeldTransactions.
 * Please note that this causes a sql exception when adding held_item rows.
 *
 * Revision 1.5  2003/07/23 17:19:26  pelle
 * Ledgers now have a required display name.
 *
 * Revision 1.4  2003/07/21 18:35:13  pelle
 * Completed Exception handling refactoring
 *
 * Revision 1.3  2003/01/25 23:57:51  pelle
 * Added some new testcases for testing the versioning code.
 * These picked up some errors in SimpleLedger that were fixed.
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
 */

import java.util.Date;
import java.util.Iterator;

/**
 * Class implemting information about a Transaction. Programs initially create UnPostedTransaction's which get Posted to the
 * Ledger and returned as imutable PostedTransaction objects.
 */
public abstract class Transaction {

    Transaction(Ledger ledger,Date transactionTime,String comment) throws InvalidTransactionException {
        if (ledger==null)
            throw new InvalidTransactionException(ledger,"Transaction must have an associated ledger");
        this.ledger=ledger;
        this.transactionTime=transactionTime;
        this.comment=comment;
    }

    /**
     * Creates a simple Transfer Transaction between two accounts and posts it.
     * @param ledger
     * @param debit
     * @param credit
     * @param amount Must be positive
     * @param comment
     * @param transactionTime
     * @return
     */

    public static PostedTransaction createTransfer(Ledger ledger, Book debit, Book credit, double amount, String comment, Date transactionTime) throws InvalidTransactionException, UnBalancedTransactionException, LowlevelLedgerException {
        if (amount<0)
            throw new InvalidTransactionException(ledger,"The amount must be positive in a Transfer");
        UnPostedTransaction transfer=new UnPostedTransaction(ledger,comment,transactionTime);
        transfer.addItem(debit,-amount);
        transfer.addItem(credit,amount);
        return transfer.post();

    }
    /**
     * Creates a simple Transfer Transaction between two accounts and posts it.
     * @param ledger
     * @param debit
     * @param credit
     * @param amount Must be positive
     * @param comment
     * @param transactionTime
     * @return
     */

    public static PostedHeldTransaction createHeldTransfer(Ledger ledger, Book debit, Book credit, double amount, String comment, Date transactionTime,Date heldUntil) throws InvalidTransactionException, UnBalancedTransactionException, LowlevelLedgerException {
        if (amount<0)
            throw new InvalidTransactionException(ledger,"The amount must be positive in a Transfer");
        UnPostedTransaction transfer=new UnPostedHeldTransaction(ledger,comment,transactionTime,heldUntil);
        transfer.addItem(debit,-amount);
        transfer.addItem(credit,amount);
        return (PostedHeldTransaction)transfer.post();

    }


    public Date getTransactionTime() {
        return transactionTime;
    }


    public String getComment() {
        return comment;
    }
    public abstract Iterator getItems();


    protected Ledger getLedger() {
        return ledger;
    }

    final private Ledger ledger;
    private final Date transactionTime;
    private final String comment;

}
