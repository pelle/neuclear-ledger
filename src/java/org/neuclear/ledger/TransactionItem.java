package org.neuclear.ledger;
/**
 * (C) 2003 Antilles Software Ventures SA
 * User: pelleb
 * Date: Jan 17, 2003
 * Time: 5:40:26 PM
 * $Id: TransactionItem.java,v 1.1 2003/09/20 23:16:18 pelle Exp $
 * $Log: TransactionItem.java,v $
 * Revision 1.1  2003/09/20 23:16:18  pelle
 * Initial revision
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

    private Book book;
    private double amount;
}
