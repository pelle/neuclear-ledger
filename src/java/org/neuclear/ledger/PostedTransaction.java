package org.neuclear.ledger;
/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Jan 25, 2003
 * Time: 12:48:26 PM
 * $Id: PostedTransaction.java,v 1.5 2003/12/01 17:11:01 pelle Exp $
 * $Log: PostedTransaction.java,v $
 * Revision 1.5  2003/12/01 17:11:01  pelle
 * Added initial Support for entityengine (OFBiz)
 *
 * Revision 1.4  2003/11/21 04:43:20  pelle
 * EncryptedFileStore now works. It uses the PBECipher with DES3 afair.
 * Otherwise You will Finaliate.
 * Anything that can be final has been made final throughout everyting. We've used IDEA's Inspector tool to find all instance of variables that could be final.
 * This should hopefully make everything more stable (and secure).
 *
 * Revision 1.3  2003/11/11 21:17:32  pelle
 * Further vital reshuffling.
 * org.neudist.crypto.* and org.neudist.utils.* have been moved to respective areas under org.neuclear.commons
 * org.neuclear.signers.* as well as org.neuclear.passphraseagents have been moved under org.neuclear.commons.crypto as well.
 * Did a bit of work on the Canonicalizer and changed a few other minor bits.
 *
 * Revision 1.2  2003/10/01 17:35:53  pelle
 * Made as much as possible immutable for security and reliability reasons.
 * The only thing that isnt immutable are the items and balance of the
 * UnpostedTransaction
 *
 * Revision 1.1.1.1  2003/09/20 23:16:18  pelle
 * First revision of neuclear-ledger in /cvsroot/neuclear
 * Older versions can be found /cvsroot/neuclear
 *
 * Revision 1.5  2003/08/06 16:41:22  pelle
 * Fixed a few implementation bugs with regards to the Held Transactions
 *
 * Revision 1.4  2003/07/29 22:57:44  pelle
 * New version with refactored support for HeldTransactions.
 * Please note that this causes a sql exception when adding held_item rows.
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

import javax.transaction.UserTransaction;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
/**
 * This class defines the type of Transactions that have been posted. They are therefore immutable.
 * They are created by the Ledger normally based on an UnPostedTransaction.
 * They are assumed to be balanced.
 */
public class PostedTransaction extends Transaction {

    /**
     * Standard Constructor.
     * @param orig
     * @param xid
     */
    PostedTransaction(final UnPostedTransaction orig,final String xid) throws InvalidTransactionException {
        super(orig.getLedger(),orig.getTransactionTime(),orig.getComment());
        this.xid=xid;
        this.items=orig.getItemArray();
    }

/*
   PostedTransaction(Ledger ledger, String comment, Date transactionTime, Date expiryTime,String xid) throws TransactionException {
        this(new UnPostedTransaction(ledger,comment,transactionTime,expiryTime,true),xid);
    }
*/

    /**
     * Creates an identical but reverse entry of a transaction. Use this to recredit a transaction.
     * Note! The original Transaction must be posted.
     * @param comment Comment Describing the Reversal
     * @return Unique Transaction ID
      */
    PostedTransaction reverse(final String comment) throws InvalidTransactionException, UnBalancedTransactionException, LowlevelLedgerException {
        final UnPostedTransaction reverse=new UnPostedTransaction(getLedger(),"REVERSAL of "+getXid(),getTransactionTime(),false);
        final Iterator iter=getItems();
        while (iter.hasNext()){
            final TransactionItem item=(TransactionItem)iter.next();
            reverse.addItem(item.getBook(),-item.getAmount());
        }
        return reverse.post();
    }

    /**
     * Create a copy of a Transaction with new transaction times and comment
     * @param transactionTime
     * @param comment
     * @return
     */
    final PostedTransaction copy(final Date transactionTime, final String comment) throws InvalidTransactionException, UnBalancedTransactionException, LowlevelLedgerException {
        final UnPostedTransaction copy=new UnPostedTransaction(getLedger(),comment,transactionTime);
        final Iterator iter=getItems();
        while (iter.hasNext()){
            final TransactionItem item=(TransactionItem)iter.next();
            copy.addItem(item.getBook(),item.getAmount());
        }
        return copy.post();
    }
    /**
     * Creates a new Version of a Transaction. This is useful when you want to only change the times of the transaction.
     * @param transactionTime
     * @param comment
     * @return New Version
     */
    final PostedTransaction revise(final Date transactionTime, final String comment) throws InvalidTransactionException, UnBalancedTransactionException, LowlevelLedgerException {
        final PostedTransaction rev=reverse(comment);
        final UnPostedTransaction tran=new UnPostedTransaction(getLedger(),comment,transactionTime,false);
        final Iterator iter=getItems();
        while (iter.hasNext()){
            final TransactionItem item=(TransactionItem)iter.next();
            tran.addItem(item.getBook(),item.getAmount());
        }
        return tran.post();
    }

    /**
     * Creates a new version of a Transaction, based on the UnPostedTransaction in the Parameter
     * @param revised
     * @return New Version
     */
    final PostedTransaction revise(final UnPostedTransaction revised) throws InvalidTransactionException, UnBalancedTransactionException, LowlevelLedgerException {
        reverse("REVERSE"+revised.getComment());
        final PostedTransaction tran=revised.post();
        return tran;
    }


    public final Iterator getItems() {
        return new Iterator() {
            int i=0;
            public boolean hasNext() {
                return i<items.length;
            }

            public Object next() {
                return items[i++];
            }

            public void remove() {

            }

        };
    }

    public final String getXid() {
        return xid;
    }
    private final TransactionItem[] items;
    private final String xid;

}
