package org.neuclear.ledger;
/**
 * $Id: Book.java,v 1.2 2003/10/01 17:35:53 pelle Exp $
 * $Log: Book.java,v $
 * Revision 1.2  2003/10/01 17:35:53  pelle
 * Made as much as possible immutable for security and reliability reasons.
 * The only thing that isnt immutable are the items and balance of the
 * UnpostedTransaction
 *
 * Revision 1.1.1.1  2003/09/20 23:16:18  pelle
 * First revision of neuclear-ledger in /cvsroot/neuclear
 * Older versions can be found /cvsroot/neudist
 *
 * Revision 1.7  2003/07/29 22:57:43  pelle
 * New version with refactored support for HeldTransactions.
 * Please note that this causes a sql exception when adding held_item rows.
 *
 * Revision 1.6  2003/07/23 18:17:44  pelle
 * Added support for display names in the books.
 *
 * Revision 1.5  2003/07/21 18:35:12  pelle
 * Completed Exception handling refactoring
 *
 * Revision 1.4  2003/07/17 21:21:17  pelle
 * Most SQLLedger methods have been implemented (Now on to debugging them)
 *
 * Revision 1.3  2003/01/25 23:57:50  pelle
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
 * Revision 1.2  2003/01/16 23:03:28  pelle
 * Updated Ledger Architecture.
 * We now have a Ledger class and a stubbed out SimpleLedger implementation.
 * Book is now final. Everything it needs to access it's data is defined as abstract methods in Ledger.
 *
 * We still need a Ledger Factory and to actually implement SimpleLedger with a database.
 *
 */
import java.util.Date;

/**
 * Intelligent <i>Secure</i> Book Class.
 * Implementing classes need some sort of access control on the Constructor.
 * The Construct contains the book id used to identify the current book (or account).
 * There are 2 kinds of Transactions.
 * Permanent Transactions and Temporary Transactions.
 * <ul>
 * <li>Temporary Transactions are used to implement temporary holds on accounts.
 * <li>Permanent Transactions are regular transactions.
 * </ul>
 *
 * Permanent Transaction types always affect balances. Temporary Transaction types only affect the Available Balance of the debit account, not the credit account.
 *
 * TODO For certain operations we require the current time. This is currently done using new Date(). We need a better way of doing this.
 *
 *
 */
public final class Book {
     Book(String book,String name, Ledger ledger) {
        this.book=book;
        this.name=name;
        this.ledger=ledger;
    }

    /**
     * The Unique Identifier of the Book
     * @return String containing the Identifier
     */
    public final String getBookID() {
        return book;
    }
    public final String getDisplayName() {
        return name;
    }
    public final Ledger getLedger() {
        return ledger;
    }

    public boolean equals(Object o){
        return (o instanceof Book)&&(((Book)o).getLedger().equals(getLedger()))&&(((Book)o).getBookID().equals(getBookID()));
    }
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
     *
     * </pre>
     * @param balancedate
     * @return the balance as a double
     */
    public final double getBalance(Date balancedate) throws LowlevelLedgerException {
        return getLedger().getBalance(this,balancedate);
    }
    public final double getBalance() throws LowlevelLedgerException {
        return getLedger().getBalance(this);
    }

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
     *
     * </pre>
     * @param balancedate
     * @return the balance as a double
     */
    public final double getAvailableBalance(Date balancedate) throws LowlevelLedgerException {
        return getLedger().getAvailableBalance(this,balancedate);
    }

    public final double getAvailableBalance() throws LowlevelLedgerException {
        return getLedger().getAvailableBalance(this);
    }

    /**
     * Implementing classes use this to create Transaction objects.
     * @param destination
     * @param amount
     * @param transactionTime
     * @param expiryTime
     * @return The Transaction object
     */
/*
    protected final Transaction createTransaction(String destination,double amount,Date transactionTime, Date expiryTime)  {
        return new Transaction(getBookID(),destination,amount,transactionTime,expiryTime);
    }
*/

/*
    public double getBalance() {
        return getBalance(new Date());
    }
*/


    /**
     * This is the main application level transaction creation method.
     * @param destination Destination Account
     * @param amount Positive Amount
     * @param transactionTime Transaction Date
     * @return Unique Transaction ID
     * @ If there was a problem with Transaction.
     */
    public final PostedTransaction transfer(Book destination, double amount,String comment, Date transactionTime) throws InvalidTransactionException, UnBalancedTransactionException, LowlevelLedgerException {
        return Transaction.createTransfer(getLedger(),this,destination,amount,comment,transactionTime);

    }

    /**
     * Creates a held transaction. This is a temporary transaction, that must be made permanent.
     * It only affects the available balance of the the debit account. This effect disappears after the expiry time.
     * @param destination
     * @param amount
     * @param transactionTime Transaction Date
     * @param expiryTime
     * @return Transaction ID
     * @
     */
    public final PostedHeldTransaction hold(Book destination, double amount, String comment, Date transactionTime, Date expiryTime) throws InvalidTransactionException, UnBalancedTransactionException, LowlevelLedgerException {
        return Transaction.createHeldTransfer(getLedger(),this,destination,amount,comment,transactionTime,expiryTime);
    }


    private final String book;
    private final String name;
    private final Ledger ledger;

}
