package org.neuclear.ledger.implementations;
/**
 * $Id: SimpleLedger.java,v 1.2 2003/11/11 21:17:31 pelle Exp $
 * $Log: SimpleLedger.java,v $
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

import java.util.*;

/**
 * This implementation is very simple and only is meant for testing. It uses the Java Collection for the implementation and is in no way
 * thread safe or supportive of transactions.
 */
public final class SimpleLedger extends Ledger {

    public SimpleLedger(String name) {
        super(name,name);
        ledger=new LinkedHashMap();
        books=new HashMap();
    }

    public boolean bookExists(String bookID) {
        return books.containsKey(bookID); //Strictly speaking not true
    }

    /**
     * As this simple implementation doesnt actually have a table of books it just  creates a new
     * instance at every execution.
     * @param bookID
     * @return
     */
    public Book createNewBook(String bookID,String title) throws BookExistsException {
        if (bookExists(bookID))
            throw new BookExistsException(this,bookID);
        Book book=createBookInstance(bookID,title);
        books.put(bookID,book);
        return book;
    }

    /**
     * The basic interface for creating Transactions in the database.
     * The implementing class takes this transacion information and stores it with an automatically generated uniqueid.
     * This id is returned as an identifier of the transaction.
     * @param trans Transaction to perform
     * @ If there was a problem with the Transaction
     * @return Unique ID
     */
    public PostedTransaction performTransaction(UnPostedTransaction trans) throws UnBalancedTransactionException, InvalidTransactionException {
        if (!trans.isBalanced())
            throw new UnBalancedTransactionException(this,trans);
        String id=getID();
        PostedTransaction posted=createTransaction(trans,id);
        ledger.put(id,posted);
        return posted;
    }

    /**
     * The basic interface for creating Transactions in the database.
     * The implementing class takes this transacion information and stores it with an automatically generated uniqueid.
     * This id is returned as an identifier of the transaction.
     * @param trans Transaction to perform
     * @return Unique ID
     */
    public PostedHeldTransaction performHeldTransaction(UnPostedHeldTransaction trans) throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException {
        if (!trans.isBalanced())
             throw new UnBalancedTransactionException(this,trans);
         String id=getID();
         PostedHeldTransaction posted=createHeldTransaction(trans,id);
         ledger.put(id,posted);
         return posted;
     }

    /**
     * Searches for a Transaction based on its Transaction ID
     * @param id A valid ID
     * @return The Transaction object
     * @  If it couldnt find the Transaction
     */
    public PostedTransaction findTransaction(String id) throws UnknownTransactionException {
        if (!ledger.containsKey(id))
            throw new UnknownTransactionException(this,id);

        return (PostedTransaction)ledger.get(id);
    }

    /**
     * Calculate the true accounting balance at a given time. This does not take into account any held transactions, thus may not necessarily
     * show the Available balance.<p>
     * Basic Algorithm:
     * <ol><li>If transactiondate is AFTER balance date SKIP
     * <li>If Transaction DOES NOT have an expiry date its a regular transaction and ADD to balance and SKIP
     * </ol>
     * @param balancedate
     * @return the balance as a double
     */
    public double getBalance(Book book, Date balancedate)  {
        double balance=0;
        // Very silly slow and lazy implementation
        Iterator iter=ledger.keySet().iterator();
        boolean going=true;
        while(iter.hasNext()&&going){
            Transaction tran=(Transaction)ledger.get((String)iter.next());
            // The reason I'm doing !xx.after is because I need this to be <=
            if (!tran.getTransactionTime().after(balancedate)) {
                Iterator items=tran.getItems();
                boolean isHold=(tran instanceof HeldTransaction);
                while (items.hasNext()) {
                    TransactionItem item = (TransactionItem) items.next();
                    if (item.getBook().equals(book)&&!isHold)
                        balance+=item.getAmount();
                }
            } //else going=false;

        }
        System.out.println("Book: "+book.getBookID()+" has a balance of: "+balance);
        return balance;
    }

    public double getBalance(Book book)  {
        return getBalance(book,new Date());
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
     * @param balancedate
     * @return the balance as a double
     */
    public double getAvailableBalance(Book book, Date balancedate)  {
        double balance=0;
        // Very silly slow and lazy implementation
        Iterator iter=ledger.keySet().iterator();
        boolean going=true;
        while(iter.hasNext()){
            Transaction tran=(Transaction)ledger.get((String)iter.next());
            // The reason I'm doing !xx.after is because I need this to be <=
            if (!tran.getTransactionTime().after(balancedate)) {
                Iterator items=tran.getItems();

                boolean isHold=(tran instanceof HeldTransaction);
                boolean isValidHold=isHold&&(!((HeldTransaction)tran).getExpiryTime().before(balancedate));

                while (items.hasNext()) {
                    TransactionItem item = (TransactionItem) items.next();
                    if (
                            item.getBook().equals(book)&&
                            (
                                (!isHold)||
                                (isValidHold&&item.getAmount()<0)||
                                (isValidHold&&item.getAmount()>0&&tran!=null)// TODO This is a quick fix to ensure
                                                                                            // Reversed Holds get taken into account
                                                                                            // in the Available Balance.
                                                                                            // This will break if the new version is also held
                            )
                        )
                        balance+=item.getAmount();
                }
            } //else going=false;

        }
        System.out.println("Book: "+book.getBookID()+" has an available balance of: "+balance);
        return balance;
    }

    public double getAvailableBalance(Book book)  {
     return getAvailableBalance(book,new Date());
    }

    /**
     * Use this to indicate to the underlying system that we want to start a database transaction.
     * If implementing ledger supports Transactions in the database layer this implements it.
     */
    public void beginLinkedTransaction() {

    }

    /**
     * Use this to indicate to the underlying system that we want to end a database transaction.
     * If implementing ledger supports Transactions in the database layer this implements it.
     */
    public void endLinkedTransactions() {

    }

    public String toString() {
        return "Simple Ledger: "+getName();
    }

    /**
     * Searches for a Held Transaction based on its Transaction ID
     * @param idstring A valid ID
     * @return The Transaction object
     */
    public PostedHeldTransaction findHeldTransaction(String idstring) throws LowlevelLedgerException, UnknownTransactionException {
        return null;  //To change body of implemented methods use Options | File Templates.
    }

    /**
     * Cancels a Held Transaction.
     * @param hold
     * @throws LowlevelLedgerException
     * @throws UnknownTransactionException
     */
    public void performCancelHold(PostedHeldTransaction hold) throws LowlevelLedgerException, UnknownTransactionException {
        //To change body of implemented methods use Options | File Templates.
    }

    public PostedTransaction performCompleteHold(PostedHeldTransaction hold, double amount, Date time, String comment) throws InvalidTransactionException, LowlevelLedgerException {
        return null;  //To change body of implemented methods use Options | File Templates.
    }

    private synchronized String getID() {
        return Long.toString(idSeq++);
    }
    private long idSeq=0;
    private LinkedHashMap ledger;
    private HashMap books;

    public Book getBook(String bookID) throws UnknownBookException,LowlevelLedgerException {
        if (bookExists(bookID))
            return (Book)books.get(bookID);
        else
            throw new UnknownBookException(this,bookID);
    }

}
