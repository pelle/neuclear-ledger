package org.neuclear.ledger.browser;

/*
 *  The NeuClear Project and it's libraries are
 *  (c) 2002-2004 Antilles Software Ventures SA
 *  For more information see: http://neuclear.org
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import org.neuclear.ledger.Book;

/**
 * User: pelleb
 * Date: Dec 30, 2003
 * Time: 4:26:52 PM
 */
public abstract class BookListBrowser implements Browser {
    public BookListBrowser(String ledger) {
        this.ledger = ledger;
    }


    protected final void setRow(Book book, int count, double balance) {
        this.book = book;
        this.count = count;
        this.balance = balance;
    }

    public Book getBook() {
        return book;
    }

    public String getLedger() {
        return ledger;
    }

    public int getCount() {
        return count;
    }

    public double getBalance() {
        return balance;
    }

    protected final String ledger;
    private Book book;
    private int count;
    private double balance;
}
