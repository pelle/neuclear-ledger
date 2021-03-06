package org.neuclear.ledger.browser;

import org.neuclear.ledger.Book;

import java.util.Date;

/*
NeuClear Distributed Transaction Clearing Platform
(C) 2003 Pelle Braendgaard

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

$Id: BookBrowser.java,v 1.7 2004/05/05 20:46:23 pelle Exp $
$Log: BookBrowser.java,v $
Revision 1.7  2004/05/05 20:46:23  pelle
BookListBrowser works both in SimpleLedgerController and HibernateLedgerController
Added new interface Browser, which is implemented by both BookBrowser and BookListBrowser

Revision 1.6  2004/04/19 18:57:25  pelle
Updated Ledger to support more advanced book information.
You can now create a book or fetch a book by doing getBook(String id) on the ledger.
You can register a book or upddate an existing one using registerBook()
SimpleLedger now works and passes all tests.
HibernateLedger has been implemented, but there are a few things that dont work yet.

Revision 1.5  2004/03/31 23:11:09  pelle
Reworked the ID's of the transactions. The primary ID is now the request ID.
Receipt ID's are optional and added using a separate set method.
The various interactive passphrase agents now have shell methods for the new interactive approach.

Revision 1.4  2004/03/26 23:36:34  pelle
The simple browse(book) now works on hibernate, I have implemented the other two, which currently don not constrain the query correctly.

Revision 1.3  2004/03/25 22:04:45  pelle
The first shell for the HibernateBookBrowser

Revision 1.2  2004/03/21 00:48:35  pelle
The problem with Enveloped signatures has now been fixed. It was a problem in the way transforms work. I have bandaided it, but in the future if better support for transforms need to be made, we need to rethink it a bit. Perhaps using the new crypto channel's in neuclear-commons.

Revision 1.1  2004/01/02 23:18:34  pelle
Added StatementFactory pattern and refactored the ledger to use it.

Revision 1.1  2003/12/31 00:39:04  pelle
Added Drivers for handling different Database dialects in the entity model.
Added BookBrowser pattern to ledger, simplifying the statement writing process.

*/

/**
 * User: pelleb
 * Date: Dec 30, 2003
 * Time: 4:26:52 PM
 */
public abstract class BookBrowser implements Browser {
    public BookBrowser(String book) {
        this.book = book;
    }


    protected final void setRow(String reqid, Book counterparty, String comment, Date valuetime, double amount, Date expirytime, Date cancelled, String completedId) {
        this.reqid = reqid;
        this.counterparty = counterparty;
        this.comment = comment;
        this.valuetime = valuetime;
        this.amount = amount;
        this.cancelled = cancelled;
        this.expirytime = expirytime;
        this.completedId = completedId;
    }

    public String getBook() {
        return book;
    }

    public String getRequestId() {
        return reqid;
    }

    public Book getCounterparty() {
        return counterparty;
    }

    public String getComment() {
        return comment;
    }

    public Date getValuetime() {
        return valuetime;
    }

    public double getAmount() {
        return amount;
    }

    public Date getExpirytime() {
        return expirytime;
    }

    public Date getCancelled() {
        return cancelled;
    }

    public String getCompletedId() {
        return completedId;
    }

    boolean isHeld() {
        return (expirytime != null);
    }

    private final String book;

    private String reqid;
    private Book counterparty;
    private String comment;
    private Date valuetime;
    private Date expirytime;
    private Date cancelled;
    private String completedId;
    private double amount;
}
