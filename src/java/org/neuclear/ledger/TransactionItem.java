package org.neuclear.ledger;

import java.io.Serializable;

/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Jan 17, 2003
 * Time: 5:40:26 PM
 * $Id: TransactionItem.java,v 1.6 2004/04/19 18:57:27 pelle Exp $
 * $Log: TransactionItem.java,v $
 * Revision 1.6  2004/04/19 18:57:27  pelle
 * Updated Ledger to support more advanced book information.
 * You can now create a book or fetch a book by doing getBook(String id) on the ledger.
 * You can register a book or upddate an existing one using registerBook()
 * SimpleLedger now works and passes all tests.
 * HibernateLedger has been implemented, but there are a few things that dont work yet.
 *
 * Revision 1.5  2004/03/21 00:48:36  pelle
 * The problem with Enveloped signatures has now been fixed. It was a problem in the way transforms work. I have bandaided it, but in the future if better support for transforms need to be made, we need to rethink it a bit. Perhaps using the new crypto channel's in neuclear-commons.
 * <p/>
 * Revision 1.4  2003/11/21 04:43:20  pelle
 * EncryptedFileStore now works. It uses the PBECipher with DES3 afair.
 * Otherwise You will Finaliate.
 * Anything that can be final has been made final throughout everyting. We've used IDEA's Inspector tool to find all instance of variables that could be final.
 * This should hopefully make everything more stable (and secure).
 * <p/>
 * Revision 1.3  2003/11/11 21:17:32  pelle
 * Further vital reshuffling.
 * org.neudist.crypto.* and org.neudist.utils.* have been moved to respective areas under org.neuclear.commons
 * org.neuclear.signers.* as well as org.neuclear.passphraseagents have been moved under org.neuclear.commons.crypto as well.
 * Did a bit of work on the Canonicalizer and changed a few other minor bits.
 * <p/>
 * Revision 1.2  2003/10/01 17:35:53  pelle
 * Made as much as possible immutable for security and reliability reasons.
 * The only thing that isnt immutable are the items and balance of the
 * UnpostedTransaction
 * <p/>
 * Revision 1.1.1.1  2003/09/20 23:16:18  pelle
 * First revision of neuclear-ledger in /cvsroot/neuclear
 * Older versions can be found /cvsroot/neuclear
 * <p/>
 * Revision 1.1  2003/01/18 16:17:46  pelle
 * First checkin of the NeuClear Ledger API.
 * This is meant as a standardized super simple API for applications to use when posting transactions to a ledger.
 * Ledger's could be General Ledger's for accounting applications or Bank Account ledger's for financial applications.
 */
public final class TransactionItem implements Serializable {
    TransactionItem(final Book book, final double amount) {
        this.amount = amount;
        this.book = book;
    }

    public double getAmount() {
        return amount;
    }

    public Book getBook() {
        return book;
    }

    private final Book book;
    private final double amount;
}
