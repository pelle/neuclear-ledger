package org.neuclear.ledger;

/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Jan 25, 2003
 * Time: 12:54:28 PM
 * $Id: UnPostedHeldTransaction.java,v 1.6 2004/03/31 23:11:10 pelle Exp $
 * $Log: UnPostedHeldTransaction.java,v $
 * Revision 1.6  2004/03/31 23:11:10  pelle
 * Reworked the ID's of the transactions. The primary ID is now the request ID.
 * Receipt ID's are optional and added using a separate set method.
 * The various interactive passphrase agents now have shell methods for the new interactive approach.
 *
 * Revision 1.5  2004/03/21 00:48:36  pelle
 * The problem with Enveloped signatures has now been fixed. It was a problem in the way transforms work. I have bandaided it, but in the future if better support for transforms need to be made, we need to rethink it a bit. Perhaps using the new crypto channel's in neuclear-commons.
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

/**
 * Class for building Transactions
 */
public final class UnPostedHeldTransaction extends UnPostedTransaction implements HeldTransaction {
    /**
     * Basic rules for creating Transactions:
     * <ul>
     * <li>Ledger must not be null
     * <li>transactionTime must not be null
     * <li>if there is an expiryTime it must not be before the transactionTime
     * </ul>
     *
     * @param comment
     * @param expiryTime
     */
    public UnPostedHeldTransaction(final String req, final String comment, final Date expiryTime) throws InvalidTransactionException {
        super(req, comment);
        this.expiryTime = expiryTime;
    }


    public final Date getExpiryTime() {
        return expiryTime;
    }

    private final Date expiryTime;
}
