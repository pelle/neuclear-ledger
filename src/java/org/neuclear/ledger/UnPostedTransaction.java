package org.neuclear.ledger;

/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Jan 25, 2003
 * Time: 12:54:28 PM
 * $Id: UnPostedTransaction.java,v 1.11 2004/05/01 00:23:40 pelle Exp $
 * $Log: UnPostedTransaction.java,v $
 * Revision 1.11  2004/05/01 00:23:40  pelle
 * Added Ledger field to Transaction as well as to getBalance() and friends.
 *
 * Revision 1.10  2004/04/19 18:57:27  pelle
 * Updated Ledger to support more advanced book information.
 * You can now create a book or fetch a book by doing getBook(String id) on the ledger.
 * You can register a book or upddate an existing one using registerBook()
 * SimpleLedger now works and passes all tests.
 * HibernateLedger has been implemented, but there are a few things that dont work yet.
 *
 * Revision 1.9  2004/03/31 23:11:10  pelle
 * Reworked the ID's of the transactions. The primary ID is now the request ID.
 * Receipt ID's are optional and added using a separate set method.
 * The various interactive passphrase agents now have shell methods for the new interactive approach.
 *
 * Revision 1.8  2004/03/21 00:48:36  pelle
 * The problem with Enveloped signatures has now been fixed. It was a problem in the way transforms work. I have bandaided it, but in the future if better support for transforms need to be made, we need to rethink it a bit. Perhaps using the new crypto channel's in neuclear-commons.
 *
 * Revision 1.7  2003/12/01 17:11:01  pelle
 * Added initial Support for entityengine (OFBiz)
 *
 * Revision 1.6  2003/11/21 04:43:20  pelle
 * EncryptedFileStore now works. It uses the PBECipher with DES3 afair.
 * Otherwise You will Finaliate.
 * Anything that can be final has been made final throughout everyting. We've used IDEA's Inspector tool to find all instance of variables that could be final.
 * This should hopefully make everything more stable (and secure).
 *
 * Revision 1.5  2003/11/11 21:17:32  pelle
 * Further vital reshuffling.
 * org.neudist.crypto.* and org.neudist.utils.* have been moved to respective areas under org.neuclear.commons
 * org.neuclear.signers.* as well as org.neuclear.passphraseagents have been moved under org.neuclear.commons.crypto as well.
 * Did a bit of work on the Canonicalizer and changed a few other minor bits.
 *
 * Revision 1.4  2003/10/29 21:15:13  pelle
 * Refactored the whole signing process. Now we have an interface called Signer which is the old SignerStore.
 * To use it you pass a byte array and an alias. The sign method then returns the signature.
 * If a Signer needs a passphrase it uses a PassPhraseAgent to present a dialogue box, read it from a command line etc.
 * This new Signer pattern allows us to use secure signing hardware such as N-Cipher in the future for server applications as well
 * as SmartCards for end user applications.
 *
 * Revision 1.3  2003/10/28 23:43:15  pelle
 * The GuiDialogAgent now works. It simply presents itself as a simple modal dialog box asking for a passphrase.
 * The two Signer implementations both use it for the passphrase.
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

import java.util.ArrayList;

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
     */
    public UnPostedTransaction(String ledger, final String req, final String comment) throws InvalidTransactionException {
        super(ledger, req, comment, new ArrayList(2));

        balance = 0;
    }


    /**
     * Is the Transaction Balanced
     * 
     * @return 
     */
    public final boolean isBalanced() {
        return (getBalance() == 0);
    }

    /**
     * Get the balance of the Transaction. This should be 0 for posting.
     * 
     * @return 
     */
    public final double getBalance() {
        return balance;
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
    public final synchronized double addItem(final Book book, final double amount) throws InvalidTransactionException {
        if (book == null)
            throw new InvalidTransactionException(null, "You must supply a valid Book");
        items.add(new TransactionItem(book, amount));
        balance += amount;
        return balance;
    }

    private double balance;
}
