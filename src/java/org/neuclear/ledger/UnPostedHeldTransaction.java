package org.neuclear.ledger;

/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Jan 25, 2003
 * Time: 12:54:28 PM
 * $Id: UnPostedHeldTransaction.java,v 1.1 2003/09/20 23:16:18 pelle Exp $
 * $Log: UnPostedHeldTransaction.java,v $
 * Revision 1.1  2003/09/20 23:16:18  pelle
 * Initial revision
 *
 * Revision 1.1  2003/07/29 22:57:44  pelle
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
public class UnPostedHeldTransaction extends UnPostedTransaction implements HeldTransaction {
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
     * @param expiryTime
     */
   public UnPostedHeldTransaction(Ledger ledger, String comment, Date transactionTime, Date expiryTime) throws InvalidTransactionException {
        this(ledger,comment,transactionTime,expiryTime,false);
   }
    UnPostedHeldTransaction(Ledger ledger, String comment, Date transactionTime, Date expiryTime, boolean posted) throws InvalidTransactionException {
        super(ledger,comment,transactionTime,posted);
        if (expiryTime!=null&&expiryTime.before(transactionTime))
            throw new InvalidTransactionException(ledger,"Expiration Time must not be before Transaction Time");
        this.expiryTime=expiryTime;
    }

    protected PostedTransaction postTransaction() throws UnBalancedTransactionException, LowlevelLedgerException, InvalidTransactionException {
        return getLedger().performHeldTransaction(this);
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    private Date expiryTime;
}
