package org.neuclear.ledger;

/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Jan 25, 2003
 * Time: 12:48:26 PM
 * $Id: PostedTransaction.java,v 1.8 2004/03/25 19:03:23 pelle Exp $
 * $Log: PostedTransaction.java,v $
 * Revision 1.8  2004/03/25 19:03:23  pelle
 * PostedTransaction and friend now verify the unpostedtransaction is balanced.
 * Updated schema for HHeld to include a cancelled field and a completed field. (The latter doesnt yet work right). Need to read more Hibernate docs to find out why.
 *
 * Revision 1.7  2004/03/23 22:01:43  pelle
 * Bumped version numbers for commons and xmlsig througout.
 * Updated repositories and webservers to use old.neuclear.org
 * Various other fixes in project.xml and project.properties on misc projects.
 *
 * Revision 1.6  2004/03/21 00:48:36  pelle
 * The problem with Enveloped signatures has now been fixed. It was a problem in the way transforms work. I have bandaided it, but in the future if better support for transforms need to be made, we need to rethink it a bit. Perhaps using the new crypto channel's in neuclear-commons.
 *
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

import java.util.Date;

/**
 * This class defines the type of Transactions that have been posted. They are therefore immutable.
 * They are created by the Ledger normally based on an UnPostedTransaction.
 * They are assumed to be balanced.
 */
public class PostedTransaction extends Transaction {

    /**
     * Standard Constructor.
     *
     * @param orig
     */
    public PostedTransaction(final UnPostedTransaction orig, final Date time) throws InvalidTransactionException, UnBalancedTransactionException {
        super(orig.getRequestId(), orig.getId(), orig.getComment(), orig.getItemList());
        if (!orig.isBalanced())
            throw new UnBalancedTransactionException(null, orig);
        this.transactionTime = time;

    }

    public PostedTransaction(final PostedHeldTransaction orig, final Date time, final double amount, final String comment) throws InvalidTransactionException {
        super(orig.getRequestId(), orig.getId(), comment, orig.getItemList());
        this.transactionTime = time;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }


    private final Date transactionTime;

}
