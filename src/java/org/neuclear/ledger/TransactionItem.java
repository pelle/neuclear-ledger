package org.neuclear.ledger;
/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Jan 17, 2003
 * Time: 5:40:26 PM
 * $Id: TransactionItem.java,v 1.2 2003/10/01 17:35:53 pelle Exp $
 * $Log: TransactionItem.java,v $
 * Revision 1.2  2003/10/01 17:35:53  pelle
 * Made as much as possible immutable for security and reliability reasons.
 * The only thing that isnt immutable are the items and balance of the
 * UnpostedTransaction
 *
 * Revision 1.1.1.1  2003/09/20 23:16:18  pelle
 * First revision of neuclear-ledger in /cvsroot/neuclear
 * Older versions can be found /cvsroot/neudist
 *
 * Revision 1.1  2003/01/18 16:17:46  pelle
 * First checkin of the NeuClear Ledger API.
 * This is meant as a standardized super simple API for applications to use when posting transactions to a ledger.
 * Ledger's could be General Ledger's for accounting applications or Bank Account ledger's for financial applications.
 *
 */
public final class TransactionItem {
    TransactionItem(Book book,double amount) {
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
